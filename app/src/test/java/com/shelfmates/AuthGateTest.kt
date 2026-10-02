package com.shelfmates

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.shelfmates.data.model.UserRole
import com.shelfmates.ui.AuthDestination
import com.shelfmates.ui.resolveAuthDestination
import com.shelfmates.ui.screens.LoginScreen
import com.shelfmates.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Regression coverage for the login gate.
 *
 * The gate was the app's hardest blocker: "Continue as Guest" only revealed the
 * role picker and never created a Firebase session, so a guest chose a role,
 * was sent straight back to the sign-in form, and could never reach MainScreen.
 * These tests pin the routing rule, that the guest button actually starts an
 * authentication attempt, and that a chosen role can never stand in for one.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthGateTest {

    // ── The routing rule ───────────────────────────────────────────────────

    @Test
    fun unauthenticatedUserIsSentToSignIn() {
        assertEquals(
            AuthDestination.SIGN_IN,
            resolveAuthDestination(isAuthenticated = false, userRole = null)
        )
    }

    @Test
    fun aRoleWithoutASessionIsNotALogin() {
        // The combination the old inline condition got wrong in the user's
        // favour: holding a role but no Firebase session must not be enough to
        // enter the app, because that role has no uid to be persisted under.
        assertEquals(
            AuthDestination.SIGN_IN,
            resolveAuthDestination(isAuthenticated = false, userRole = UserRole.READER)
        )
    }

    @Test
    fun authenticatedUserWithoutARoleStillHasToChooseOne() {
        assertEquals(
            AuthDestination.ROLE_SELECTION,
            resolveAuthDestination(isAuthenticated = true, userRole = null)
        )
    }

    @Test
    fun authenticatedUserWithARoleReachesTheApp() {
        assertEquals(
            AuthDestination.MAIN,
            resolveAuthDestination(isAuthenticated = true, userRole = UserRole.READER)
        )
        assertEquals(
            AuthDestination.MAIN,
            resolveAuthDestination(isAuthenticated = true, userRole = UserRole.AUTHOR)
        )
        assertEquals(
            AuthDestination.MAIN,
            resolveAuthDestination(isAuthenticated = true, userRole = UserRole.BOOK_CLUB_MEMBER)
        )
    }

    @get:Rule val composeTestRule = createComposeRule()

    // ── The guest route ────────────────────────────────────────────────────

    @Test
    fun tappingContinueAsGuestStartsAnAuthenticationAttempt() {
        var guestCalls = 0

        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(
                    onRoleSelected = {},
                    onGuestSignIn = { guestCalls++ },
                    isAuthenticated = false
                )
            }
        }

        // The regression itself: this button used to only flip local UI state.
        // It must now reach the ViewModel so a real session gets created.
        composeTestRule.onNodeWithText("Continue as Guest")
            .performScrollTo()
            .performClick()

        assertEquals(1, guestCalls)
    }

    @Test
    fun choosingARoleIsNotEnoughOnItsOwn() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = false)
            }
        }

        // With no session there is no uid to save a role under, so the role step
        // must stay unreachable until authentication actually happens.
        composeTestRule.onNodeWithText("Reader").assertDoesNotExist()
        composeTestRule.onNodeWithText("Author").assertDoesNotExist()
    }

    // ── The screens the rule selects between ───────────────────────────────

    @Test
    fun signInStepOffersGuestAndGoogleEntryPoints() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = false)
            }
        }

        // Both must stay reachable: without a guest entry point, a user who
        // cannot complete Google Sign-In has no way into the app at all.
        composeTestRule.onNodeWithText("Continue with Google", substring = true).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue as Guest").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun anAuthenticatedUserLandsOnTheRoleStepNotTheSignInForm() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = true)
            }
        }

        // Holding a session must move the user forward rather than strand them
        // on a credentials form they no longer need to fill in.
        composeTestRule.onNodeWithText("Reader").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Author").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Book Club Member").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue as Guest").assertDoesNotExist()
    }

    @Test
    fun adminIsNotOfferedAsASelectableRole() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = {}, isAuthenticated = true)
            }
        }

        // ADMIN is a privilege claim granted server-side, never self-selected.
        composeTestRule.onNodeWithText("Admin").assertDoesNotExist()
    }

    @Test
    fun selectingARoleInvokesTheCallback() {
        val selected = mutableListOf<UserRole>()

        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(onRoleSelected = { selected += it }, isAuthenticated = true)
            }
        }

        composeTestRule.onNodeWithText("Author").performScrollTo().performClick()

        assertEquals(listOf(UserRole.AUTHOR), selected)
    }

    @Test
    fun anAuthenticationFailureIsVisibleOnTheSignInStep() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(
                    onRoleSelected = {},
                    isAuthenticated = false,
                    errorMessage = "Could not continue as guest."
                )
            }
        }

        // Failures used to be written only to a channel MainScreen renders, and
        // MainScreen is withheld until the gate passes, so the user saw nothing.
        composeTestRule.onNodeWithText("Could not continue as guest.")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
