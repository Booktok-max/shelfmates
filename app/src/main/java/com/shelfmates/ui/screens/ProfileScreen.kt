package com.shelfmates.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.BookLogEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.BookmarkWallet
import com.shelfmates.data.model.UserRole
import com.shelfmates.data.remote.AuthUserState
import com.shelfmates.ui.components.StarRatingDisplay
import com.shelfmates.ui.components.StatusBadge
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    bookLogs: List<BookLogEntity>,
    bookmarkWallet: BookmarkWallet? = null,
    authState: AuthUserState? = null,
    onSignInWithGoogle: () -> Unit = {},
    onSignInAnonymously: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSwitchUser: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToArcOpportunities: () -> Unit = {},
    onOpenCreateArcDialog: () -> Unit = {},
    onOpenAuthorProModal: () -> Unit,
    onOpenCsvExportModal: () -> Unit,
    onOpenBroadcastDialog: () -> Unit,
    onOpenPromoBookingDialog: () -> Unit,
    onOpenAddBookLogDialog: () -> Unit
) {
    val context = LocalContext.current
    val isAuthor = currentUser?.role == UserRole.AUTHOR

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Surface(
                color = ShelfmatesDeepBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ShelfmatesGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.displayName?.take(1) ?: "U",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser?.displayName ?: "User",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                if (currentUser?.isAuthorPro == true) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ShelfmatesGold)
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            Text(
                                text = currentUser?.email ?: "",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusBadge(
                                text = when (currentUser?.role) {
                                    UserRole.AUTHOR -> "INDIE AUTHOR"
                                    UserRole.READER -> "ARC REVIEWER / READER"
                                    UserRole.BOOK_CLUB_MEMBER -> "BOOK CLUB MEMBER"
                                    UserRole.ADMIN -> "ATOMIC SHELF TEAM"
                                    else -> "MEMBER"
                                },
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        }
                    }

                    if (!currentUser?.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentUser?.bio ?: "",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    if (!currentUser?.amazonAuthorUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUser?.amazonAuthorUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = ShelfmatesGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Amazon Author Central Page",
                                fontSize = 11.sp,
                                color = ShelfmatesGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followersCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text("Followers", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.followingCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text("Following", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentUser?.booksCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(if (isAuthor) "Published" else "Books Read", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        // BOOKMARKS REWARDS CARD
        item {
            val wallet = bookmarkWallet ?: BookmarkWallet()
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("profile_bookmarks_rewards_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔖", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Bookmarks Rewards",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${wallet.tier.tierName} ${wallet.tier.badgeSymbol} · ${wallet.tier.multiplierText} points",
                                    fontSize = 11.sp,
                                    color = ShelfmatesDeepBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ShelfmatesAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${wallet.balance} 🔖",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Redeem ARCs, book swag, badges & author boosts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onNavigateToBookmarks,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Rewards Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // INDIE AUTHOR ARC OPPORTUNITIES CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("profile_arc_opportunities_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📖", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Indie Author ARC Platform",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "List opportunities & manage reader requests",
                                    fontSize = 11.sp,
                                    color = ShelfmatesDeepBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ShelfmatesLightBlue
                        ) {
                            Text(
                                text = "INDIE HUB",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = ShelfmatesDeepBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Are you an indie author launching a new book? List ARC opportunities and review access requests from vetted reviewers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToArcOpportunities,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).height(38.dp)
                        ) {
                            Text("Open ARC Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenCreateArcDialog,
                            border = BorderStroke(1.dp, ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("+ List ARC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                        }
                    }
                }
            }
        }

        // Demo Persona Switcher Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = ShelfmatesDeepBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Persona Switcher",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Test the app across different author & reader user roles:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val personas = listOf(
                            "user_chloe" to "Chloe (PRD Author)",
                            "user_sarah" to "Sarah (PRD Reader)",
                            "user_ray" to "Ray (Indie Author)",
                            "user_priya" to "Priya (ARC Reader)",
                            "user_jamie" to "Jamie (Casual)",
                            "user_elena" to "Elena (Pro VIP)"
                        )
                        items(personas) { (userId, label) ->
                            val isSelected = currentUser?.id == userId
                            Surface(
                                onClick = { onSwitchUser(userId) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, ShelfmatesGold) else null,
                                modifier = Modifier.testTag("switch_persona_$userId")
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // FIREBASE AUTH & CLOUD FIRESTORE STATUS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("firebase_auth_sync_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ShelfmatesEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firebase Cloud Integration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ShelfmatesDeepBlue
                            )
                        }

                        Surface(
                            color = ShelfmatesEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (authState?.isAuthenticated == true) "AUTHENTICATED" else "GUEST SYNC",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = authState?.syncStatusMessage ?: "Reading progress & book logs synced with Firebase Firestore.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (authState?.email != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Account: ${authState.email}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShelfmatesDeepBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSignInWithGoogle,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("firebase_google_signin_button")
                        ) {
                            Text(
                                text = if (authState?.isAuthenticated == true) "Switch Google Account" else "Sign in with Google",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (authState?.isAuthenticated == true) {
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Sign Out", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // AUTHOR TOOLS (If user is author)
        if (isAuthor) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Author Pro & List-Building Suite",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Pro Subscription Status
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentUser?.isAuthorPro == true) ShelfmatesLightBlue else ShelfmatesAmber.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ShelfmatesGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentUser?.isAuthorPro == true) "Author Pro Active" else "Upgrade to Author Pro",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ShelfmatesNavy
                                )
                                Text(
                                    text = if (currentUser?.isAuthorPro == true)
                                        "Unlimited ARC slots, direct broadcasts & list building"
                                    else
                                        "$19.99/mo • Unlock unlimited ARC reviewers & CSV exports",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (currentUser?.isAuthorPro != true) {
                                Button(
                                    onClick = onOpenAuthorProModal,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                    modifier = Modifier.height(34.dp).testTag("upgrade_pro_banner_button")
                                ) {
                                    Text("Upgrade", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direct List Building & CSV Export
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ShelfmatesDeepBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Direct Reader List Building", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                StatusBadge(text = "GDPR COMPLIANT", containerColor = ShelfmatesEmerald)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Every reader who follows your profile can opt in to your email updates. Export clean CSV files anytime for your MailerLite or ConvertKit newsletter.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onOpenCsvExportModal,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("export_csv_button")
                                ) {
                                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export List (CSV)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = onOpenBroadcastDialog,
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("broadcast_followers_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = ShelfmatesDeepBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Broadcast", fontSize = 11.sp, color = ShelfmatesDeepBlue)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Atomic Shelf Launch Services
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = ShelfmatesAmber)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Atomic Shelf Promo Packages", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Newsletter blasts (45k readers), BookTok reviews & sticky club spots", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = onOpenPromoBookingDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesAmber),
                                modifier = Modifier.height(34.dp).testTag("book_promo_button")
                            ) {
                                Text("Explore", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Atomic Shelf Stack Infrastructure & Webhook Telemetry
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth().testTag("atomic_shelf_telemetry_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ShelfmatesEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Atomic Shelf Stack Telemetry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                StatusBadge(text = "WEBHOOK 200 OK", containerColor = ShelfmatesEmerald)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("API Endpoint", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("v1/webhooks/atomic-shelf", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ShelfmatesDeepBlue)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Latency", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("38 ms", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShelfmatesEmerald)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Subscribed Readers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("45,200 Sci-Fi / Fantasy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Partner Reach", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("180k+ Impressions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShelfmatesAmber)
                                }
                            }
                        }
                    }
                }
            }
        }

        // READER READING LOG
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = ShelfmatesDeepBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Reading Log (${bookLogs.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    TextButton(onClick = onOpenAddBookLogDialog) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Book", fontSize = 12.sp, color = ShelfmatesDeepBlue)
                    }
                }
            }
        }

        if (bookLogs.isEmpty()) {
            item {
                Text(
                    text = "No finished books in your reading log yet.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(bookLogs) { log ->
                BookLogCardItem(log = log)
            }
        }
    }
}

@Composable
fun BookLogCardItem(log: BookLogEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ShelfmatesLightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = ShelfmatesDeepBlue)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(log.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    StarRatingDisplay(rating = log.rating, starSize = 14)
                }
                Text("by ${log.author} • ${log.genre}", fontSize = 11.sp, color = ShelfmatesDeepBlue)
                if (log.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(log.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Text("Completed ${log.dateCompleted}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}