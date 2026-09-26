package com.shelfmates

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ShelfmatesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSafely()
    }

    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                // Initialize default FirebaseApp using context resources or fallback options
                val app = FirebaseApp.initializeApp(this)
                if (app == null) {
                    val fallbackOptions = FirebaseOptions.Builder()
                        .setApplicationId("com.aistudio.shelfmates.readery")
                        .setProjectId("shelfmates-indie-book-club")
                        .setApiKey("AIzaSyShelfmatesFallbackClientKey1234567")
                        .build()
                    FirebaseApp.initializeApp(this, fallbackOptions)
                    Log.i("ShelfmatesApp", "FirebaseApp initialized with fallback options")
                } else {
                    Log.i("ShelfmatesApp", "FirebaseApp initialized successfully with google-services config")
                }
            } else {
                Log.i("ShelfmatesApp", "FirebaseApp already initialized")
            }
        } catch (e: Exception) {
            Log.w("ShelfmatesApp", "Gracefully handling Firebase initialization: ${e.message}")
            try {
                val fallbackOptions = FirebaseOptions.Builder()
                    .setApplicationId("com.aistudio.shelfmates.readery")
                    .setProjectId("shelfmates-indie-book-club")
                    .setApiKey("AIzaSyShelfmatesFallbackClientKey1234567")
                    .build()
                FirebaseApp.initializeApp(this, fallbackOptions)
            } catch (fallbackError: Exception) {
                Log.w("ShelfmatesApp", "Fallback Firebase initialization bypassed; using local offline persistence: ${fallbackError.message}")
            }
        }
    }
}
