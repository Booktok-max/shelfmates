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
import com.shelfmates.ui.AuthDestination
import com.shelfmates.ui.resolveAuthDestination
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
                    val isSignedIn = authState.isAuthenticated
                    val isAuthenticating by viewModel.isAuthenticating.collectAsStateWithLifecycle()
                    val authError by viewModel.authErrorMessage.collectAsStateWithLifecycle()

                    LaunchedEffect(isSignedIn) {
                        if (isSignedIn) {
                            viewModel.loadUserRole()
                        }
                    }

                    // MainScreen is reachable only with a real session AND a role.
                    // A role on its own is not a login: it has no uid to be saved
                    // under, so treating it as one would let an unauthenticated
                    // user straight into the app.
                    when (resolveAuthDestination(isSignedIn, userRole)) {
                        AuthDestination.MAIN -> MainScreen(viewModel = viewModel)
                        AuthDestination.SIGN_IN, AuthDestination.ROLE_SELECTION -> LoginScreen(
                            onRoleSelected = { role -> viewModel.setUserRole(role) },
                            onGoogleSignIn = { viewModel.signInWithGoogle(this@MainActivity) },
                            // "Continue as Guest" previously only revealed the role
                            // picker, so no session was ever created.
                            onGuestSignIn = { viewModel.signInAnonymously() },
                            isAuthenticated = isSignedIn,
                            isLoading = isAuthenticating,
                            errorMessage = authError
                        )
                    }
                }
            }
        }
    }
}