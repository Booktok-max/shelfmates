package com.shelfmates.data.repository

import com.shelfmates.data.local.ReadingProgressDao
import com.shelfmates.data.local.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the Room data layer contract for reading progress persistence.
 * Provides specialized operations for saving and retrieving the 'last_page' index mapped to a specific book_id.
 */
interface ReadingProgressRepository {

    /**
     * Observes the last read page index for a given book_id as a reactive Flow.
     * Returns null if no progress record exists yet.
     */
    fun getLastPage(bookId: String): Flow<Int?>

    /**
     * Retrieves the current last read page index for a given book_id synchronously on background coroutine.
     */
    suspend fun getLastPageDirect(bookId: String): Int?

    /**
     * Saves or updates the last_page index mapped to a specific book_id.
     * If an existing record exists, it updates the last_page, last_read_timestamp, and optional metadata.
     * If no record exists, it creates a new ReadingProgressEntity record.
     */
    suspend fun saveLastPage(
        bookId: String,
        lastPage: Int,
        lastChapterIndex: Int = 0,
        totalPagesOrChapters: Int = 0,
        progressPercent: Float = 0f,
        format: String = "EPUB"
    )

    /**
     * Observes the full ReadingProgressEntity for a given book_id.
     */
    fun getReadingProgress(bookId: String): Flow<ReadingProgressEntity?>

    /**
     * Direct one-shot retrieval of the full ReadingProgressEntity for a given book_id.
     */
    suspend fun getReadingProgressDirect(bookId: String): ReadingProgressEntity?

    /**
     * Saves or replaces a complete ReadingProgressEntity in Room.
     */
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    /**
     * Observes all saved reading progress entries across all books, ordered by most recently read.
     */
    fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>>

    /**
     * Deletes reading progress for a specific book_id.
     */
    suspend fun deleteReadingProgress(bookId: String)

    /**
     * Clears all reading progress records.
     */
    suspend fun clearAll()
}

/**
 * Production implementation of [ReadingProgressRepository] backed by Room [ReadingProgressDao].
 */
class ReadingProgressRepositoryImpl(
    private val readingProgressDao: ReadingProgressDao
) : ReadingProgressRepository {

    override fun getLastPage(bookId: String): Flow<Int?> {
        return readingProgressDao.getLastPageIndex(bookId)
    }

    override suspend fun getLastPageDirect(bookId: String): Int? {
        return readingProgressDao.getLastPageIndexDirect(bookId)
    }

    override suspend fun saveLastPage(
        bookId: String,
        lastPage: Int,
        lastChapterIndex: Int,
        totalPagesOrChapters: Int,
        progressPercent: Float,
        format: String
    ) {
        val existing = readingProgressDao.getReadingProgressDirect(bookId)
        val updatedEntity = if (existing != null) {
            existing.copy(
                lastPageIndex = lastPage,
                lastChapterIndex = if (lastChapterIndex > 0) lastChapterIndex else existing.lastChapterIndex,
                totalPagesOrChapters = if (totalPagesOrChapters > 0) totalPagesOrChapters else existing.totalPagesOrChapters,
                progressPercent = if (progressPercent > 0f) progressPercent else {
                    if (existing.totalPagesOrChapters > 0) {
                        (lastPage.toFloat() / existing.totalPagesOrChapters.toFloat()).coerceIn(0f, 1f)
                    } else existing.progressPercent
                },
                format = format,
                lastReadTimestamp = System.currentTimeMillis()
            )
        } else {
            val calcPercent = if (totalPagesOrChapters > 0) {
                (lastPage.toFloat() / totalPagesOrChapters.toFloat()).coerceIn(0f, 1f)
            } else progressPercent

            ReadingProgressEntity(
                bookId = bookId,
                lastPageIndex = lastPage,
                lastChapterIndex = lastChapterIndex,
                totalPagesOrChapters = totalPagesOrChapters,
                progressPercent = calcPercent,
                format = format,
                lastReadTimestamp = System.currentTimeMillis()
            )
        }

        readingProgressDao.saveReadingProgress(updatedEntity)
    }

    override fun getReadingProgress(bookId: String): Flow<ReadingProgressEntity?> {
        return readingProgressDao.getReadingProgress(bookId)
    }

    override suspend fun getReadingProgressDirect(bookId: String): ReadingProgressEntity? {
        return readingProgressDao.getReadingProgressDirect(bookId)
    }

    override suspend fun saveReadingProgress(progress: ReadingProgressEntity) {
        readingProgressDao.saveReadingProgress(progress)
    }

    override fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>> {
        return readingProgressDao.getAllReadingProgress()
    }

    override suspend fun deleteReadingProgress(bookId: String) {
        readingProgressDao.deleteReadingProgress(bookId)
    }

    override suspend fun clearAll() {
        readingProgressDao.clearAll()
    }
}
