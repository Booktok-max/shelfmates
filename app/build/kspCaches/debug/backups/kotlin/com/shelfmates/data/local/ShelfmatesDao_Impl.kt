package com.shelfmates.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.shelfmates.`data`.model.ApplicationStatus
import com.shelfmates.`data`.model.ArcStatus
import com.shelfmates.`data`.model.BroadcastType
import com.shelfmates.`data`.model.NotificationType
import com.shelfmates.`data`.model.ReviewStatus
import com.shelfmates.`data`.model.UserRole
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Float
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ShelfmatesDao_Impl(
  __db: RoomDatabase,
) : ShelfmatesDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserEntity: EntityInsertAdapter<UserEntity>

  private val __insertAdapterOfPublicClubEntity: EntityInsertAdapter<PublicClubEntity>

  private val __insertAdapterOfArcClubEntity: EntityInsertAdapter<ArcClubEntity>

  private val __insertAdapterOfArcApplicationEntity: EntityInsertAdapter<ArcApplicationEntity>

  private val __insertAdapterOfArcReviewEntity: EntityInsertAdapter<ArcReviewEntity>

  private val __insertAdapterOfClubThreadEntity: EntityInsertAdapter<ClubThreadEntity>

  private val __insertAdapterOfThreadReplyEntity: EntityInsertAdapter<ThreadReplyEntity>

  private val __insertAdapterOfBroadcastEntity: EntityInsertAdapter<BroadcastEntity>

  private val __insertAdapterOfNotificationEntity: EntityInsertAdapter<NotificationEntity>

  private val __insertAdapterOfFollowEntity: EntityInsertAdapter<FollowEntity>

  private val __insertAdapterOfBookLogEntity: EntityInsertAdapter<BookLogEntity>

  private val __insertAdapterOfReadingProgressEntity: EntityInsertAdapter<ReadingProgressEntity>

  private val __insertAdapterOfBookmarkTransactionEntity:
      EntityInsertAdapter<BookmarkTransactionEntity>

  private val __insertAdapterOfBookmarkRedemptionEntity:
      EntityInsertAdapter<BookmarkRedemptionEntity>

  private val __insertAdapterOfSavedBookEntity: EntityInsertAdapter<SavedBookEntity>

  private val __insertAdapterOfCustomShelfEntity: EntityInsertAdapter<CustomShelfEntity>

  private val __insertAdapterOfAtomicShelfAnalyticsEntity:
      EntityInsertAdapter<AtomicShelfAnalyticsEntity>

  private val __insertAdapterOfRedemptionRequestEntity: EntityInsertAdapter<RedemptionRequestEntity>

  private val __deleteAdapterOfBookLogEntity: EntityDeleteOrUpdateAdapter<BookLogEntity>

  private val __updateAdapterOfUserEntity: EntityDeleteOrUpdateAdapter<UserEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserEntity = object : EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `users` (`id`,`role`,`displayName`,`email`,`phone`,`avatarUrl`,`bio`,`genresCsv`,`amazonAuthorUrl`,`asClientId`,`isAuthorPro`,`followersCount`,`followingCount`,`booksCount`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, __UserRole_enumToString(entity.role))
        statement.bindText(3, entity.displayName)
        statement.bindText(4, entity.email)
        statement.bindText(5, entity.phone)
        statement.bindText(6, entity.avatarUrl)
        statement.bindText(7, entity.bio)
        statement.bindText(8, entity.genresCsv)
        statement.bindText(9, entity.amazonAuthorUrl)
        statement.bindText(10, entity.asClientId)
        val _tmp: Int = if (entity.isAuthorPro) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.followersCount.toLong())
        statement.bindLong(13, entity.followingCount.toLong())
        statement.bindLong(14, entity.booksCount.toLong())
        statement.bindLong(15, entity.createdAt)
      }
    }
    this.__insertAdapterOfPublicClubEntity = object : EntityInsertAdapter<PublicClubEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `public_clubs` (`id`,`name`,`genre`,`description`,`coverUrl`,`currentBookId`,`currentBookTitle`,`currentBookAuthor`,`currentBookCover`,`currentBookDescription`,`currentBookAmazonUrl`,`currentBookGoodreadsUrl`,`adminUserId`,`adminName`,`memberCount`,`isJoined`,`isVoiceRoomActive`,`activeVoiceListeners`,`announcement`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PublicClubEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.genre)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.coverUrl)
        statement.bindText(6, entity.currentBookId)
        statement.bindText(7, entity.currentBookTitle)
        statement.bindText(8, entity.currentBookAuthor)
        statement.bindText(9, entity.currentBookCover)
        statement.bindText(10, entity.currentBookDescription)
        statement.bindText(11, entity.currentBookAmazonUrl)
        statement.bindText(12, entity.currentBookGoodreadsUrl)
        statement.bindText(13, entity.adminUserId)
        statement.bindText(14, entity.adminName)
        statement.bindLong(15, entity.memberCount.toLong())
        val _tmp: Int = if (entity.isJoined) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        val _tmp_1: Int = if (entity.isVoiceRoomActive) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
        statement.bindLong(18, entity.activeVoiceListeners.toLong())
        statement.bindText(19, entity.announcement)
        statement.bindLong(20, entity.createdAt)
      }
    }
    this.__insertAdapterOfArcClubEntity = object : EntityInsertAdapter<ArcClubEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `arc_clubs` (`id`,`bookId`,`bookTitle`,`authorUserId`,`authorName`,`coverUrl`,`genre`,`blurb`,`format`,`slotLimit`,`slotsFilled`,`deadlineDate`,`daysRemaining`,`fileUrl`,`fileSizeMb`,`asin`,`status`,`minimumReviewsRequired`,`isApplied`,`isApproved`,`hasDownloaded`,`isReviewSubmitted`,`bookfunnelLink`,`autoApprove`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ArcClubEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.bookId)
        statement.bindText(3, entity.bookTitle)
        statement.bindText(4, entity.authorUserId)
        statement.bindText(5, entity.authorName)
        statement.bindText(6, entity.coverUrl)
        statement.bindText(7, entity.genre)
        statement.bindText(8, entity.blurb)
        statement.bindText(9, entity.format)
        statement.bindLong(10, entity.slotLimit.toLong())
        statement.bindLong(11, entity.slotsFilled.toLong())
        statement.bindText(12, entity.deadlineDate)
        statement.bindLong(13, entity.daysRemaining.toLong())
        statement.bindText(14, entity.fileUrl)
        statement.bindDouble(15, entity.fileSizeMb)
        statement.bindText(16, entity.asin)
        statement.bindText(17, __ArcStatus_enumToString(entity.status))
        statement.bindLong(18, entity.minimumReviewsRequired.toLong())
        val _tmp: Int = if (entity.isApplied) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        val _tmp_1: Int = if (entity.isApproved) 1 else 0
        statement.bindLong(20, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.hasDownloaded) 1 else 0
        statement.bindLong(21, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.isReviewSubmitted) 1 else 0
        statement.bindLong(22, _tmp_3.toLong())
        statement.bindText(23, entity.bookfunnelLink)
        val _tmp_4: Int = if (entity.autoApprove) 1 else 0
        statement.bindLong(24, _tmp_4.toLong())
        statement.bindLong(25, entity.createdAt)
      }
    }
    this.__insertAdapterOfArcApplicationEntity = object :
        EntityInsertAdapter<ArcApplicationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `arc_applications` (`id`,`arcClubId`,`bookTitle`,`readerUserId`,`readerName`,`readerAvatarUrl`,`message`,`goodreadsUrl`,`pastReviewsCount`,`status`,`bookfunnelLink`,`genresCsv`,`externalPlatforms`,`isFlaggedForAdmin`,`appliedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ArcApplicationEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.arcClubId)
        statement.bindText(3, entity.bookTitle)
        statement.bindText(4, entity.readerUserId)
        statement.bindText(5, entity.readerName)
        statement.bindText(6, entity.readerAvatarUrl)
        statement.bindText(7, entity.message)
        statement.bindText(8, entity.goodreadsUrl)
        statement.bindLong(9, entity.pastReviewsCount.toLong())
        statement.bindText(10, __ApplicationStatus_enumToString(entity.status))
        statement.bindText(11, entity.bookfunnelLink)
        statement.bindText(12, entity.genresCsv)
        statement.bindText(13, entity.externalPlatforms)
        val _tmp: Int = if (entity.isFlaggedForAdmin) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        statement.bindText(15, entity.appliedAt)
      }
    }
    this.__insertAdapterOfArcReviewEntity = object : EntityInsertAdapter<ArcReviewEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `arc_reviews` (`id`,`arcClubId`,`bookTitle`,`authorUserId`,`readerUserId`,`readerName`,`readerAvatarUrl`,`rating`,`reviewText`,`wordCount`,`amazonPosted`,`goodreadsPosted`,`booktokPosted`,`platformPosted`,`status`,`submittedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ArcReviewEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.arcClubId)
        statement.bindText(3, entity.bookTitle)
        statement.bindText(4, entity.authorUserId)
        statement.bindText(5, entity.readerUserId)
        statement.bindText(6, entity.readerName)
        statement.bindText(7, entity.readerAvatarUrl)
        statement.bindLong(8, entity.rating.toLong())
        statement.bindText(9, entity.reviewText)
        statement.bindLong(10, entity.wordCount.toLong())
        val _tmp: Int = if (entity.amazonPosted) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        val _tmp_1: Int = if (entity.goodreadsPosted) 1 else 0
        statement.bindLong(12, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.booktokPosted) 1 else 0
        statement.bindLong(13, _tmp_2.toLong())
        statement.bindText(14, entity.platformPosted)
        statement.bindText(15, __ReviewStatus_enumToString(entity.status))
        statement.bindText(16, entity.submittedAt)
      }
    }
    this.__insertAdapterOfClubThreadEntity = object : EntityInsertAdapter<ClubThreadEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `club_threads` (`id`,`clubId`,`clubType`,`category`,`title`,`body`,`authorUserId`,`authorName`,`authorAvatarUrl`,`replyCount`,`likesCount`,`isPinned`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ClubThreadEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.clubId)
        statement.bindText(3, entity.clubType)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.title)
        statement.bindText(6, entity.body)
        statement.bindText(7, entity.authorUserId)
        statement.bindText(8, entity.authorName)
        statement.bindText(9, entity.authorAvatarUrl)
        statement.bindLong(10, entity.replyCount.toLong())
        statement.bindLong(11, entity.likesCount.toLong())
        val _tmp: Int = if (entity.isPinned) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        statement.bindText(13, entity.createdAt)
      }
    }
    this.__insertAdapterOfThreadReplyEntity = object : EntityInsertAdapter<ThreadReplyEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `thread_replies` (`id`,`threadId`,`userId`,`userName`,`userAvatarUrl`,`body`,`createdAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ThreadReplyEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.threadId)
        statement.bindText(3, entity.userId)
        statement.bindText(4, entity.userName)
        statement.bindText(5, entity.userAvatarUrl)
        statement.bindText(6, entity.body)
        statement.bindText(7, entity.createdAt)
      }
    }
    this.__insertAdapterOfBroadcastEntity = object : EntityInsertAdapter<BroadcastEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `broadcasts` (`id`,`authorUserId`,`authorName`,`authorAvatarUrl`,`title`,`message`,`type`,`actionUrl`,`sentAt`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BroadcastEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.authorUserId)
        statement.bindText(3, entity.authorName)
        statement.bindText(4, entity.authorAvatarUrl)
        statement.bindText(5, entity.title)
        statement.bindText(6, entity.message)
        statement.bindText(7, __BroadcastType_enumToString(entity.type))
        statement.bindText(8, entity.actionUrl)
        statement.bindText(9, entity.sentAt)
      }
    }
    this.__insertAdapterOfNotificationEntity = object : EntityInsertAdapter<NotificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `notifications` (`id`,`type`,`title`,`message`,`targetId`,`isRead`,`timeAgo`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NotificationEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, __NotificationType_enumToString(entity.type))
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.message)
        statement.bindText(5, entity.targetId)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.timeAgo)
        statement.bindLong(8, entity.createdAt)
      }
    }
    this.__insertAdapterOfFollowEntity = object : EntityInsertAdapter<FollowEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `follows` (`id`,`authorUserId`,`authorName`,`followerUserId`,`followerName`,`followerEmail`,`emailConsent`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FollowEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.authorUserId)
        statement.bindText(3, entity.authorName)
        statement.bindText(4, entity.followerUserId)
        statement.bindText(5, entity.followerName)
        statement.bindText(6, entity.followerEmail)
        val _tmp: Int = if (entity.emailConsent) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.createdAt)
      }
    }
    this.__insertAdapterOfBookLogEntity = object : EntityInsertAdapter<BookLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `book_logs` (`id`,`userId`,`title`,`author`,`coverUrl`,`rating`,`notes`,`dateCompleted`,`genre`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.author)
        statement.bindText(5, entity.coverUrl)
        statement.bindLong(6, entity.rating.toLong())
        statement.bindText(7, entity.notes)
        statement.bindText(8, entity.dateCompleted)
        statement.bindText(9, entity.genre)
      }
    }
    this.__insertAdapterOfReadingProgressEntity = object :
        EntityInsertAdapter<ReadingProgressEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `reading_progress` (`book_id`,`last_page`,`last_chapter_index`,`total_pages_or_chapters`,`progress_percent`,`format`,`last_read_timestamp`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReadingProgressEntity) {
        statement.bindText(1, entity.bookId)
        statement.bindLong(2, entity.lastPageIndex.toLong())
        statement.bindLong(3, entity.lastChapterIndex.toLong())
        statement.bindLong(4, entity.totalPagesOrChapters.toLong())
        statement.bindDouble(5, entity.progressPercent.toDouble())
        statement.bindText(6, entity.format)
        statement.bindLong(7, entity.lastReadTimestamp)
      }
    }
    this.__insertAdapterOfBookmarkTransactionEntity = object :
        EntityInsertAdapter<BookmarkTransactionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bookmark_transactions` (`id`,`userId`,`title`,`note`,`bookmarksAmount`,`type`,`timestamp`,`iconEmoji`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookmarkTransactionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.note)
        statement.bindLong(5, entity.bookmarksAmount.toLong())
        statement.bindText(6, entity.type)
        statement.bindLong(7, entity.timestamp)
        statement.bindText(8, entity.iconEmoji)
      }
    }
    this.__insertAdapterOfBookmarkRedemptionEntity = object :
        EntityInsertAdapter<BookmarkRedemptionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bookmark_redemptions` (`id`,`userId`,`rewardId`,`rewardTitle`,`rewardSubtitle`,`costBookmarks`,`redeemedTimestamp`,`redemptionCode`,`iconEmoji`,`status`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookmarkRedemptionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.rewardId)
        statement.bindText(4, entity.rewardTitle)
        statement.bindText(5, entity.rewardSubtitle)
        statement.bindLong(6, entity.costBookmarks.toLong())
        statement.bindLong(7, entity.redeemedTimestamp)
        statement.bindText(8, entity.redemptionCode)
        statement.bindText(9, entity.iconEmoji)
        statement.bindText(10, entity.status)
      }
    }
    this.__insertAdapterOfSavedBookEntity = object : EntityInsertAdapter<SavedBookEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `saved_books` (`id`,`userId`,`googleBooksId`,`title`,`authors`,`coverUrl`,`category`,`description`,`genre`,`pageCount`,`rating`,`isbn13`,`notes`,`personalRating`,`savedAt`,`cloudSyncedAt`,`syncState`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SavedBookEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.googleBooksId)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.authors)
        statement.bindText(6, entity.coverUrl)
        statement.bindText(7, entity.category)
        statement.bindText(8, entity.description)
        statement.bindText(9, entity.genre)
        statement.bindLong(10, entity.pageCount.toLong())
        statement.bindDouble(11, entity.rating.toDouble())
        statement.bindText(12, entity.isbn13)
        statement.bindText(13, entity.notes)
        statement.bindLong(14, entity.personalRating.toLong())
        statement.bindLong(15, entity.savedAt)
        statement.bindLong(16, entity.cloudSyncedAt)
        statement.bindText(17, entity.syncState)
      }
    }
    this.__insertAdapterOfCustomShelfEntity = object : EntityInsertAdapter<CustomShelfEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `custom_shelves` (`id`,`userId`,`name`,`iconEmoji`,`isDefault`,`createdAt`,`cloudSyncedAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CustomShelfEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.iconEmoji)
        val _tmp: Int = if (entity.isDefault) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.cloudSyncedAt)
      }
    }
    this.__insertAdapterOfAtomicShelfAnalyticsEntity = object :
        EntityInsertAdapter<AtomicShelfAnalyticsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `as_analytics` (`id`,`authorId`,`authorName`,`asClientId`,`period`,`newsletterPlacements`,`newsletterSubscribersReached`,`tiktokViews`,`ytViews`,`topVideoUrl`,`syncedAt`,`syncStatus`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AtomicShelfAnalyticsEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.authorId)
        statement.bindText(3, entity.authorName)
        statement.bindText(4, entity.asClientId)
        statement.bindText(5, entity.period)
        statement.bindLong(6, entity.newsletterPlacements.toLong())
        statement.bindLong(7, entity.newsletterSubscribersReached.toLong())
        statement.bindLong(8, entity.tiktokViews.toLong())
        statement.bindLong(9, entity.ytViews.toLong())
        statement.bindText(10, entity.topVideoUrl)
        statement.bindLong(11, entity.syncedAt)
        statement.bindText(12, entity.syncStatus)
      }
    }
    this.__insertAdapterOfRedemptionRequestEntity = object :
        EntityInsertAdapter<RedemptionRequestEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `redemption_requests` (`id`,`userId`,`userName`,`userEmail`,`catalogueItemId`,`catalogueItemName`,`costBookmarks`,`category`,`status`,`notes`,`shippingAddress`,`trackingCode`,`createdAt`,`fulfilledAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RedemptionRequestEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.userName)
        statement.bindText(4, entity.userEmail)
        statement.bindText(5, entity.catalogueItemId)
        statement.bindText(6, entity.catalogueItemName)
        statement.bindLong(7, entity.costBookmarks.toLong())
        statement.bindText(8, entity.category)
        statement.bindText(9, entity.status)
        statement.bindText(10, entity.notes)
        statement.bindText(11, entity.shippingAddress)
        statement.bindText(12, entity.trackingCode)
        statement.bindLong(13, entity.createdAt)
        val _tmpFulfilledAt: Long? = entity.fulfilledAt
        if (_tmpFulfilledAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpFulfilledAt)
        }
      }
    }
    this.__deleteAdapterOfBookLogEntity = object : EntityDeleteOrUpdateAdapter<BookLogEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `book_logs` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BookLogEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__updateAdapterOfUserEntity = object : EntityDeleteOrUpdateAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `users` SET `id` = ?,`role` = ?,`displayName` = ?,`email` = ?,`phone` = ?,`avatarUrl` = ?,`bio` = ?,`genresCsv` = ?,`amazonAuthorUrl` = ?,`asClientId` = ?,`isAuthorPro` = ?,`followersCount` = ?,`followingCount` = ?,`booksCount` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, __UserRole_enumToString(entity.role))
        statement.bindText(3, entity.displayName)
        statement.bindText(4, entity.email)
        statement.bindText(5, entity.phone)
        statement.bindText(6, entity.avatarUrl)
        statement.bindText(7, entity.bio)
        statement.bindText(8, entity.genresCsv)
        statement.bindText(9, entity.amazonAuthorUrl)
        statement.bindText(10, entity.asClientId)
        val _tmp: Int = if (entity.isAuthorPro) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.followersCount.toLong())
        statement.bindLong(13, entity.followingCount.toLong())
        statement.bindLong(14, entity.booksCount.toLong())
        statement.bindLong(15, entity.createdAt)
        statement.bindText(16, entity.id)
      }
    }
  }

  public override suspend fun insertUser(user: UserEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfUserEntity.insert(_connection, user)
  }

  public override suspend fun insertUsers(users: List<UserEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUserEntity.insert(_connection, users)
  }

  public override suspend fun insertPublicClub(club: PublicClubEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPublicClubEntity.insert(_connection, club)
  }

  public override suspend fun insertPublicClubs(clubs: List<PublicClubEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPublicClubEntity.insert(_connection, clubs)
  }

  public override suspend fun insertArcClub(arcClub: ArcClubEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfArcClubEntity.insert(_connection, arcClub)
  }

  public override suspend fun insertArcClubs(arcClubs: List<ArcClubEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfArcClubEntity.insert(_connection, arcClubs)
  }

  public override suspend fun insertArcApplication(application: ArcApplicationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfArcApplicationEntity.insert(_connection, application)
  }

  public override suspend fun insertArcApplications(applications: List<ArcApplicationEntity>): Unit
      = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfArcApplicationEntity.insert(_connection, applications)
  }

  public override suspend fun insertArcReview(review: ArcReviewEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfArcReviewEntity.insert(_connection, review)
  }

  public override suspend fun insertArcReviews(reviews: List<ArcReviewEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfArcReviewEntity.insert(_connection, reviews)
  }

  public override suspend fun insertThread(thread: ClubThreadEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfClubThreadEntity.insert(_connection, thread)
  }

  public override suspend fun insertThreads(threads: List<ClubThreadEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfClubThreadEntity.insert(_connection, threads)
  }

  public override suspend fun insertThreadReply(reply: ThreadReplyEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfThreadReplyEntity.insert(_connection, reply)
  }

  public override suspend fun insertThreadReplies(replies: List<ThreadReplyEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfThreadReplyEntity.insert(_connection, replies)
  }

  public override suspend fun insertBroadcast(broadcast: BroadcastEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBroadcastEntity.insert(_connection, broadcast)
  }

  public override suspend fun insertBroadcasts(broadcasts: List<BroadcastEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBroadcastEntity.insert(_connection, broadcasts)
  }

  public override suspend fun insertNotification(notification: NotificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, notification)
  }

  public override suspend fun insertNotifications(notifications: List<NotificationEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, notifications)
  }

  public override suspend fun insertFollow(follow: FollowEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFollowEntity.insert(_connection, follow)
  }

  public override suspend fun insertFollows(follows: List<FollowEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFollowEntity.insert(_connection, follows)
  }

  public override suspend fun insertBookLog(log: BookLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBookLogEntity.insert(_connection, log)
  }

  public override suspend fun insertBookLogs(logs: List<BookLogEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookLogEntity.insert(_connection, logs)
  }

  public override suspend fun saveReadingProgress(progress: ReadingProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfReadingProgressEntity.insert(_connection, progress)
  }

  public override suspend fun insertBookmarkTransaction(transaction: BookmarkTransactionEntity):
      Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookmarkTransactionEntity.insert(_connection, transaction)
  }

  public override suspend
      fun insertBookmarkTransactions(transactions: List<BookmarkTransactionEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookmarkTransactionEntity.insert(_connection, transactions)
  }

  public override suspend fun insertBookmarkRedemption(redemption: BookmarkRedemptionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookmarkRedemptionEntity.insert(_connection, redemption)
  }

  public override suspend
      fun insertBookmarkRedemptions(redemptions: List<BookmarkRedemptionEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookmarkRedemptionEntity.insert(_connection, redemptions)
  }

  public override suspend fun insertSavedBook(book: SavedBookEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfSavedBookEntity.insert(_connection, book)
  }

  public override suspend fun insertSavedBooks(books: List<SavedBookEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSavedBookEntity.insert(_connection, books)
  }

  public override suspend fun insertCustomShelf(shelf: CustomShelfEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCustomShelfEntity.insert(_connection, shelf)
  }

  public override suspend fun insertCustomShelves(shelves: List<CustomShelfEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCustomShelfEntity.insert(_connection, shelves)
  }

  public override suspend fun insertAtomicShelfAnalytics(analytics: AtomicShelfAnalyticsEntity):
      Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAtomicShelfAnalyticsEntity.insert(_connection, analytics)
  }

  public override suspend
      fun insertAllAtomicShelfAnalytics(analyticsList: List<AtomicShelfAnalyticsEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAtomicShelfAnalyticsEntity.insert(_connection, analyticsList)
  }

  public override suspend fun insertRedemptionRequest(request: RedemptionRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRedemptionRequestEntity.insert(_connection, request)
  }

  public override suspend fun insertRedemptionRequests(requests: List<RedemptionRequestEntity>):
      Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRedemptionRequestEntity.insert(_connection, requests)
  }

  public override suspend fun deleteBookLog(log: BookLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfBookLogEntity.handle(_connection, log)
  }

  public override suspend fun updateUser(user: UserEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfUserEntity.handle(_connection, user)
  }

  public override fun getUser(userId: String): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "avatarUrl")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfGenresCsv: Int = getColumnIndexOrThrow(_stmt, "genresCsv")
        val _columnIndexOfAmazonAuthorUrl: Int = getColumnIndexOrThrow(_stmt, "amazonAuthorUrl")
        val _columnIndexOfAsClientId: Int = getColumnIndexOrThrow(_stmt, "asClientId")
        val _columnIndexOfIsAuthorPro: Int = getColumnIndexOrThrow(_stmt, "isAuthorPro")
        val _columnIndexOfFollowersCount: Int = getColumnIndexOrThrow(_stmt, "followersCount")
        val _columnIndexOfFollowingCount: Int = getColumnIndexOrThrow(_stmt, "followingCount")
        val _columnIndexOfBooksCount: Int = getColumnIndexOrThrow(_stmt, "booksCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRole: UserRole
          _tmpRole = __UserRole_stringToEnum(_stmt.getText(_columnIndexOfRole))
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpAvatarUrl: String
          _tmpAvatarUrl = _stmt.getText(_columnIndexOfAvatarUrl)
          val _tmpBio: String
          _tmpBio = _stmt.getText(_columnIndexOfBio)
          val _tmpGenresCsv: String
          _tmpGenresCsv = _stmt.getText(_columnIndexOfGenresCsv)
          val _tmpAmazonAuthorUrl: String
          _tmpAmazonAuthorUrl = _stmt.getText(_columnIndexOfAmazonAuthorUrl)
          val _tmpAsClientId: String
          _tmpAsClientId = _stmt.getText(_columnIndexOfAsClientId)
          val _tmpIsAuthorPro: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAuthorPro).toInt()
          _tmpIsAuthorPro = _tmp != 0
          val _tmpFollowersCount: Int
          _tmpFollowersCount = _stmt.getLong(_columnIndexOfFollowersCount).toInt()
          val _tmpFollowingCount: Int
          _tmpFollowingCount = _stmt.getLong(_columnIndexOfFollowingCount).toInt()
          val _tmpBooksCount: Int
          _tmpBooksCount = _stmt.getLong(_columnIndexOfBooksCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              UserEntity(_tmpId,_tmpRole,_tmpDisplayName,_tmpEmail,_tmpPhone,_tmpAvatarUrl,_tmpBio,_tmpGenresCsv,_tmpAmazonAuthorUrl,_tmpAsClientId,_tmpIsAuthorPro,_tmpFollowersCount,_tmpFollowingCount,_tmpBooksCount,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllAuthors(): Flow<List<UserEntity>> {
    val _sql: String = "SELECT * FROM users WHERE role = 'AUTHOR' OR role = 'BOTH'"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "avatarUrl")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfGenresCsv: Int = getColumnIndexOrThrow(_stmt, "genresCsv")
        val _columnIndexOfAmazonAuthorUrl: Int = getColumnIndexOrThrow(_stmt, "amazonAuthorUrl")
        val _columnIndexOfAsClientId: Int = getColumnIndexOrThrow(_stmt, "asClientId")
        val _columnIndexOfIsAuthorPro: Int = getColumnIndexOrThrow(_stmt, "isAuthorPro")
        val _columnIndexOfFollowersCount: Int = getColumnIndexOrThrow(_stmt, "followersCount")
        val _columnIndexOfFollowingCount: Int = getColumnIndexOrThrow(_stmt, "followingCount")
        val _columnIndexOfBooksCount: Int = getColumnIndexOrThrow(_stmt, "booksCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRole: UserRole
          _tmpRole = __UserRole_stringToEnum(_stmt.getText(_columnIndexOfRole))
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpAvatarUrl: String
          _tmpAvatarUrl = _stmt.getText(_columnIndexOfAvatarUrl)
          val _tmpBio: String
          _tmpBio = _stmt.getText(_columnIndexOfBio)
          val _tmpGenresCsv: String
          _tmpGenresCsv = _stmt.getText(_columnIndexOfGenresCsv)
          val _tmpAmazonAuthorUrl: String
          _tmpAmazonAuthorUrl = _stmt.getText(_columnIndexOfAmazonAuthorUrl)
          val _tmpAsClientId: String
          _tmpAsClientId = _stmt.getText(_columnIndexOfAsClientId)
          val _tmpIsAuthorPro: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAuthorPro).toInt()
          _tmpIsAuthorPro = _tmp != 0
          val _tmpFollowersCount: Int
          _tmpFollowersCount = _stmt.getLong(_columnIndexOfFollowersCount).toInt()
          val _tmpFollowingCount: Int
          _tmpFollowingCount = _stmt.getLong(_columnIndexOfFollowingCount).toInt()
          val _tmpBooksCount: Int
          _tmpBooksCount = _stmt.getLong(_columnIndexOfBooksCount).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              UserEntity(_tmpId,_tmpRole,_tmpDisplayName,_tmpEmail,_tmpPhone,_tmpAvatarUrl,_tmpBio,_tmpGenresCsv,_tmpAmazonAuthorUrl,_tmpAsClientId,_tmpIsAuthorPro,_tmpFollowersCount,_tmpFollowingCount,_tmpBooksCount,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllPublicClubs(): Flow<List<PublicClubEntity>> {
    val _sql: String = "SELECT * FROM public_clubs ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("public_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCurrentBookId: Int = getColumnIndexOrThrow(_stmt, "currentBookId")
        val _columnIndexOfCurrentBookTitle: Int = getColumnIndexOrThrow(_stmt, "currentBookTitle")
        val _columnIndexOfCurrentBookAuthor: Int = getColumnIndexOrThrow(_stmt, "currentBookAuthor")
        val _columnIndexOfCurrentBookCover: Int = getColumnIndexOrThrow(_stmt, "currentBookCover")
        val _columnIndexOfCurrentBookDescription: Int = getColumnIndexOrThrow(_stmt,
            "currentBookDescription")
        val _columnIndexOfCurrentBookAmazonUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookAmazonUrl")
        val _columnIndexOfCurrentBookGoodreadsUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookGoodreadsUrl")
        val _columnIndexOfAdminUserId: Int = getColumnIndexOrThrow(_stmt, "adminUserId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfMemberCount: Int = getColumnIndexOrThrow(_stmt, "memberCount")
        val _columnIndexOfIsJoined: Int = getColumnIndexOrThrow(_stmt, "isJoined")
        val _columnIndexOfIsVoiceRoomActive: Int = getColumnIndexOrThrow(_stmt, "isVoiceRoomActive")
        val _columnIndexOfActiveVoiceListeners: Int = getColumnIndexOrThrow(_stmt,
            "activeVoiceListeners")
        val _columnIndexOfAnnouncement: Int = getColumnIndexOrThrow(_stmt, "announcement")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<PublicClubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PublicClubEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCurrentBookId: String
          _tmpCurrentBookId = _stmt.getText(_columnIndexOfCurrentBookId)
          val _tmpCurrentBookTitle: String
          _tmpCurrentBookTitle = _stmt.getText(_columnIndexOfCurrentBookTitle)
          val _tmpCurrentBookAuthor: String
          _tmpCurrentBookAuthor = _stmt.getText(_columnIndexOfCurrentBookAuthor)
          val _tmpCurrentBookCover: String
          _tmpCurrentBookCover = _stmt.getText(_columnIndexOfCurrentBookCover)
          val _tmpCurrentBookDescription: String
          _tmpCurrentBookDescription = _stmt.getText(_columnIndexOfCurrentBookDescription)
          val _tmpCurrentBookAmazonUrl: String
          _tmpCurrentBookAmazonUrl = _stmt.getText(_columnIndexOfCurrentBookAmazonUrl)
          val _tmpCurrentBookGoodreadsUrl: String
          _tmpCurrentBookGoodreadsUrl = _stmt.getText(_columnIndexOfCurrentBookGoodreadsUrl)
          val _tmpAdminUserId: String
          _tmpAdminUserId = _stmt.getText(_columnIndexOfAdminUserId)
          val _tmpAdminName: String
          _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          val _tmpMemberCount: Int
          _tmpMemberCount = _stmt.getLong(_columnIndexOfMemberCount).toInt()
          val _tmpIsJoined: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsJoined).toInt()
          _tmpIsJoined = _tmp != 0
          val _tmpIsVoiceRoomActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsVoiceRoomActive).toInt()
          _tmpIsVoiceRoomActive = _tmp_1 != 0
          val _tmpActiveVoiceListeners: Int
          _tmpActiveVoiceListeners = _stmt.getLong(_columnIndexOfActiveVoiceListeners).toInt()
          val _tmpAnnouncement: String
          _tmpAnnouncement = _stmt.getText(_columnIndexOfAnnouncement)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PublicClubEntity(_tmpId,_tmpName,_tmpGenre,_tmpDescription,_tmpCoverUrl,_tmpCurrentBookId,_tmpCurrentBookTitle,_tmpCurrentBookAuthor,_tmpCurrentBookCover,_tmpCurrentBookDescription,_tmpCurrentBookAmazonUrl,_tmpCurrentBookGoodreadsUrl,_tmpAdminUserId,_tmpAdminName,_tmpMemberCount,_tmpIsJoined,_tmpIsVoiceRoomActive,_tmpActiveVoiceListeners,_tmpAnnouncement,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getJoinedPublicClubs(): Flow<List<PublicClubEntity>> {
    val _sql: String = "SELECT * FROM public_clubs WHERE isJoined = 1"
    return createFlow(__db, false, arrayOf("public_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCurrentBookId: Int = getColumnIndexOrThrow(_stmt, "currentBookId")
        val _columnIndexOfCurrentBookTitle: Int = getColumnIndexOrThrow(_stmt, "currentBookTitle")
        val _columnIndexOfCurrentBookAuthor: Int = getColumnIndexOrThrow(_stmt, "currentBookAuthor")
        val _columnIndexOfCurrentBookCover: Int = getColumnIndexOrThrow(_stmt, "currentBookCover")
        val _columnIndexOfCurrentBookDescription: Int = getColumnIndexOrThrow(_stmt,
            "currentBookDescription")
        val _columnIndexOfCurrentBookAmazonUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookAmazonUrl")
        val _columnIndexOfCurrentBookGoodreadsUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookGoodreadsUrl")
        val _columnIndexOfAdminUserId: Int = getColumnIndexOrThrow(_stmt, "adminUserId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfMemberCount: Int = getColumnIndexOrThrow(_stmt, "memberCount")
        val _columnIndexOfIsJoined: Int = getColumnIndexOrThrow(_stmt, "isJoined")
        val _columnIndexOfIsVoiceRoomActive: Int = getColumnIndexOrThrow(_stmt, "isVoiceRoomActive")
        val _columnIndexOfActiveVoiceListeners: Int = getColumnIndexOrThrow(_stmt,
            "activeVoiceListeners")
        val _columnIndexOfAnnouncement: Int = getColumnIndexOrThrow(_stmt, "announcement")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<PublicClubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PublicClubEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCurrentBookId: String
          _tmpCurrentBookId = _stmt.getText(_columnIndexOfCurrentBookId)
          val _tmpCurrentBookTitle: String
          _tmpCurrentBookTitle = _stmt.getText(_columnIndexOfCurrentBookTitle)
          val _tmpCurrentBookAuthor: String
          _tmpCurrentBookAuthor = _stmt.getText(_columnIndexOfCurrentBookAuthor)
          val _tmpCurrentBookCover: String
          _tmpCurrentBookCover = _stmt.getText(_columnIndexOfCurrentBookCover)
          val _tmpCurrentBookDescription: String
          _tmpCurrentBookDescription = _stmt.getText(_columnIndexOfCurrentBookDescription)
          val _tmpCurrentBookAmazonUrl: String
          _tmpCurrentBookAmazonUrl = _stmt.getText(_columnIndexOfCurrentBookAmazonUrl)
          val _tmpCurrentBookGoodreadsUrl: String
          _tmpCurrentBookGoodreadsUrl = _stmt.getText(_columnIndexOfCurrentBookGoodreadsUrl)
          val _tmpAdminUserId: String
          _tmpAdminUserId = _stmt.getText(_columnIndexOfAdminUserId)
          val _tmpAdminName: String
          _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          val _tmpMemberCount: Int
          _tmpMemberCount = _stmt.getLong(_columnIndexOfMemberCount).toInt()
          val _tmpIsJoined: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsJoined).toInt()
          _tmpIsJoined = _tmp != 0
          val _tmpIsVoiceRoomActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsVoiceRoomActive).toInt()
          _tmpIsVoiceRoomActive = _tmp_1 != 0
          val _tmpActiveVoiceListeners: Int
          _tmpActiveVoiceListeners = _stmt.getLong(_columnIndexOfActiveVoiceListeners).toInt()
          val _tmpAnnouncement: String
          _tmpAnnouncement = _stmt.getText(_columnIndexOfAnnouncement)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              PublicClubEntity(_tmpId,_tmpName,_tmpGenre,_tmpDescription,_tmpCoverUrl,_tmpCurrentBookId,_tmpCurrentBookTitle,_tmpCurrentBookAuthor,_tmpCurrentBookCover,_tmpCurrentBookDescription,_tmpCurrentBookAmazonUrl,_tmpCurrentBookGoodreadsUrl,_tmpAdminUserId,_tmpAdminName,_tmpMemberCount,_tmpIsJoined,_tmpIsVoiceRoomActive,_tmpActiveVoiceListeners,_tmpAnnouncement,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPublicClubById(clubId: String): Flow<PublicClubEntity?> {
    val _sql: String = "SELECT * FROM public_clubs WHERE id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("public_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clubId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCurrentBookId: Int = getColumnIndexOrThrow(_stmt, "currentBookId")
        val _columnIndexOfCurrentBookTitle: Int = getColumnIndexOrThrow(_stmt, "currentBookTitle")
        val _columnIndexOfCurrentBookAuthor: Int = getColumnIndexOrThrow(_stmt, "currentBookAuthor")
        val _columnIndexOfCurrentBookCover: Int = getColumnIndexOrThrow(_stmt, "currentBookCover")
        val _columnIndexOfCurrentBookDescription: Int = getColumnIndexOrThrow(_stmt,
            "currentBookDescription")
        val _columnIndexOfCurrentBookAmazonUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookAmazonUrl")
        val _columnIndexOfCurrentBookGoodreadsUrl: Int = getColumnIndexOrThrow(_stmt,
            "currentBookGoodreadsUrl")
        val _columnIndexOfAdminUserId: Int = getColumnIndexOrThrow(_stmt, "adminUserId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfMemberCount: Int = getColumnIndexOrThrow(_stmt, "memberCount")
        val _columnIndexOfIsJoined: Int = getColumnIndexOrThrow(_stmt, "isJoined")
        val _columnIndexOfIsVoiceRoomActive: Int = getColumnIndexOrThrow(_stmt, "isVoiceRoomActive")
        val _columnIndexOfActiveVoiceListeners: Int = getColumnIndexOrThrow(_stmt,
            "activeVoiceListeners")
        val _columnIndexOfAnnouncement: Int = getColumnIndexOrThrow(_stmt, "announcement")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: PublicClubEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCurrentBookId: String
          _tmpCurrentBookId = _stmt.getText(_columnIndexOfCurrentBookId)
          val _tmpCurrentBookTitle: String
          _tmpCurrentBookTitle = _stmt.getText(_columnIndexOfCurrentBookTitle)
          val _tmpCurrentBookAuthor: String
          _tmpCurrentBookAuthor = _stmt.getText(_columnIndexOfCurrentBookAuthor)
          val _tmpCurrentBookCover: String
          _tmpCurrentBookCover = _stmt.getText(_columnIndexOfCurrentBookCover)
          val _tmpCurrentBookDescription: String
          _tmpCurrentBookDescription = _stmt.getText(_columnIndexOfCurrentBookDescription)
          val _tmpCurrentBookAmazonUrl: String
          _tmpCurrentBookAmazonUrl = _stmt.getText(_columnIndexOfCurrentBookAmazonUrl)
          val _tmpCurrentBookGoodreadsUrl: String
          _tmpCurrentBookGoodreadsUrl = _stmt.getText(_columnIndexOfCurrentBookGoodreadsUrl)
          val _tmpAdminUserId: String
          _tmpAdminUserId = _stmt.getText(_columnIndexOfAdminUserId)
          val _tmpAdminName: String
          _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          val _tmpMemberCount: Int
          _tmpMemberCount = _stmt.getLong(_columnIndexOfMemberCount).toInt()
          val _tmpIsJoined: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsJoined).toInt()
          _tmpIsJoined = _tmp != 0
          val _tmpIsVoiceRoomActive: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsVoiceRoomActive).toInt()
          _tmpIsVoiceRoomActive = _tmp_1 != 0
          val _tmpActiveVoiceListeners: Int
          _tmpActiveVoiceListeners = _stmt.getLong(_columnIndexOfActiveVoiceListeners).toInt()
          val _tmpAnnouncement: String
          _tmpAnnouncement = _stmt.getText(_columnIndexOfAnnouncement)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              PublicClubEntity(_tmpId,_tmpName,_tmpGenre,_tmpDescription,_tmpCoverUrl,_tmpCurrentBookId,_tmpCurrentBookTitle,_tmpCurrentBookAuthor,_tmpCurrentBookCover,_tmpCurrentBookDescription,_tmpCurrentBookAmazonUrl,_tmpCurrentBookGoodreadsUrl,_tmpAdminUserId,_tmpAdminName,_tmpMemberCount,_tmpIsJoined,_tmpIsVoiceRoomActive,_tmpActiveVoiceListeners,_tmpAnnouncement,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllArcClubs(): Flow<List<ArcClubEntity>> {
    val _sql: String = "SELECT * FROM arc_clubs ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("arc_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfBlurb: Int = getColumnIndexOrThrow(_stmt, "blurb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfSlotLimit: Int = getColumnIndexOrThrow(_stmt, "slotLimit")
        val _columnIndexOfSlotsFilled: Int = getColumnIndexOrThrow(_stmt, "slotsFilled")
        val _columnIndexOfDeadlineDate: Int = getColumnIndexOrThrow(_stmt, "deadlineDate")
        val _columnIndexOfDaysRemaining: Int = getColumnIndexOrThrow(_stmt, "daysRemaining")
        val _columnIndexOfFileUrl: Int = getColumnIndexOrThrow(_stmt, "fileUrl")
        val _columnIndexOfFileSizeMb: Int = getColumnIndexOrThrow(_stmt, "fileSizeMb")
        val _columnIndexOfAsin: Int = getColumnIndexOrThrow(_stmt, "asin")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMinimumReviewsRequired: Int = getColumnIndexOrThrow(_stmt,
            "minimumReviewsRequired")
        val _columnIndexOfIsApplied: Int = getColumnIndexOrThrow(_stmt, "isApplied")
        val _columnIndexOfIsApproved: Int = getColumnIndexOrThrow(_stmt, "isApproved")
        val _columnIndexOfHasDownloaded: Int = getColumnIndexOrThrow(_stmt, "hasDownloaded")
        val _columnIndexOfIsReviewSubmitted: Int = getColumnIndexOrThrow(_stmt, "isReviewSubmitted")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfAutoApprove: Int = getColumnIndexOrThrow(_stmt, "autoApprove")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ArcClubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcClubEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpBlurb: String
          _tmpBlurb = _stmt.getText(_columnIndexOfBlurb)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpSlotLimit: Int
          _tmpSlotLimit = _stmt.getLong(_columnIndexOfSlotLimit).toInt()
          val _tmpSlotsFilled: Int
          _tmpSlotsFilled = _stmt.getLong(_columnIndexOfSlotsFilled).toInt()
          val _tmpDeadlineDate: String
          _tmpDeadlineDate = _stmt.getText(_columnIndexOfDeadlineDate)
          val _tmpDaysRemaining: Int
          _tmpDaysRemaining = _stmt.getLong(_columnIndexOfDaysRemaining).toInt()
          val _tmpFileUrl: String
          _tmpFileUrl = _stmt.getText(_columnIndexOfFileUrl)
          val _tmpFileSizeMb: Double
          _tmpFileSizeMb = _stmt.getDouble(_columnIndexOfFileSizeMb)
          val _tmpAsin: String
          _tmpAsin = _stmt.getText(_columnIndexOfAsin)
          val _tmpStatus: ArcStatus
          _tmpStatus = __ArcStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMinimumReviewsRequired: Int
          _tmpMinimumReviewsRequired = _stmt.getLong(_columnIndexOfMinimumReviewsRequired).toInt()
          val _tmpIsApplied: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsApplied).toInt()
          _tmpIsApplied = _tmp != 0
          val _tmpIsApproved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsApproved).toInt()
          _tmpIsApproved = _tmp_1 != 0
          val _tmpHasDownloaded: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfHasDownloaded).toInt()
          _tmpHasDownloaded = _tmp_2 != 0
          val _tmpIsReviewSubmitted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsReviewSubmitted).toInt()
          _tmpIsReviewSubmitted = _tmp_3 != 0
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpAutoApprove: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfAutoApprove).toInt()
          _tmpAutoApprove = _tmp_4 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ArcClubEntity(_tmpId,_tmpBookId,_tmpBookTitle,_tmpAuthorUserId,_tmpAuthorName,_tmpCoverUrl,_tmpGenre,_tmpBlurb,_tmpFormat,_tmpSlotLimit,_tmpSlotsFilled,_tmpDeadlineDate,_tmpDaysRemaining,_tmpFileUrl,_tmpFileSizeMb,_tmpAsin,_tmpStatus,_tmpMinimumReviewsRequired,_tmpIsApplied,_tmpIsApproved,_tmpHasDownloaded,_tmpIsReviewSubmitted,_tmpBookfunnelLink,_tmpAutoApprove,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getArcClubsByAuthor(authorId: String): Flow<List<ArcClubEntity>> {
    val _sql: String = "SELECT * FROM arc_clubs WHERE authorUserId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("arc_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfBlurb: Int = getColumnIndexOrThrow(_stmt, "blurb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfSlotLimit: Int = getColumnIndexOrThrow(_stmt, "slotLimit")
        val _columnIndexOfSlotsFilled: Int = getColumnIndexOrThrow(_stmt, "slotsFilled")
        val _columnIndexOfDeadlineDate: Int = getColumnIndexOrThrow(_stmt, "deadlineDate")
        val _columnIndexOfDaysRemaining: Int = getColumnIndexOrThrow(_stmt, "daysRemaining")
        val _columnIndexOfFileUrl: Int = getColumnIndexOrThrow(_stmt, "fileUrl")
        val _columnIndexOfFileSizeMb: Int = getColumnIndexOrThrow(_stmt, "fileSizeMb")
        val _columnIndexOfAsin: Int = getColumnIndexOrThrow(_stmt, "asin")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMinimumReviewsRequired: Int = getColumnIndexOrThrow(_stmt,
            "minimumReviewsRequired")
        val _columnIndexOfIsApplied: Int = getColumnIndexOrThrow(_stmt, "isApplied")
        val _columnIndexOfIsApproved: Int = getColumnIndexOrThrow(_stmt, "isApproved")
        val _columnIndexOfHasDownloaded: Int = getColumnIndexOrThrow(_stmt, "hasDownloaded")
        val _columnIndexOfIsReviewSubmitted: Int = getColumnIndexOrThrow(_stmt, "isReviewSubmitted")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfAutoApprove: Int = getColumnIndexOrThrow(_stmt, "autoApprove")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ArcClubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcClubEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpBlurb: String
          _tmpBlurb = _stmt.getText(_columnIndexOfBlurb)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpSlotLimit: Int
          _tmpSlotLimit = _stmt.getLong(_columnIndexOfSlotLimit).toInt()
          val _tmpSlotsFilled: Int
          _tmpSlotsFilled = _stmt.getLong(_columnIndexOfSlotsFilled).toInt()
          val _tmpDeadlineDate: String
          _tmpDeadlineDate = _stmt.getText(_columnIndexOfDeadlineDate)
          val _tmpDaysRemaining: Int
          _tmpDaysRemaining = _stmt.getLong(_columnIndexOfDaysRemaining).toInt()
          val _tmpFileUrl: String
          _tmpFileUrl = _stmt.getText(_columnIndexOfFileUrl)
          val _tmpFileSizeMb: Double
          _tmpFileSizeMb = _stmt.getDouble(_columnIndexOfFileSizeMb)
          val _tmpAsin: String
          _tmpAsin = _stmt.getText(_columnIndexOfAsin)
          val _tmpStatus: ArcStatus
          _tmpStatus = __ArcStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMinimumReviewsRequired: Int
          _tmpMinimumReviewsRequired = _stmt.getLong(_columnIndexOfMinimumReviewsRequired).toInt()
          val _tmpIsApplied: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsApplied).toInt()
          _tmpIsApplied = _tmp != 0
          val _tmpIsApproved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsApproved).toInt()
          _tmpIsApproved = _tmp_1 != 0
          val _tmpHasDownloaded: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfHasDownloaded).toInt()
          _tmpHasDownloaded = _tmp_2 != 0
          val _tmpIsReviewSubmitted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsReviewSubmitted).toInt()
          _tmpIsReviewSubmitted = _tmp_3 != 0
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpAutoApprove: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfAutoApprove).toInt()
          _tmpAutoApprove = _tmp_4 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ArcClubEntity(_tmpId,_tmpBookId,_tmpBookTitle,_tmpAuthorUserId,_tmpAuthorName,_tmpCoverUrl,_tmpGenre,_tmpBlurb,_tmpFormat,_tmpSlotLimit,_tmpSlotsFilled,_tmpDeadlineDate,_tmpDaysRemaining,_tmpFileUrl,_tmpFileSizeMb,_tmpAsin,_tmpStatus,_tmpMinimumReviewsRequired,_tmpIsApplied,_tmpIsApproved,_tmpHasDownloaded,_tmpIsReviewSubmitted,_tmpBookfunnelLink,_tmpAutoApprove,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMyArcReads(): Flow<List<ArcClubEntity>> {
    val _sql: String = "SELECT * FROM arc_clubs WHERE isApplied = 1 OR isApproved = 1"
    return createFlow(__db, false, arrayOf("arc_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfBlurb: Int = getColumnIndexOrThrow(_stmt, "blurb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfSlotLimit: Int = getColumnIndexOrThrow(_stmt, "slotLimit")
        val _columnIndexOfSlotsFilled: Int = getColumnIndexOrThrow(_stmt, "slotsFilled")
        val _columnIndexOfDeadlineDate: Int = getColumnIndexOrThrow(_stmt, "deadlineDate")
        val _columnIndexOfDaysRemaining: Int = getColumnIndexOrThrow(_stmt, "daysRemaining")
        val _columnIndexOfFileUrl: Int = getColumnIndexOrThrow(_stmt, "fileUrl")
        val _columnIndexOfFileSizeMb: Int = getColumnIndexOrThrow(_stmt, "fileSizeMb")
        val _columnIndexOfAsin: Int = getColumnIndexOrThrow(_stmt, "asin")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMinimumReviewsRequired: Int = getColumnIndexOrThrow(_stmt,
            "minimumReviewsRequired")
        val _columnIndexOfIsApplied: Int = getColumnIndexOrThrow(_stmt, "isApplied")
        val _columnIndexOfIsApproved: Int = getColumnIndexOrThrow(_stmt, "isApproved")
        val _columnIndexOfHasDownloaded: Int = getColumnIndexOrThrow(_stmt, "hasDownloaded")
        val _columnIndexOfIsReviewSubmitted: Int = getColumnIndexOrThrow(_stmt, "isReviewSubmitted")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfAutoApprove: Int = getColumnIndexOrThrow(_stmt, "autoApprove")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ArcClubEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcClubEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpBlurb: String
          _tmpBlurb = _stmt.getText(_columnIndexOfBlurb)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpSlotLimit: Int
          _tmpSlotLimit = _stmt.getLong(_columnIndexOfSlotLimit).toInt()
          val _tmpSlotsFilled: Int
          _tmpSlotsFilled = _stmt.getLong(_columnIndexOfSlotsFilled).toInt()
          val _tmpDeadlineDate: String
          _tmpDeadlineDate = _stmt.getText(_columnIndexOfDeadlineDate)
          val _tmpDaysRemaining: Int
          _tmpDaysRemaining = _stmt.getLong(_columnIndexOfDaysRemaining).toInt()
          val _tmpFileUrl: String
          _tmpFileUrl = _stmt.getText(_columnIndexOfFileUrl)
          val _tmpFileSizeMb: Double
          _tmpFileSizeMb = _stmt.getDouble(_columnIndexOfFileSizeMb)
          val _tmpAsin: String
          _tmpAsin = _stmt.getText(_columnIndexOfAsin)
          val _tmpStatus: ArcStatus
          _tmpStatus = __ArcStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMinimumReviewsRequired: Int
          _tmpMinimumReviewsRequired = _stmt.getLong(_columnIndexOfMinimumReviewsRequired).toInt()
          val _tmpIsApplied: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsApplied).toInt()
          _tmpIsApplied = _tmp != 0
          val _tmpIsApproved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsApproved).toInt()
          _tmpIsApproved = _tmp_1 != 0
          val _tmpHasDownloaded: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfHasDownloaded).toInt()
          _tmpHasDownloaded = _tmp_2 != 0
          val _tmpIsReviewSubmitted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsReviewSubmitted).toInt()
          _tmpIsReviewSubmitted = _tmp_3 != 0
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpAutoApprove: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfAutoApprove).toInt()
          _tmpAutoApprove = _tmp_4 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              ArcClubEntity(_tmpId,_tmpBookId,_tmpBookTitle,_tmpAuthorUserId,_tmpAuthorName,_tmpCoverUrl,_tmpGenre,_tmpBlurb,_tmpFormat,_tmpSlotLimit,_tmpSlotsFilled,_tmpDeadlineDate,_tmpDaysRemaining,_tmpFileUrl,_tmpFileSizeMb,_tmpAsin,_tmpStatus,_tmpMinimumReviewsRequired,_tmpIsApplied,_tmpIsApproved,_tmpHasDownloaded,_tmpIsReviewSubmitted,_tmpBookfunnelLink,_tmpAutoApprove,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getArcClubById(arcId: String): Flow<ArcClubEntity?> {
    val _sql: String = "SELECT * FROM arc_clubs WHERE id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("arc_clubs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfBlurb: Int = getColumnIndexOrThrow(_stmt, "blurb")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfSlotLimit: Int = getColumnIndexOrThrow(_stmt, "slotLimit")
        val _columnIndexOfSlotsFilled: Int = getColumnIndexOrThrow(_stmt, "slotsFilled")
        val _columnIndexOfDeadlineDate: Int = getColumnIndexOrThrow(_stmt, "deadlineDate")
        val _columnIndexOfDaysRemaining: Int = getColumnIndexOrThrow(_stmt, "daysRemaining")
        val _columnIndexOfFileUrl: Int = getColumnIndexOrThrow(_stmt, "fileUrl")
        val _columnIndexOfFileSizeMb: Int = getColumnIndexOrThrow(_stmt, "fileSizeMb")
        val _columnIndexOfAsin: Int = getColumnIndexOrThrow(_stmt, "asin")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfMinimumReviewsRequired: Int = getColumnIndexOrThrow(_stmt,
            "minimumReviewsRequired")
        val _columnIndexOfIsApplied: Int = getColumnIndexOrThrow(_stmt, "isApplied")
        val _columnIndexOfIsApproved: Int = getColumnIndexOrThrow(_stmt, "isApproved")
        val _columnIndexOfHasDownloaded: Int = getColumnIndexOrThrow(_stmt, "hasDownloaded")
        val _columnIndexOfIsReviewSubmitted: Int = getColumnIndexOrThrow(_stmt, "isReviewSubmitted")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfAutoApprove: Int = getColumnIndexOrThrow(_stmt, "autoApprove")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: ArcClubEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpBlurb: String
          _tmpBlurb = _stmt.getText(_columnIndexOfBlurb)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpSlotLimit: Int
          _tmpSlotLimit = _stmt.getLong(_columnIndexOfSlotLimit).toInt()
          val _tmpSlotsFilled: Int
          _tmpSlotsFilled = _stmt.getLong(_columnIndexOfSlotsFilled).toInt()
          val _tmpDeadlineDate: String
          _tmpDeadlineDate = _stmt.getText(_columnIndexOfDeadlineDate)
          val _tmpDaysRemaining: Int
          _tmpDaysRemaining = _stmt.getLong(_columnIndexOfDaysRemaining).toInt()
          val _tmpFileUrl: String
          _tmpFileUrl = _stmt.getText(_columnIndexOfFileUrl)
          val _tmpFileSizeMb: Double
          _tmpFileSizeMb = _stmt.getDouble(_columnIndexOfFileSizeMb)
          val _tmpAsin: String
          _tmpAsin = _stmt.getText(_columnIndexOfAsin)
          val _tmpStatus: ArcStatus
          _tmpStatus = __ArcStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpMinimumReviewsRequired: Int
          _tmpMinimumReviewsRequired = _stmt.getLong(_columnIndexOfMinimumReviewsRequired).toInt()
          val _tmpIsApplied: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsApplied).toInt()
          _tmpIsApplied = _tmp != 0
          val _tmpIsApproved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsApproved).toInt()
          _tmpIsApproved = _tmp_1 != 0
          val _tmpHasDownloaded: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfHasDownloaded).toInt()
          _tmpHasDownloaded = _tmp_2 != 0
          val _tmpIsReviewSubmitted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsReviewSubmitted).toInt()
          _tmpIsReviewSubmitted = _tmp_3 != 0
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpAutoApprove: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfAutoApprove).toInt()
          _tmpAutoApprove = _tmp_4 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              ArcClubEntity(_tmpId,_tmpBookId,_tmpBookTitle,_tmpAuthorUserId,_tmpAuthorName,_tmpCoverUrl,_tmpGenre,_tmpBlurb,_tmpFormat,_tmpSlotLimit,_tmpSlotsFilled,_tmpDeadlineDate,_tmpDaysRemaining,_tmpFileUrl,_tmpFileSizeMb,_tmpAsin,_tmpStatus,_tmpMinimumReviewsRequired,_tmpIsApplied,_tmpIsApproved,_tmpHasDownloaded,_tmpIsReviewSubmitted,_tmpBookfunnelLink,_tmpAutoApprove,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getApplicationsForArcClub(arcClubId: String):
      Flow<List<ArcApplicationEntity>> {
    val _sql: String = "SELECT * FROM arc_applications WHERE arcClubId = ? ORDER BY appliedAt DESC"
    return createFlow(__db, false, arrayOf("arc_applications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcClubId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfArcClubId: Int = getColumnIndexOrThrow(_stmt, "arcClubId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfReaderUserId: Int = getColumnIndexOrThrow(_stmt, "readerUserId")
        val _columnIndexOfReaderName: Int = getColumnIndexOrThrow(_stmt, "readerName")
        val _columnIndexOfReaderAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "readerAvatarUrl")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfGoodreadsUrl: Int = getColumnIndexOrThrow(_stmt, "goodreadsUrl")
        val _columnIndexOfPastReviewsCount: Int = getColumnIndexOrThrow(_stmt, "pastReviewsCount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfGenresCsv: Int = getColumnIndexOrThrow(_stmt, "genresCsv")
        val _columnIndexOfExternalPlatforms: Int = getColumnIndexOrThrow(_stmt, "externalPlatforms")
        val _columnIndexOfIsFlaggedForAdmin: Int = getColumnIndexOrThrow(_stmt, "isFlaggedForAdmin")
        val _columnIndexOfAppliedAt: Int = getColumnIndexOrThrow(_stmt, "appliedAt")
        val _result: MutableList<ArcApplicationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcApplicationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpArcClubId: String
          _tmpArcClubId = _stmt.getText(_columnIndexOfArcClubId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpReaderUserId: String
          _tmpReaderUserId = _stmt.getText(_columnIndexOfReaderUserId)
          val _tmpReaderName: String
          _tmpReaderName = _stmt.getText(_columnIndexOfReaderName)
          val _tmpReaderAvatarUrl: String
          _tmpReaderAvatarUrl = _stmt.getText(_columnIndexOfReaderAvatarUrl)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpGoodreadsUrl: String
          _tmpGoodreadsUrl = _stmt.getText(_columnIndexOfGoodreadsUrl)
          val _tmpPastReviewsCount: Int
          _tmpPastReviewsCount = _stmt.getLong(_columnIndexOfPastReviewsCount).toInt()
          val _tmpStatus: ApplicationStatus
          _tmpStatus = __ApplicationStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpGenresCsv: String
          _tmpGenresCsv = _stmt.getText(_columnIndexOfGenresCsv)
          val _tmpExternalPlatforms: String
          _tmpExternalPlatforms = _stmt.getText(_columnIndexOfExternalPlatforms)
          val _tmpIsFlaggedForAdmin: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsFlaggedForAdmin).toInt()
          _tmpIsFlaggedForAdmin = _tmp != 0
          val _tmpAppliedAt: String
          _tmpAppliedAt = _stmt.getText(_columnIndexOfAppliedAt)
          _item =
              ArcApplicationEntity(_tmpId,_tmpArcClubId,_tmpBookTitle,_tmpReaderUserId,_tmpReaderName,_tmpReaderAvatarUrl,_tmpMessage,_tmpGoodreadsUrl,_tmpPastReviewsCount,_tmpStatus,_tmpBookfunnelLink,_tmpGenresCsv,_tmpExternalPlatforms,_tmpIsFlaggedForAdmin,_tmpAppliedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllArcApplications(): Flow<List<ArcApplicationEntity>> {
    val _sql: String = "SELECT * FROM arc_applications ORDER BY appliedAt DESC"
    return createFlow(__db, false, arrayOf("arc_applications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfArcClubId: Int = getColumnIndexOrThrow(_stmt, "arcClubId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfReaderUserId: Int = getColumnIndexOrThrow(_stmt, "readerUserId")
        val _columnIndexOfReaderName: Int = getColumnIndexOrThrow(_stmt, "readerName")
        val _columnIndexOfReaderAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "readerAvatarUrl")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfGoodreadsUrl: Int = getColumnIndexOrThrow(_stmt, "goodreadsUrl")
        val _columnIndexOfPastReviewsCount: Int = getColumnIndexOrThrow(_stmt, "pastReviewsCount")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBookfunnelLink: Int = getColumnIndexOrThrow(_stmt, "bookfunnelLink")
        val _columnIndexOfGenresCsv: Int = getColumnIndexOrThrow(_stmt, "genresCsv")
        val _columnIndexOfExternalPlatforms: Int = getColumnIndexOrThrow(_stmt, "externalPlatforms")
        val _columnIndexOfIsFlaggedForAdmin: Int = getColumnIndexOrThrow(_stmt, "isFlaggedForAdmin")
        val _columnIndexOfAppliedAt: Int = getColumnIndexOrThrow(_stmt, "appliedAt")
        val _result: MutableList<ArcApplicationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcApplicationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpArcClubId: String
          _tmpArcClubId = _stmt.getText(_columnIndexOfArcClubId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpReaderUserId: String
          _tmpReaderUserId = _stmt.getText(_columnIndexOfReaderUserId)
          val _tmpReaderName: String
          _tmpReaderName = _stmt.getText(_columnIndexOfReaderName)
          val _tmpReaderAvatarUrl: String
          _tmpReaderAvatarUrl = _stmt.getText(_columnIndexOfReaderAvatarUrl)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpGoodreadsUrl: String
          _tmpGoodreadsUrl = _stmt.getText(_columnIndexOfGoodreadsUrl)
          val _tmpPastReviewsCount: Int
          _tmpPastReviewsCount = _stmt.getLong(_columnIndexOfPastReviewsCount).toInt()
          val _tmpStatus: ApplicationStatus
          _tmpStatus = __ApplicationStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpBookfunnelLink: String
          _tmpBookfunnelLink = _stmt.getText(_columnIndexOfBookfunnelLink)
          val _tmpGenresCsv: String
          _tmpGenresCsv = _stmt.getText(_columnIndexOfGenresCsv)
          val _tmpExternalPlatforms: String
          _tmpExternalPlatforms = _stmt.getText(_columnIndexOfExternalPlatforms)
          val _tmpIsFlaggedForAdmin: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsFlaggedForAdmin).toInt()
          _tmpIsFlaggedForAdmin = _tmp != 0
          val _tmpAppliedAt: String
          _tmpAppliedAt = _stmt.getText(_columnIndexOfAppliedAt)
          _item =
              ArcApplicationEntity(_tmpId,_tmpArcClubId,_tmpBookTitle,_tmpReaderUserId,_tmpReaderName,_tmpReaderAvatarUrl,_tmpMessage,_tmpGoodreadsUrl,_tmpPastReviewsCount,_tmpStatus,_tmpBookfunnelLink,_tmpGenresCsv,_tmpExternalPlatforms,_tmpIsFlaggedForAdmin,_tmpAppliedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReviewsForArcClub(arcClubId: String): Flow<List<ArcReviewEntity>> {
    val _sql: String = "SELECT * FROM arc_reviews WHERE arcClubId = ? ORDER BY submittedAt DESC"
    return createFlow(__db, false, arrayOf("arc_reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcClubId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfArcClubId: Int = getColumnIndexOrThrow(_stmt, "arcClubId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfReaderUserId: Int = getColumnIndexOrThrow(_stmt, "readerUserId")
        val _columnIndexOfReaderName: Int = getColumnIndexOrThrow(_stmt, "readerName")
        val _columnIndexOfReaderAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "readerAvatarUrl")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfReviewText: Int = getColumnIndexOrThrow(_stmt, "reviewText")
        val _columnIndexOfWordCount: Int = getColumnIndexOrThrow(_stmt, "wordCount")
        val _columnIndexOfAmazonPosted: Int = getColumnIndexOrThrow(_stmt, "amazonPosted")
        val _columnIndexOfGoodreadsPosted: Int = getColumnIndexOrThrow(_stmt, "goodreadsPosted")
        val _columnIndexOfBooktokPosted: Int = getColumnIndexOrThrow(_stmt, "booktokPosted")
        val _columnIndexOfPlatformPosted: Int = getColumnIndexOrThrow(_stmt, "platformPosted")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _result: MutableList<ArcReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcReviewEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpArcClubId: String
          _tmpArcClubId = _stmt.getText(_columnIndexOfArcClubId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpReaderUserId: String
          _tmpReaderUserId = _stmt.getText(_columnIndexOfReaderUserId)
          val _tmpReaderName: String
          _tmpReaderName = _stmt.getText(_columnIndexOfReaderName)
          val _tmpReaderAvatarUrl: String
          _tmpReaderAvatarUrl = _stmt.getText(_columnIndexOfReaderAvatarUrl)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpReviewText: String
          _tmpReviewText = _stmt.getText(_columnIndexOfReviewText)
          val _tmpWordCount: Int
          _tmpWordCount = _stmt.getLong(_columnIndexOfWordCount).toInt()
          val _tmpAmazonPosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAmazonPosted).toInt()
          _tmpAmazonPosted = _tmp != 0
          val _tmpGoodreadsPosted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfGoodreadsPosted).toInt()
          _tmpGoodreadsPosted = _tmp_1 != 0
          val _tmpBooktokPosted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfBooktokPosted).toInt()
          _tmpBooktokPosted = _tmp_2 != 0
          val _tmpPlatformPosted: String
          _tmpPlatformPosted = _stmt.getText(_columnIndexOfPlatformPosted)
          val _tmpStatus: ReviewStatus
          _tmpStatus = __ReviewStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpSubmittedAt: String
          _tmpSubmittedAt = _stmt.getText(_columnIndexOfSubmittedAt)
          _item =
              ArcReviewEntity(_tmpId,_tmpArcClubId,_tmpBookTitle,_tmpAuthorUserId,_tmpReaderUserId,_tmpReaderName,_tmpReaderAvatarUrl,_tmpRating,_tmpReviewText,_tmpWordCount,_tmpAmazonPosted,_tmpGoodreadsPosted,_tmpBooktokPosted,_tmpPlatformPosted,_tmpStatus,_tmpSubmittedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReviewsForAuthor(authorId: String): Flow<List<ArcReviewEntity>> {
    val _sql: String = "SELECT * FROM arc_reviews WHERE authorUserId = ? ORDER BY submittedAt DESC"
    return createFlow(__db, false, arrayOf("arc_reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfArcClubId: Int = getColumnIndexOrThrow(_stmt, "arcClubId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfReaderUserId: Int = getColumnIndexOrThrow(_stmt, "readerUserId")
        val _columnIndexOfReaderName: Int = getColumnIndexOrThrow(_stmt, "readerName")
        val _columnIndexOfReaderAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "readerAvatarUrl")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfReviewText: Int = getColumnIndexOrThrow(_stmt, "reviewText")
        val _columnIndexOfWordCount: Int = getColumnIndexOrThrow(_stmt, "wordCount")
        val _columnIndexOfAmazonPosted: Int = getColumnIndexOrThrow(_stmt, "amazonPosted")
        val _columnIndexOfGoodreadsPosted: Int = getColumnIndexOrThrow(_stmt, "goodreadsPosted")
        val _columnIndexOfBooktokPosted: Int = getColumnIndexOrThrow(_stmt, "booktokPosted")
        val _columnIndexOfPlatformPosted: Int = getColumnIndexOrThrow(_stmt, "platformPosted")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _result: MutableList<ArcReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcReviewEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpArcClubId: String
          _tmpArcClubId = _stmt.getText(_columnIndexOfArcClubId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpReaderUserId: String
          _tmpReaderUserId = _stmt.getText(_columnIndexOfReaderUserId)
          val _tmpReaderName: String
          _tmpReaderName = _stmt.getText(_columnIndexOfReaderName)
          val _tmpReaderAvatarUrl: String
          _tmpReaderAvatarUrl = _stmt.getText(_columnIndexOfReaderAvatarUrl)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpReviewText: String
          _tmpReviewText = _stmt.getText(_columnIndexOfReviewText)
          val _tmpWordCount: Int
          _tmpWordCount = _stmt.getLong(_columnIndexOfWordCount).toInt()
          val _tmpAmazonPosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAmazonPosted).toInt()
          _tmpAmazonPosted = _tmp != 0
          val _tmpGoodreadsPosted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfGoodreadsPosted).toInt()
          _tmpGoodreadsPosted = _tmp_1 != 0
          val _tmpBooktokPosted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfBooktokPosted).toInt()
          _tmpBooktokPosted = _tmp_2 != 0
          val _tmpPlatformPosted: String
          _tmpPlatformPosted = _stmt.getText(_columnIndexOfPlatformPosted)
          val _tmpStatus: ReviewStatus
          _tmpStatus = __ReviewStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpSubmittedAt: String
          _tmpSubmittedAt = _stmt.getText(_columnIndexOfSubmittedAt)
          _item =
              ArcReviewEntity(_tmpId,_tmpArcClubId,_tmpBookTitle,_tmpAuthorUserId,_tmpReaderUserId,_tmpReaderName,_tmpReaderAvatarUrl,_tmpRating,_tmpReviewText,_tmpWordCount,_tmpAmazonPosted,_tmpGoodreadsPosted,_tmpBooktokPosted,_tmpPlatformPosted,_tmpStatus,_tmpSubmittedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReviewsByReader(readerId: String): Flow<List<ArcReviewEntity>> {
    val _sql: String = "SELECT * FROM arc_reviews WHERE readerUserId = ? ORDER BY submittedAt DESC"
    return createFlow(__db, false, arrayOf("arc_reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, readerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfArcClubId: Int = getColumnIndexOrThrow(_stmt, "arcClubId")
        val _columnIndexOfBookTitle: Int = getColumnIndexOrThrow(_stmt, "bookTitle")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfReaderUserId: Int = getColumnIndexOrThrow(_stmt, "readerUserId")
        val _columnIndexOfReaderName: Int = getColumnIndexOrThrow(_stmt, "readerName")
        val _columnIndexOfReaderAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "readerAvatarUrl")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfReviewText: Int = getColumnIndexOrThrow(_stmt, "reviewText")
        val _columnIndexOfWordCount: Int = getColumnIndexOrThrow(_stmt, "wordCount")
        val _columnIndexOfAmazonPosted: Int = getColumnIndexOrThrow(_stmt, "amazonPosted")
        val _columnIndexOfGoodreadsPosted: Int = getColumnIndexOrThrow(_stmt, "goodreadsPosted")
        val _columnIndexOfBooktokPosted: Int = getColumnIndexOrThrow(_stmt, "booktokPosted")
        val _columnIndexOfPlatformPosted: Int = getColumnIndexOrThrow(_stmt, "platformPosted")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _result: MutableList<ArcReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArcReviewEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpArcClubId: String
          _tmpArcClubId = _stmt.getText(_columnIndexOfArcClubId)
          val _tmpBookTitle: String
          _tmpBookTitle = _stmt.getText(_columnIndexOfBookTitle)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpReaderUserId: String
          _tmpReaderUserId = _stmt.getText(_columnIndexOfReaderUserId)
          val _tmpReaderName: String
          _tmpReaderName = _stmt.getText(_columnIndexOfReaderName)
          val _tmpReaderAvatarUrl: String
          _tmpReaderAvatarUrl = _stmt.getText(_columnIndexOfReaderAvatarUrl)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpReviewText: String
          _tmpReviewText = _stmt.getText(_columnIndexOfReviewText)
          val _tmpWordCount: Int
          _tmpWordCount = _stmt.getLong(_columnIndexOfWordCount).toInt()
          val _tmpAmazonPosted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAmazonPosted).toInt()
          _tmpAmazonPosted = _tmp != 0
          val _tmpGoodreadsPosted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfGoodreadsPosted).toInt()
          _tmpGoodreadsPosted = _tmp_1 != 0
          val _tmpBooktokPosted: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfBooktokPosted).toInt()
          _tmpBooktokPosted = _tmp_2 != 0
          val _tmpPlatformPosted: String
          _tmpPlatformPosted = _stmt.getText(_columnIndexOfPlatformPosted)
          val _tmpStatus: ReviewStatus
          _tmpStatus = __ReviewStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpSubmittedAt: String
          _tmpSubmittedAt = _stmt.getText(_columnIndexOfSubmittedAt)
          _item =
              ArcReviewEntity(_tmpId,_tmpArcClubId,_tmpBookTitle,_tmpAuthorUserId,_tmpReaderUserId,_tmpReaderName,_tmpReaderAvatarUrl,_tmpRating,_tmpReviewText,_tmpWordCount,_tmpAmazonPosted,_tmpGoodreadsPosted,_tmpBooktokPosted,_tmpPlatformPosted,_tmpStatus,_tmpSubmittedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getThreadsForClub(clubId: String): Flow<List<ClubThreadEntity>> {
    val _sql: String =
        "SELECT * FROM club_threads WHERE clubId = ? ORDER BY isPinned DESC, createdAt DESC"
    return createFlow(__db, false, arrayOf("club_threads")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, clubId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfClubId: Int = getColumnIndexOrThrow(_stmt, "clubId")
        val _columnIndexOfClubType: Int = getColumnIndexOrThrow(_stmt, "clubType")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfAuthorAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "authorAvatarUrl")
        val _columnIndexOfReplyCount: Int = getColumnIndexOrThrow(_stmt, "replyCount")
        val _columnIndexOfLikesCount: Int = getColumnIndexOrThrow(_stmt, "likesCount")
        val _columnIndexOfIsPinned: Int = getColumnIndexOrThrow(_stmt, "isPinned")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ClubThreadEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ClubThreadEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpClubId: String
          _tmpClubId = _stmt.getText(_columnIndexOfClubId)
          val _tmpClubType: String
          _tmpClubType = _stmt.getText(_columnIndexOfClubType)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpAuthorAvatarUrl: String
          _tmpAuthorAvatarUrl = _stmt.getText(_columnIndexOfAuthorAvatarUrl)
          val _tmpReplyCount: Int
          _tmpReplyCount = _stmt.getLong(_columnIndexOfReplyCount).toInt()
          val _tmpLikesCount: Int
          _tmpLikesCount = _stmt.getLong(_columnIndexOfLikesCount).toInt()
          val _tmpIsPinned: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPinned).toInt()
          _tmpIsPinned = _tmp != 0
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item =
              ClubThreadEntity(_tmpId,_tmpClubId,_tmpClubType,_tmpCategory,_tmpTitle,_tmpBody,_tmpAuthorUserId,_tmpAuthorName,_tmpAuthorAvatarUrl,_tmpReplyCount,_tmpLikesCount,_tmpIsPinned,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRepliesForThread(threadId: String): Flow<List<ThreadReplyEntity>> {
    val _sql: String = "SELECT * FROM thread_replies WHERE threadId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("thread_replies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, threadId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfThreadId: Int = getColumnIndexOrThrow(_stmt, "threadId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "userAvatarUrl")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ThreadReplyEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ThreadReplyEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpThreadId: String
          _tmpThreadId = _stmt.getText(_columnIndexOfThreadId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserAvatarUrl: String
          _tmpUserAvatarUrl = _stmt.getText(_columnIndexOfUserAvatarUrl)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item =
              ThreadReplyEntity(_tmpId,_tmpThreadId,_tmpUserId,_tmpUserName,_tmpUserAvatarUrl,_tmpBody,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllBroadcasts(): Flow<List<BroadcastEntity>> {
    val _sql: String = "SELECT * FROM broadcasts ORDER BY sentAt DESC"
    return createFlow(__db, false, arrayOf("broadcasts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfAuthorAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "authorAvatarUrl")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfActionUrl: Int = getColumnIndexOrThrow(_stmt, "actionUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _result: MutableList<BroadcastEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BroadcastEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpAuthorAvatarUrl: String
          _tmpAuthorAvatarUrl = _stmt.getText(_columnIndexOfAuthorAvatarUrl)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: BroadcastType
          _tmpType = __BroadcastType_stringToEnum(_stmt.getText(_columnIndexOfType))
          val _tmpActionUrl: String
          _tmpActionUrl = _stmt.getText(_columnIndexOfActionUrl)
          val _tmpSentAt: String
          _tmpSentAt = _stmt.getText(_columnIndexOfSentAt)
          _item =
              BroadcastEntity(_tmpId,_tmpAuthorUserId,_tmpAuthorName,_tmpAuthorAvatarUrl,_tmpTitle,_tmpMessage,_tmpType,_tmpActionUrl,_tmpSentAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBroadcastsByAuthor(authorId: String): Flow<List<BroadcastEntity>> {
    val _sql: String = "SELECT * FROM broadcasts WHERE authorUserId = ? ORDER BY sentAt DESC"
    return createFlow(__db, false, arrayOf("broadcasts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfAuthorAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "authorAvatarUrl")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfActionUrl: Int = getColumnIndexOrThrow(_stmt, "actionUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _result: MutableList<BroadcastEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BroadcastEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpAuthorAvatarUrl: String
          _tmpAuthorAvatarUrl = _stmt.getText(_columnIndexOfAuthorAvatarUrl)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: BroadcastType
          _tmpType = __BroadcastType_stringToEnum(_stmt.getText(_columnIndexOfType))
          val _tmpActionUrl: String
          _tmpActionUrl = _stmt.getText(_columnIndexOfActionUrl)
          val _tmpSentAt: String
          _tmpSentAt = _stmt.getText(_columnIndexOfSentAt)
          _item =
              BroadcastEntity(_tmpId,_tmpAuthorUserId,_tmpAuthorName,_tmpAuthorAvatarUrl,_tmpTitle,_tmpMessage,_tmpType,_tmpActionUrl,_tmpSentAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllNotifications(): Flow<List<NotificationEntity>> {
    val _sql: String = "SELECT * FROM notifications ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfTimeAgo: Int = getColumnIndexOrThrow(_stmt, "timeAgo")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpType: NotificationType
          _tmpType = __NotificationType_stringToEnum(_stmt.getText(_columnIndexOfType))
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpTargetId: String
          _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpTimeAgo: String
          _tmpTimeAgo = _stmt.getText(_columnIndexOfTimeAgo)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              NotificationEntity(_tmpId,_tmpType,_tmpTitle,_tmpMessage,_tmpTargetId,_tmpIsRead,_tmpTimeAgo,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUnreadNotificationsCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM notifications WHERE isRead = 0"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFollowersForAuthor(authorId: String): Flow<List<FollowEntity>> {
    val _sql: String = "SELECT * FROM follows WHERE authorUserId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfFollowerUserId: Int = getColumnIndexOrThrow(_stmt, "followerUserId")
        val _columnIndexOfFollowerName: Int = getColumnIndexOrThrow(_stmt, "followerName")
        val _columnIndexOfFollowerEmail: Int = getColumnIndexOrThrow(_stmt, "followerEmail")
        val _columnIndexOfEmailConsent: Int = getColumnIndexOrThrow(_stmt, "emailConsent")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<FollowEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FollowEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpFollowerUserId: String
          _tmpFollowerUserId = _stmt.getText(_columnIndexOfFollowerUserId)
          val _tmpFollowerName: String
          _tmpFollowerName = _stmt.getText(_columnIndexOfFollowerName)
          val _tmpFollowerEmail: String
          _tmpFollowerEmail = _stmt.getText(_columnIndexOfFollowerEmail)
          val _tmpEmailConsent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEmailConsent).toInt()
          _tmpEmailConsent = _tmp != 0
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item =
              FollowEntity(_tmpId,_tmpAuthorUserId,_tmpAuthorName,_tmpFollowerUserId,_tmpFollowerName,_tmpFollowerEmail,_tmpEmailConsent,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFollowingsForUser(followerId: String): Flow<List<FollowEntity>> {
    val _sql: String = "SELECT * FROM follows WHERE followerUserId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, followerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfFollowerUserId: Int = getColumnIndexOrThrow(_stmt, "followerUserId")
        val _columnIndexOfFollowerName: Int = getColumnIndexOrThrow(_stmt, "followerName")
        val _columnIndexOfFollowerEmail: Int = getColumnIndexOrThrow(_stmt, "followerEmail")
        val _columnIndexOfEmailConsent: Int = getColumnIndexOrThrow(_stmt, "emailConsent")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<FollowEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FollowEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpFollowerUserId: String
          _tmpFollowerUserId = _stmt.getText(_columnIndexOfFollowerUserId)
          val _tmpFollowerName: String
          _tmpFollowerName = _stmt.getText(_columnIndexOfFollowerName)
          val _tmpFollowerEmail: String
          _tmpFollowerEmail = _stmt.getText(_columnIndexOfFollowerEmail)
          val _tmpEmailConsent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEmailConsent).toInt()
          _tmpEmailConsent = _tmp != 0
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item =
              FollowEntity(_tmpId,_tmpAuthorUserId,_tmpAuthorName,_tmpFollowerUserId,_tmpFollowerName,_tmpFollowerEmail,_tmpEmailConsent,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isFollowing(authorId: String, followerId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM follows WHERE authorUserId = ? AND followerUserId = ?"
    return createFlow(__db, false, arrayOf("follows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        _argIndex = 2
        _stmt.bindText(_argIndex, followerId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBookLogs(userId: String): Flow<List<BookLogEntity>> {
    val _sql: String = "SELECT * FROM book_logs WHERE userId = ? ORDER BY dateCompleted DESC"
    return createFlow(__db, false, arrayOf("book_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfDateCompleted: Int = getColumnIndexOrThrow(_stmt, "dateCompleted")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _result: MutableList<BookLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BookLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthor: String
          _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpRating: Int
          _tmpRating = _stmt.getLong(_columnIndexOfRating).toInt()
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpDateCompleted: String
          _tmpDateCompleted = _stmt.getText(_columnIndexOfDateCompleted)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          _item =
              BookLogEntity(_tmpId,_tmpUserId,_tmpTitle,_tmpAuthor,_tmpCoverUrl,_tmpRating,_tmpNotes,_tmpDateCompleted,_tmpGenre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReadingProgress(bookId: String): Flow<ReadingProgressEntity?> {
    val _sql: String = "SELECT * FROM reading_progress WHERE book_id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("reading_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "book_id")
        val _columnIndexOfLastPageIndex: Int = getColumnIndexOrThrow(_stmt, "last_page")
        val _columnIndexOfLastChapterIndex: Int = getColumnIndexOrThrow(_stmt, "last_chapter_index")
        val _columnIndexOfTotalPagesOrChapters: Int = getColumnIndexOrThrow(_stmt,
            "total_pages_or_chapters")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progress_percent")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfLastReadTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "last_read_timestamp")
        val _result: ReadingProgressEntity?
        if (_stmt.step()) {
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpLastPageIndex: Int
          _tmpLastPageIndex = _stmt.getLong(_columnIndexOfLastPageIndex).toInt()
          val _tmpLastChapterIndex: Int
          _tmpLastChapterIndex = _stmt.getLong(_columnIndexOfLastChapterIndex).toInt()
          val _tmpTotalPagesOrChapters: Int
          _tmpTotalPagesOrChapters = _stmt.getLong(_columnIndexOfTotalPagesOrChapters).toInt()
          val _tmpProgressPercent: Float
          _tmpProgressPercent = _stmt.getDouble(_columnIndexOfProgressPercent).toFloat()
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpLastReadTimestamp: Long
          _tmpLastReadTimestamp = _stmt.getLong(_columnIndexOfLastReadTimestamp)
          _result =
              ReadingProgressEntity(_tmpBookId,_tmpLastPageIndex,_tmpLastChapterIndex,_tmpTotalPagesOrChapters,_tmpProgressPercent,_tmpFormat,_tmpLastReadTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getReadingProgressDirect(bookId: String): ReadingProgressEntity? {
    val _sql: String = "SELECT * FROM reading_progress WHERE book_id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "book_id")
        val _columnIndexOfLastPageIndex: Int = getColumnIndexOrThrow(_stmt, "last_page")
        val _columnIndexOfLastChapterIndex: Int = getColumnIndexOrThrow(_stmt, "last_chapter_index")
        val _columnIndexOfTotalPagesOrChapters: Int = getColumnIndexOrThrow(_stmt,
            "total_pages_or_chapters")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progress_percent")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfLastReadTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "last_read_timestamp")
        val _result: ReadingProgressEntity?
        if (_stmt.step()) {
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpLastPageIndex: Int
          _tmpLastPageIndex = _stmt.getLong(_columnIndexOfLastPageIndex).toInt()
          val _tmpLastChapterIndex: Int
          _tmpLastChapterIndex = _stmt.getLong(_columnIndexOfLastChapterIndex).toInt()
          val _tmpTotalPagesOrChapters: Int
          _tmpTotalPagesOrChapters = _stmt.getLong(_columnIndexOfTotalPagesOrChapters).toInt()
          val _tmpProgressPercent: Float
          _tmpProgressPercent = _stmt.getDouble(_columnIndexOfProgressPercent).toFloat()
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpLastReadTimestamp: Long
          _tmpLastReadTimestamp = _stmt.getLong(_columnIndexOfLastReadTimestamp)
          _result =
              ReadingProgressEntity(_tmpBookId,_tmpLastPageIndex,_tmpLastChapterIndex,_tmpTotalPagesOrChapters,_tmpProgressPercent,_tmpFormat,_tmpLastReadTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLastPageIndex(bookId: String): Flow<Int?> {
    val _sql: String = "SELECT last_page FROM reading_progress WHERE book_id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("reading_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        val _result: Int?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0).toInt()
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLastPageIndexDirect(bookId: String): Int? {
    val _sql: String = "SELECT last_page FROM reading_progress WHERE book_id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        val _result: Int?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0).toInt()
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>> {
    val _sql: String = "SELECT * FROM reading_progress ORDER BY last_read_timestamp DESC"
    return createFlow(__db, false, arrayOf("reading_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "book_id")
        val _columnIndexOfLastPageIndex: Int = getColumnIndexOrThrow(_stmt, "last_page")
        val _columnIndexOfLastChapterIndex: Int = getColumnIndexOrThrow(_stmt, "last_chapter_index")
        val _columnIndexOfTotalPagesOrChapters: Int = getColumnIndexOrThrow(_stmt,
            "total_pages_or_chapters")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progress_percent")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfLastReadTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "last_read_timestamp")
        val _result: MutableList<ReadingProgressEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReadingProgressEntity
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_columnIndexOfBookId)
          val _tmpLastPageIndex: Int
          _tmpLastPageIndex = _stmt.getLong(_columnIndexOfLastPageIndex).toInt()
          val _tmpLastChapterIndex: Int
          _tmpLastChapterIndex = _stmt.getLong(_columnIndexOfLastChapterIndex).toInt()
          val _tmpTotalPagesOrChapters: Int
          _tmpTotalPagesOrChapters = _stmt.getLong(_columnIndexOfTotalPagesOrChapters).toInt()
          val _tmpProgressPercent: Float
          _tmpProgressPercent = _stmt.getDouble(_columnIndexOfProgressPercent).toFloat()
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpLastReadTimestamp: Long
          _tmpLastReadTimestamp = _stmt.getLong(_columnIndexOfLastReadTimestamp)
          _item =
              ReadingProgressEntity(_tmpBookId,_tmpLastPageIndex,_tmpLastChapterIndex,_tmpTotalPagesOrChapters,_tmpProgressPercent,_tmpFormat,_tmpLastReadTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBookmarkTransactions(userId: String):
      Flow<List<BookmarkTransactionEntity>> {
    val _sql: String =
        "SELECT * FROM bookmark_transactions WHERE userId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("bookmark_transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfBookmarksAmount: Int = getColumnIndexOrThrow(_stmt, "bookmarksAmount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfIconEmoji: Int = getColumnIndexOrThrow(_stmt, "iconEmoji")
        val _result: MutableList<BookmarkTransactionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BookmarkTransactionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpBookmarksAmount: Int
          _tmpBookmarksAmount = _stmt.getLong(_columnIndexOfBookmarksAmount).toInt()
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpIconEmoji: String
          _tmpIconEmoji = _stmt.getText(_columnIndexOfIconEmoji)
          _item =
              BookmarkTransactionEntity(_tmpId,_tmpUserId,_tmpTitle,_tmpNote,_tmpBookmarksAmount,_tmpType,_tmpTimestamp,_tmpIconEmoji)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBookmarkRedemptions(userId: String): Flow<List<BookmarkRedemptionEntity>> {
    val _sql: String =
        "SELECT * FROM bookmark_redemptions WHERE userId = ? ORDER BY redeemedTimestamp DESC"
    return createFlow(__db, false, arrayOf("bookmark_redemptions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRewardId: Int = getColumnIndexOrThrow(_stmt, "rewardId")
        val _columnIndexOfRewardTitle: Int = getColumnIndexOrThrow(_stmt, "rewardTitle")
        val _columnIndexOfRewardSubtitle: Int = getColumnIndexOrThrow(_stmt, "rewardSubtitle")
        val _columnIndexOfCostBookmarks: Int = getColumnIndexOrThrow(_stmt, "costBookmarks")
        val _columnIndexOfRedeemedTimestamp: Int = getColumnIndexOrThrow(_stmt, "redeemedTimestamp")
        val _columnIndexOfRedemptionCode: Int = getColumnIndexOrThrow(_stmt, "redemptionCode")
        val _columnIndexOfIconEmoji: Int = getColumnIndexOrThrow(_stmt, "iconEmoji")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<BookmarkRedemptionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BookmarkRedemptionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRewardId: String
          _tmpRewardId = _stmt.getText(_columnIndexOfRewardId)
          val _tmpRewardTitle: String
          _tmpRewardTitle = _stmt.getText(_columnIndexOfRewardTitle)
          val _tmpRewardSubtitle: String
          _tmpRewardSubtitle = _stmt.getText(_columnIndexOfRewardSubtitle)
          val _tmpCostBookmarks: Int
          _tmpCostBookmarks = _stmt.getLong(_columnIndexOfCostBookmarks).toInt()
          val _tmpRedeemedTimestamp: Long
          _tmpRedeemedTimestamp = _stmt.getLong(_columnIndexOfRedeemedTimestamp)
          val _tmpRedemptionCode: String
          _tmpRedemptionCode = _stmt.getText(_columnIndexOfRedemptionCode)
          val _tmpIconEmoji: String
          _tmpIconEmoji = _stmt.getText(_columnIndexOfIconEmoji)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              BookmarkRedemptionEntity(_tmpId,_tmpUserId,_tmpRewardId,_tmpRewardTitle,_tmpRewardSubtitle,_tmpCostBookmarks,_tmpRedeemedTimestamp,_tmpRedemptionCode,_tmpIconEmoji,_tmpStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSavedBooks(userId: String): Flow<List<SavedBookEntity>> {
    val _sql: String = "SELECT * FROM saved_books WHERE userId = ? ORDER BY savedAt DESC"
    return createFlow(__db, false, arrayOf("saved_books")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGoogleBooksId: Int = getColumnIndexOrThrow(_stmt, "googleBooksId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthors: Int = getColumnIndexOrThrow(_stmt, "authors")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfIsbn13: Int = getColumnIndexOrThrow(_stmt, "isbn13")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPersonalRating: Int = getColumnIndexOrThrow(_stmt, "personalRating")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "syncState")
        val _result: MutableList<SavedBookEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SavedBookEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGoogleBooksId: String
          _tmpGoogleBooksId = _stmt.getText(_columnIndexOfGoogleBooksId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthors: String
          _tmpAuthors = _stmt.getText(_columnIndexOfAuthors)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpIsbn13: String
          _tmpIsbn13 = _stmt.getText(_columnIndexOfIsbn13)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpPersonalRating: Int
          _tmpPersonalRating = _stmt.getLong(_columnIndexOfPersonalRating).toInt()
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _item =
              SavedBookEntity(_tmpId,_tmpUserId,_tmpGoogleBooksId,_tmpTitle,_tmpAuthors,_tmpCoverUrl,_tmpCategory,_tmpDescription,_tmpGenre,_tmpPageCount,_tmpRating,_tmpIsbn13,_tmpNotes,_tmpPersonalRating,_tmpSavedAt,_tmpCloudSyncedAt,_tmpSyncState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSavedBooksDirect(userId: String): List<SavedBookEntity> {
    val _sql: String = "SELECT * FROM saved_books WHERE userId = ? ORDER BY savedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGoogleBooksId: Int = getColumnIndexOrThrow(_stmt, "googleBooksId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthors: Int = getColumnIndexOrThrow(_stmt, "authors")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfIsbn13: Int = getColumnIndexOrThrow(_stmt, "isbn13")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPersonalRating: Int = getColumnIndexOrThrow(_stmt, "personalRating")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "syncState")
        val _result: MutableList<SavedBookEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SavedBookEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGoogleBooksId: String
          _tmpGoogleBooksId = _stmt.getText(_columnIndexOfGoogleBooksId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthors: String
          _tmpAuthors = _stmt.getText(_columnIndexOfAuthors)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpIsbn13: String
          _tmpIsbn13 = _stmt.getText(_columnIndexOfIsbn13)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpPersonalRating: Int
          _tmpPersonalRating = _stmt.getLong(_columnIndexOfPersonalRating).toInt()
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _item =
              SavedBookEntity(_tmpId,_tmpUserId,_tmpGoogleBooksId,_tmpTitle,_tmpAuthors,_tmpCoverUrl,_tmpCategory,_tmpDescription,_tmpGenre,_tmpPageCount,_tmpRating,_tmpIsbn13,_tmpNotes,_tmpPersonalRating,_tmpSavedAt,_tmpCloudSyncedAt,_tmpSyncState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSavedBooksByCategory(userId: String, category: String):
      Flow<List<SavedBookEntity>> {
    val _sql: String =
        "SELECT * FROM saved_books WHERE userId = ? AND category = ? ORDER BY savedAt DESC"
    return createFlow(__db, false, arrayOf("saved_books")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGoogleBooksId: Int = getColumnIndexOrThrow(_stmt, "googleBooksId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthors: Int = getColumnIndexOrThrow(_stmt, "authors")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfIsbn13: Int = getColumnIndexOrThrow(_stmt, "isbn13")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPersonalRating: Int = getColumnIndexOrThrow(_stmt, "personalRating")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "syncState")
        val _result: MutableList<SavedBookEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SavedBookEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGoogleBooksId: String
          _tmpGoogleBooksId = _stmt.getText(_columnIndexOfGoogleBooksId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthors: String
          _tmpAuthors = _stmt.getText(_columnIndexOfAuthors)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpIsbn13: String
          _tmpIsbn13 = _stmt.getText(_columnIndexOfIsbn13)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpPersonalRating: Int
          _tmpPersonalRating = _stmt.getLong(_columnIndexOfPersonalRating).toInt()
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _item =
              SavedBookEntity(_tmpId,_tmpUserId,_tmpGoogleBooksId,_tmpTitle,_tmpAuthors,_tmpCoverUrl,_tmpCategory,_tmpDescription,_tmpGenre,_tmpPageCount,_tmpRating,_tmpIsbn13,_tmpNotes,_tmpPersonalRating,_tmpSavedAt,_tmpCloudSyncedAt,_tmpSyncState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSavedBookByGoogleId(userId: String, googleBooksId: String):
      Flow<SavedBookEntity?> {
    val _sql: String = "SELECT * FROM saved_books WHERE userId = ? AND googleBooksId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("saved_books")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, googleBooksId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGoogleBooksId: Int = getColumnIndexOrThrow(_stmt, "googleBooksId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthors: Int = getColumnIndexOrThrow(_stmt, "authors")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfIsbn13: Int = getColumnIndexOrThrow(_stmt, "isbn13")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPersonalRating: Int = getColumnIndexOrThrow(_stmt, "personalRating")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "syncState")
        val _result: SavedBookEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGoogleBooksId: String
          _tmpGoogleBooksId = _stmt.getText(_columnIndexOfGoogleBooksId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthors: String
          _tmpAuthors = _stmt.getText(_columnIndexOfAuthors)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpIsbn13: String
          _tmpIsbn13 = _stmt.getText(_columnIndexOfIsbn13)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpPersonalRating: Int
          _tmpPersonalRating = _stmt.getLong(_columnIndexOfPersonalRating).toInt()
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _result =
              SavedBookEntity(_tmpId,_tmpUserId,_tmpGoogleBooksId,_tmpTitle,_tmpAuthors,_tmpCoverUrl,_tmpCategory,_tmpDescription,_tmpGenre,_tmpPageCount,_tmpRating,_tmpIsbn13,_tmpNotes,_tmpPersonalRating,_tmpSavedAt,_tmpCloudSyncedAt,_tmpSyncState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSavedBookByGoogleIdDirect(userId: String, googleBooksId: String):
      SavedBookEntity? {
    val _sql: String = "SELECT * FROM saved_books WHERE userId = ? AND googleBooksId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, googleBooksId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfGoogleBooksId: Int = getColumnIndexOrThrow(_stmt, "googleBooksId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthors: Int = getColumnIndexOrThrow(_stmt, "authors")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfGenre: Int = getColumnIndexOrThrow(_stmt, "genre")
        val _columnIndexOfPageCount: Int = getColumnIndexOrThrow(_stmt, "pageCount")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfIsbn13: Int = getColumnIndexOrThrow(_stmt, "isbn13")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPersonalRating: Int = getColumnIndexOrThrow(_stmt, "personalRating")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "syncState")
        val _result: SavedBookEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpGoogleBooksId: String
          _tmpGoogleBooksId = _stmt.getText(_columnIndexOfGoogleBooksId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthors: String
          _tmpAuthors = _stmt.getText(_columnIndexOfAuthors)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpGenre: String
          _tmpGenre = _stmt.getText(_columnIndexOfGenre)
          val _tmpPageCount: Int
          _tmpPageCount = _stmt.getLong(_columnIndexOfPageCount).toInt()
          val _tmpRating: Float
          _tmpRating = _stmt.getDouble(_columnIndexOfRating).toFloat()
          val _tmpIsbn13: String
          _tmpIsbn13 = _stmt.getText(_columnIndexOfIsbn13)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpPersonalRating: Int
          _tmpPersonalRating = _stmt.getLong(_columnIndexOfPersonalRating).toInt()
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _result =
              SavedBookEntity(_tmpId,_tmpUserId,_tmpGoogleBooksId,_tmpTitle,_tmpAuthors,_tmpCoverUrl,_tmpCategory,_tmpDescription,_tmpGenre,_tmpPageCount,_tmpRating,_tmpIsbn13,_tmpNotes,_tmpPersonalRating,_tmpSavedAt,_tmpCloudSyncedAt,_tmpSyncState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCustomShelves(userId: String): Flow<List<CustomShelfEntity>> {
    val _sql: String =
        "SELECT * FROM custom_shelves WHERE userId = ? ORDER BY isDefault DESC, createdAt ASC"
    return createFlow(__db, false, arrayOf("custom_shelves")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfIconEmoji: Int = getColumnIndexOrThrow(_stmt, "iconEmoji")
        val _columnIndexOfIsDefault: Int = getColumnIndexOrThrow(_stmt, "isDefault")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _result: MutableList<CustomShelfEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomShelfEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpIconEmoji: String
          _tmpIconEmoji = _stmt.getText(_columnIndexOfIconEmoji)
          val _tmpIsDefault: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDefault).toInt()
          _tmpIsDefault = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          _item =
              CustomShelfEntity(_tmpId,_tmpUserId,_tmpName,_tmpIconEmoji,_tmpIsDefault,_tmpCreatedAt,_tmpCloudSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCustomShelvesDirect(userId: String): List<CustomShelfEntity> {
    val _sql: String =
        "SELECT * FROM custom_shelves WHERE userId = ? ORDER BY isDefault DESC, createdAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfIconEmoji: Int = getColumnIndexOrThrow(_stmt, "iconEmoji")
        val _columnIndexOfIsDefault: Int = getColumnIndexOrThrow(_stmt, "isDefault")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCloudSyncedAt: Int = getColumnIndexOrThrow(_stmt, "cloudSyncedAt")
        val _result: MutableList<CustomShelfEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomShelfEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpIconEmoji: String
          _tmpIconEmoji = _stmt.getText(_columnIndexOfIconEmoji)
          val _tmpIsDefault: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDefault).toInt()
          _tmpIsDefault = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCloudSyncedAt: Long
          _tmpCloudSyncedAt = _stmt.getLong(_columnIndexOfCloudSyncedAt)
          _item =
              CustomShelfEntity(_tmpId,_tmpUserId,_tmpName,_tmpIconEmoji,_tmpIsDefault,_tmpCreatedAt,_tmpCloudSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAtomicShelfAnalytics(authorId: String): Flow<AtomicShelfAnalyticsEntity?> {
    val _sql: String = "SELECT * FROM as_analytics WHERE authorId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("as_analytics")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfAsClientId: Int = getColumnIndexOrThrow(_stmt, "asClientId")
        val _columnIndexOfPeriod: Int = getColumnIndexOrThrow(_stmt, "period")
        val _columnIndexOfNewsletterPlacements: Int = getColumnIndexOrThrow(_stmt,
            "newsletterPlacements")
        val _columnIndexOfNewsletterSubscribersReached: Int = getColumnIndexOrThrow(_stmt,
            "newsletterSubscribersReached")
        val _columnIndexOfTiktokViews: Int = getColumnIndexOrThrow(_stmt, "tiktokViews")
        val _columnIndexOfYtViews: Int = getColumnIndexOrThrow(_stmt, "ytViews")
        val _columnIndexOfTopVideoUrl: Int = getColumnIndexOrThrow(_stmt, "topVideoUrl")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _result: AtomicShelfAnalyticsEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpAsClientId: String
          _tmpAsClientId = _stmt.getText(_columnIndexOfAsClientId)
          val _tmpPeriod: String
          _tmpPeriod = _stmt.getText(_columnIndexOfPeriod)
          val _tmpNewsletterPlacements: Int
          _tmpNewsletterPlacements = _stmt.getLong(_columnIndexOfNewsletterPlacements).toInt()
          val _tmpNewsletterSubscribersReached: Int
          _tmpNewsletterSubscribersReached =
              _stmt.getLong(_columnIndexOfNewsletterSubscribersReached).toInt()
          val _tmpTiktokViews: Int
          _tmpTiktokViews = _stmt.getLong(_columnIndexOfTiktokViews).toInt()
          val _tmpYtViews: Int
          _tmpYtViews = _stmt.getLong(_columnIndexOfYtViews).toInt()
          val _tmpTopVideoUrl: String
          _tmpTopVideoUrl = _stmt.getText(_columnIndexOfTopVideoUrl)
          val _tmpSyncedAt: Long
          _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          _result =
              AtomicShelfAnalyticsEntity(_tmpId,_tmpAuthorId,_tmpAuthorName,_tmpAsClientId,_tmpPeriod,_tmpNewsletterPlacements,_tmpNewsletterSubscribersReached,_tmpTiktokViews,_tmpYtViews,_tmpTopVideoUrl,_tmpSyncedAt,_tmpSyncStatus)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllAtomicShelfAnalytics(): Flow<List<AtomicShelfAnalyticsEntity>> {
    val _sql: String = "SELECT * FROM as_analytics ORDER BY syncedAt DESC"
    return createFlow(__db, false, arrayOf("as_analytics")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfAuthorName: Int = getColumnIndexOrThrow(_stmt, "authorName")
        val _columnIndexOfAsClientId: Int = getColumnIndexOrThrow(_stmt, "asClientId")
        val _columnIndexOfPeriod: Int = getColumnIndexOrThrow(_stmt, "period")
        val _columnIndexOfNewsletterPlacements: Int = getColumnIndexOrThrow(_stmt,
            "newsletterPlacements")
        val _columnIndexOfNewsletterSubscribersReached: Int = getColumnIndexOrThrow(_stmt,
            "newsletterSubscribersReached")
        val _columnIndexOfTiktokViews: Int = getColumnIndexOrThrow(_stmt, "tiktokViews")
        val _columnIndexOfYtViews: Int = getColumnIndexOrThrow(_stmt, "ytViews")
        val _columnIndexOfTopVideoUrl: Int = getColumnIndexOrThrow(_stmt, "topVideoUrl")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSyncStatus: Int = getColumnIndexOrThrow(_stmt, "syncStatus")
        val _result: MutableList<AtomicShelfAnalyticsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AtomicShelfAnalyticsEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpAuthorName: String
          _tmpAuthorName = _stmt.getText(_columnIndexOfAuthorName)
          val _tmpAsClientId: String
          _tmpAsClientId = _stmt.getText(_columnIndexOfAsClientId)
          val _tmpPeriod: String
          _tmpPeriod = _stmt.getText(_columnIndexOfPeriod)
          val _tmpNewsletterPlacements: Int
          _tmpNewsletterPlacements = _stmt.getLong(_columnIndexOfNewsletterPlacements).toInt()
          val _tmpNewsletterSubscribersReached: Int
          _tmpNewsletterSubscribersReached =
              _stmt.getLong(_columnIndexOfNewsletterSubscribersReached).toInt()
          val _tmpTiktokViews: Int
          _tmpTiktokViews = _stmt.getLong(_columnIndexOfTiktokViews).toInt()
          val _tmpYtViews: Int
          _tmpYtViews = _stmt.getLong(_columnIndexOfYtViews).toInt()
          val _tmpTopVideoUrl: String
          _tmpTopVideoUrl = _stmt.getText(_columnIndexOfTopVideoUrl)
          val _tmpSyncedAt: Long
          _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          val _tmpSyncStatus: String
          _tmpSyncStatus = _stmt.getText(_columnIndexOfSyncStatus)
          _item =
              AtomicShelfAnalyticsEntity(_tmpId,_tmpAuthorId,_tmpAuthorName,_tmpAsClientId,_tmpPeriod,_tmpNewsletterPlacements,_tmpNewsletterSubscribersReached,_tmpTiktokViews,_tmpYtViews,_tmpTopVideoUrl,_tmpSyncedAt,_tmpSyncStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllRedemptionRequests(): Flow<List<RedemptionRequestEntity>> {
    val _sql: String = "SELECT * FROM redemption_requests ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("redemption_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfCatalogueItemId: Int = getColumnIndexOrThrow(_stmt, "catalogueItemId")
        val _columnIndexOfCatalogueItemName: Int = getColumnIndexOrThrow(_stmt, "catalogueItemName")
        val _columnIndexOfCostBookmarks: Int = getColumnIndexOrThrow(_stmt, "costBookmarks")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfTrackingCode: Int = getColumnIndexOrThrow(_stmt, "trackingCode")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfFulfilledAt: Int = getColumnIndexOrThrow(_stmt, "fulfilledAt")
        val _result: MutableList<RedemptionRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RedemptionRequestEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserEmail: String
          _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          val _tmpCatalogueItemId: String
          _tmpCatalogueItemId = _stmt.getText(_columnIndexOfCatalogueItemId)
          val _tmpCatalogueItemName: String
          _tmpCatalogueItemName = _stmt.getText(_columnIndexOfCatalogueItemName)
          val _tmpCostBookmarks: Int
          _tmpCostBookmarks = _stmt.getLong(_columnIndexOfCostBookmarks).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpTrackingCode: String
          _tmpTrackingCode = _stmt.getText(_columnIndexOfTrackingCode)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpFulfilledAt: Long?
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt)
          }
          _item =
              RedemptionRequestEntity(_tmpId,_tmpUserId,_tmpUserName,_tmpUserEmail,_tmpCatalogueItemId,_tmpCatalogueItemName,_tmpCostBookmarks,_tmpCategory,_tmpStatus,_tmpNotes,_tmpShippingAddress,_tmpTrackingCode,_tmpCreatedAt,_tmpFulfilledAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRedemptionRequestsForUser(userId: String):
      Flow<List<RedemptionRequestEntity>> {
    val _sql: String = "SELECT * FROM redemption_requests WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("redemption_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserEmail: Int = getColumnIndexOrThrow(_stmt, "userEmail")
        val _columnIndexOfCatalogueItemId: Int = getColumnIndexOrThrow(_stmt, "catalogueItemId")
        val _columnIndexOfCatalogueItemName: Int = getColumnIndexOrThrow(_stmt, "catalogueItemName")
        val _columnIndexOfCostBookmarks: Int = getColumnIndexOrThrow(_stmt, "costBookmarks")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfShippingAddress: Int = getColumnIndexOrThrow(_stmt, "shippingAddress")
        val _columnIndexOfTrackingCode: Int = getColumnIndexOrThrow(_stmt, "trackingCode")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfFulfilledAt: Int = getColumnIndexOrThrow(_stmt, "fulfilledAt")
        val _result: MutableList<RedemptionRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RedemptionRequestEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserEmail: String
          _tmpUserEmail = _stmt.getText(_columnIndexOfUserEmail)
          val _tmpCatalogueItemId: String
          _tmpCatalogueItemId = _stmt.getText(_columnIndexOfCatalogueItemId)
          val _tmpCatalogueItemName: String
          _tmpCatalogueItemName = _stmt.getText(_columnIndexOfCatalogueItemName)
          val _tmpCostBookmarks: Int
          _tmpCostBookmarks = _stmt.getLong(_columnIndexOfCostBookmarks).toInt()
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          val _tmpShippingAddress: String
          _tmpShippingAddress = _stmt.getText(_columnIndexOfShippingAddress)
          val _tmpTrackingCode: String
          _tmpTrackingCode = _stmt.getText(_columnIndexOfTrackingCode)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpFulfilledAt: Long?
          if (_stmt.isNull(_columnIndexOfFulfilledAt)) {
            _tmpFulfilledAt = null
          } else {
            _tmpFulfilledAt = _stmt.getLong(_columnIndexOfFulfilledAt)
          }
          _item =
              RedemptionRequestEntity(_tmpId,_tmpUserId,_tmpUserName,_tmpUserEmail,_tmpCatalogueItemId,_tmpCatalogueItemName,_tmpCostBookmarks,_tmpCategory,_tmpStatus,_tmpNotes,_tmpShippingAddress,_tmpTrackingCode,_tmpCreatedAt,_tmpFulfilledAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setAuthorPro(userId: String, isPro: Boolean) {
    val _sql: String = "UPDATE users SET isAuthorPro = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isPro) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun toggleJoinPublicClub(clubId: String, joined: Boolean) {
    val _sql: String =
        "UPDATE public_clubs SET isJoined = ?, memberCount = memberCount + (CASE WHEN ? = 1 THEN 1 ELSE -1 END) WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (joined) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        val _tmp_1: Int = if (joined) 1 else 0
        _stmt.bindLong(_argIndex, _tmp_1.toLong())
        _argIndex = 3
        _stmt.bindText(_argIndex, clubId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateVoiceRoomState(
    clubId: String,
    active: Boolean,
    listeners: Int,
  ) {
    val _sql: String =
        "UPDATE public_clubs SET isVoiceRoomActive = ?, activeVoiceListeners = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (active) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, listeners.toLong())
        _argIndex = 3
        _stmt.bindText(_argIndex, clubId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markArcApplied(arcId: String) {
    val _sql: String = "UPDATE arc_clubs SET isApplied = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markArcApproved(arcId: String) {
    val _sql: String =
        "UPDATE arc_clubs SET isApproved = 1, slotsFilled = slotsFilled + 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markArcDownloaded(arcId: String) {
    val _sql: String = "UPDATE arc_clubs SET hasDownloaded = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markArcReviewSubmitted(arcId: String) {
    val _sql: String = "UPDATE arc_clubs SET isReviewSubmitted = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, arcId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateApplicationStatus(applicationId: String,
      status: ApplicationStatus) {
    val _sql: String = "UPDATE arc_applications SET status = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, __ApplicationStatus_enumToString(status))
        _argIndex = 2
        _stmt.bindText(_argIndex, applicationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAmazonPosted(reviewId: String) {
    val _sql: String = "UPDATE arc_reviews SET amazonPosted = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markGoodreadsPosted(reviewId: String) {
    val _sql: String = "UPDATE arc_reviews SET goodreadsPosted = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reviewId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementThreadReplies(threadId: String) {
    val _sql: String = "UPDATE club_threads SET replyCount = replyCount + 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, threadId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAllNotificationsAsRead() {
    val _sql: String = "UPDATE notifications SET isRead = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markNotificationAsRead(id: String) {
    val _sql: String = "UPDATE notifications SET isRead = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteFollow(authorId: String, followerId: String) {
    val _sql: String = "DELETE FROM follows WHERE authorUserId = ? AND followerUserId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
        _argIndex = 2
        _stmt.bindText(_argIndex, followerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateReadingProgressLastPage(
    bookId: String,
    lastPage: Int,
    timestamp: Long,
  ) {
    val _sql: String =
        "UPDATE reading_progress SET last_page = ?, last_read_timestamp = ? WHERE book_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, lastPage.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, bookId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteReadingProgress(bookId: String) {
    val _sql: String = "DELETE FROM reading_progress WHERE book_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSavedBookCategory(
    id: String,
    category: String,
    syncedAt: Long,
    syncState: String,
  ) {
    val _sql: String =
        "UPDATE saved_books SET category = ?, cloudSyncedAt = ?, syncState = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        _argIndex = 2
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, syncState)
        _argIndex = 4
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBooksCategoryByOldCategory(
    userId: String,
    oldCategory: String,
    newCategory: String,
  ) {
    val _sql: String = "UPDATE saved_books SET category = ? WHERE userId = ? AND category = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, newCategory)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _argIndex = 3
        _stmt.bindText(_argIndex, oldCategory)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSavedBookNotes(
    id: String,
    notes: String,
    rating: Int,
  ) {
    val _sql: String =
        "UPDATE saved_books SET notes = ?, personalRating = ?, syncState = 'PENDING' WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, notes)
        _argIndex = 2
        _stmt.bindLong(_argIndex, rating.toLong())
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBookSyncStatus(
    id: String,
    syncState: String,
    syncedAt: Long,
  ) {
    val _sql: String = "UPDATE saved_books SET syncState = ?, cloudSyncedAt = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, syncState)
        _argIndex = 2
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSavedBook(id: String) {
    val _sql: String = "DELETE FROM saved_books WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSavedBookByGoogleId(userId: String, googleBooksId: String) {
    val _sql: String = "DELETE FROM saved_books WHERE userId = ? AND googleBooksId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, googleBooksId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCustomShelf(id: String) {
    val _sql: String = "DELETE FROM custom_shelves WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCustomShelfByName(userId: String, name: String) {
    val _sql: String = "DELETE FROM custom_shelves WHERE userId = ? AND name = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, name)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateUserAsClientId(userId: String, asClientId: String) {
    val _sql: String = "UPDATE users SET asClientId = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, asClientId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateRedemptionStatus(
    id: String,
    status: String,
    notes: String,
    trackingCode: String,
    fulfilledAt: Long?,
  ) {
    val _sql: String =
        "UPDATE redemption_requests SET status = ?, notes = ?, trackingCode = ?, fulfilledAt = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindText(_argIndex, notes)
        _argIndex = 3
        _stmt.bindText(_argIndex, trackingCode)
        _argIndex = 4
        if (fulfilledAt == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, fulfilledAt)
        }
        _argIndex = 5
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun flagArcApplicationForAdmin(applicationId: String, flagged: Boolean) {
    val _sql: String = "UPDATE arc_applications SET isFlaggedForAdmin = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (flagged) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, applicationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __UserRole_enumToString(_value: UserRole): String = when (_value) {
    UserRole.READER -> "READER"
    UserRole.AUTHOR -> "AUTHOR"
    UserRole.BOOK_CLUB_MEMBER -> "BOOK_CLUB_MEMBER"
    UserRole.ADMIN -> "ADMIN"
  }

  private fun __ArcStatus_enumToString(_value: ArcStatus): String = when (_value) {
    ArcStatus.OPEN -> "OPEN"
    ArcStatus.CLOSED -> "CLOSED"
    ArcStatus.ARCHIVED -> "ARCHIVED"
  }

  private fun __ApplicationStatus_enumToString(_value: ApplicationStatus): String = when (_value) {
    ApplicationStatus.PENDING -> "PENDING"
    ApplicationStatus.APPROVED -> "APPROVED"
    ApplicationStatus.DECLINED -> "DECLINED"
  }

  private fun __ReviewStatus_enumToString(_value: ReviewStatus): String = when (_value) {
    ReviewStatus.DELIVERED -> "DELIVERED"
    ReviewStatus.REVIEW_SUBMITTED -> "REVIEW_SUBMITTED"
    ReviewStatus.OVERDUE -> "OVERDUE"
  }

  private fun __BroadcastType_enumToString(_value: BroadcastType): String = when (_value) {
    BroadcastType.RELEASE -> "RELEASE"
    BroadcastType.ARC_OPENING -> "ARC_OPENING"
    BroadcastType.PRICE_DROP -> "PRICE_DROP"
    BroadcastType.COVER_REVEAL -> "COVER_REVEAL"
    BroadcastType.ANNOUNCEMENT -> "ANNOUNCEMENT"
  }

  private fun __NotificationType_enumToString(_value: NotificationType): String = when (_value) {
    NotificationType.ARC_APPROVED -> "ARC_APPROVED"
    NotificationType.REVIEW_DEADLINE -> "REVIEW_DEADLINE"
    NotificationType.AUTHOR_BROADCAST -> "AUTHOR_BROADCAST"
    NotificationType.NEW_ARC_GENRE -> "NEW_ARC_GENRE"
    NotificationType.VOICE_ROOM_STARTED -> "VOICE_ROOM_STARTED"
    NotificationType.SHELFMATE_REQUEST -> "SHELFMATE_REQUEST"
    NotificationType.REDEMPTION_FULFILLED -> "REDEMPTION_FULFILLED"
    NotificationType.FLAGGED_ARC_APPLICATION -> "FLAGGED_ARC_APPLICATION"
  }

  private fun __UserRole_stringToEnum(_value: String): UserRole = when (_value) {
    "READER" -> UserRole.READER
    "AUTHOR" -> UserRole.AUTHOR
    "BOOK_CLUB_MEMBER" -> UserRole.BOOK_CLUB_MEMBER
    "ADMIN" -> UserRole.ADMIN
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ArcStatus_stringToEnum(_value: String): ArcStatus = when (_value) {
    "OPEN" -> ArcStatus.OPEN
    "CLOSED" -> ArcStatus.CLOSED
    "ARCHIVED" -> ArcStatus.ARCHIVED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ApplicationStatus_stringToEnum(_value: String): ApplicationStatus = when (_value) {
    "PENDING" -> ApplicationStatus.PENDING
    "APPROVED" -> ApplicationStatus.APPROVED
    "DECLINED" -> ApplicationStatus.DECLINED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __ReviewStatus_stringToEnum(_value: String): ReviewStatus = when (_value) {
    "DELIVERED" -> ReviewStatus.DELIVERED
    "REVIEW_SUBMITTED" -> ReviewStatus.REVIEW_SUBMITTED
    "OVERDUE" -> ReviewStatus.OVERDUE
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __BroadcastType_stringToEnum(_value: String): BroadcastType = when (_value) {
    "RELEASE" -> BroadcastType.RELEASE
    "ARC_OPENING" -> BroadcastType.ARC_OPENING
    "PRICE_DROP" -> BroadcastType.PRICE_DROP
    "COVER_REVEAL" -> BroadcastType.COVER_REVEAL
    "ANNOUNCEMENT" -> BroadcastType.ANNOUNCEMENT
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  private fun __NotificationType_stringToEnum(_value: String): NotificationType = when (_value) {
    "ARC_APPROVED" -> NotificationType.ARC_APPROVED
    "REVIEW_DEADLINE" -> NotificationType.REVIEW_DEADLINE
    "AUTHOR_BROADCAST" -> NotificationType.AUTHOR_BROADCAST
    "NEW_ARC_GENRE" -> NotificationType.NEW_ARC_GENRE
    "VOICE_ROOM_STARTED" -> NotificationType.VOICE_ROOM_STARTED
    "SHELFMATE_REQUEST" -> NotificationType.SHELFMATE_REQUEST
    "REDEMPTION_FULFILLED" -> NotificationType.REDEMPTION_FULFILLED
    "FLAGGED_ARC_APPLICATION" -> NotificationType.FLAGGED_ARC_APPLICATION
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
