package com.shelfmates

import com.shelfmates.data.local.BookShelfCategory
import com.shelfmates.data.local.SavedBookEntity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for Shelfmates book saving and categorization logic.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBookShelfCategoryConstants() {
    assertTrue(BookShelfCategory.ALL_CATEGORIES.contains("Currently Reading"))
    assertTrue(BookShelfCategory.ALL_CATEGORIES.contains("Want to Read"))
    assertTrue(BookShelfCategory.ALL_CATEGORIES.contains("Finished"))
    assertEquals(3, BookShelfCategory.ALL_CATEGORIES.size)
  }

  @Test
  fun testSavedBookEntityCreation() {
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

    val updated = entity.copy(category = BookShelfCategory.FINISHED)
    assertEquals(BookShelfCategory.FINISHED, updated.category)
  }

  @Test
  fun testGoogleBooksApiKeyConfigured() {
    assertTrue("GOOGLE_BOOKS_KEY should be configured in BuildConfig", BuildConfig.GOOGLE_BOOKS_KEY.isNotBlank())
  }
}

