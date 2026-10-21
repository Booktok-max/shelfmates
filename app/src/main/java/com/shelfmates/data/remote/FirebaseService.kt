package com.shelfmates.data.remote

import android.content.Context
import android.util.Log
import com.shelfmates.BuildConfig
import com.shelfmates.data.local.BookLogEntity
import com.shelfmates.data.local.CustomShelfEntity
import com.shelfmates.data.local.ReadingProgressEntity
import com.shelfmates.data.local.SavedBookEntity
import com.shelfmates.data.model.FirestoreCustomShelf
import com.shelfmates.data.model.FirestoreShelfBook
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException

/**
 * Model representing the authenticated Firebase user session.
 */
data class AuthUserState(
    val isAuthenticated: Boolean = false,
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isFirestoreConnected: Boolean = false,
    val syncStatusMessage: String = "Firebase not configured"
)

/**
 * Service managing Firebase Authentication (Google Sign-In + Anonymous/Email)
 * and Cloud Firestore data synchronization for user data, reading progress, and book logs.
 */
class FirebaseService private constructor() {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(com.google.firebase.FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "FirebaseAuth not configured: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(com.google.firebase.FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "FirebaseFirestore not configured: ${e.message}")
            null
        }
    }

    private val _authState = MutableStateFlow(
        AuthUserState(
            isAuthenticated = auth?.currentUser != null,
            uid = auth?.currentUser?.uid,
            email = auth?.currentUser?.email,
            displayName = auth?.currentUser?.displayName ?: "Shelfmates Reader",
            photoUrl = auth?.currentUser?.photoUrl?.toString(),
            isAnonymous = auth?.currentUser?.isAnonymous ?: true,
            isFirestoreConnected = firestore != null,
            syncStatusMessage = if (firestore != null) "Cloud Firestore Ready" else "Local Persistence Active"
        )
    )
    val authState = _authState.asStateFlow()

    init {
        auth?.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _authState.value = if (user != null) {
                AuthUserState(
                    isAuthenticated = true,
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Shelfmates Reader",
                    photoUrl = user.photoUrl?.toString(),
                    isAnonymous = user.isAnonymous,
                    isFirestoreConnected = firestore != null,
                    syncStatusMessage = "Authenticated via Firebase (${if (user.isAnonymous) "Guest" else "Google"})"
                )
            } else {
                AuthUserState(
                    isAuthenticated = false,
                    uid = null,
                    email = null,
                    displayName = "Guest Reader",
                    photoUrl = null,
                    isAnonymous = true,
                    isFirestoreConnected = firestore != null,
                    syncStatusMessage = "Ready for Google Sign-In"
                )
            }
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth?.currentUser

    /**
     * Sign in with Google using Android Credential Manager and Firebase Auth.
     *
     * Production boundary: this method fails closed. It never silently downgrades
     * a cancelled, mismatched or misconfigured Google sign-in into an anonymous
     * session, because that would let an unauthenticated caller look signed in.
     * The [serverClientId] defaults to the build-time `GOOGLE_WEB_CLIENT_ID`
     * (the OAuth 2.0 *web* client ID); if it is absent the call returns a
     * configuration failure instead of building a broken credential request.
     */
    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID
    ): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        val firebaseAuth = auth
            ?: return@withContext Result.failure(
                IllegalStateException(
                    "Firebase Authentication is unavailable. Add app/google-services.json and rebuild."
                )
            )

        val effectiveServerClientId = serverClientId.trim()
        if (effectiveServerClientId.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException(
                    "Google Sign-In is not configured. Set GOOGLE_WEB_CLIENT_ID at build time " +
                        "(see .env.example) to the OAuth 2.0 web client ID."
                )
            )
        }

        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(effectiveServerClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Google Sign-In returned an unexpected credential type. " +
                            "Check that the SHA-1 of the signing certificate is registered for this appId."
                    )
                )
            }

            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val user = firebaseAuth.signInWithCredential(authCredential).await().user

            if (user == null) {
                return@withContext Result.failure(
                    IllegalStateException("Google Sign-In completed without returning a user.")
                )
            }

            // Sync user profile to Firestore
            syncUserProfileToFirestore(user)

            Result.success(user)
        } catch (e: GetCredentialException) {
            // Includes user cancellation. Surface it instead of granting a session.
            Log.w(TAG, "Google Sign-In was not completed: ${e.message}")
            Result.failure(
                IllegalStateException("Google Sign-In was not completed.", e)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in Google Sign-In: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Sign in as Anonymous / Guest user
     */
    suspend fun signInAnonymously(): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        try {
            val result = auth?.signInAnonymously()?.await()
            val user = result?.user
            user?.let { syncUserProfileToFirestore(it) }
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Sign out current Firebase user
     */
    fun signOut() {
        try {
            auth?.signOut()
            _authState.value = AuthUserState(
                isAuthenticated = false,
                uid = null,
                email = null,
                displayName = "Guest Reader",
                photoUrl = null,
                isAnonymous = true,
                isFirestoreConnected = firestore != null,
                syncStatusMessage = "Signed Out"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out: ${e.message}")
        }
    }

    // ==========================================
    // CLOUD FIRESTORE DATA PERSISTENCE & SYNC
    // ==========================================

    /**
     * Persist user profile entity to Cloud Firestore
     */
    suspend fun syncUserProfileToFirestore(user: FirebaseUser) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: "Reader"),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "lastActive" to System.currentTimeMillis(),
                "isAnonymous" to user.isAnonymous
            )
            firestore?.collection("users")?.document(user.uid)
                ?.set(userData, SetOptions.merge())?.await()
            Log.d(TAG, "User profile successfully synced to Firestore for uid: ${user.uid}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user to Firestore: ${e.message}")
        }
    }

    /**
     * Persist or update reading progress to Firestore
     */
    suspend fun syncReadingProgressToFirestore(userId: String, progress: ReadingProgressEntity) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val progressMap = hashMapOf(
                "userId" to userId,
                "bookId" to progress.bookId,
                "lastPage" to progress.lastPageIndex,
                "lastChapterIndex" to progress.lastChapterIndex,
                "totalPagesOrChapters" to progress.totalPagesOrChapters,
                "progressPercent" to progress.progressPercent,
                "format" to progress.format,
                "lastReadTimestamp" to progress.lastReadTimestamp
            )
            firestore?.collection("users")?.document(userId)
                ?.collection("reading_progress")?.document(progress.bookId)
                ?.set(progressMap, SetOptions.merge())?.await()
            Log.d(TAG, "Reading progress synced to Firestore for book: ${progress.bookId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync reading progress to Firestore: ${e.message}")
        }
    }

    /**
     * Persist a user book log to Firestore
     */




    suspend fun syncBookLogToFirestore(userId: String, bookLog: BookLogEntity) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val logMap = hashMapOf(
                "id" to bookLog.id,
                "userId" to userId,
                "title" to bookLog.title,
                "author" to bookLog.author,
                "rating" to bookLog.rating,
                "notes" to bookLog.notes,
                "genre" to bookLog.genre,
                "dateCompleted" to bookLog.dateCompleted
            )
            firestore?.collection("users")?.document(userId)
                ?.collection("book_logs")?.document(bookLog.id)
                ?.set(logMap, SetOptions.merge())?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync book log to Firestore: ${e.message}")
        }
    }

    /**
     * Persist the user's selected role to Firestore
     */
    suspend fun saveUserRole(userId: String, role: String) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val roleMap = hashMapOf("role" to role)
            firestore?.collection("users")?.document(userId)
                ?.set(roleMap, SetOptions.merge())?.await()
            Log.d(TAG, "User role synced to Firestore: $role for uid: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user role to Firestore: ${e.message}")
        }
    }

    /**
     * Persist or remove the user's membership in a public book club.
     *
     * Membership is stored under the reader's own subtree
     * (`users/{userId}/club_memberships/{clubId}`), so the security rules can
     * enforce that a reader may only write their own membership — the UI can
     * offer join/leave buttons, but the server is the source of truth.
     *
     * Joining creates the document (union write merges an idempotent re-join);
     * leaving deletes it so an empty membership set is the absence of rows,
     * matching the local Room `isJoined` flag's semantics.
     */
    suspend fun syncClubMembershipToFirestore(
        userId: String,
        clubId: String,
        joined: Boolean
    ) = withContext(Dispatchers.IO) {
        if (firestore == null) return@withContext
        try {
            val membershipRef = firestore?.collection("users")?.document(userId)
                ?.collection("club_memberships")?.document(clubId)
            if (joined) {
                membershipRef?.set(
                    hashMapOf(
                        "userId" to userId,
                        "clubId" to clubId,
                        "joinedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                )?.await()
                Log.d(TAG, "Club membership synced to Firestore: $userId -> $clubId")
            } else {
                membershipRef?.delete()?.await()
                Log.d(TAG, "Club membership removed from Firestore: $userId -> $clubId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync club membership to Firestore: ${e.message}")
        }
    }

    /**
     * Fetch the user's saved role from Firestore, if any
     */
    suspend fun getUserRole(userId: String): String? = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext null
        try {
            val doc = fs.collection("users").document(userId).get().await()
            doc.getString("role")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user role from Firestore: ${e.message}")
            null
        }
    }

    /**
     * Listen in real-time to reading progress from Firestore
     */
    fun observeFirestoreReadingProgress(userId: String): Flow<List<ReadingProgressEntity>> = callbackFlow {
        if (firestore == null || userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore?.collection("users")?.document(userId)
            ?.collection("reading_progress")
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore reading_progress listener error: ${error.message}")
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ReadingProgressEntity(
                            bookId = doc.getString("bookId") ?: doc.id,
                            lastPageIndex = doc.getLong("lastPage")?.toInt() ?: 0,
                            lastChapterIndex = doc.getLong("lastChapterIndex")?.toInt() ?: 0,
                            totalPagesOrChapters = doc.getLong("totalPagesOrChapters")?.toInt() ?: 0,
                            progressPercent = (doc.getDouble("progressPercent") ?: 0.0).toFloat(),
                            format = doc.getString("format") ?: "EPUB",
                            lastReadTimestamp = doc.getLong("lastReadTimestamp") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(items)
            }

        awaitClose { listener?.remove() }
    }

    // ========================================================
    // CLOUD FIRESTORE CUSTOM SHELVES & BOOKSHELF SYNC
    // ========================================================

    /**
     * Persist or update a saved book to Cloud Firestore under users/{userId}/bookshelf/{bookId}
     */
    suspend fun syncBookToFirestoreShelf(userId: String, book: SavedBookEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore is not initialized"))
        try {
            val dto = FirestoreShelfBook.fromEntity(book)
            fs.collection("users").document(userId)
                .collection("bookshelf").document(book.id)
                .set(dto.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced book \"${book.title}\" to Firestore under shelf: ${book.category}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync book \"${book.title}\" to Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Remove a book from Cloud Firestore
     */
    suspend fun deleteBookFromFirestoreShelf(userId: String, bookId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore is not initialized"))
        try {
            fs.collection("users").document(userId)
                .collection("bookshelf").document(bookId)
                .delete().await()
            Log.d(TAG, "Deleted book $bookId from Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete book $bookId from Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Persist or update a custom shelf in Cloud Firestore under users/{userId}/custom_shelves/{shelfId}
     */
    suspend fun syncCustomShelfToFirestore(userId: String, shelf: CustomShelfEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore is not initialized"))
        try {
            val dto = FirestoreCustomShelf.fromEntity(shelf)
            fs.collection("users").document(userId)
                .collection("custom_shelves").document(shelf.id)
                .set(dto.toMap(), SetOptions.merge()).await()
            Log.d(TAG, "Synced custom shelf \"${shelf.name}\" to Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync custom shelf to Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Delete a custom shelf from Cloud Firestore
     */
    suspend fun deleteCustomShelfFromFirestore(userId: String, shelfId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore is not initialized"))
        try {
            fs.collection("users").document(userId)
                .collection("custom_shelves").document(shelfId)
                .delete().await()
            Log.d(TAG, "Deleted custom shelf $shelfId from Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete custom shelf from Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Listen in real-time to the user's books on their cloud shelves
     */
    fun observeFirestoreBookshelf(userId: String): Flow<List<SavedBookEntity>> = callbackFlow {
        val fs = firestore
        if (fs == null || userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = fs.collection("users").document(userId)
            .collection("bookshelf")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore bookshelf listener error: ${error.message}")
                    return@addSnapshotListener
                }
                val books = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        SavedBookEntity(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId") ?: userId,
                            googleBooksId = doc.getString("googleBooksId") ?: "",
                            title = doc.getString("title") ?: "Untitled",
                            authors = doc.getString("authors") ?: "Unknown Author",
                            coverUrl = doc.getString("coverUrl") ?: "",
                            category = doc.getString("category") ?: "To Read",
                            description = doc.getString("description") ?: "",
                            genre = doc.getString("genre") ?: "",
                            pageCount = doc.getLong("pageCount")?.toInt() ?: 0,
                            rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                            isbn13 = doc.getString("isbn13") ?: "",
                            notes = doc.getString("notes") ?: "",
                            personalRating = doc.getLong("personalRating")?.toInt() ?: 0,
                            savedAt = doc.getLong("savedAt") ?: System.currentTimeMillis(),
                            cloudSyncedAt = System.currentTimeMillis(),
                            syncState = "SYNCED"
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing Firestore book: ${e.message}")
                        null
                    }
                } ?: emptyList()
                trySend(books)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Listen in real-time to user's custom shelves from Cloud Firestore
     */
    fun observeFirestoreCustomShelves(userId: String): Flow<List<CustomShelfEntity>> = callbackFlow {
        val fs = firestore
        if (fs == null || userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = fs.collection("users").document(userId)
            .collection("custom_shelves")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore custom_shelves listener error: ${error.message}")
                    return@addSnapshotListener
                }
                val shelves = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CustomShelfEntity(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId") ?: userId,
                            name = doc.getString("name") ?: "Shelf",
                            iconEmoji = doc.getString("iconEmoji") ?: "📚",
                            isDefault = doc.getBoolean("isDefault") ?: false,
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            cloudSyncedAt = System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing Firestore shelf: ${e.message}")
                        null
                    }
                } ?: emptyList()
                trySend(shelves)
            }

        awaitClose { listener.remove() }
    }

    /**
     * One-shot fetch of all bookshelf items from Firestore
     */
    suspend fun fetchAllFirestoreBooks(userId: String): List<SavedBookEntity> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection("users").document(userId)
                .collection("bookshelf").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    SavedBookEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: userId,
                        googleBooksId = doc.getString("googleBooksId") ?: "",
                        title = doc.getString("title") ?: "Untitled",
                        authors = doc.getString("authors") ?: "Unknown Author",
                        coverUrl = doc.getString("coverUrl") ?: "",
                        category = doc.getString("category") ?: "To Read",
                        description = doc.getString("description") ?: "",
                        genre = doc.getString("genre") ?: "",
                        pageCount = doc.getLong("pageCount")?.toInt() ?: 0,
                        rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                        isbn13 = doc.getString("isbn13") ?: "",
                        notes = doc.getString("notes") ?: "",
                        personalRating = doc.getLong("personalRating")?.toInt() ?: 0,
                        savedAt = doc.getLong("savedAt") ?: System.currentTimeMillis(),
                        cloudSyncedAt = System.currentTimeMillis(),
                        syncState = "SYNCED"
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all books from Firestore: ${e.message}")
            emptyList()
        }
    }

    /**
     * One-shot fetch of all custom shelves from Firestore
     */
    suspend fun fetchAllFirestoreShelves(userId: String): List<CustomShelfEntity> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection("users").document(userId)
                .collection("custom_shelves").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    CustomShelfEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: userId,
                        name = doc.getString("name") ?: "Shelf",
                        iconEmoji = doc.getString("iconEmoji") ?: "📚",
                        isDefault = doc.getBoolean("isDefault") ?: false,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        cloudSyncedAt = System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching custom shelves from Firestore: ${e.message}")
            emptyList()
        }
    }

    companion object {
        private const val TAG = "FirebaseService"

        @Volatile
        private var INSTANCE: FirebaseService? = null

        fun getInstance(): FirebaseService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseService().also { INSTANCE = it }
            }
        }
    }
}
