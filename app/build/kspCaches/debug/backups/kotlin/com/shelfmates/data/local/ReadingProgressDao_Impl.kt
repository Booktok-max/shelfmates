package com.shelfmates.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Float
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
public class ReadingProgressDao_Impl(
  __db: RoomDatabase,
) : ReadingProgressDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfReadingProgressEntity: EntityInsertAdapter<ReadingProgressEntity>

  private val __deleteAdapterOfReadingProgressEntity:
      EntityDeleteOrUpdateAdapter<ReadingProgressEntity>

  private val __updateAdapterOfReadingProgressEntity:
      EntityDeleteOrUpdateAdapter<ReadingProgressEntity>
  init {
    this.__db = __db
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
    this.__deleteAdapterOfReadingProgressEntity = object :
        EntityDeleteOrUpdateAdapter<ReadingProgressEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `reading_progress` WHERE `book_id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ReadingProgressEntity) {
        statement.bindText(1, entity.bookId)
      }
    }
    this.__updateAdapterOfReadingProgressEntity = object :
        EntityDeleteOrUpdateAdapter<ReadingProgressEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `reading_progress` SET `book_id` = ?,`last_page` = ?,`last_chapter_index` = ?,`total_pages_or_chapters` = ?,`progress_percent` = ?,`format` = ?,`last_read_timestamp` = ? WHERE `book_id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ReadingProgressEntity) {
        statement.bindText(1, entity.bookId)
        statement.bindLong(2, entity.lastPageIndex.toLong())
        statement.bindLong(3, entity.lastChapterIndex.toLong())
        statement.bindLong(4, entity.totalPagesOrChapters.toLong())
        statement.bindDouble(5, entity.progressPercent.toDouble())
        statement.bindText(6, entity.format)
        statement.bindLong(7, entity.lastReadTimestamp)
        statement.bindText(8, entity.bookId)
      }
    }
  }

  public override suspend fun saveReadingProgress(progress: ReadingProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfReadingProgressEntity.insert(_connection, progress)
  }

  public override suspend fun insertOrUpdate(progress: ReadingProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfReadingProgressEntity.insert(_connection, progress)
  }

  public override suspend fun delete(progress: ReadingProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfReadingProgressEntity.handle(_connection, progress)
  }

  public override suspend fun updateReadingProgress(progress: ReadingProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfReadingProgressEntity.handle(_connection, progress)
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

  public override suspend fun updateLastPage(
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

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM reading_progress"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
