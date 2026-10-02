package com.shelfmates.ui

import com.shelfmates.data.model.UserRole

/**
 * Where the authenticated/unauthenticated split sends the user.
 *
 * This rule used to be an inline `if (!isLoggedIn || userRole == null)` in
 * MainActivity. It is the single decision that controls whether anyone can
 * reach the app at all, and as an inline expression it could not be tested:
 * "signed in but no role yet" and "not signed in" both rendered the same
 * screen, so a guest who had picked a role but held no session looked exactly
 * like a user who had not signed in yet.
 */
enum class AuthDestination {
    /** No Firebase session: show the sign-in step. */
    SIGN_IN,

    /** Authenticated but no role chosen yet: show the role step. */
    ROLE_SELECTION,

    /** Authenticated with a role: show the app. */
    MAIN
}

/**
 * Resolves which step of the auth flow the user belongs on.
 *
 * The steps are ordered: a role is only meaningful once a session exists to
 * attach it to, so ROLE_SELECTION requires authentication. A role chosen
 * without a session cannot be persisted (there is no uid to write it under)
 * and must not be treated as a completed login.
 *
 * Any non-null role is sufficient. [UserRole.ADMIN] is refused upstream in
 * ShelfmatesViewModel.setUserRole and again in firestore.rules, so it cannot
 * arrive here from the UI; this function does not re-implement that check.
 */
fun resolveAuthDestination(
    isAuthenticated: Boolean,
    userRole: UserRole?
): AuthDestination = when {
    !isAuthenticated -> AuthDestination.SIGN_IN
    userRole == null -> AuthDestination.ROLE_SELECTION
    else -> AuthDestination.MAIN
}
