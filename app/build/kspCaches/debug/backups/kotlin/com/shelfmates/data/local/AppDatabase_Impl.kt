package com.shelfmates.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _shelfmatesDao: Lazy<ShelfmatesDao> = lazy {
    ShelfmatesDao_Impl(this)
  }

  private val _readingProgressDao: Lazy<ReadingProgressDao> = lazy {
    ReadingProgressDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(4,
        "327df762b7f7a5045875fe6841f3b86c", "4283d56b3aff1c833df7234027c70f10") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `role` TEXT NOT NULL, `displayName` TEXT NOT NULL, `email` TEXT NOT NULL, `phone` TEXT NOT NULL, `avatarUrl` TEXT NOT NULL, `bio` TEXT NOT NULL, `genresCsv` TEXT NOT NULL, `amazonAuthorUrl` TEXT NOT NULL, `asClientId` TEXT NOT NULL, `isAuthorPro` INTEGER NOT NULL, `followersCount` INTEGER NOT NULL, `followingCount` INTEGER NOT NULL, `booksCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `books` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `description` TEXT NOT NULL, `genre` TEXT NOT NULL, `amazonUrl` TEXT NOT NULL, `goodreadsUrl` TEXT NOT NULL, `asin` TEXT NOT NULL, `googleBooksId` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `public_clubs` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `genre` TEXT NOT NULL, `description` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `currentBookId` TEXT NOT NULL, `currentBookTitle` TEXT NOT NULL, `currentBookAuthor` TEXT NOT NULL, `currentBookCover` TEXT NOT NULL, `currentBookDescription` TEXT NOT NULL, `currentBookAmazonUrl` TEXT NOT NULL, `currentBookGoodreadsUrl` TEXT NOT NULL, `adminUserId` TEXT NOT NULL, `adminName` TEXT NOT NULL, `memberCount` INTEGER NOT NULL, `isJoined` INTEGER NOT NULL, `isVoiceRoomActive` INTEGER NOT NULL, `activeVoiceListeners` INTEGER NOT NULL, `announcement` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `arc_clubs` (`id` TEXT NOT NULL, `bookId` TEXT NOT NULL, `bookTitle` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `genre` TEXT NOT NULL, `blurb` TEXT NOT NULL, `format` TEXT NOT NULL, `slotLimit` INTEGER NOT NULL, `slotsFilled` INTEGER NOT NULL, `deadlineDate` TEXT NOT NULL, `daysRemaining` INTEGER NOT NULL, `fileUrl` TEXT NOT NULL, `fileSizeMb` REAL NOT NULL, `asin` TEXT NOT NULL, `status` TEXT NOT NULL, `minimumReviewsRequired` INTEGER NOT NULL, `isApplied` INTEGER NOT NULL, `isApproved` INTEGER NOT NULL, `hasDownloaded` INTEGER NOT NULL, `isReviewSubmitted` INTEGER NOT NULL, `bookfunnelLink` TEXT NOT NULL, `autoApprove` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `arc_applications` (`id` TEXT NOT NULL, `arcClubId` TEXT NOT NULL, `bookTitle` TEXT NOT NULL, `readerUserId` TEXT NOT NULL, `readerName` TEXT NOT NULL, `readerAvatarUrl` TEXT NOT NULL, `message` TEXT NOT NULL, `goodreadsUrl` TEXT NOT NULL, `pastReviewsCount` INTEGER NOT NULL, `status` TEXT NOT NULL, `bookfunnelLink` TEXT NOT NULL, `genresCsv` TEXT NOT NULL, `externalPlatforms` TEXT NOT NULL, `isFlaggedForAdmin` INTEGER NOT NULL, `appliedAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `arc_reviews` (`id` TEXT NOT NULL, `arcClubId` TEXT NOT NULL, `bookTitle` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `readerUserId` TEXT NOT NULL, `readerName` TEXT NOT NULL, `readerAvatarUrl` TEXT NOT NULL, `rating` INTEGER NOT NULL, `reviewText` TEXT NOT NULL, `wordCount` INTEGER NOT NULL, `amazonPosted` INTEGER NOT NULL, `goodreadsPosted` INTEGER NOT NULL, `booktokPosted` INTEGER NOT NULL, `platformPosted` TEXT NOT NULL, `status` TEXT NOT NULL, `submittedAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `club_threads` (`id` TEXT NOT NULL, `clubId` TEXT NOT NULL, `clubType` TEXT NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `authorAvatarUrl` TEXT NOT NULL, `replyCount` INTEGER NOT NULL, `likesCount` INTEGER NOT NULL, `isPinned` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `thread_replies` (`id` TEXT NOT NULL, `threadId` TEXT NOT NULL, `userId` TEXT NOT NULL, `userName` TEXT NOT NULL, `userAvatarUrl` TEXT NOT NULL, `body` TEXT NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `broadcasts` (`id` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `authorAvatarUrl` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `actionUrl` TEXT NOT NULL, `sentAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `targetId` TEXT NOT NULL, `isRead` INTEGER NOT NULL, `timeAgo` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `follows` (`id` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `followerUserId` TEXT NOT NULL, `followerName` TEXT NOT NULL, `followerEmail` TEXT NOT NULL, `emailConsent` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `book_logs` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `rating` INTEGER NOT NULL, `notes` TEXT NOT NULL, `dateCompleted` TEXT NOT NULL, `genre` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `reading_progress` (`book_id` TEXT NOT NULL, `last_page` INTEGER NOT NULL, `last_chapter_index` INTEGER NOT NULL, `total_pages_or_chapters` INTEGER NOT NULL, `progress_percent` REAL NOT NULL, `format` TEXT NOT NULL, `last_read_timestamp` INTEGER NOT NULL, PRIMARY KEY(`book_id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bookmark_transactions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `title` TEXT NOT NULL, `note` TEXT NOT NULL, `bookmarksAmount` INTEGER NOT NULL, `type` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `iconEmoji` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bookmark_redemptions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `rewardId` TEXT NOT NULL, `rewardTitle` TEXT NOT NULL, `rewardSubtitle` TEXT NOT NULL, `costBookmarks` INTEGER NOT NULL, `redeemedTimestamp` INTEGER NOT NULL, `redemptionCode` TEXT NOT NULL, `iconEmoji` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `saved_books` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `googleBooksId` TEXT NOT NULL, `title` TEXT NOT NULL, `authors` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT NOT NULL, `genre` TEXT NOT NULL, `pageCount` INTEGER NOT NULL, `rating` REAL NOT NULL, `isbn13` TEXT NOT NULL, `notes` TEXT NOT NULL, `personalRating` INTEGER NOT NULL, `savedAt` INTEGER NOT NULL, `cloudSyncedAt` INTEGER NOT NULL, `syncState` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `custom_shelves` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `iconEmoji` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `cloudSyncedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `as_analytics` (`id` TEXT NOT NULL, `authorId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `asClientId` TEXT NOT NULL, `period` TEXT NOT NULL, `newsletterPlacements` INTEGER NOT NULL, `newsletterSubscribersReached` INTEGER NOT NULL, `tiktokViews` INTEGER NOT NULL, `ytViews` INTEGER NOT NULL, `topVideoUrl` TEXT NOT NULL, `syncedAt` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `redemption_requests` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `userName` TEXT NOT NULL, `userEmail` TEXT NOT NULL, `catalogueItemId` TEXT NOT NULL, `catalogueItemName` TEXT NOT NULL, `costBookmarks` INTEGER NOT NULL, `category` TEXT NOT NULL, `status` TEXT NOT NULL, `notes` TEXT NOT NULL, `shippingAddress` TEXT NOT NULL, `trackingCode` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `fulfilledAt` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '327df762b7f7a5045875fe6841f3b86c')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `books`")
        connection.execSQL("DROP TABLE IF EXISTS `public_clubs`")
        connection.execSQL("DROP TABLE IF EXISTS `arc_clubs`")
        connection.execSQL("DROP TABLE IF EXISTS `arc_applications`")
        connection.execSQL("DROP TABLE IF EXISTS `arc_reviews`")
        connection.execSQL("DROP TABLE IF EXISTS `club_threads`")
        connection.execSQL("DROP TABLE IF EXISTS `thread_replies`")
        connection.execSQL("DROP TABLE IF EXISTS `broadcasts`")
        connection.execSQL("DROP TABLE IF EXISTS `notifications`")
        connection.execSQL("DROP TABLE IF EXISTS `follows`")
        connection.execSQL("DROP TABLE IF EXISTS `book_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `reading_progress`")
        connection.execSQL("DROP TABLE IF EXISTS `bookmark_transactions`")
        connection.execSQL("DROP TABLE IF EXISTS `bookmark_redemptions`")
        connection.execSQL("DROP TABLE IF EXISTS `saved_books`")
        connection.execSQL("DROP TABLE IF EXISTS `custom_shelves`")
        connection.execSQL("DROP TABLE IF EXISTS `as_analytics`")
        connection.execSQL("DROP TABLE IF EXISTS `redemption_requests`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("avatarUrl", TableInfo.Column("avatarUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("bio", TableInfo.Column("bio", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("genresCsv", TableInfo.Column("genresCsv", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("amazonAuthorUrl", TableInfo.Column("amazonAuthorUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("asClientId", TableInfo.Column("asClientId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("isAuthorPro", TableInfo.Column("isAuthorPro", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("followersCount", TableInfo.Column("followersCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("followingCount", TableInfo.Column("followingCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("booksCount", TableInfo.Column("booksCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers,
            _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.shelfmates.data.local.UserEntity).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsBooks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBooks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("genre", TableInfo.Column("genre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("amazonUrl", TableInfo.Column("amazonUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("goodreadsUrl", TableInfo.Column("goodreadsUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("asin", TableInfo.Column("asin", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("googleBooksId", TableInfo.Column("googleBooksId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBooks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBooks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBooks: TableInfo = TableInfo("books", _columnsBooks, _foreignKeysBooks,
            _indicesBooks)
        val _existingBooks: TableInfo = read(connection, "books")
        if (!_infoBooks.equals(_existingBooks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |books(com.shelfmates.data.local.BookEntity).
              | Expected:
              |""".trimMargin() + _infoBooks + """
              |
              | Found:
              |""".trimMargin() + _existingBooks)
        }
        val _columnsPublicClubs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPublicClubs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("genre", TableInfo.Column("genre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookId", TableInfo.Column("currentBookId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookTitle", TableInfo.Column("currentBookTitle", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookAuthor", TableInfo.Column("currentBookAuthor", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookCover", TableInfo.Column("currentBookCover", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookDescription", TableInfo.Column("currentBookDescription",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookAmazonUrl", TableInfo.Column("currentBookAmazonUrl",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("currentBookGoodreadsUrl",
            TableInfo.Column("currentBookGoodreadsUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("adminUserId", TableInfo.Column("adminUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("adminName", TableInfo.Column("adminName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("memberCount", TableInfo.Column("memberCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("isJoined", TableInfo.Column("isJoined", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("isVoiceRoomActive", TableInfo.Column("isVoiceRoomActive",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("activeVoiceListeners", TableInfo.Column("activeVoiceListeners",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("announcement", TableInfo.Column("announcement", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPublicClubs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPublicClubs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPublicClubs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPublicClubs: TableInfo = TableInfo("public_clubs", _columnsPublicClubs,
            _foreignKeysPublicClubs, _indicesPublicClubs)
        val _existingPublicClubs: TableInfo = read(connection, "public_clubs")
        if (!_infoPublicClubs.equals(_existingPublicClubs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |public_clubs(com.shelfmates.data.local.PublicClubEntity).
              | Expected:
              |""".trimMargin() + _infoPublicClubs + """
              |
              | Found:
              |""".trimMargin() + _existingPublicClubs)
        }
        val _columnsArcClubs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArcClubs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("bookId", TableInfo.Column("bookId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("bookTitle", TableInfo.Column("bookTitle", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("genre", TableInfo.Column("genre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("blurb", TableInfo.Column("blurb", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("format", TableInfo.Column("format", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("slotLimit", TableInfo.Column("slotLimit", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("slotsFilled", TableInfo.Column("slotsFilled", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("deadlineDate", TableInfo.Column("deadlineDate", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("daysRemaining", TableInfo.Column("daysRemaining", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("fileUrl", TableInfo.Column("fileUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("fileSizeMb", TableInfo.Column("fileSizeMb", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("asin", TableInfo.Column("asin", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("minimumReviewsRequired", TableInfo.Column("minimumReviewsRequired",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("isApplied", TableInfo.Column("isApplied", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("isApproved", TableInfo.Column("isApproved", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("hasDownloaded", TableInfo.Column("hasDownloaded", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("isReviewSubmitted", TableInfo.Column("isReviewSubmitted", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("bookfunnelLink", TableInfo.Column("bookfunnelLink", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("autoApprove", TableInfo.Column("autoApprove", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcClubs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArcClubs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesArcClubs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoArcClubs: TableInfo = TableInfo("arc_clubs", _columnsArcClubs,
            _foreignKeysArcClubs, _indicesArcClubs)
        val _existingArcClubs: TableInfo = read(connection, "arc_clubs")
        if (!_infoArcClubs.equals(_existingArcClubs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |arc_clubs(com.shelfmates.data.local.ArcClubEntity).
              | Expected:
              |""".trimMargin() + _infoArcClubs + """
              |
              | Found:
              |""".trimMargin() + _existingArcClubs)
        }
        val _columnsArcApplications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArcApplications.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("arcClubId", TableInfo.Column("arcClubId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("bookTitle", TableInfo.Column("bookTitle", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("readerUserId", TableInfo.Column("readerUserId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("readerName", TableInfo.Column("readerName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("readerAvatarUrl", TableInfo.Column("readerAvatarUrl", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("goodreadsUrl", TableInfo.Column("goodreadsUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("pastReviewsCount", TableInfo.Column("pastReviewsCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("bookfunnelLink", TableInfo.Column("bookfunnelLink", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("genresCsv", TableInfo.Column("genresCsv", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("externalPlatforms", TableInfo.Column("externalPlatforms",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("isFlaggedForAdmin", TableInfo.Column("isFlaggedForAdmin",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcApplications.put("appliedAt", TableInfo.Column("appliedAt", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArcApplications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesArcApplications: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoArcApplications: TableInfo = TableInfo("arc_applications", _columnsArcApplications,
            _foreignKeysArcApplications, _indicesArcApplications)
        val _existingArcApplications: TableInfo = read(connection, "arc_applications")
        if (!_infoArcApplications.equals(_existingArcApplications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |arc_applications(com.shelfmates.data.local.ArcApplicationEntity).
              | Expected:
              |""".trimMargin() + _infoArcApplications + """
              |
              | Found:
              |""".trimMargin() + _existingArcApplications)
        }
        val _columnsArcReviews: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArcReviews.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("arcClubId", TableInfo.Column("arcClubId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("bookTitle", TableInfo.Column("bookTitle", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("readerUserId", TableInfo.Column("readerUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("readerName", TableInfo.Column("readerName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("readerAvatarUrl", TableInfo.Column("readerAvatarUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("rating", TableInfo.Column("rating", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("reviewText", TableInfo.Column("reviewText", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("wordCount", TableInfo.Column("wordCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("amazonPosted", TableInfo.Column("amazonPosted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("goodreadsPosted", TableInfo.Column("goodreadsPosted", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("booktokPosted", TableInfo.Column("booktokPosted", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("platformPosted", TableInfo.Column("platformPosted", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArcReviews.put("submittedAt", TableInfo.Column("submittedAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArcReviews: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesArcReviews: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoArcReviews: TableInfo = TableInfo("arc_reviews", _columnsArcReviews,
            _foreignKeysArcReviews, _indicesArcReviews)
        val _existingArcReviews: TableInfo = read(connection, "arc_reviews")
        if (!_infoArcReviews.equals(_existingArcReviews)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |arc_reviews(com.shelfmates.data.local.ArcReviewEntity).
              | Expected:
              |""".trimMargin() + _infoArcReviews + """
              |
              | Found:
              |""".trimMargin() + _existingArcReviews)
        }
        val _columnsClubThreads: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsClubThreads.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("clubId", TableInfo.Column("clubId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("clubType", TableInfo.Column("clubType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("authorAvatarUrl", TableInfo.Column("authorAvatarUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("replyCount", TableInfo.Column("replyCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("likesCount", TableInfo.Column("likesCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("isPinned", TableInfo.Column("isPinned", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClubThreads.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysClubThreads: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesClubThreads: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoClubThreads: TableInfo = TableInfo("club_threads", _columnsClubThreads,
            _foreignKeysClubThreads, _indicesClubThreads)
        val _existingClubThreads: TableInfo = read(connection, "club_threads")
        if (!_infoClubThreads.equals(_existingClubThreads)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |club_threads(com.shelfmates.data.local.ClubThreadEntity).
              | Expected:
              |""".trimMargin() + _infoClubThreads + """
              |
              | Found:
              |""".trimMargin() + _existingClubThreads)
        }
        val _columnsThreadReplies: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsThreadReplies.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("threadId", TableInfo.Column("threadId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("userName", TableInfo.Column("userName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("userAvatarUrl", TableInfo.Column("userAvatarUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadReplies.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysThreadReplies: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesThreadReplies: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoThreadReplies: TableInfo = TableInfo("thread_replies", _columnsThreadReplies,
            _foreignKeysThreadReplies, _indicesThreadReplies)
        val _existingThreadReplies: TableInfo = read(connection, "thread_replies")
        if (!_infoThreadReplies.equals(_existingThreadReplies)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |thread_replies(com.shelfmates.data.local.ThreadReplyEntity).
              | Expected:
              |""".trimMargin() + _infoThreadReplies + """
              |
              | Found:
              |""".trimMargin() + _existingThreadReplies)
        }
        val _columnsBroadcasts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBroadcasts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("authorAvatarUrl", TableInfo.Column("authorAvatarUrl", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("actionUrl", TableInfo.Column("actionUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBroadcasts.put("sentAt", TableInfo.Column("sentAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBroadcasts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBroadcasts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBroadcasts: TableInfo = TableInfo("broadcasts", _columnsBroadcasts,
            _foreignKeysBroadcasts, _indicesBroadcasts)
        val _existingBroadcasts: TableInfo = read(connection, "broadcasts")
        if (!_infoBroadcasts.equals(_existingBroadcasts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |broadcasts(com.shelfmates.data.local.BroadcastEntity).
              | Expected:
              |""".trimMargin() + _infoBroadcasts + """
              |
              | Found:
              |""".trimMargin() + _existingBroadcasts)
        }
        val _columnsNotifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotifications.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("targetId", TableInfo.Column("targetId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("timeAgo", TableInfo.Column("timeAgo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNotifications: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoNotifications: TableInfo = TableInfo("notifications", _columnsNotifications,
            _foreignKeysNotifications, _indicesNotifications)
        val _existingNotifications: TableInfo = read(connection, "notifications")
        if (!_infoNotifications.equals(_existingNotifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notifications(com.shelfmates.data.local.NotificationEntity).
              | Expected:
              |""".trimMargin() + _infoNotifications + """
              |
              | Found:
              |""".trimMargin() + _existingNotifications)
        }
        val _columnsFollows: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFollows.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("authorUserId", TableInfo.Column("authorUserId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("followerUserId", TableInfo.Column("followerUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("followerName", TableInfo.Column("followerName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("followerEmail", TableInfo.Column("followerEmail", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("emailConsent", TableInfo.Column("emailConsent", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFollows: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFollows: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFollows: TableInfo = TableInfo("follows", _columnsFollows, _foreignKeysFollows,
            _indicesFollows)
        val _existingFollows: TableInfo = read(connection, "follows")
        if (!_infoFollows.equals(_existingFollows)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |follows(com.shelfmates.data.local.FollowEntity).
              | Expected:
              |""".trimMargin() + _infoFollows + """
              |
              | Found:
              |""".trimMargin() + _existingFollows)
        }
        val _columnsBookLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookLogs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("author", TableInfo.Column("author", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("rating", TableInfo.Column("rating", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("dateCompleted", TableInfo.Column("dateCompleted", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookLogs.put("genre", TableInfo.Column("genre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBookLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBookLogs: TableInfo = TableInfo("book_logs", _columnsBookLogs,
            _foreignKeysBookLogs, _indicesBookLogs)
        val _existingBookLogs: TableInfo = read(connection, "book_logs")
        if (!_infoBookLogs.equals(_existingBookLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |book_logs(com.shelfmates.data.local.BookLogEntity).
              | Expected:
              |""".trimMargin() + _infoBookLogs + """
              |
              | Found:
              |""".trimMargin() + _existingBookLogs)
        }
        val _columnsReadingProgress: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReadingProgress.put("book_id", TableInfo.Column("book_id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("last_page", TableInfo.Column("last_page", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("last_chapter_index", TableInfo.Column("last_chapter_index",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("total_pages_or_chapters",
            TableInfo.Column("total_pages_or_chapters", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("progress_percent", TableInfo.Column("progress_percent", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("format", TableInfo.Column("format", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReadingProgress.put("last_read_timestamp", TableInfo.Column("last_read_timestamp",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReadingProgress: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReadingProgress: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoReadingProgress: TableInfo = TableInfo("reading_progress", _columnsReadingProgress,
            _foreignKeysReadingProgress, _indicesReadingProgress)
        val _existingReadingProgress: TableInfo = read(connection, "reading_progress")
        if (!_infoReadingProgress.equals(_existingReadingProgress)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |reading_progress(com.shelfmates.data.local.ReadingProgressEntity).
              | Expected:
              |""".trimMargin() + _infoReadingProgress + """
              |
              | Found:
              |""".trimMargin() + _existingReadingProgress)
        }
        val _columnsBookmarkTransactions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookmarkTransactions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("bookmarksAmount", TableInfo.Column("bookmarksAmount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkTransactions.put("iconEmoji", TableInfo.Column("iconEmoji", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookmarkTransactions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBookmarkTransactions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBookmarkTransactions: TableInfo = TableInfo("bookmark_transactions",
            _columnsBookmarkTransactions, _foreignKeysBookmarkTransactions,
            _indicesBookmarkTransactions)
        val _existingBookmarkTransactions: TableInfo = read(connection, "bookmark_transactions")
        if (!_infoBookmarkTransactions.equals(_existingBookmarkTransactions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bookmark_transactions(com.shelfmates.data.local.BookmarkTransactionEntity).
              | Expected:
              |""".trimMargin() + _infoBookmarkTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingBookmarkTransactions)
        }
        val _columnsBookmarkRedemptions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookmarkRedemptions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("rewardId", TableInfo.Column("rewardId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("rewardTitle", TableInfo.Column("rewardTitle", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("rewardSubtitle", TableInfo.Column("rewardSubtitle", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("costBookmarks", TableInfo.Column("costBookmarks",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("redeemedTimestamp", TableInfo.Column("redeemedTimestamp",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("redemptionCode", TableInfo.Column("redemptionCode", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("iconEmoji", TableInfo.Column("iconEmoji", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarkRedemptions.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookmarkRedemptions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBookmarkRedemptions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBookmarkRedemptions: TableInfo = TableInfo("bookmark_redemptions",
            _columnsBookmarkRedemptions, _foreignKeysBookmarkRedemptions,
            _indicesBookmarkRedemptions)
        val _existingBookmarkRedemptions: TableInfo = read(connection, "bookmark_redemptions")
        if (!_infoBookmarkRedemptions.equals(_existingBookmarkRedemptions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bookmark_redemptions(com.shelfmates.data.local.BookmarkRedemptionEntity).
              | Expected:
              |""".trimMargin() + _infoBookmarkRedemptions + """
              |
              | Found:
              |""".trimMargin() + _existingBookmarkRedemptions)
        }
        val _columnsSavedBooks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSavedBooks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("googleBooksId", TableInfo.Column("googleBooksId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("authors", TableInfo.Column("authors", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("genre", TableInfo.Column("genre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("pageCount", TableInfo.Column("pageCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("rating", TableInfo.Column("rating", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("isbn13", TableInfo.Column("isbn13", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("personalRating", TableInfo.Column("personalRating", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("savedAt", TableInfo.Column("savedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("cloudSyncedAt", TableInfo.Column("cloudSyncedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedBooks.put("syncState", TableInfo.Column("syncState", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedBooks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSavedBooks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSavedBooks: TableInfo = TableInfo("saved_books", _columnsSavedBooks,
            _foreignKeysSavedBooks, _indicesSavedBooks)
        val _existingSavedBooks: TableInfo = read(connection, "saved_books")
        if (!_infoSavedBooks.equals(_existingSavedBooks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |saved_books(com.shelfmates.data.local.SavedBookEntity).
              | Expected:
              |""".trimMargin() + _infoSavedBooks + """
              |
              | Found:
              |""".trimMargin() + _existingSavedBooks)
        }
        val _columnsCustomShelves: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCustomShelves.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("iconEmoji", TableInfo.Column("iconEmoji", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("isDefault", TableInfo.Column("isDefault", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomShelves.put("cloudSyncedAt", TableInfo.Column("cloudSyncedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCustomShelves: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCustomShelves: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCustomShelves: TableInfo = TableInfo("custom_shelves", _columnsCustomShelves,
            _foreignKeysCustomShelves, _indicesCustomShelves)
        val _existingCustomShelves: TableInfo = read(connection, "custom_shelves")
        if (!_infoCustomShelves.equals(_existingCustomShelves)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |custom_shelves(com.shelfmates.data.local.CustomShelfEntity).
              | Expected:
              |""".trimMargin() + _infoCustomShelves + """
              |
              | Found:
              |""".trimMargin() + _existingCustomShelves)
        }
        val _columnsAsAnalytics: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAsAnalytics.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("authorId", TableInfo.Column("authorId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("authorName", TableInfo.Column("authorName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("asClientId", TableInfo.Column("asClientId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("period", TableInfo.Column("period", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("newsletterPlacements", TableInfo.Column("newsletterPlacements",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("newsletterSubscribersReached",
            TableInfo.Column("newsletterSubscribersReached", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("tiktokViews", TableInfo.Column("tiktokViews", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("ytViews", TableInfo.Column("ytViews", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("topVideoUrl", TableInfo.Column("topVideoUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAsAnalytics.put("syncStatus", TableInfo.Column("syncStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAsAnalytics: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAsAnalytics: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAsAnalytics: TableInfo = TableInfo("as_analytics", _columnsAsAnalytics,
            _foreignKeysAsAnalytics, _indicesAsAnalytics)
        val _existingAsAnalytics: TableInfo = read(connection, "as_analytics")
        if (!_infoAsAnalytics.equals(_existingAsAnalytics)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |as_analytics(com.shelfmates.data.local.AtomicShelfAnalyticsEntity).
              | Expected:
              |""".trimMargin() + _infoAsAnalytics + """
              |
              | Found:
              |""".trimMargin() + _existingAsAnalytics)
        }
        val _columnsRedemptionRequests: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRedemptionRequests.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("userName", TableInfo.Column("userName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("userEmail", TableInfo.Column("userEmail", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("catalogueItemId", TableInfo.Column("catalogueItemId",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("catalogueItemName", TableInfo.Column("catalogueItemName",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("costBookmarks", TableInfo.Column("costBookmarks", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("category", TableInfo.Column("category", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("shippingAddress", TableInfo.Column("shippingAddress",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("trackingCode", TableInfo.Column("trackingCode", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRedemptionRequests.put("fulfilledAt", TableInfo.Column("fulfilledAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRedemptionRequests: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRedemptionRequests: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRedemptionRequests: TableInfo = TableInfo("redemption_requests",
            _columnsRedemptionRequests, _foreignKeysRedemptionRequests, _indicesRedemptionRequests)
        val _existingRedemptionRequests: TableInfo = read(connection, "redemption_requests")
        if (!_infoRedemptionRequests.equals(_existingRedemptionRequests)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |redemption_requests(com.shelfmates.data.local.RedemptionRequestEntity).
              | Expected:
              |""".trimMargin() + _infoRedemptionRequests + """
              |
              | Found:
              |""".trimMargin() + _existingRedemptionRequests)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "books",
        "public_clubs", "arc_clubs", "arc_applications", "arc_reviews", "club_threads",
        "thread_replies", "broadcasts", "notifications", "follows", "book_logs", "reading_progress",
        "bookmark_transactions", "bookmark_redemptions", "saved_books", "custom_shelves",
        "as_analytics", "redemption_requests")
  }

  public override fun clearAllTables() {
    super.performClear(false, "users", "books", "public_clubs", "arc_clubs", "arc_applications",
        "arc_reviews", "club_threads", "thread_replies", "broadcasts", "notifications", "follows",
        "book_logs", "reading_progress", "bookmark_transactions", "bookmark_redemptions",
        "saved_books", "custom_shelves", "as_analytics", "redemption_requests")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ShelfmatesDao::class, ShelfmatesDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReadingProgressDao::class,
        ReadingProgressDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun shelfmatesDao(): ShelfmatesDao = _shelfmatesDao.value

  public override fun readingProgressDao(): ReadingProgressDao = _readingProgressDao.value
}
