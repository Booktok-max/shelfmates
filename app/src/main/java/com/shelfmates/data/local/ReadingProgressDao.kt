package com.shelfmates.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for Ebook reading progress.
 * Persists and retrieves reader bookmarks with book_id, last_page, and last_read_timestamp.
 */
@Dao
interface ReadingProgressDao {

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: ReadingProgressEntity)

    @Update
    suspend fun updateReadingProgress(progress: ReadingProgressEntity)

    @Query("UPDATE reading_progress SET last_page = :lastPage, last_read_timestamp = :timestamp WHERE book_id = :bookId")
    suspend fun updateLastPage(bookId: String, lastPage: Int, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM reading_progress WHERE book_id = :bookId")
    suspend fun deleteReadingProgress(bookId: String)

    @Delete
    suspend fun delete(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress")
    suspend fun clearAll()
}
