package com.shelfmates

import com.shelfmates.data.local.BookShelfCategory
import com.shelfmates.data.local.SavedBookEntity
import com.shelfmates.data.remote.GoogleBooksService
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Book saving / categorisation and the Google Books API-key policy.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BookshelfTest {

    @Test
    fun shelfCategoriesAreTheExpectedThree() {
        assertEquals(3, BookShelfCategory.ALL_CATEGORIES.size)
        assertEquals(true, BookShelfCategory.ALL_CATEGORIES.contains("Currently Reading"))
        assertEquals(true, BookShelfCategory.ALL_CATEGORIES.contains("Want to Read"))
        assertEquals(true, BookShelfCategory.ALL_CATEGORIES.contains("Finished"))
    }

    @Test
    fun savedBookKeepsItsFieldsAcrossACategoryChange() {
        val entity = SavedBookEntity(
            id = "saved_u1_gbook123",
            userId = "u1",
            googleBooksId = "gbook123",
            title = "Dune",
            authors = "Frank Herbert",
            coverUrl = "https://example.com/cover.jpg",
            category = BookShelfCategory.CURRENTLY_READING,
            description = "Epic sci-fi novel",
            genre = "Science Fiction",
            pageCount = 412,
            rating = 4.7f,
            savedAt = System.currentTimeMillis()
        )

        assertEquals("saved_u1_gbook123", entity.id)
        assertEquals(BookShelfCategory.CURRENTLY_READING, entity.category)
        assertEquals("Dune", entity.title)
        assertEquals("Frank Herbert", entity.authors)

        // Moving shelf must not disturb the book metadata.
        val moved = entity.copy(category = BookShelfCategory.FINISHED)
        assertEquals(BookShelfCategory.FINISHED, moved.category)
        assertEquals("Dune", moved.title)
        assertEquals("Frank Herbert", moved.authors)
        assertEquals(412, moved.pageCount)
    }

    /**
     * The previous version of this suite asserted
     * `assertTrue(BuildConfig.GOOGLE_BOOKS_KEY.isNotBlank())`.
     *
     * That tested the build environment, not the program: it passed in CI only
     * because .env.example supplies the `MY_GOOGLE_BOOKS_KEY` placeholder, and
     * failed for any developer running with a bare `.env`. Deleting it and
     * asserting the behaviour below instead -- which holds either way -- is the
     * fix. See GoogleBooksService.withApiKey.
     */
    @Test
    fun apiKeyPolicyLeavesUnconfiguredBuildsUnchanged() {
        val url = "https://www.googleapis.com/books/v1/volumes?q=dune".toHttpUrl()
        val result = GoogleBooksService.getInstance().withApiKey(url)

        // Whatever BuildConfig holds, the URL must stay parseable and the
        // `q` parameter must survive untouched.
        assertEquals("dune", result.queryParameter("q"))

        // If a key was configured it must be a real parameter; if not, none is
        // added. Asserting the conditional rather than a fixed value keeps this
        // green in CI and on a developer's machine alike.
        val key = result.queryParameter("key")
        if (BuildConfig.GOOGLE_BOOKS_KEY.isBlank()) {
            assertNull("An unconfigured build must not send a key parameter", key)
        } else {
            assertEquals(BuildConfig.GOOGLE_BOOKS_KEY.trim(), key)
        }
    }

    @Test
    fun apiKeyIsNotOverwrittenWhenTheCallerAlreadySuppliedOne() {
        val url = "https://www.googleapis.com/books/v1/volumes?q=dune&key=caller-supplied"
            .toHttpUrl()
        val result = GoogleBooksService.getInstance().withApiKey(url)
        assertEquals("caller-supplied", result.queryParameter("key"))
    }
}