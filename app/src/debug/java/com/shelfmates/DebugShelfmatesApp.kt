package com.shelfmates

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

/**
 * Debug-variant Application: installs the Firebase App Check *debug* provider.
 *
 * The debug provider returns a token that App Check accepts only for registered
 * debug tokens, so a debug build cannot be used to attack production. The
 * token it prints must be allowlisted in the App Check console.
 *
 * firebase-appcheck-debug is a debugImplementation dependency, so this class
 * only compiles for debug variants. That is the point: the provider must never
 * reach a release APK. See ReleaseShelfmatesApp for the release counterpart.
 */
class DebugShelfmatesApp : ShelfmatesApp() {

    private companion object {
        const val TAG = "DebugShelfmatesApp"
    }

    override fun installAppCheckProvider() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            // No Firebase initialized: App Check has nothing to install into.
            return
        }

        try {
            FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            Log.i(TAG, "App Check installed (debug provider)")
        } catch (e: Exception) {
            // Fail soft: Auth and the Firestore rules still apply, and the app
            // must start regardless of an attestation-layer problem.
            Log.e(TAG, "App Check not installed: ${e.message}")
        }
    }
}