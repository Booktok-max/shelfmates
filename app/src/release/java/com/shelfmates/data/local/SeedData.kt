package com.shelfmates.data.local

/**
 * Release-build stub for the demo dataset.
 *
 * The real implementation lives in `src/debug/java/com/shelfmates/data/local/`
 * and is therefore NOT compiled into a release APK. This no-op exists so that
 * [AppDatabase]'s seeding call resolves in every variant without a
 * `BuildConfig.DEBUG` branch at the call site — and, more importantly, so that
 * shipping builds cannot repopulate a user's real database with demo rows.
 *
 * Release users start with an empty database and build their own shelf.
 */
object SeedData {
    suspend fun populateDatabase(dao: ShelfmatesDao) {
        // Intentionally empty: no demo data ships in release builds.
    }
}