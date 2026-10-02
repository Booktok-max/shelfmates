package com.shelfmates.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.model.UserRole
import com.shelfmates.ui.theme.*

/**
 * Full-screen login + role selection.
 *
 * Flow:
 *   1. User enters email + password (or taps "Continue as Guest").
 *   2. After auth succeeds the screen shows role cards.
 *   3. On role selection → onRoleSelected(role) is called and MainActivity
 *      navigates to the relevant home screen.
 *
 * Wire up in MainActivity / NavHost:
 *   if (!isLoggedIn) LoginScreen(onRoleSelected = { role -> vm.setUserRole(role) })
 */
@Composable
fun LoginScreen(
    onRoleSelected: (UserRole) -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onGuestSignIn: () -> Unit = {},
    isAuthenticated: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rolePickerRequested by remember { mutableStateOf(false) }

    // The role step opens once a Firebase session exists, not merely because
    // a button was tapped. That is what makes the guest route honest:
    // choosing a role cannot stand in for a completed sign-in, because
    // MainActivity still requires a real session before MainScreen renders.
    val showRolePicker = rolePickerRequested || isAuthenticated

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ShelfmatesDeepBlue, ShelfmatesNavy)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo / wordmark ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ShelfmatesGold),
                contentAlignment = Alignment.Center
            ) {
                Text("📚", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Shelfmates",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontFamily = BookDisplayFont
            )
            Text(
                text = "Your indie reading community",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (!showRolePicker) {
                // ── Auth form ────────────────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = Color.White.copy(alpha = 0.8f)) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, null, tint = ShelfmatesGold)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShelfmatesGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = Color.White.copy(alpha = 0.8f)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null, tint = ShelfmatesGold)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        null,
                                        tint = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShelfmatesGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                color = ShelfmatesCoral,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { rolePickerRequested = true },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ShelfmatesNavy,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Continue",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ShelfmatesNavy
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
                    Text(
                        "  or  ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google sign-in
                OutlinedButton(
                    onClick = onGoogleSignIn,
                    enabled = !isLoading,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color.White.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("🔵  Continue with Google", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onGuestSignIn, enabled = !isLoading) {
                    Text(
                        "Continue as Guest",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }

            } else {
                // ── Role picker ──────────────────────────────────────────────
                Text(
                    text = "I am a…",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = BookDisplayFont
                )
                Text(
                    text = "Choose the experience that fits you.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                val roles = listOf(
                    RoleOption(
                        role = UserRole.READER,
                        icon = Icons.Default.MenuBook,
                        title = "Reader",
                        subtitle = "Request ARCs, join book clubs,\nearn Bookmarks & discover new reads"
                    ),
                    RoleOption(
                        role = UserRole.AUTHOR,
                        icon = Icons.Default.Edit,
                        title = "Author",
                        subtitle = "List ARCs, manage readers,\nview analytics & launch campaigns"
                    ),
                    RoleOption(
                        role = UserRole.BOOK_CLUB_MEMBER,
                        icon = Icons.Default.Groups,
                        title = "Book Club Member",
                        subtitle = "Join reading groups, discuss\nchapters & vote on next reads"
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    roles.forEach { option ->
                        RoleCard(option = option, onSelect = { onRoleSelected(option.role) })
                    }
                }

                // ADMIN is intentionally absent from the picker above. It is a
                // privilege claim, not a user preference: self-selecting it would
                // let any account grant itself staff access, because the client
                // is not a trust boundary. Staff accounts are granted out of
                // band by writing the user document server-side (Firebase
                // console or Admin SDK), and firestore.rules refuses the role
                // from the client on create.
                //
                // Re-adding it here without a corresponding server-side control
                // is a privilege-escalation bug, not a UI regression.
            }
        }
    }
}

// ── Data + sub-composables ───────────────────────────────────────────────────

private data class RoleOption(
    val role: UserRole,
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Composable
private fun RoleCard(option: RoleOption, onSelect: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, Color.White.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShelfmatesGold.copy(alpha = 0.2f))
                    .border(
                        1.dp, ShelfmatesGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = ShelfmatesGold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = option.subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = ShelfmatesGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}