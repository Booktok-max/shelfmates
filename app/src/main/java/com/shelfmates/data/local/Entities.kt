package com.shelfmates.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shelfmates.data.model.ApplicationStatus
import com.shelfmates.data.model.ArcStatus
import com.shelfmates.data.model.BroadcastType
import com.shelfmates.data.model.NotificationType
import com.shelfmates.data.model.ReviewStatus
import com.shelfmates.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val role: UserRole,
    val displayName: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val bio: String,
    val genresCsv: String,
    val amazonAuthorUrl: String = "",
    val asClientId: String = "",
    val isAuthorPro: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val booksCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authorUserId: String,
    val authorName: String,
    val coverUrl: String,
    val description: String,
    val genre: String,
    val amazonUrl: String,
    val goodreadsUrl: String,
    val asin: String = "",
    val googleBooksId: String = ""
)

@Entity(tableName = "public_clubs")
data class PublicClubEntity(
    @PrimaryKey val id: String,
    val name: String,
    val genre: String,
    val description: String,
    val coverUrl: String,
    val currentBookId: String,
    val currentBookTitle: String,
    val currentBookAuthor: String,
    val currentBookCover: String,
    val currentBookDescription: String,
    val currentBookAmazonUrl: String,
    val currentBookGoodreadsUrl: String,
    val adminUserId: String,
    val adminName: String,
    val memberCount: Int,
    val isJoined: Boolean = false,
    val isVoiceRoomActive: Boolean = false,
    val activeVoiceListeners: Int = 0,
    val announcement: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "arc_clubs")
data class ArcClubEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val bookTitle: String,
    val authorUserId: String,
    val authorName: String,
    val coverUrl: String,
    val genre: String,
    val blurb: String,
    val format: String, // "EPUB", "PDF", "MOBI"
    val slotLimit: Int,
    val slotsFilled: Int,
    val deadlineDate: String,
    val daysRemaining: Int,
    val fileUrl: String,
    val fileSizeMb: Double,
    val asin: String,
    val status: ArcStatus = ArcStatus.OPEN,
    val minimumReviewsRequired: Int = 0,
    val isApplied: Boolean = false,
    val isApproved: Boolean = false,
    val hasDownloaded: Boolean = false,
    val isReviewSubmitted: Boolean = false,
    val bookfunnelLink: String = "https://dl.bookfunnel.com/sample_arc",
    val autoApprove: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "arc_applications")
data class ArcApplicationEntity(
    @PrimaryKey val id: String,
    val arcClubId: String,
    val bookTitle: String,
    val readerUserId: String,
    val readerName: String,
    val readerAvatarUrl: String,
    val message: String,
    val goodreadsUrl: String,
    val pastReviewsCount: Int,
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val bookfunnelLink: String = "https://dl.bookfunnel.com/sample_arc",
    val genresCsv: String = "Fantasy,Sci-Fi",
    val externalPlatforms: String = "Amazon,Goodreads",
    val isFlaggedForAdmin: Boolean = false,
    val appliedAt: String
)

@Entity(tableName = "arc_reviews")
data class ArcReviewEntity(
    @PrimaryKey val id: String,
    val arcClubId: String,
    val bookTitle: String,
    val authorUserId: String,
    val readerUserId: String,
    val readerName: String,
    val readerAvatarUrl: String,
    val rating: Int, // 1 - 5
    val reviewText: String,
    val wordCount: Int = 0,
    val amazonPosted: Boolean = false,
    val goodreadsPosted: Boolean = false,
    val booktokPosted: Boolean = false,
    val platformPosted: String = "Shelfmates",
    val status: ReviewStatus = ReviewStatus.REVIEW_SUBMITTED,
    val submittedAt: String
)

@Entity(tableName = "club_threads")
data class ClubThreadEntity(
    @PrimaryKey val id: String,
    val clubId: String,
    val clubType: String, // "PUBLIC" or "ARC"
    val category: String, // "Chapter Discussion", "General", "Theories", "Q&A"
    val title: String,
    val body: String,
    val authorUserId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val replyCount: Int = 0,
    val likesCount: Int = 0,
    val isPinned: Boolean = false,
    val createdAt: String
)

@Entity(tableName = "thread_replies")
data class ThreadReplyEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String,
    val body: String,
    val createdAt: String
)

@Entity(tableName = "broadcasts")
data class BroadcastEntity(
    @PrimaryKey val id: String,
    val authorUserId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val title: String,
    val message: String,
    val type: BroadcastType,
    val actionUrl: String = "",
    val sentAt: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val targetId: String = "", // clubId or authorId
    val isRead: Boolean = false,
    val timeAgo: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows")
data class FollowEntity(
    @PrimaryKey val id: String,
    val authorUserId: String,
    val authorName: String,
    val followerUserId: String,
    val followerName: String,
    val followerEmail: String,
    val emailConsent: Boolean = true,
    val createdAt: String
)

@Entity(tableName = "book_logs")
data class BookLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Int,
    val notes: String,
    val dateCompleted: String,
    val genre: String
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "book_id")
    val bookId: String,

    @ColumnInfo(name = "last_page")
    val lastPageIndex: Int = 0,

    @ColumnInfo(name = "last_chapter_index")
    val lastChapterIndex: Int = 0,

    @ColumnInfo(name = "total_pages_or_chapters")
    val totalPagesOrChapters: Int = 0,

    @ColumnInfo(name = "progress_percent")
    val progressPercent: Float = 0f,

    @ColumnInfo(name = "format")
    val format: String = "EPUB", // "EPUB" or "PDF"

    @ColumnInfo(name = "last_read_timestamp")
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmark_transactions")
data class BookmarkTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val note: String,
    val bookmarksAmount: Int,
    val type: String, // "EARNED" or "REDEEMED"
    val timestamp: Long = System.currentTimeMillis(),
    val iconEmoji: String = "🔖"
)

@Entity(tableName = "bookmark_redemptions")
data class BookmarkRedemptionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val rewardId: String,
    val rewardTitle: String,
    val rewardSubtitle: String = "",
    val costBookmarks: Int,
    val redeemedTimestamp: Long = System.currentTimeMillis(),
    val redemptionCode: String,
    val iconEmoji: String,
    val status: String = "ACTIVE"

)

object BookShelfCategory {
    const val CURRENTLY_READING = "Currently Reading"
    const val TO_READ = "To Read"
    const val WANT_TO_READ = "Want to Read" // Supported for backwards compatibility
    const val FINISHED = "Finished"

    val DEFAULT_SHELVES = listOf(CURRENTLY_READING, TO_READ, FINISHED)
    val ALL_CATEGORIES = listOf(CURRENTLY_READING, WANT_TO_READ, FINISHED)

    fun normalize(shelfName: String): String {
        return when (shelfName.trim()) {
            WANT_TO_READ -> TO_READ
            else -> shelfName.trim()
        }
    }
}

@Entity(tableName = "custom_shelves")
data class CustomShelfEntity(
    @PrimaryKey val id: String, // e.g. shelf_{userId}_{cleanName}
    val userId: String,
    val name: String, // e.g. "Currently Reading", "To Read", "Finished", or custom like "Sci-Fi Favorites"
    val iconEmoji: String = "📚",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val cloudSyncedAt: Long = 0L
)

@Entity(tableName = "saved_books")
data class SavedBookEntity(
    @PrimaryKey val id: String, // e.g. saved_{userId}_{googleBooksId}
    val userId: String,
    val googleBooksId: String,
    val title: String,
    val authors: String,
    val coverUrl: String,
    val category: String = BookShelfCategory.TO_READ, // Currently Reading, To Read, Finished, or Custom Shelf
    val description: String = "",
    val genre: String = "",
    val pageCount: Int = 0,
    val rating: Float = 0f,
    val isbn13: String = "",
    val notes: String = "",
    val personalRating: Int = 0,
    val savedAt: Long = System.currentTimeMillis(),
    val cloudSyncedAt: Long = 0L,
    val syncState: String = "SYNCED" // "SYNCED", "PENDING", "LOCAL"
)

@Entity(tableName = "as_analytics")
data class AtomicShelfAnalyticsEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val asClientId: String,
    val period: String,
    val newsletterPlacements: Int,
    val newsletterSubscribersReached: Int,
    val tiktokViews: Int,
    val ytViews: Int,
    val topVideoUrl: String,
    val syncedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "Active Synced (Apps Script Webhook)"
)

@Entity(tableName = "redemption_requests")
data class RedemptionRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val catalogueItemId: String,
    val catalogueItemName: String,
    val costBookmarks: Int,
    val category: String, // MERCH, AUTHOR_PERK, PLATFORM, COMMUNITY
    val status: String = "PENDING", // PENDING, PROCESSING, FULFILLED, CANCELLED
    val notes: String = "",
    val shippingAddress: String = "",
    val trackingCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fulfilledAt: Long? = null
)


enum class TransactionType {
    EARNED,
    REDEEMED
}

