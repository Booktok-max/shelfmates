package com.shelfmates.data.model

import com.shelfmates.data.local.CustomShelfEntity
import com.shelfmates.data.local.SavedBookEntity

/**
 * Data Model for a book synchronized to Cloud Firestore under
 * users/{userId}/bookshelf/{bookId}.
 */
data class FirestoreShelfBook(
    val id: String = "",
    val userId: String = "",
    val googleBooksId: String = "",
    val title: String = "",
    val authors: String = "",
    val coverUrl: String = "",
    val category: String = "To Read", // Currently Reading, To Read, Finished, or Custom
    val description: String = "",
    val genre: String = "",
    val pageCount: Int = 0,
    val rating: Double = 0.0,
    val isbn13: String = "",
    val notes: String = "",
    val personalRating: Int = 0,
    val savedAt: Long = 0L,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toEntity(syncState: String = "SYNCED"): SavedBookEntity = SavedBookEntity(
        id = id.ifBlank { "saved_${userId}_$googleBooksId" },
        userId = userId,
        googleBooksId = googleBooksId,
        title = title,
        authors = authors,
        coverUrl = coverUrl,
        category = category,
        description = description,
        genre = genre,
        pageCount = pageCount,
        rating = rating.toFloat(),
        isbn13 = isbn13,
        notes = notes,
        personalRating = personalRating,
        savedAt = if (savedAt > 0) savedAt else System.currentTimeMillis(),
        cloudSyncedAt = System.currentTimeMillis(),
        syncState = syncState
    )

    fun toMap(): Map<String, Any?> = hashMapOf(
        "id" to id,
        "userId" to userId,
        "googleBooksId" to googleBooksId,
        "title" to title,
        "authors" to authors,
        "coverUrl" to coverUrl,
        "category" to category,
        "description" to description,
        "genre" to genre,
        "pageCount" to pageCount,
        "rating" to rating,
        "isbn13" to isbn13,
        "notes" to notes,
        "personalRating" to personalRating,
        "savedAt" to savedAt,
        "lastUpdated" to lastUpdated
    )

    companion object {
        fun fromEntity(entity: SavedBookEntity): FirestoreShelfBook = FirestoreShelfBook(
            id = entity.id,
            userId = entity.userId,
            googleBooksId = entity.googleBooksId,
            title = entity.title,
            authors = entity.authors,
            coverUrl = entity.coverUrl,
            category = entity.category,
            description = entity.description,
            genre = entity.genre,
            pageCount = entity.pageCount,
            rating = entity.rating.toDouble(),
            isbn13 = entity.isbn13,
            notes = entity.notes,
            personalRating = entity.personalRating,
            savedAt = entity.savedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
}

/**
 * Data Model for a custom shelf synchronized to Cloud Firestore under
 * users/{userId}/custom_shelves/{shelfId}.
 */
data class FirestoreCustomShelf(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val iconEmoji: String = "📚",
    val isDefault: Boolean = false,
    val createdAt: Long = 0L,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toEntity(): CustomShelfEntity = CustomShelfEntity(
        id = id.ifBlank { "shelf_${userId}_${name.lowercase().replace(" ", "_")}" },
        userId = userId,
        name = name,
        iconEmoji = iconEmoji,
        isDefault = isDefault,
        createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis(),
        cloudSyncedAt = System.currentTimeMillis()
    )

    fun toMap(): Map<String, Any?> = hashMapOf(
        "id" to id,
        "userId" to userId,
        "name" to name,
        "iconEmoji" to iconEmoji,
        "isDefault" to isDefault,
        "createdAt" to createdAt,
        "lastUpdated" to lastUpdated
    )

    companion object {
        fun fromEntity(entity: CustomShelfEntity): FirestoreCustomShelf = FirestoreCustomShelf(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            iconEmoji = entity.iconEmoji,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt,
            lastUpdated = System.currentTimeMillis()
        )
    }
}

/**
 * Status representation for Cloud Firestore sync.
 */
sealed class CloudSyncState {
    data object Idle : CloudSyncState()
    data class Syncing(val message: String = "Syncing with Cloud Firestore...") : CloudSyncState()
    data class Synced(val lastSyncTimestamp: Long = System.currentTimeMillis(), val itemsCount: Int = 0) : CloudSyncState()
    data class Error(val message: String) : CloudSyncState()
}
