package com.shelfmates.ui.viewmodel

import android.app.Application
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shelfmates.BuildConfig
import com.shelfmates.data.local.AppDatabase
import com.shelfmates.data.local.ArcApplicationEntity
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.BookmarkTransactionEntity
import com.shelfmates.data.local.BookmarkRedemptionEntity
import com.shelfmates.data.model.BookmarkRewardCategory
import com.shelfmates.data.model.BookmarkRewardItem
import com.shelfmates.data.model.BookmarkQuest
import com.shelfmates.data.model.BookmarkTier
import com.shelfmates.data.model.BookmarkWallet
import com.shelfmates.data.model.UserRole
import com.shelfmates.data.local.PublicClubEntity
import com.shelfmates.data.local.ReadingProgressEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.local.AtomicShelfAnalyticsEntity
import com.shelfmates.data.local.RedemptionRequestEntity
import com.shelfmates.data.model.ApplicationStatus
import com.shelfmates.data.model.BroadcastType
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.data.model.PromoServiceItem
import com.shelfmates.data.model.VoiceParticipant
import com.shelfmates.data.repository.ReadingProgressRepository
import com.shelfmates.data.repository.ReadingProgressRepositoryImpl
import com.shelfmates.data.repository.ShelfmatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import com.shelfmates.data.remote.ChatMessage
import com.shelfmates.data.remote.ChatbotRole
import com.shelfmates.data.remote.FirebaseService
import com.shelfmates.data.remote.GeminiLiveVoiceService
import com.shelfmates.data.remote.GeminiModel
import com.shelfmates.data.remote.GeminiService
import com.shelfmates.data.remote.MessageSender

enum class MainTab {
    HOME,
    DISCOVER,
    CLUBS,
    BOOKMARKS_REWARDS,
    ARC_OPPORTUNITIES,
    AI_CHAT,
    LIVE_VOICE,
    NOTIFICATIONS,
    PROFILE
}

enum class DiscoverTab {
    PUBLIC_CLUBS,
    ARC_CLUBS,
    GOOGLE_BOOKS
}

enum class MyClubsTab {
    JOINED_PUBLIC,
    ARC_READS,
    AUTHOR_CAMPAIGNS
}

data class ActiveVoiceRoom(
    val clubId: String,
    val clubName: String,
    val genre: String,
    val currentBook: String,
    val isHost: Boolean = false,
    val isMuted: Boolean = false,
    val isHandRaised: Boolean = false,
    val participants: List<VoiceParticipant> = emptyList(),
    val durationSeconds: Int = 0
)

data class ShelfmatesUiState(
    val currentTab: MainTab = MainTab.HOME,
    val selectedGenre: String = "All",
    val searchQuery: String = "",
    val discoverTab: DiscoverTab = DiscoverTab.PUBLIC_CLUBS,
    val myClubsTab: MyClubsTab = MyClubsTab.JOINED_PUBLIC,
    val activeVoiceRoom: ActiveVoiceRoom? = null,
    val selectedPublicClubId: String? = null,
    val selectedArcClubId: String? = null,
    val selectedThreadId: String? = null,
    val isCreateArcDialogOpen: Boolean = false,
    val isCreatePublicClubDialogOpen: Boolean = false,
    val isApplyArcDialogOpen: Boolean = false,
    val isSubmitReviewDialogOpen: Boolean = false,
    val isAmazonDeepLinkPromptOpen: Boolean = false,
    val lastSubmittedReviewId: String? = null,
    val lastReviewedAsin: String = "",
    val isBroadcastDialogOpen: Boolean = false,
    val isPromoBookingDialogOpen: Boolean = false,
    val isAuthorProModalOpen: Boolean = false,
    val isCsvExportModalOpen: Boolean = false,
    val isAddBookLogDialogOpen: Boolean = false,
    val readingBookId: String? = null,
    val readingBookTitle: String = "",
    val readingBookAuthor: String = "",
    val readingBookCover: String = "",
    val readingBookAsin: String = "",
    val readingIsArc: Boolean = false,
    val readingChapterIndex: Int = 0,
    val readingFormat: String = "EPUB",
    val readingSupabaseUrl: String? = null,
    val userFeedbackMessage: String? = null,
    val selectedBookmarkRewardCategory: BookmarkRewardCategory = BookmarkRewardCategory.ALL,
    val selectedRewardForRedeem: BookmarkRewardItem? = null,
    val lastRedemptionSuccess: BookmarkRedemptionEntity? = null,
    val isDailyCheckInSuccessDialogOpen: Boolean = false,
    val earnedBookmarksAnimationAmount: Int? = null,
    val hasClaimedDailyCheckInToday: Boolean = false,
    // Google Books Integration State
    val googleBooksQuery: String = "",
    val googleBooksResults: List<GoogleBookVolumeItem> = emptyList(),
    val isGoogleBooksLoading: Boolean = false,
    val googleBooksError: String? = null,
    val selectedGoogleBookDetail: GoogleBookVolumeItem? = null,
    val isGoogleBookDetailDialogOpen: Boolean = false,
    val selectedShelfCategoryFilter: String = "All",
    val isCreateCustomShelfDialogOpen: Boolean = false
)

class ShelfmatesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ShelfmatesRepository(database.shelfmatesDao())
    val readingProgressRepository: ReadingProgressRepository = ReadingProgressRepositoryImpl(database.readingProgressDao())

    private val _uiState = MutableStateFlow(ShelfmatesUiState())
    val uiState: StateFlow<ShelfmatesUiState> = _uiState.asStateFlow()

    // Login gate: user's selected role (READER / AUTHOR / BOOK_CLUB_MEMBER / ADMIN)
    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    // Authentication progress and failure state for the login gate.
    //
    // Auth failures used to be written only to userFeedbackMessage, which only
    // MainScreen renders. MainScreen is withheld until the gate is passed, so a
    // failed sign-in showed the user nothing at all: the button was tapped and
    // no feedback ever appeared.
    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    fun clearAuthError() {
        _authErrorMessage.value = null
    }

    // Data streams
    val currentUser = repository.currentUser
    val allPublicClubs = repository.allPublicClubs
    val joinedPublicClubs = repository.joinedPublicClubs
    val allArcClubs = repository.allArcClubs
    val myArcReads = repository.myArcReads
    val allBroadcasts = repository.allBroadcasts
    val allNotifications = repository.allNotifications
    val unreadNotificationsCount = repository.unreadNotificationsCount
    val allArcApplications = repository.allArcApplications
    val allReadingProgress = repository.allReadingProgress
    val savedBooks = repository.getSavedBooks()
    val savedBooksMap = repository.getSavedBooksMap()
    val customShelves = repository.getCustomShelves()
    val cloudSyncState = repository.cloudSyncState

    // --- Bookmarks Rewards Ecosystem Data Streams ---
    val allBookmarkRewards: List<BookmarkRewardItem> = repository.getAvailableBookmarkRewards()

    private val _bookmarkQuests: MutableStateFlow<List<BookmarkQuest>> = MutableStateFlow(repository.getInitialBookmarkQuests())
    val bookmarkQuests: StateFlow<List<BookmarkQuest>> = _bookmarkQuests.asStateFlow()

    val bookmarkTransactions: Flow<List<BookmarkTransactionEntity>> = currentUser.flatMapLatest { user ->
        val uid = user?.id ?: repository.currentUserId
        repository.getBookmarkTransactions(uid)
    }

    val bookmarkRedemptions: Flow<List<BookmarkRedemptionEntity>> = currentUser.flatMapLatest { user ->
        val uid = user?.id ?: repository.currentUserId
        repository.getBookmarkRedemptions(uid)
    }

    val bookmarkWallet: StateFlow<BookmarkWallet> = combine(
        bookmarkTransactions,
        bookmarkRedemptions
    ) { txs, reds ->
        val totalEarned = txs.filter { it.type == "EARNED" }.sumOf { it.bookmarksAmount }
        val totalSpent = reds.sumOf { it.costBookmarks }
        val balance = (totalEarned - totalSpent).coerceAtLeast(0)
        val currentTier = BookmarkTier.fromPoints(totalEarned)
        val multiplier = when (currentTier) {
            BookmarkTier.SHELF_CURIOUS -> 1.0f
            BookmarkTier.PAGE_TURNER -> 1.0f
            BookmarkTier.CHAPTER_MASTER -> 1.1f
            BookmarkTier.SHELF_LEGEND -> 1.2f
        }
        BookmarkWallet(
            balance = balance,
            lifetimeEarned = totalEarned,
            tier = currentTier,
            activeStreakDays = 14,
            multiplier = multiplier,
            redemptionsCount = reds.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookmarkWallet(balance = 740, lifetimeEarned = 1040, tier = BookmarkTier.CHAPTER_MASTER)
    )

    // --- Atomic Shelf Stack Analytics (Google Apps Script Webhook) ---
    val currentAuthorAnalytics: StateFlow<AtomicShelfAnalyticsEntity?> = currentUser.flatMapLatest { user ->
        val uid = user?.id ?: repository.currentUserId
        repository.getAtomicShelfAnalytics(uid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // --- Bookmark Redemption Requests (Admin Queue) ---
    val allRedemptionRequests: StateFlow<List<RedemptionRequestEntity>> = repository.getAllRedemptionRequests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- AI Sentiment & Assistant State ---
    private val _reviewSentimentSummary = MutableStateFlow<com.shelfmates.data.model.ReviewSentimentSummary?>(null)
    val reviewSentimentSummary: StateFlow<com.shelfmates.data.model.ReviewSentimentSummary?> = _reviewSentimentSummary.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val geminiService = GeminiService.getInstance()

    val availableGenres = listOf("All", "Fantasy", "Sci-Fi", "Romance", "Mystery", "LitRPG", "Thriller", "Horror", "Non-Fiction", "YA")

    init {
        // Start simulated active voice room participants
    }

    fun setTab(tab: MainTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            selectedPublicClubId = null,
            selectedArcClubId = null
        )
    }

    fun setDiscoverTab(tab: DiscoverTab) {
        _uiState.value = _uiState.value.copy(discoverTab = tab)
    }

    fun setMyClubsTab(tab: MyClubsTab) {
        _uiState.value = _uiState.value.copy(myClubsTab = tab)
    }

    fun setGenreFilter(genre: String) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectPublicClub(clubId: String?) {
        _uiState.value = _uiState.value.copy(selectedPublicClubId = clubId)
    }

    fun selectArcClub(arcId: String?) {
        _uiState.value = _uiState.value.copy(selectedArcClubId = arcId)
    }

    fun selectThread(threadId: String?) {
        _uiState.value = _uiState.value.copy(selectedThreadId = threadId)
    }

    /**
     * Debug-only persona switcher: re-points the local database at one of the
     * demo users from SeedData so screens can be exercised without signing in
     * as several accounts.
     *
     * Refused in release builds. It assigns
     * [ShelfmatesRepository.currentUserId] from an arbitrary string, which would
     * let anyone with a release build read another account's local rows. It also
     * has no meaning in release: the demo database is only seeded in debug (see
     * app/src/release/.../SeedData.kt), so there are no personas to switch to.
     */
    fun switchUserPersona(userId: String) {
        if (!BuildConfig.DEBUG) {
            Log.w(TAG_PERSONA, "Refused persona switch in a release build")
            return
        }

        viewModelScope.launch {
            repository.switchActiveUser(userId)
            val name = when(userId) {
                "user_ray" -> "Ray K. Vance (Author & Reader)"
                "user_priya" -> "Priya Sharma (ARC Reviewer)"
                "user_jamie" -> "Jamie Miller (Book Club Reader)"
                "user_elena" -> "Elena Vance (Sci-Fi Author)"
                else -> "User"
            }
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Switched active persona to $name"
            )
        }
    }

    fun getPublicClubDetail(clubId: String) = repository.getPublicClubById(clubId)
    fun getArcClubDetail(arcId: String) = repository.getArcClubById(arcId)
    fun getApplicationsForArc(arcId: String) = repository.getApplicationsForArc(arcId)
    fun getReviewsForArc(arcId: String) = repository.getReviewsForArc(arcId)
    fun getThreadsForClub(clubId: String) = repository.getThreadsForClub(clubId)
    fun getRepliesForThread(threadId: String) = repository.getRepliesForThread(threadId)
    fun getFollowersForAuthor(authorId: String) = repository.getFollowersForAuthor(authorId)
    fun getBookLogs(userId: String) = repository.getBookLogs(userId)
    fun getPromoServices(): List<PromoServiceItem> = repository.getAvailablePromoServices()

    fun joinPublicClub(clubId: String, join: Boolean) {
        viewModelScope.launch {
            repository.joinPublicClub(clubId, join)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = if (join) "Joined book club! Welcome to the discussions." else "Left book club."
            )
        }
    }

    /**
     * Sets the active user's role and syncs it to Firestore.
     *
     * [UserRole.ADMIN] is refused here as well as in firestore.rules. The UI no
     * longer offers it, but this ViewModel is not the only thing that can call
     * this method, and a role assignment that fails server-side is worse than one
     * that fails loudly and locally.
     */
    fun setUserRole(role: UserRole) {
        if (role == UserRole.ADMIN) {
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "This role can only be granted by the Atomic Shelf team."
            )
            return
        }

        _userRole.value = role
        viewModelScope.launch {
            firebaseService.getCurrentFirebaseUser()?.uid?.let { uid ->
                firebaseService.saveUserRole(uid, role.name)
            }
        }
    }

    fun loadUserRole() {
        viewModelScope.launch {
            firebaseService.getCurrentFirebaseUser()?.uid?.let { uid ->
                val roleName = firebaseService.getUserRole(uid)
                _userRole.value = roleName?.let {
                    try {
                        UserRole.valueOf(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
        }
    }

    fun applyForArc(
        arcClub: ArcClubEntity,
        user: UserEntity,
        message: String,
        goodreadsUrl: String,
        pastReviews: Int,
        externalPlatforms: String = "Amazon, Goodreads"
    ) {
        viewModelScope.launch {
            repository.applyForArc(arcClub, user, message, goodreadsUrl, pastReviews, externalPlatforms)
            val feedbackMsg = if (arcClub.autoApprove) {
                "Instant approval granted! '${arcClub.bookTitle}' is ready for download."
            } else {
                "ARC Application submitted to author ${arcClub.authorName}! You'll be notified upon approval."
            }
            _uiState.value = _uiState.value.copy(
                isApplyArcDialogOpen = false,
                userFeedbackMessage = feedbackMsg
            )
        }
    }

    fun updateApplicationStatus(application: ArcApplicationEntity, status: ApplicationStatus, arcClub: ArcClubEntity) {
        viewModelScope.launch {
            repository.updateApplicationStatus(
                applicationId = application.id,
                status = status,
                arcClubId = arcClub.id,
                readerName = application.readerName,
                bookTitle = arcClub.bookTitle
            )
            val action = if (status == ApplicationStatus.APPROVED) "Approved" else "Declined"
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "$action application for ${application.readerName}."
            )
        }
    }

    fun downloadArcFile(arcClub: ArcClubEntity) {
        viewModelScope.launch {
            val user = currentUser.firstOrNull()
            repository.downloadArcFile(arcClub.id, user?.id ?: repository.currentUserId)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Downloaded '${arcClub.bookTitle}' (${arcClub.format}). Earned +10 🔖!"
            )
        }
    }

    fun submitArcReview(
        arcClub: ArcClubEntity,
        user: UserEntity,
        rating: Int,
        reviewText: String,
        postToAmazon: Boolean,
        postToGoodreads: Boolean,
        postToBooktok: Boolean = false
    ) {
        viewModelScope.launch {
            val words = reviewText.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
            val isDetailed = words >= 200
            val rewardDesc = if (isDetailed) "+60 🔖 (with 200+ word bonus)" else "+40 🔖"

            val reviewId = repository.submitArcReview(
                arcClub = arcClub,
                user = user,
                rating = rating,
                reviewText = reviewText,
                postToAmazon = postToAmazon,
                postToGoodreads = postToGoodreads,
                postToBooktok = postToBooktok
            )
            _uiState.value = _uiState.value.copy(
                isSubmitReviewDialogOpen = false,
                lastSubmittedReviewId = reviewId,
                lastReviewedAsin = arcClub.asin,
                isAmazonDeepLinkPromptOpen = true,
                userFeedbackMessage = "Review submitted in Shelfmates! Earned $rewardDesc. Next: Copy to Amazon."
            )
        }
    }

    fun markAmazonReviewCompleted(reviewId: String) {
        viewModelScope.launch {
            repository.markAmazonPosted(reviewId)
            _uiState.value = _uiState.value.copy(
                isAmazonDeepLinkPromptOpen = false,
                userFeedbackMessage = "Awesome! Amazon review marked as completed."
            )
        }
    }

    fun dismissAmazonPrompt() {
        _uiState.value = _uiState.value.copy(isAmazonDeepLinkPromptOpen = false)
    }

    fun sendOneTapReviewReminders(arcClub: ArcClubEntity) {
        viewModelScope.launch {
            val count = repository.sendOneTapReminderPush(arcClub)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Dispatched 1-tap push notifications to $count pending reviewers."
            )
        }
    }

    fun createArcClub(
        user: UserEntity,
        title: String,
        genre: String,
        blurb: String,
        format: String,
        slotLimit: Int,
        deadlineDate: String,
        asin: String,
        minReviews: Int,
        coverUrl: String? = null,
        bookfunnelLink: String? = null,
        autoApprove: Boolean = false
    ) {
        viewModelScope.launch {
            repository.createArcClub(
                user = user,
                bookTitle = title,
                genre = genre,
                blurb = blurb,
                format = format,
                slotLimit = slotLimit,
                deadlineDate = deadlineDate,
                asin = asin,
                minReviews = minReviews,
                coverUrl = coverUrl,
                bookfunnelLink = bookfunnelLink,
                autoApprove = autoApprove
            )
            _uiState.value = _uiState.value.copy(
                isCreateArcDialogOpen = false,
                userFeedbackMessage = "ARC Opportunity listed for '$title'! Discoverable to early reviewers."
            )
        }
    }

    // --- Atomic Shelf Stack Analytics Methods ---
    fun syncAuthorAtomicShelfStack(author: UserEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(userFeedbackMessage = "Connecting to Atomic Shelf Command Centre Webhook...")
            val result = repository.syncAtomicShelfAnalytics(author)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Synced Atomic Shelf Stack metrics for Client ${result.asClientId}!"
            )
        }
    }

    fun updateAuthorAsClientId(authorId: String, asClientId: String) {
        viewModelScope.launch {
            repository.updateAuthorAsClientId(authorId, asClientId)
            _uiState.value = _uiState.value.copy(userFeedbackMessage = "Atomic Shelf Client ID updated: $asClientId")
        }
    }

    // --- Redemption Requests Methods (Admin & Reader) ---
    fun submitRedemption(
        reward: BookmarkRewardItem,
        shippingAddress: String = "",
        notes: String = "",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = currentUser.firstOrNull() ?: return@launch
            repository.submitRedemptionRequest(user, reward, shippingAddress, notes)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Redeemed '${reward.title}' for ${reward.costBookmarks} 🔖! Admin fulfillment queued."
            )
            onSuccess()
        }
    }

    fun updateRedemptionStatus(id: String, status: String, notes: String, trackingCode: String) {
        viewModelScope.launch {
            repository.updateRedemptionStatus(id, status, notes, trackingCode)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Redemption status updated to $status."
            )
        }
    }

    fun flagApplicationForAdmin(appId: String, flagged: Boolean) {
        viewModelScope.launch {
            repository.flagArcApplicationForAdmin(appId, flagged)
            val msg = if (flagged) "Application flagged for Admin review." else "Admin flag cleared."
            _uiState.value = _uiState.value.copy(userFeedbackMessage = msg)
        }
    }

    // --- AI Capabilities (PRD Section 10) ---
    fun generateSentimentAnalysis(bookTitle: String, reviews: List<String>) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val summary = geminiService.generateSentimentSummary(bookTitle, reviews)
            _reviewSentimentSummary.value = summary
            _isGeneratingAi.value = false
            _uiState.value = _uiState.value.copy(userFeedbackMessage = "Synthesized review sentiment for '$bookTitle'.")
        }
    }

    fun generateBookBlurb(title: String, genre: String, synopsis: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val blurb = geminiService.generateBookBlurb(title, genre, synopsis)
            _isGeneratingAi.value = false
            onResult(blurb)
        }
    }

    fun generateReviewNudge(readerName: String, bookTitle: String, daysRemaining: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val nudge = geminiService.generateReviewNudgeCopy(readerName, bookTitle, daysRemaining)
            _isGeneratingAi.value = false
            onResult(nudge)
        }
    }

    fun generateClubDiscussionPrompts(bookTitle: String, chapter: String, genre: String, onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val prompts = geminiService.generateClubDiscussionPrompts(bookTitle, chapter, genre)
            _isGeneratingAi.value = false
            onResult(prompts)
        }
    }

    fun openArcOpportunities() {
        _uiState.value = _uiState.value.copy(
            currentTab = MainTab.ARC_OPPORTUNITIES,
            selectedPublicClubId = null,
            selectedArcClubId = null
        )
    }

    fun createPublicClub(
        user: UserEntity,
        name: String,
        genre: String,
        description: String,
        currentBookTitle: String,
        currentBookAuthor: String,
        currentBookDescription: String
    ) {
        viewModelScope.launch {
            repository.createPublicClub(user, name, genre, description, currentBookTitle, currentBookAuthor, currentBookDescription)
            _uiState.value = _uiState.value.copy(
                isCreatePublicClubDialogOpen = false,
                userFeedbackMessage = "Public Book Club '$name' created successfully!"
            )
        }
    }

    fun addThreadReply(threadId: String, user: UserEntity, body: String) {
        if (body.isBlank()) return
        viewModelScope.launch {
            repository.addThreadReply(threadId, user, body)
        }
    }

    fun createThread(clubId: String, clubType: String, user: UserEntity, title: String, body: String, category: String) {
        viewModelScope.launch {
            repository.createThread(clubId, clubType, user, title, body, category)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "New discussion thread posted!"
            )
        }
    }

    fun sendBroadcast(user: UserEntity, title: String, message: String, type: BroadcastType, actionUrl: String) {
        viewModelScope.launch {
            repository.sendBroadcast(user, title, message, type, actionUrl)
            _uiState.value = _uiState.value.copy(
                isBroadcastDialogOpen = false,
                userFeedbackMessage = "Broadcast sent to all ${user.followersCount} followers!"
            )
        }
    }

    fun followAuthor(author: UserEntity, follower: UserEntity, emailConsent: Boolean) {
        viewModelScope.launch {
            repository.followAuthor(author, follower, emailConsent)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "You are now following ${author.displayName}!"
            )
        }
    }

    fun unfollowAuthor(authorId: String) {
        viewModelScope.launch {
            repository.unfollowAuthor(authorId)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Unfollowed author."
            )
        }
    }

    fun upgradeToAuthorPro(userId: String) {
        viewModelScope.launch {
            repository.setAuthorPro(userId, true)
            _uiState.value = _uiState.value.copy(
                isAuthorProModalOpen = false,
                userFeedbackMessage = "Subscribed to Author Pro! Unlimited ARC slots & List Export unlocked."
            )
        }
    }

    fun bookPromoService(service: PromoServiceItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPromoBookingDialogOpen = false,
                userFeedbackMessage = "Booked '${service.title}' ($${service.price})! Atomic Shelf ops team will contact you."
            )
        }
    }

    fun joinVoiceRoom(club: PublicClubEntity, user: UserEntity) {
        val simulatedParticipants = listOf(
            VoiceParticipant("1", club.adminName, "Host / Admin", "https://images.unsplash.com/photo-1534528741775-53994a69daeb", isSpeaking = true),
            VoiceParticipant("2", user.displayName, "Speaker", user.avatarUrl, isSpeaking = false),
            VoiceParticipant("3", "Priya Sharma", "Listener", "https://images.unsplash.com/photo-1517841905240-472988babdf9"),
            VoiceParticipant("4", "Jordan Hayes", "Listener", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6"),
            VoiceParticipant("5", "Liam O'Connor", "Listener", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e")
        )
        _uiState.value = _uiState.value.copy(
            activeVoiceRoom = ActiveVoiceRoom(
                clubId = club.id,
                clubName = club.name,
                genre = club.genre,
                currentBook = club.currentBookTitle,
                isHost = club.adminUserId == user.id,
                isMuted = false,
                isHandRaised = false,
                participants = simulatedParticipants
            ),
            userFeedbackMessage = "Joined live voice room: ${club.name}"
        )
        viewModelScope.launch {
            repository.toggleVoiceRoom(club.id, true, simulatedParticipants.size)
        }
    }

    fun toggleVoiceMute() {
        val current = _uiState.value.activeVoiceRoom ?: return
        _uiState.value = _uiState.value.copy(
            activeVoiceRoom = current.copy(isMuted = !current.isMuted)
        )
    }

    fun toggleHandRaise() {
        val current = _uiState.value.activeVoiceRoom ?: return
        val newHand = !current.isHandRaised
        _uiState.value = _uiState.value.copy(
            activeVoiceRoom = current.copy(isHandRaised = newHand),
            userFeedbackMessage = if (newHand) "Hand raised to speak." else "Hand lowered."
        )
    }

    fun leaveVoiceRoom() {
        _uiState.value = _uiState.value.copy(
            activeVoiceRoom = null,
            userFeedbackMessage = "Left voice room."
        )
    }

    fun addBookLog(userId: String, title: String, author: String, rating: Int, notes: String, genre: String) {
        viewModelScope.launch {
            repository.addBookLog(userId, title, author, rating, notes, genre)
            _uiState.value = _uiState.value.copy(
                isAddBookLogDialogOpen = false,
                userFeedbackMessage = "Added '$title' to your Books Read log."
            )
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    fun openCreateArcDialog() { _uiState.value = _uiState.value.copy(isCreateArcDialogOpen = true) }
    fun closeCreateArcDialog() { _uiState.value = _uiState.value.copy(isCreateArcDialogOpen = false) }

    fun openCreatePublicClubDialog() { _uiState.value = _uiState.value.copy(isCreatePublicClubDialogOpen = true) }
    fun closeCreatePublicClubDialog() { _uiState.value = _uiState.value.copy(isCreatePublicClubDialogOpen = false) }

    fun openApplyArcDialog(arcClubId: String? = null) {
        _uiState.value = _uiState.value.copy(
            selectedArcClubId = arcClubId ?: _uiState.value.selectedArcClubId,
            isApplyArcDialogOpen = true
        )
    }
    fun closeApplyArcDialog() { _uiState.value = _uiState.value.copy(isApplyArcDialogOpen = false) }

    fun openSubmitReviewDialog() { _uiState.value = _uiState.value.copy(isSubmitReviewDialogOpen = true) }
    fun closeSubmitReviewDialog() { _uiState.value = _uiState.value.copy(isSubmitReviewDialogOpen = false) }

    fun openBroadcastDialog() { _uiState.value = _uiState.value.copy(isBroadcastDialogOpen = true) }
    fun closeBroadcastDialog() { _uiState.value = _uiState.value.copy(isBroadcastDialogOpen = false) }

    fun openPromoBookingDialog() { _uiState.value = _uiState.value.copy(isPromoBookingDialogOpen = true) }
    fun closePromoBookingDialog() { _uiState.value = _uiState.value.copy(isPromoBookingDialogOpen = false) }

    fun openAuthorProModal() { _uiState.value = _uiState.value.copy(isAuthorProModalOpen = true) }
    fun closeAuthorProModal() { _uiState.value = _uiState.value.copy(isAuthorProModalOpen = false) }

    fun openCsvExportModal() { _uiState.value = _uiState.value.copy(isCsvExportModalOpen = true) }
    fun closeCsvExportModal() { _uiState.value = _uiState.value.copy(isCsvExportModalOpen = false) }

    fun openAddBookLogDialog() { _uiState.value = _uiState.value.copy(isAddBookLogDialogOpen = true) }
    fun closeAddBookLogDialog() { _uiState.value = _uiState.value.copy(isAddBookLogDialogOpen = false) }

    fun openInAppReader(
        bookId: String,
        title: String,
        author: String,
        coverUrl: String = "",
        asin: String = "",
        isArc: Boolean = false,
        chapterIndex: Int = 0,
        format: String = "EPUB",
        supabaseUrl: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            readingBookId = bookId,
            readingBookTitle = title,
            readingBookAuthor = author,
            readingBookCover = coverUrl,
            readingBookAsin = asin,
            readingIsArc = isArc,
            readingChapterIndex = chapterIndex,
            readingFormat = format,
            readingSupabaseUrl = supabaseUrl
        )
    }

    fun getReadingProgress(bookId: String): Flow<ReadingProgressEntity?> = repository.getReadingProgress(bookId)

    fun saveReadingProgress(
        bookId: String,
        lastPageIndex: Int,
        lastChapterIndex: Int = 0,
        totalPagesOrChapters: Int = 0,
        progressPercent: Float = 0f,
        format: String = "EPUB"
    ) {
        viewModelScope.launch {
            val progressEntity = ReadingProgressEntity(
                bookId = bookId,
                lastPageIndex = lastPageIndex,
                lastChapterIndex = lastChapterIndex,
                totalPagesOrChapters = totalPagesOrChapters,
                progressPercent = progressPercent,
                format = format,
                lastReadTimestamp = System.currentTimeMillis()
            )
            repository.saveReadingProgress(
                bookId = bookId,
                lastPageIndex = lastPageIndex,
                lastChapterIndex = lastChapterIndex,
                totalPagesOrChapters = totalPagesOrChapters,
                progressPercent = progressPercent,
                format = format
            )

            // Sync to Firebase Firestore
            // Only sync under a real, signed-in uid. The previous fallback wrote into a
            // fixed demo namespace, so an unauthenticated session could write rows
            // that firestore.rules attributes to that account.
            val uid = firebaseService.getCurrentFirebaseUser()?.uid ?: return@launch
            firebaseService.syncReadingProgressToFirestore(uid, progressEntity)
        }
    }

    fun getLastPage(bookId: String): Flow<Int?> = readingProgressRepository.getLastPage(bookId)

    suspend fun getLastPageDirect(bookId: String): Int? = readingProgressRepository.getLastPageDirect(bookId)

    fun saveLastPage(bookId: String, lastPage: Int) {
        viewModelScope.launch {
            readingProgressRepository.saveLastPage(bookId, lastPage)
        }
    }

    fun closeInAppReader() {
        _uiState.value = _uiState.value.copy(
            readingBookId = null
        )
    }

    fun clearFeedbackMessage() {
        _uiState.value = _uiState.value.copy(userFeedbackMessage = null)
    }

    // ==========================================
    // FIREBASE AUTH & FIRESTORE INTEGRATION
    // ==========================================
    val firebaseService = FirebaseService.getInstance()
    val firebaseAuthState = firebaseService.authState

    fun signInWithGoogle(context: Context) {
        _authErrorMessage.value = null
        _isAuthenticating.value = true
        viewModelScope.launch {
            val result = firebaseService.signInWithGoogle(context)
            result.onSuccess { user ->
                // Point the local Room queries at this account now that it exists.
                user?.let { repository.setActiveUser(it.uid) }
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "Welcome ${user?.displayName ?: "Reader"}! Synced with Firebase Auth."
                )
            }.onFailure { err ->
                _authErrorMessage.value = err.message ?: "Google Sign-In failed."
            }
            _isAuthenticating.value = false
        }
    }

    /**
     * Signs in anonymously. This is the mechanism behind "Continue as Guest".
     *
     * That button used to reveal the role picker without ever calling this, so
     * no session was ever created and the gate sent the user straight back to
     * the sign-in form. The gate advances on the session this produces, never
     * on the role selection that follows it.
     */
    fun signInAnonymously() {
        _authErrorMessage.value = null
        _isAuthenticating.value = true
        viewModelScope.launch {
            val result = firebaseService.signInAnonymously()
            result.onSuccess { user ->
                if (user == null) {
                    // FirebaseService reports success with a null user when Auth
                    // is unavailable. No user means no session, so treating this
                    // as a finished sign-in would strand the user on the gate
                    // with no explanation.
                    _authErrorMessage.value =
                        "Could not start a guest session. Firebase Authentication is not configured for this build."
                } else {
                    repository.setActiveUser(user.uid)
                    _uiState.value = _uiState.value.copy(
                        userFeedbackMessage = "Signed in as Guest with Firebase Firestore persistence."
                    )
                }
            }.onFailure { err ->
                // This branch did not exist before, so a rejected guest sign-in
                // was dropped silently.
                _authErrorMessage.value = err.message ?: "Could not continue as guest."
            }
            _isAuthenticating.value = false
        }
    }

    fun signOutFirebase() {
        firebaseService.signOut()
        // Stop reading the signed-out account's local rows. Without this the
        // previous user's shelf stays on screen behind the login gate.
        repository.setActiveUser(SIGNED_OUT_USER_ID)
        _userRole.value = null
        _uiState.value = _uiState.value.copy(
            userFeedbackMessage = "Signed out of Firebase."
        )
    }



    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating = _isAiGenerating.asStateFlow()

    private val _selectedChatModel = MutableStateFlow(GeminiModel.FLASH)
    val selectedChatModel = _selectedChatModel.asStateFlow()

    private val _selectedChatRole = MutableStateFlow(ChatbotRole.LITERARY_MENTOR)
    val selectedChatRole = _selectedChatRole.asStateFlow()

    fun selectChatModel(model: GeminiModel) {
        _selectedChatModel.value = model
    }

    fun selectChatRole(role: ChatbotRole) {
        _selectedChatRole.value = role
    }

    fun sendChatMessage(userText: String, bookContext: String? = null) {
        if (userText.isBlank() || _isAiGenerating.value) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = userText.trim()
        )
        val currentHistory = _chatMessages.value + userMessage
        _chatMessages.value = currentHistory

        _isAiGenerating.value = true

        viewModelScope.launch {
            val activeModel = _selectedChatModel.value
            val activeRole = _selectedChatRole.value

            val result = geminiService.sendMessage(
                history = currentHistory,
                userMessage = userText.trim(),
                model = activeModel,
                role = activeRole,
                bookContext = bookContext ?: "Shelfmates Club ARC Reading: Pride & Prejudice and Contemporary ARC Selections"
            )

            _isAiGenerating.value = false

            result.onSuccess { replyText ->
                val aiMsg = ChatMessage(
                    sender = MessageSender.GEMINI_AI,
                    text = replyText,
                    modelUsed = "${activeModel.displayName} (${activeRole.title})"
                )
                _chatMessages.value = _chatMessages.value + aiMsg
            }.onFailure { error ->
                val errorMsg = ChatMessage(
                    sender = MessageSender.GEMINI_AI,
                    text = "I encountered an issue connecting to ${activeModel.displayName}: ${error.message}. Please verify your API key or network connection.",
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    // ==========================================
    // GEMINI LIVE VOICE (Live API: gemini-3.1-flash-live-preview)
    // ==========================================
    val geminiLiveVoiceService by lazy {
        GeminiLiveVoiceService(getApplication(), viewModelScope)
    }
    val liveVoiceSessionState by lazy {
        geminiLiveVoiceService.sessionState
    }

    fun startLiveVoiceListening() {
        geminiLiveVoiceService.startListening()
    }

    fun stopLiveVoiceListening() {
        geminiLiveVoiceService.stopListening()
    }

    fun toggleLiveVoiceTts() {
        geminiLiveVoiceService.toggleTts()
    }

    fun sendLiveVoicePrompt(prompt: String) {
        geminiLiveVoiceService.sendVoicePromptToGeminiLive(prompt)
    }

    fun clearLiveVoiceHistory() {
        geminiLiveVoiceService.clearHistory()
    }

    // ==========================================
    // BOOKMARKS REWARDS ECOSYSTEM ACTIONS
    // ==========================================
    fun setBookmarkRewardCategory(category: BookmarkRewardCategory) {
        _uiState.value = _uiState.value.copy(selectedBookmarkRewardCategory = category)
    }

    fun setSelectedRewardForRedeem(reward: BookmarkRewardItem?) {
        _uiState.value = _uiState.value.copy(selectedRewardForRedeem = reward)
    }

    fun closeRedemptionSuccessDialog() {
        _uiState.value = _uiState.value.copy(lastRedemptionSuccess = null)
    }

    fun closeDailyCheckInDialog() {
        _uiState.value = _uiState.value.copy(isDailyCheckInSuccessDialogOpen = false)
    }

    fun clearEarnedBookmarksAnimation() {
        _uiState.value = _uiState.value.copy(earnedBookmarksAnimationAmount = null)
    }

    fun claimQuestReward(questId: String) {
        val currentQuests: List<BookmarkQuest> = _bookmarkQuests.value
        val quest = currentQuests.find { it.id == questId } ?: return
        if (quest.isCompleted && !quest.isClaimed) {
            _bookmarkQuests.value = currentQuests.map {
                if (it.id == questId) it.copy(isClaimed = true) else it
            }
            viewModelScope.launch {
                val uid = repository.currentUserId
                val bonus = (quest.rewardBookmarks * bookmarkWallet.value.multiplier).toInt()
                repository.recordBookmarkTransaction(
                    userId = uid,
                    title = "Quest: ${quest.title}",
                    note = "Claimed quest reward (+${(bookmarkWallet.value.multiplier * 100 - 100).toInt()}% tier bonus)",
                    amount = bonus,
                    type = "EARNED",
                    iconEmoji = quest.iconEmoji
                )
                _uiState.value = _uiState.value.copy(
                    earnedBookmarksAnimationAmount = bonus,
                    userFeedbackMessage = "Claimed +$bonus Bookmarks!"
                )
            }
        }
    }

    fun performDailyCheckIn() {
        if (_uiState.value.hasClaimedDailyCheckInToday) return
        viewModelScope.launch {
            val uid = repository.currentUserId
            val basePoints = 25
            val pointsWithMultiplier = (basePoints * bookmarkWallet.value.multiplier).toInt()
            repository.recordBookmarkTransaction(
                userId = uid,
                title = "Daily Reading Check-in",
                note = "Day 14 Streak bonus active",
                amount = pointsWithMultiplier,
                type = "EARNED",
                iconEmoji = "🔥"
            )
            // Update quest if daily checkin quest exists
            val updatedQuests: List<BookmarkQuest> = _bookmarkQuests.value.map { q ->
                if (q.id == "quest_daily_checkin") q.copy(isCompleted = true, isClaimed = true) else q
            }
            _bookmarkQuests.value = updatedQuests
            _uiState.value = _uiState.value.copy(
                hasClaimedDailyCheckInToday = true,
                isDailyCheckInSuccessDialogOpen = true,
                earnedBookmarksAnimationAmount = pointsWithMultiplier
            )
        }
    }

    fun redeemBookmarkReward(reward: BookmarkRewardItem) {
        val wallet = bookmarkWallet.value
        if (wallet.balance < reward.costBookmarks) {
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Insufficient Bookmarks balance (Need ${reward.costBookmarks} 🔖, Have ${wallet.balance} 🔖)"
            )
            return
        }

        viewModelScope.launch {
            val uid = repository.currentUserId
            val redemption = repository.redeemReward(uid, reward)
            _uiState.value = _uiState.value.copy(
                selectedRewardForRedeem = null,
                lastRedemptionSuccess = redemption,
                userFeedbackMessage = "Successfully redeemed ${reward.title}!"
            )
        }
    }

    fun awardReadingBookmarks(pagesRead: Int, bookTitle: String) {
        if (pagesRead <= 0) return
        viewModelScope.launch {
            val uid = repository.currentUserId
            val earned = (pagesRead * 2).coerceAtLeast(10)
            repository.recordBookmarkTransaction(
                userId = uid,
                title = "Pages Read: $bookTitle",
                note = "Completed reading $pagesRead pages in FolioReader",
                amount = earned,
                type = "EARNED",
                iconEmoji = "📖"
            )
        }
    }

    // ==========================================
    // --- GOOGLE BOOKS API INTEGRATION ---
    // ==========================================

    fun setGoogleBooksQuery(query: String) {
        _uiState.value = _uiState.value.copy(googleBooksQuery = query)
    }

    fun searchGoogleBooks(query: String? = null) {
        val searchQuery = query ?: _uiState.value.googleBooksQuery
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGoogleBooksLoading = true,
                googleBooksError = null,
                googleBooksQuery = searchQuery
            )

            val result = repository.searchGoogleBooks(searchQuery)
            result.onSuccess { books ->
                _uiState.value = _uiState.value.copy(
                    isGoogleBooksLoading = false,
                    googleBooksResults = books,
                    googleBooksError = if (books.isEmpty()) "No books found matching \"$searchQuery\"" else null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGoogleBooksLoading = false,
                    googleBooksError = "Book search error: ${error.localizedMessage ?: "Network issue"}"
                )
            }
        }
    }

    fun searchGoogleBooksByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGoogleBooksLoading = true,
                googleBooksError = null
            )

            val result = repository.searchGoogleBooksByCategory(category)
            result.onSuccess { books ->
                _uiState.value = _uiState.value.copy(
                    isGoogleBooksLoading = false,
                    googleBooksResults = books,
                    googleBooksError = if (books.isEmpty()) "No books found for category \"$category\"" else null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGoogleBooksLoading = false,
                    googleBooksError = "Catalog category search error: ${error.localizedMessage}"
                )
            }
        }
    }

    fun openGoogleBookDetail(book: GoogleBookVolumeItem) {
        _uiState.value = _uiState.value.copy(
            selectedGoogleBookDetail = book,
            isGoogleBookDetailDialogOpen = true
        )
    }

    fun closeGoogleBookDetail() {
        _uiState.value = _uiState.value.copy(
            selectedGoogleBookDetail = null,
            isGoogleBookDetailDialogOpen = false
        )
    }

    fun createPublicClubFromGoogleBook(book: GoogleBookVolumeItem, customClubName: String? = null) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull() ?: UserEntity(
                id = repository.currentUserId,
                role = com.shelfmates.data.model.UserRole.READER,
                displayName = "Reader",
                email = "reader@shelfmates.app",
                phone = "+1 555 0192",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                bio = "Book lover & reviewer",
                genresCsv = "Fiction, Sci-Fi"
            )

            val clubName = customClubName?.takeIf { it.isNotBlank() } ?: "${book.displayTitle} Club"

            repository.createPublicClub(
                user = user,
                name = clubName,
                genre = book.displayCategory,
                description = "Discussion space for ${book.displayTitle} by ${book.displayAuthors}. ${book.displayDescription.take(200)}",
                currentBookTitle = book.displayTitle,
                currentBookAuthor = book.displayAuthors,
                currentBookDescription = book.displayDescription,
                coverUrl = book.secureCoverUrl,
                bookCover = book.secureCoverUrl,
                bookAmazonUrl = book.volumeInfo?.infoLink ?: "https://books.google.com/books?id=${book.id}",
                bookGoodreadsUrl = "https://www.goodreads.com/search?q=${book.isbn13.ifBlank { book.displayTitle }}"
            )

            _uiState.value = _uiState.value.copy(
                isGoogleBookDetailDialogOpen = false,
                selectedGoogleBookDetail = null,
                userFeedbackMessage = "Created Public Club for \"${book.displayTitle}\"!"
            )
        }
    }

    fun createArcCampaignFromGoogleBook(
        book: GoogleBookVolumeItem,
        quota: Int = 25,
        targetReviews: Int = 20,
        formats: String = "EPUB, PDF"
    ) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull() ?: UserEntity(
                id = repository.currentUserId,
                role = com.shelfmates.data.model.UserRole.AUTHOR,
                displayName = book.primaryAuthor.ifBlank { "Author" },
                email = "author@shelfmates.app",
                phone = "+1 555 0192",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                bio = "Author of ${book.displayTitle}",
                genresCsv = book.displayCategory
            )

            repository.createArcClub(
                user = user,
                bookTitle = book.displayTitle,
                genre = book.displayCategory,
                blurb = book.displayDescription,
                format = formats,
                slotLimit = quota,
                deadlineDate = "Within 14 days of receipt",
                asin = book.isbn13.ifBlank { "B0GB${book.id.take(6).uppercase()}" },
                minReviews = targetReviews,
                coverUrl = book.secureCoverUrl,
                amazonUrl = book.volumeInfo?.infoLink ?: "https://books.google.com/books?id=${book.id}",
                goodreadsUrl = "https://www.goodreads.com/search?q=${book.isbn13.ifBlank { book.displayTitle }}"
            )

            _uiState.value = _uiState.value.copy(
                isGoogleBookDetailDialogOpen = false,
                selectedGoogleBookDetail = null,
                userFeedbackMessage = "Created ARC Campaign for \"${book.displayTitle}\"!"
            )
        }
    }

    // --- Virtual Bookshelf Management ---
    fun setShelfCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(selectedShelfCategoryFilter = category)
    }

    fun openCreateCustomShelfDialog() {
        _uiState.value = _uiState.value.copy(isCreateCustomShelfDialogOpen = true)
    }

    fun closeCreateCustomShelfDialog() {
        _uiState.value = _uiState.value.copy(isCreateCustomShelfDialogOpen = false)
    }

    fun createCustomShelf(name: String, iconEmoji: String = "📚") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createCustomShelf(name.trim(), iconEmoji)
            _uiState.value = _uiState.value.copy(
                isCreateCustomShelfDialogOpen = false,
                userFeedbackMessage = "Created shelf \"$name\" and synced to Cloud!"
            )
        }
    }

    fun deleteCustomShelf(shelfId: String, shelfName: String) {
        viewModelScope.launch {
            repository.deleteCustomShelf(shelfId, shelfName)
            // If current filter was this shelf, reset to "All"
            if (_uiState.value.selectedShelfCategoryFilter == shelfName) {
                _uiState.value = _uiState.value.copy(selectedShelfCategoryFilter = "All")
            }
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Deleted shelf \"$shelfName\". Books moved to To Read."
            )
        }
    }

    fun syncBookshelfWithCloud() {
        viewModelScope.launch {
            val result = repository.syncWithFirestoreCloud()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "Bookshelf successfully synced with Cloud Firestore!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    userFeedbackMessage = "Cloud sync note: ${result.exceptionOrNull()?.message ?: "Local copy active"}"
                )
            }
        }
    }

    fun saveGoogleBookToShelf(
        book: GoogleBookVolumeItem,
        category: String = com.shelfmates.data.local.BookShelfCategory.TO_READ,
        personalNotes: String = "",
        personalRating: Int = 0
    ) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull() ?: UserEntity(
                id = repository.currentUserId,
                role = com.shelfmates.data.model.UserRole.READER,
                displayName = "Reader",
                email = "reader@shelfmates.app",
                phone = "+1 555 0192",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                bio = "Book lover & reviewer",
                genresCsv = "Fiction, Sci-Fi"
            )
            repository.saveGoogleBookToShelf(
                user = user,
                book = book,
                category = category,
                personalNotes = personalNotes,
                personalRating = personalRating
            )
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Saved \"${book.displayTitle}\" to $category (synced to Cloud)!"
            )
        }
    }

    fun updateSavedBookCategory(id: String, title: String, newCategory: String) {
        viewModelScope.launch {
            repository.updateSavedBookCategory(id, title, newCategory)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Moved \"$title\" to $newCategory (synced to Cloud)!"
            )
        }
    }

    fun updateSavedBookNotes(id: String, notes: String, rating: Int) {
        viewModelScope.launch {
            repository.updateSavedBookNotes(id, notes, rating)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Updated notes and synced to Cloud!"
            )
        }
    }

    fun removeBookFromShelf(id: String, title: String = "Book") {
        viewModelScope.launch {
            repository.removeSavedBook(id)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Removed \"$title\" from your bookshelf."
            )
        }
    }

    fun removeBookFromShelfByGoogleId(googleBooksId: String, title: String = "Book") {
        viewModelScope.launch {
            repository.removeSavedBookByGoogleId(googleBooksId)
            _uiState.value = _uiState.value.copy(
                userFeedbackMessage = "Removed \"$title\" from your bookshelf."
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        geminiLiveVoiceService.destroy()
    }

    private companion object {
        const val TAG_PERSONA = "ShelfmatesViewModel"

        /**
         * Local-database key used while nobody is signed in. Matches no seeded
         * row, so the UI falls back to empty states instead of demo content.
         */
        const val SIGNED_OUT_USER_ID = "signed_out"
    }
}
