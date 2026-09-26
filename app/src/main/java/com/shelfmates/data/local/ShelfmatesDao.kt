package com.shelfmates.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shelfmates.data.model.ApplicationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfmatesDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = 'AUTHOR' OR role = 'BOTH'")
    fun getAllAuthors(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isAuthorPro = :isPro WHERE id = :userId")
    suspend fun setAuthorPro(userId: String, isPro: Boolean)

    // --- Public Clubs ---
    @Query("SELECT * FROM public_clubs ORDER BY createdAt DESC")
    fun getAllPublicClubs(): Flow<List<PublicClubEntity>>

    @Query("SELECT * FROM public_clubs WHERE isJoined = 1")
    fun getJoinedPublicClubs(): Flow<List<PublicClubEntity>>

    @Query("SELECT * FROM public_clubs WHERE id = :clubId LIMIT 1")
    fun getPublicClubById(clubId: String): Flow<PublicClubEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublicClub(club: PublicClubEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublicClubs(clubs: List<PublicClubEntity>)

    @Query("UPDATE public_clubs SET isJoined = :joined, memberCount = memberCount + (CASE WHEN :joined = 1 THEN 1 ELSE -1 END) WHERE id = :clubId")
    suspend fun toggleJoinPublicClub(clubId: String, joined: Boolean)

    @Query("UPDATE public_clubs SET isVoiceRoomActive = :active, activeVoiceListeners = :listeners WHERE id = :clubId")
    suspend fun updateVoiceRoomState(clubId: String, active: Boolean, listeners: Int)

    // --- ARC Clubs ---
    @Query("SELECT * FROM arc_clubs ORDER BY createdAt DESC")
    fun getAllArcClubs(): Flow<List<ArcClubEntity>>

    @Query("SELECT * FROM arc_clubs WHERE authorUserId = :authorId ORDER BY createdAt DESC")
    fun getArcClubsByAuthor(authorId: String): Flow<List<ArcClubEntity>>

    @Query("SELECT * FROM arc_clubs WHERE isApplied = 1 OR isApproved = 1")
    fun getMyArcReads(): Flow<List<ArcClubEntity>>

    @Query("SELECT * FROM arc_clubs WHERE id = :arcId LIMIT 1")
    fun getArcClubById(arcId: String): Flow<ArcClubEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcClub(arcClub: ArcClubEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcClubs(arcClubs: List<ArcClubEntity>)

    @Query("UPDATE arc_clubs SET isApplied = 1 WHERE id = :arcId")
    suspend fun markArcApplied(arcId: String)

    @Query("UPDATE arc_clubs SET isApproved = 1, slotsFilled = slotsFilled + 1 WHERE id = :arcId")
    suspend fun markArcApproved(arcId: String)

    @Query("UPDATE arc_clubs SET hasDownloaded = 1 WHERE id = :arcId")
    suspend fun markArcDownloaded(arcId: String)

    @Query("UPDATE arc_clubs SET isReviewSubmitted = 1 WHERE id = :arcId")
    suspend fun markArcReviewSubmitted(arcId: String)

    // --- ARC Applications ---
    @Query("SELECT * FROM arc_applications WHERE arcClubId = :arcClubId ORDER BY appliedAt DESC")
    fun getApplicationsForArcClub(arcClubId: String): Flow<List<ArcApplicationEntity>>

    @Query("SELECT * FROM arc_applications ORDER BY appliedAt DESC")
    fun getAllArcApplications(): Flow<List<ArcApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcApplication(application: ArcApplicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcApplications(applications: List<ArcApplicationEntity>)

    @Query("UPDATE arc_applications SET status = :status WHERE id = :applicationId")
    suspend fun updateApplicationStatus(applicationId: String, status: ApplicationStatus)

    // --- ARC Reviews ---
    @Query("SELECT * FROM arc_reviews WHERE arcClubId = :arcClubId ORDER BY submittedAt DESC")
    fun getReviewsForArcClub(arcClubId: String): Flow<List<ArcReviewEntity>>

    @Query("SELECT * FROM arc_reviews WHERE authorUserId = :authorId ORDER BY submittedAt DESC")
    fun getReviewsForAuthor(authorId: String): Flow<List<ArcReviewEntity>>

    @Query("SELECT * FROM arc_reviews WHERE readerUserId = :readerId ORDER BY submittedAt DESC")
    fun getReviewsByReader(readerId: String): Flow<List<ArcReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcReview(review: ArcReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArcReviews(reviews: List<ArcReviewEntity>)

    @Query("UPDATE arc_reviews SET amazonPosted = 1 WHERE id = :reviewId")
    suspend fun markAmazonPosted(reviewId: String)

    @Query("UPDATE arc_reviews SET goodreadsPosted = 1 WHERE id = :reviewId")
    suspend fun markGoodreadsPosted(reviewId: String)

    // --- Club Threads & Replies ---
    @Query("SELECT * FROM club_threads WHERE clubId = :clubId ORDER BY isPinned DESC, createdAt DESC")
    fun getThreadsForClub(clubId: String): Flow<List<ClubThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ClubThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreads(threads: List<ClubThreadEntity>)

    @Query("SELECT * FROM thread_replies WHERE threadId = :threadId ORDER BY createdAt ASC")
    fun getRepliesForThread(threadId: String): Flow<List<ThreadReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreadReply(reply: ThreadReplyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreadReplies(replies: List<ThreadReplyEntity>)

    @Query("UPDATE club_threads SET replyCount = replyCount + 1 WHERE id = :threadId")
    suspend fun incrementThreadReplies(threadId: String)

    // --- Broadcasts ---
    @Query("SELECT * FROM broadcasts ORDER BY sentAt DESC")
    fun getAllBroadcasts(): Flow<List<BroadcastEntity>>

    @Query("SELECT * FROM broadcasts WHERE authorUserId = :authorId ORDER BY sentAt DESC")
    fun getBroadcastsByAuthor(authorId: String): Flow<List<BroadcastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcast(broadcast: BroadcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcasts(broadcasts: List<BroadcastEntity>)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    // --- Follows & List Building ---
    @Query("SELECT * FROM follows WHERE authorUserId = :authorId")
    fun getFollowersForAuthor(authorId: String): Flow<List<FollowEntity>>

    @Query("SELECT * FROM follows WHERE followerUserId = :followerId")
    fun getFollowingsForUser(followerId: String): Flow<List<FollowEntity>>

    @Query("SELECT COUNT(*) FROM follows WHERE authorUserId = :authorId AND followerUserId = :followerId")
    fun isFollowing(authorId: String, followerId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollows(follows: List<FollowEntity>)

    @Query("DELETE FROM follows WHERE authorUserId = :authorId AND followerUserId = :followerId")
    suspend fun deleteFollow(authorId: String, followerId: String)

    // --- Book Logs ---
    @Query("SELECT * FROM book_logs WHERE userId = :userId ORDER BY dateCompleted DESC")
    fun getBookLogs(userId: String): Flow<List<BookLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookLog(log: BookLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookLogs(logs: List<BookLogEntity>)

    @Delete
    suspend fun deleteBookLog(log: BookLogEntity)

    // --- Reading Progress ---
    @Query("SELECT * FROM reading_progress WHERE book_id = :bookId LIMIT 1")
    fun getReadingProgress(bookId: String): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE book_id = :bookId LIMIT 1")
    suspend fun getReadingProgressDirect(bookId: String): ReadingProgressEntity?

    @Query("SELECT last_page FROM reading_progress WHERE book_id = :bookId LIMIT 1")
    fun getLastPageIndex(bookId: String): Flow<Int?>

    @Query("SELECT last_page FROM reading_progress WHERE book_id = :bookId LIMIT 1")
    suspend fun getLastPageIndexDirect(bookId: String): Int?

    @Query("SELECT * FROM reading_progress ORDER BY last_read_timestamp DESC")
    fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    @Query("UPDATE reading_progress SET last_page = :lastPage, last_read_timestamp = :timestamp WHERE book_id = :bookId")
    suspend fun updateReadingProgressLastPage(bookId: String, lastPage: Int, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM reading_progress WHERE book_id = :bookId")
    suspend fun deleteReadingProgress(bookId: String)

    // --- Bookmarks Rewards Ecosystem ---
    @Query("SELECT * FROM bookmark_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBookmarkTransactions(userId: String): Flow<List<BookmarkTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkTransaction(transaction: BookmarkTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkTransactions(transactions: List<BookmarkTransactionEntity>)

    @Query("SELECT * FROM bookmark_redemptions WHERE userId = :userId ORDER BY redeemedTimestamp DESC")
    fun getBookmarkRedemptions(userId: String): Flow<List<BookmarkRedemptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkRedemption(redemption: BookmarkRedemptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkRedemptions(redemptions: List<BookmarkRedemptionEntity>)

    // --- Saved Books (Virtual Bookshelf) ---
    @Query("SELECT * FROM saved_books WHERE userId = :userId ORDER BY savedAt DESC")
    fun getSavedBooks(userId: String): Flow<List<SavedBookEntity>>

    @Query("SELECT * FROM saved_books WHERE userId = :userId ORDER BY savedAt DESC")
    suspend fun getAllSavedBooksDirect(userId: String): List<SavedBookEntity>

    @Query("SELECT * FROM saved_books WHERE userId = :userId AND category = :category ORDER BY savedAt DESC")
    fun getSavedBooksByCategory(userId: String, category: String): Flow<List<SavedBookEntity>>

    @Query("SELECT * FROM saved_books WHERE userId = :userId AND googleBooksId = :googleBooksId LIMIT 1")
    fun getSavedBookByGoogleId(userId: String, googleBooksId: String): Flow<SavedBookEntity?>

    @Query("SELECT * FROM saved_books WHERE userId = :userId AND googleBooksId = :googleBooksId LIMIT 1")
    suspend fun getSavedBookByGoogleIdDirect(userId: String, googleBooksId: String): SavedBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedBook(book: SavedBookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedBooks(books: List<SavedBookEntity>)

    @Query("UPDATE saved_books SET category = :category, cloudSyncedAt = :syncedAt, syncState = :syncState WHERE id = :id")
    suspend fun updateSavedBookCategory(id: String, category: String, syncedAt: Long = System.currentTimeMillis(), syncState: String = "SYNCED")

    @Query("UPDATE saved_books SET category = :newCategory WHERE userId = :userId AND category = :oldCategory")
    suspend fun updateBooksCategoryByOldCategory(userId: String, oldCategory: String, newCategory: String)

    @Query("UPDATE saved_books SET notes = :notes, personalRating = :rating, syncState = 'PENDING' WHERE id = :id")
    suspend fun updateSavedBookNotes(id: String, notes: String, rating: Int)

    @Query("UPDATE saved_books SET syncState = :syncState, cloudSyncedAt = :syncedAt WHERE id = :id")
    suspend fun updateBookSyncStatus(id: String, syncState: String, syncedAt: Long)

    @Query("DELETE FROM saved_books WHERE id = :id")
    suspend fun deleteSavedBook(id: String)

    @Query("DELETE FROM saved_books WHERE userId = :userId AND googleBooksId = :googleBooksId")
    suspend fun deleteSavedBookByGoogleId(userId: String, googleBooksId: String)

    // --- Custom Shelves ---
    @Query("SELECT * FROM custom_shelves WHERE userId = :userId ORDER BY isDefault DESC, createdAt ASC")
    fun getCustomShelves(userId: String): Flow<List<CustomShelfEntity>>

    @Query("SELECT * FROM custom_shelves WHERE userId = :userId ORDER BY isDefault DESC, createdAt ASC")
    suspend fun getCustomShelvesDirect(userId: String): List<CustomShelfEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomShelf(shelf: CustomShelfEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomShelves(shelves: List<CustomShelfEntity>)

    @Query("DELETE FROM custom_shelves WHERE id = :id")
    suspend fun deleteCustomShelf(id: String)

    @Query("DELETE FROM custom_shelves WHERE userId = :userId AND name = :name")
    suspend fun deleteCustomShelfByName(userId: String, name: String)

    // --- Atomic Shelf Stack Analytics (Command Centre Webhook) ---
    @Query("SELECT * FROM as_analytics WHERE authorId = :authorId LIMIT 1")
    fun getAtomicShelfAnalytics(authorId: String): Flow<AtomicShelfAnalyticsEntity?>

    @Query("SELECT * FROM as_analytics ORDER BY syncedAt DESC")
    fun getAllAtomicShelfAnalytics(): Flow<List<AtomicShelfAnalyticsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAtomicShelfAnalytics(analytics: AtomicShelfAnalyticsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAtomicShelfAnalytics(analyticsList: List<AtomicShelfAnalyticsEntity>)

    @Query("UPDATE users SET asClientId = :asClientId WHERE id = :userId")
    suspend fun updateUserAsClientId(userId: String, asClientId: String)

    // --- Redemption Requests (Admin Queue & Fulfillment) ---
    @Query("SELECT * FROM redemption_requests ORDER BY createdAt DESC")
    fun getAllRedemptionRequests(): Flow<List<RedemptionRequestEntity>>

    @Query("SELECT * FROM redemption_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun getRedemptionRequestsForUser(userId: String): Flow<List<RedemptionRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemptionRequest(request: RedemptionRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemptionRequests(requests: List<RedemptionRequestEntity>)

    @Query("UPDATE redemption_requests SET status = :status, notes = :notes, trackingCode = :trackingCode, fulfilledAt = :fulfilledAt WHERE id = :id")
    suspend fun updateRedemptionStatus(id: String, status: String, notes: String, trackingCode: String, fulfilledAt: Long?)

    @Query("UPDATE arc_applications SET isFlaggedForAdmin = :flagged WHERE id = :applicationId")
    suspend fun flagArcApplicationForAdmin(applicationId: String, flagged: Boolean)
}


