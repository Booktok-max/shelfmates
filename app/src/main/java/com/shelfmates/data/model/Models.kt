package com.shelfmates.data.model

 enum class UserRole {
      READER,
       AUTHOR,
       BOOK_CLUB_MEMBER,
ADMIN
       }


enum class ArcStatus {
    OPEN,
    CLOSED,
    ARCHIVED
}

enum class ApplicationStatus {
    PENDING,
    APPROVED,
    DECLINED
}

enum class ReviewStatus {
    DELIVERED,
    REVIEW_SUBMITTED,
    OVERDUE
}

enum class BroadcastType {
    RELEASE,
    ARC_OPENING,
    PRICE_DROP,
    COVER_REVEAL,
    ANNOUNCEMENT
}

enum class NotificationType {
    ARC_APPROVED,
    REVIEW_DEADLINE,
    AUTHOR_BROADCAST,
    NEW_ARC_GENRE,
    VOICE_ROOM_STARTED,
    SHELFMATE_REQUEST,
    REDEMPTION_FULFILLED,
    FLAGGED_ARC_APPLICATION
}

data class VoiceParticipant(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val isHandRaised: Boolean = false
)

data class PromoServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val duration: String,
    val reachEstimate: String,
    val iconName: String
)

data class AtomicShelfAnalytics(
    val id: String,
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

data class RedemptionRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val catalogueItemId: String,
    val catalogueItemName: String,
    val costBookmarks: Int,
    val category: String,
    val status: String = "PENDING", // PENDING, PROCESSING, FULFILLED, CANCELLED
    val notes: String = "",
    val shippingAddress: String = "",
    val trackingCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fulfilledAt: Long? = null
)

data class ReviewSentimentSummary(
    val bookTitle: String,
    val totalReviews: Int,
    val averageRating: Float,
    val sentimentTone: String, // e.g. "Overwhelmingly Positive (92% Praise)"
    val positiveThemes: List<String>,
    val constructiveFeedback: List<String>,
    val summaryText: String
)

