package com.shelfmates.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BookEntity::class,
        PublicClubEntity::class,
        ArcClubEntity::class,
        ArcApplicationEntity::class,
        ArcReviewEntity::class,
        ClubThreadEntity::class,
        ThreadReplyEntity::class,
        BroadcastEntity::class,
        NotificationEntity::class,
        FollowEntity::class,
        BookLogEntity::class,
        ReadingProgressEntity::class,
        BookmarkTransactionEntity::class,
        BookmarkRedemptionEntity::class,
        SavedBookEntity::class,
        CustomShelfEntity::class,
        AtomicShelfAnalyticsEntity::class,
        RedemptionRequestEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shelfmatesDao(): ShelfmatesDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shelfmates_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        SeedData.populateDatabase(database.shelfmatesDao())
                    }
                }
            }
        }
    }
}
