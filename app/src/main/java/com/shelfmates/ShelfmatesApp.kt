package com.shelfmates

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ShelfmatesApp : Application() {

    private companion object {
        const val TAG = "ShelfmatesApp"
    }

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSafely()
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
}
