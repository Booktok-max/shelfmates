package com.shelfmates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shelfmates.ui.screens.LoginScreen
import com.shelfmates.ui.screens.MainScreen
import com.shelfmates.ui.theme.MyApplicationTheme
import com.shelfmates.ui.viewmodel.ShelfmatesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShelfmatesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    val authState by viewModel.firebaseAuthState.collectAsStateWithLifecycle()
                    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
                    val isLoggedIn = authState.isAuthenticated

                    LaunchedEffect(isLoggedIn) {
                        if (isLoggedIn) {
                            viewModel.loadUserRole()
                        }
                    }

                    if (!isLoggedIn || userRole == null) {
                        LoginScreen(
                            onRoleSelected = { role -> viewModel.setUserRole(role) },
                            onGoogleSignIn = { viewModel.signInWithGoogle(this@MainActivity) }
                        )
                    } else {
                        MainScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}