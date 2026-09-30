package com.shelfmates

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

/**
 * Release-variant Application: installs the reCAPTCHA v3 App Check provider.
 *
 * Deliberately does NOT reference DebugAppCheckProviderFactory. That factory
 * hands out a token App Check accepts unconditionally, and
 * firebase-appcheck-debug is declared as debugImplementation so it is absent
 * from the release classpath entirely. Keeping the reference out of this class
 * means the release build cannot compile against it even by accident.
 *
 * When RECAPTCHA_SITE_KEY is not configured the provider is skipped rather than
 * installed with an empty key. Auth and the Firestore security rules continue
 * to apply; only the app-attestation layer is missing, and the app still starts.
 */
class ReleaseShelfmatesApp : ShelfmatesApp() {

    private companion object {
        const val TAG = "ReleaseShelfmatesApp"
    }

    override fun installAppCheckProvider() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            // No Firebase initialized: App Check has nothing to install into.
            return
        }

        val siteKey = BuildConfig.RECAPTCHA_SITE_KEY.trim()
        if (siteKey.isEmpty()) {
            Log.w(TAG, "App Check skipped: RECAPTCHA_SITE_KEY is not configured")
            return
        }

        try {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                RecaptchaAppCheckProviderFactory.getInstance(siteKey)
            )
            Log.i(TAG, "App Check installed (reCAPTCHA provider)")
        } catch (e: Exception) {
            // Fail soft, for the same reason as the debug variant: a provider
            // problem must not turn into an app that will not launch.
            Log.e(TAG, "App Check not installed: ${e.message}")
        }
    }
}