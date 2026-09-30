package com.shelfmates.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    // Writes each version's schema JSON to app/schemas/, which is the only
    // reliable record of what a given version's tables actually looked like.
    // Without it a migration has to be written by guessing at the old shape.
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shelfmatesDao(): ShelfmatesDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Application-lifetime scope used exclusively for first-run seeding.
         *
         * This scope is deliberately owned here rather than supplied by the caller.
         * Seeding must complete even if the component that first opened the database
         * is destroyed first; a caller-supplied scope (e.g. a ViewModel's scope) would
         * cancel the seed and leave the app permanently unpopulated. SupervisorJob
         * keeps a seed failure from tearing down the scope.
         */
        private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shelfmates_database"
                )
                // No fallbackToDestructiveMigration(). It silently dropped every
                // row on any version bump -- bookmark balances, redemption requests
                // and reading progress alike -- and data_extraction_rules.xml
                // excludes the database from backup, so nothing could be
                // recovered afterwards.
                //
                // Without it, a version bump that ships without a Migration now
                // throws on open instead of quietly erasing the user's data. That
                // is the intended trade: a loud failure in development is cheaper
                // than silent data loss in production.
                //
                // The next schema change MUST add a Migration here AND a test in
                // app/src/androidTest using MigrationTestHelper against the schema
                // JSONs in app/schemas/.
                .addCallback(DatabaseCallback(appScope))
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
