package com.shelfmates.data.model

enum class BookmarkRewardCategory(val displayName: String) {
    ALL("All Rewards"),
    MERCH("Physical Merch"),
    AUTHOR_PERKS("Author Perks"),
    PLATFORM("Platform Access"),
    COMMUNITY("Community")
}

enum class QuestType(val displayName: String) {
    DAILY("Daily Quest"),
    WEEKLY("Weekly Challenge"),
    MILESTONE("Milestone")
}

enum class TransactionType {
    EARNED,
    REDEEMED
}

enum class BookmarkTier(
    val tierName: String,
    val minPoints: Int,
    val badgeSymbol: String,
    val perkDescription: String,
    val multiplierText: String
) {
    SHELF_CURIOUS(
        "Shelf Curious",
        0,
        "📖",
        "Access to open ARCs · Club access · Shelfmates reader profile",
        "1.0x Base"
    ),
    PAGE_TURNER(
        "Page Turner",
        300,
        "📗",
        "Early ARC access (24h before general pool) · Priority reader matching · Silver profile badge",
        "1.0x Base"
    ),
    CHAPTER_MASTER(
        "Chapter Master",
        1000,
        "📙",
        "Featured reviewer placement · Author direct messaging · Gold badge · 10% earn bonus",
        "1.1x Bonus"
    ),
    SHELF_LEGEND(
        "Shelf Legend",
        3000,
        "📓",
        "Invite-only ARCs · Co-create book clubs with authors · Obsidian badge · 20% earn bonus · Annual merch drop",
        "1.2x Bonus"
    );

    companion object {
        fun fromPoints(points: Int): BookmarkTier {
            return when {
                points >= SHELF_LEGEND.minPoints -> SHELF_LEGEND
                points >= CHAPTER_MASTER.minPoints -> CHAPTER_MASTER
                points >= PAGE_TURNER.minPoints -> PAGE_TURNER
                else -> SHELF_CURIOUS
            }
        }

        fun nextTier(current: BookmarkTier): BookmarkTier? {
            return when (current) {
                SHELF_CURIOUS -> PAGE_TURNER
                PAGE_TURNER -> CHAPTER_MASTER
                CHAPTER_MASTER -> SHELF_LEGEND
                SHELF_LEGEND -> null
            }
        }
    }
}

data class BookmarkRewardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val costBookmarks: Int,
    val category: BookmarkRewardCategory,
    val iconEmoji: String,
    val isFeatured: Boolean = false,
    val isDigital: Boolean = true,
    val stockRemaining: Int? = null,
    val promoCode: String? = null,
    val tierRequirement: BookmarkTier = BookmarkTier.SHELF_CURIOUS
)


data class BookmarkQuest(
    val id: String,
    val title: String,
    val description: String,
    val rewardBookmarks: Int,
    val type: QuestType,
    val currentProgress: Int,
    val targetProgress: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val iconEmoji: String
)

data class BookmarkTransaction(
    val id: String,
    val title: String,
    val note: String,
    val bookmarksAmount: Int,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis(),
    val iconEmoji: String = if (type == TransactionType.EARNED) "🔖" else "🎁"
)

data class BookmarkRedemption(
    val id: String,
    val rewardId: String,
    val rewardTitle: String,
    val costBookmarks: Int,
    val redeemedTimestamp: Long,
    val redemptionCode: String,
    val iconEmoji: String,
    val status: String = "ACTIVE" // ACTIVE, CLAIMED, EXPIRED
)

data class BookmarkWallet(
    val balance: Int = 380,
    val lifetimeEarned: Int = 850,
    val tier: BookmarkTier = BookmarkTier.PAGE_TURNER,
    val activeStreakDays: Int = 14,
    val multiplier: Float = 1.0f,
    val redemptionsCount: Int = 2
)

