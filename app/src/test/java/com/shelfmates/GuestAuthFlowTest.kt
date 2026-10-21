package com.shelfmates

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.shelfmates.data.model.UserRole
import com.shelfmates.ui.screens.LoginScreen
import com.shelfmates.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The guest route, as a reader experiences it.
 *
 * The original defect was that "Continue as Guest" only flipped a local UI
 * flag, so no Firebase session was ever created and the auth gate returned the
 * reader to the sign-in form every time. These cover the shape of the fix:
 *
 *  - the button reaches the caller (the real device pass confirms the session it
 *    creates reaches Firebase);
 *  - a session moves the reader FORWARD, to role selection, not back to the
 *    credentials form;
 *  - choosing a role is the last step, and ADMIN is not self-selectable.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GuestAuthFlowTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun tappingGuestInvokesTheAuthenticationCallback() {
        var calls = 0

        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(
                    onRoleSelected = {},
                    onGuestSignIn = { calls++ },
                    isAuthenticated = false
                )
            }
        }

        composeTestRule.onNodeWithText("Continue as Guest")
            .performScrollTo()
            .performClick()

        // Before the fix this callback did not exist and the button was inert.
        assertEquals(1, calls)
    }

    @Test
    fun aSessionAdvancesTheReaderToRoleSelection() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = true)
            }
        }

        // A held session must not return the reader to a form they no longer
        // need to fill in.
        composeTestRule.onNodeWithText("Reader").performScrollTo().assertExists()
        composeTestRule.onNodeWithText("Continue as Guest").assertDoesNotExist()
    }

    @Test
    fun readerCanChooseARoleToFinishJoining() {
        val chosen = mutableListOf<UserRole>()

        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = { chosen += it }, isAuthenticated = true)
            }
        }

        composeTestRule.onNodeWithText("Reader").performScrollTo().performClick()

        assertEquals(listOf(UserRole.READER), chosen)
    }

    @Test
    fun adminIsNeverSelfSelectable() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = true)
            }
        }

        // ADMIN is granted server-side only; offering it here would let any
        // account grant itself staff access, because the client is not a
        // trust boundary.
        composeTestRule.onNodeWithText("Admin").assertDoesNotExist()
    }
}
