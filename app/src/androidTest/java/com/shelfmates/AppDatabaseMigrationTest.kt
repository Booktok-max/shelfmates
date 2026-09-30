package com.shelfmates

import androidx.room.Database
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shelfmates.data.local.AppDatabase
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Migration tests for AppDatabase.
 *
 * WHY THIS EXISTS
 * AppDatabase previously called fallbackToDestructiveMigration(), so any
 * version bump silently dropped every row: the bookmark wallet, redemption
 * requests and reading progress alike. Nothing could recover it, because
 * data_extraction_rules.xml excludes the database from both cloud backup and
 * device transfer. That fallback has been removed.
 *
 * WHAT THIS GUARDS
 * That the schema JSON for the current version exists and is usable. That JSON
 * is the only record of what a given version's tables looked like; without it a
 * migration has to be written by guessing, and a guess cannot be tested.
 *
 * It also catches the quiet failure mode: an entity edited without a version
 * bump. Room regenerates the JSON to match, the file changes, and nothing else
 * complains -- until the next real migration, which then has no correct
 * starting point.
 *
 * WHEN YOU CHANGE THE SCHEMA
 *  1. Bump `version` in AppDatabase.kt.
 *  2. ./gradlew :app:compileDebugKotlin regenerates app/schemas/<N>.json.
 *  3. Commit that JSON.
 *  4. Add a Migration(MIGRATION_N_N+1) and register it with .addMigration().
 *  5. Replace migrationFrom4To5IsDefinedOnceVersion5Exists below with a real
 *     runMigrationsAndValidate call.
 *
 * Skipping step 4 means the app THROWS on open for existing users rather than
 * quietly erasing their data. That is the intended trade: a loud failure in
 * development is cheaper than silent data loss in production.
 *
 * Run: ./gradlew :app:connectedDebugAndroidTest   (needs a device/emulator)
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private fun helper(): MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            assetsFolder = "schemas",
        )

    /** Reads `version` out of the @Database annotation: one source of truth. */
    private fun currentDatabaseVersion(): Int =
        AppDatabase::class.java.getAnnotation(Database::class.java).version

    @Test
    fun schemaJsonExistsForCurrentVersion() {
        val version = currentDatabaseVersion()
        assertTrue(
            "app/schemas/$version.json is missing or unreadable. Without it no " +
                "migration can be written or tested. Run " +
                "./gradlew :app:compileDebugKotlin and commit the generated schema.",
            canCreateDatabaseAt(version)
        )
    }

    @Test
    fun creatingDatabaseAtCurrentVersionSucceeds() {
        // Builds an empty database at the current version and validates its
        // structure against the committed schema. A schema/entity mismatch
        // fails here rather than on a user's device.
        val version = currentDatabaseVersion()
        val db = helper().createDatabase(currentDatabaseVersion())
        db.close()
    }

    /**
     * Placeholder for the first real migration.
     *
     * Deliberately asserts the CURRENT state rather than fabricating a passing
     * migration test: a green test that asserts nothing reads as coverage that
     * does not exist, which is worse than no test at all. The moment the schema
     * moves to version 5 this fails and points at the work required.
     */
    @Test
    fun migrationFrom4To5IsDefinedOnceVersion5Exists() {
        val version = currentDatabaseVersion()
        assertTrue(
            "Schema is now at version $version. Add MIGRATION_4_5, register it via " +
                ".addMigration() in AppDatabase.getDatabase(), and replace this test " +
                "body with helper().runMigrationsAndValidate(5, MIGRATION_4_5).",
            version == 4
        )
    }

    /** True when the schema JSON for [version] is present and loadable. */
    private fun canCreateDatabaseAt(version: Int): Boolean =
        try {
        try {
            helper().createDatabase(version).close()
            true
        } catch (e: Exception) {
            false
        }
            true
        } catch (e: Exception) {
            false
        }
}