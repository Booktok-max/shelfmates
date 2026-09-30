package com.shelfmates

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

abstract class ShelfmatesApp : Application() {

    private companion object {
        const val TAG = "ShelfmatesApp"
    }

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSafely()
        // Per-variant hook. The debug provider dependency is debug-scoped, so
        // it can only be referenced from src/debug -- see DebugShelfmatesApp
        // and ReleaseShelfmatesApp.
        installAppCheckProvider()
    }

    /**
     * Initializes Firebase from the real `google-services.json`, which the
     * google-services Gradle plugin turns into generated resources.
     *
     * Production boundary: this method NEVER fabricates a Firebase configuration.
     * If the project is not configured, the app fails closed:
     *  - release builds throw, so a misconfigured build can never ship;
     *  - debug builds log an actionable error and continue WITHOUT Firebase, so
     *    [com.shelfmates.data.remote.FirebaseService] reports "not configured"
     *    instead of appearing to be signed in against an invented project.
     */
    private fun initializeFirebaseSafely() {
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            Log.i(TAG, "FirebaseApp already initialized")
            return
        }

        val app = try {
            FirebaseApp.initializeApp(this)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Failed to initialize FirebaseApp: ${e.message}")
            null
        }

        if (app != null) {
            Log.i(TAG, "FirebaseApp initialized successfully with google-services config")
            return
        }

        val message = "Firebase is not configured. Add app/google-services.json for this applicationId " +
            "to enable Firebase Auth and Cloud Firestore."
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "$message Running without Firebase: authentication and cloud sync are disabled.")
        } else {
            throw IllegalStateException(message)
        }
    }

    /**
     * Installs the Firebase App Check provider for this build variant.
     *
     * Implemented separately per source set. The debug provider is only on the
     * debug classpath, so referencing it from src/main would break the release
     * compile. DebugShelfmatesApp uses DebugAppCheckProviderFactory;
     * ReleaseShelfmatesApp uses reCAPTCHA and skips when unconfigured.
     *
     * Both implementations fail soft. App Check sits in front of Auth and the
     * Firestore rules, so failing to install it must not stop the app from
     * starting. The Firebase initialization above still throws in release,
     * because that one genuinely leaves the app unable to authenticate.
     */
    protected abstract fun installAppCheckProvider()
}