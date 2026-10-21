package com.shelfmates

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  /**
   * The app name comes from the resource, not a literal in this test.
   *
   * This previously hard-coded "Shelfmates" and broke the moment the product was
   * renamed. Asserting against [R.string.app_name] itself would be vacuous, so
   * this instead pins the two properties that actually matter for the launcher's
   * label: it is non-blank, and it is a single line with no stray whitespace
   * that would render as a truncated app name under the icon.
   */
  @Test
  fun `app name is a single non-blank label`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)

    assertTrue("app_name must not be blank", appName.isNotBlank())
    assertTrue("app_name must not contain newlines", !appName.contains("\n"))
    assertEquals("app_name must not be padded with whitespace", appName.trim(), appName)
  }
}
