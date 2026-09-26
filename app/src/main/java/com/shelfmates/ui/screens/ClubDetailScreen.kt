package com.shelfmates.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.ArcApplicationEntity
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.ArcReviewEntity
import com.shelfmates.data.local.ClubThreadEntity
import com.shelfmates.data.local.PublicClubEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.ApplicationStatus
import com.shelfmates.ui.components.StarRatingDisplay
import com.shelfmates.ui.components.StatusBadge
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy
import com.shelfmates.ui.viewmodel.ActiveVoiceRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicClubDetailScreen(
    club: PublicClubEntity,
    currentUser: UserEntity?,
    threads: List<ClubThreadEntity>,
    activeVoiceRoom: ActiveVoiceRoom?,
    onBack: () -> Unit,
    onToggleJoin: (Boolean) -> Unit,
    onJoinVoiceRoom: () -> Unit,
    onLeaveVoiceRoom: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleHandRaise: () -> Unit,
    onCreateThread: (title: String, body: String, category: String) -> Unit,
    onSelectThread: (String) -> Unit,
    onReadBook: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var isNewThreadComposerOpen by remember { mutableStateOf(false) }
    var newThreadTitle by remember { mutableStateOf("") }
    var newThreadBody by remember { mutableStateOf("") }
    var newThreadCategory by remember { mutableStateOf("Chapter Discussion") }

    val filteredThreads = threads.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    val isThisVoiceActive = activeVoiceRoom?.clubId == club.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(club.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("club_detail_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShelfmatesDeepBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("public_club_detail_scroll"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Club Card
            item {
                Surface(
                    color = ShelfmatesDeepBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(text = club.genre.uppercase(), containerColor = ShelfmatesGold, contentColor = Color.Black)
                            Text(
                                text = "${club.memberCount} Shelfmates",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = club.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = club.description,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Admin: ${club.adminName}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Button(
                                onClick = { onToggleJoin(!club.isJoined) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (club.isJoined) Color.White.copy(alpha = 0.2f) else ShelfmatesGold
                                )
                            ) {
                                Text(
                                    text = if (club.isJoined) "Joined ✓" else "+ Join Club",
                                    color = if (club.isJoined) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Announcement
            if (club.announcement.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = ShelfmatesDeepBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = club.announcement,
                                fontSize = 12.sp,
                                color = ShelfmatesNavy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Current Book Section
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Book Selection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ShelfmatesNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = club.currentBookTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "by ${club.currentBookAuthor}",
                                        fontSize = 12.sp,
                                        color = ShelfmatesDeepBlue
                                    )
                                }
                            }

                            if (club.currentBookDescription.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = club.currentBookDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onReadBook,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("read_current_book_button")
                                ) {
                                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Read in App", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(club.currentBookAmazonUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {}
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesAmber),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buy Amazon", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(club.currentBookGoodreadsUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {}
                                    },
                                    modifier = Modifier.weight(0.9f).height(36.dp)
                                ) {
                                    Text("Goodreads", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Live Voice Room interactive widget
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        text = "Live Audio Read-Along & Discussion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ShelfmatesNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ShelfmatesEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isThisVoiceActive) "CONNECTED TO AUDIO" else "LIVE ROOM AVAILABLE",
                                        color = ShelfmatesEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Max 20 Listeners",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            // Participants Avatars Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(
                                    club.adminName to "Host",
                                    (currentUser?.displayName ?: "You") to "Speaker",
                                    "Priya S." to "Listener",
                                    "Jordan H." to "Listener"
                                ).forEach { (name, role) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(ShelfmatesDeepBlue)
                                                .border(
                                                    width = if (role == "Host") 2.dp else 0.dp,
                                                    color = ShelfmatesGold,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.take(1),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = name.split(" ").firstOrNull() ?: "",
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            if (isThisVoiceActive) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onToggleMute,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (activeVoiceRoom?.isMuted == true) ShelfmatesCoral else ShelfmatesEmerald
                                        ),
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (activeVoiceRoom?.isMuted == true) Icons.Default.MicOff else Icons.Default.Mic,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (activeVoiceRoom?.isMuted == true) "Muted" else "Mute Mic", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = onToggleHandRaise,
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PanTool,
                                            contentDescription = null,
                                            tint = if (activeVoiceRoom?.isHandRaised == true) ShelfmatesGold else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (activeVoiceRoom?.isHandRaised == true) "Hand Raised" else "Raise Hand", color = Color.White, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = onLeaveVoiceRoom,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text("Leave", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onJoinVoiceRoom,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Join Live Audio Discussion", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Discussion Threads Section
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Club Discussion Threads",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        TextButton(onClick = { isNewThreadComposerOpen = !isNewThreadComposerOpen }) {
                            Text(if (isNewThreadComposerOpen) "Close" else "+ New Thread", color = ShelfmatesDeepBlue, fontSize = 12.sp)
                        }
                    }

                    // Thread Category Filter
                    ScrollableTabRow(
                        selectedTabIndex = listOf("All", "Chapter Discussion", "Theories", "Worldbuilding").indexOf(selectedCategory).coerceAtLeast(0),
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        listOf("All", "Chapter Discussion", "Theories", "Worldbuilding").forEach { category ->
                            val isSelected = selectedCategory == category
                            Tab(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                text = {
                                    Text(
                                        text = category,
                                        color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }

                    // Composer Box
                    if (isNewThreadComposerOpen) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Create Discussion Topic", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ShelfmatesNavy)
                                OutlinedTextField(
                                    value = newThreadTitle,
                                    onValueChange = { newThreadTitle = it },
                                    label = { Text("Topic Title") },
                                    placeholder = { Text("e.g. Chapter 6 reaction!") },
                                    modifier = Modifier.fillMaxWidth().testTag("new_thread_title_input")
                                )
                                OutlinedTextField(
                                    value = newThreadBody,
                                    onValueChange = { newThreadBody = it },
                                    label = { Text("Your Thoughts") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth().testTag("new_thread_body_input")
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            if (newThreadTitle.isNotBlank() && newThreadBody.isNotBlank()) {
                                                onCreateThread(newThreadTitle, newThreadBody, newThreadCategory)
                                                newThreadTitle = ""
                                                newThreadBody = ""
                                                isNewThreadComposerOpen = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                        modifier = Modifier.testTag("post_thread_button")
                                    ) {
                                        Text("Post Thread")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Thread List
            if (filteredThreads.isEmpty()) {
                item {
                    Text(
                        text = "No discussion threads in this category yet. Be the first to start one!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            } else {
                items(filteredThreads) { thread ->
                    ThreadCardItem(
                        thread = thread,
                        onClick = { onSelectThread(thread.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThreadCardItem(
    thread: ClubThreadEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("thread_item_${thread.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ShelfmatesLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.authorName.take(1),
                            color = ShelfmatesNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = thread.authorName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${thread.createdAt}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(text = thread.category, containerColor = ShelfmatesLightBlue, contentColor = ShelfmatesNavy)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = thread.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.body,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = ShelfmatesDeepBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${thread.replyCount} Replies",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShelfmatesDeepBlue
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcClubDetailScreen(
    arc: ArcClubEntity,
    currentUser: UserEntity?,
    applications: List<ArcApplicationEntity>,
    reviews: List<ArcReviewEntity>,
    onBack: () -> Unit,
    onOpenApplyDialog: () -> Unit,
    onDownloadFile: () -> Unit,
    onOpenSubmitReviewDialog: () -> Unit,
    onSendReminders: () -> Unit,
    onUpdateAppStatus: (ArcApplicationEntity, ApplicationStatus) -> Unit,
    onReadInApp: () -> Unit = {}
) {
    val context = LocalContext.current
    val isAuthor = currentUser?.id == arc.authorUserId
    val progress = (arc.slotsFilled.toFloat() / arc.slotLimit.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ARC Campaign", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("arc_detail_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShelfmatesDeepBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("arc_club_detail_scroll"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    color = ShelfmatesDeepBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(text = "ADVANCE REVIEW COPY", containerColor = ShelfmatesGold, contentColor = Color.Black)
                            StatusBadge(
                                text = "DUE IN ${arc.daysRemaining} DAYS",
                                containerColor = if (arc.daysRemaining <= 3) ShelfmatesCoral else ShelfmatesAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = arc.bookTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "by ${arc.authorName}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = arc.blurb,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Slots Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Reader Slots: ${arc.slotsFilled} / ${arc.slotLimit} Filled",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = ShelfmatesGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ShelfmatesGold,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            // Specs Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FORMAT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                            Text(arc.format, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ShelfmatesNavy)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DEADLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                            Text(arc.deadlineDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ShelfmatesNavy)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ASIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                            Text(arc.asin, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ShelfmatesNavy)
                        }
                    }
                }
            }

            // AUTHOR VIEW: Campaign Management Suite
            if (isAuthor) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Author Campaign Controls",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            StatusBadge(text = "AUTHOR HOST", containerColor = ShelfmatesDeepBlue)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Author 1-Tap Reminder Push Action Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ShelfmatesNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1-Tap Review Deadline Push",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Send an automatic push notification reminder to all approved readers who have not yet submitted reviews.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onSendReminders,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesGold),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("send_reminders_button")
                                ) {
                                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send 1-Tap Reminder Push", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        // Reader Applications Queue
                        Text(
                            text = "Reader Applications Queue (${applications.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (applications.isEmpty()) {
                    item {
                        Text(
                            text = "No pending reader applications yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(applications) { app ->
                        ApplicationQueueCard(
                            application = app,
                            onApprove = { onUpdateAppStatus(app, ApplicationStatus.APPROVED) },
                            onDecline = { onUpdateAppStatus(app, ApplicationStatus.DECLINED) }
                        )
                    }
                }
            } else {
                // READER VIEW: Step-by-Step ARC Lifecycle
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reader ARC Progression",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 1: Request Access
                        ArcStepCard(
                            stepNumber = 1,
                            title = "Request ARC Access",
                            statusText = if (arc.isApproved) "Approved ✓" else if (arc.isApplied) "Pending Author Approval" else "Access Not Requested",
                            isComplete = arc.isApproved,
                            isActive = !arc.isApplied && !arc.isApproved
                        ) {
                            if (!arc.isApplied && !arc.isApproved) {
                                Button(
                                    onClick = onOpenApplyDialog,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                    modifier = Modifier.fillMaxWidth().testTag("apply_arc_step_button").testTag("request_arc_step_button")
                                ) {
                                    Text("Request Access to ARC")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Step 2: Read & Download Manuscript
                        ArcStepCard(
                            stepNumber = 2,
                            title = "Read ARC Manuscript",
                            statusText = if (arc.hasDownloaded) "Available in Reader ✓" else if (arc.isApproved) "Ready to Read & Download" else "Locked",
                            isComplete = arc.hasDownloaded,
                            isActive = arc.isApproved && !arc.hasDownloaded
                        ) {
                            if (arc.isApproved) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onReadInApp,
                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                        modifier = Modifier.fillMaxWidth().testTag("read_arc_in_app_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open In-App Reader", color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = onDownloadFile,
                                        modifier = Modifier.fillMaxWidth().testTag("download_arc_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (arc.hasDownloaded) "Download File (${arc.format})" else "Download File (${arc.format})")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Step 3: Leave Review
                        ArcStepCard(
                            stepNumber = 3,
                            title = "Submit Honest Review",
                            statusText = if (arc.isReviewSubmitted) "Review Submitted ✓" else if (arc.hasDownloaded) "Ready for Review" else "Locked",
                            isComplete = arc.isReviewSubmitted,
                            isActive = arc.hasDownloaded && !arc.isReviewSubmitted
                        ) {
                            if (arc.hasDownloaded && !arc.isReviewSubmitted) {
                                Button(
                                    onClick = onOpenSubmitReviewDialog,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                    modifier = Modifier.fillMaxWidth().testTag("submit_review_step_button")
                                ) {
                                    Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Submit ARC Review")
                                }
                            }
                        }

                        if (arc.isReviewSubmitted) {
                            Spacer(modifier = Modifier.height(10.dp))
                            // Amazon Deep Link Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ShelfmatesAmber.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ShelfmatesEmerald, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Review Submitted in Shelfmates!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Please copy your review text to Amazon to support the book's launch algorithm.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            val url = "https://amazon.com/review/create-review?asin=${arc.asin}"
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesAmber),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open Amazon Review Page", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submitted Reviews Section
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Submitted ARC Reviews (${reviews.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "No reviews submitted yet for this ARC campaign.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(reviews) { review ->
                    ReviewCardItem(review = review)
                }
            }
        }
    }
}

@Composable
fun ArcStepCard(
    stepNumber: Int,
    title: String,
    statusText: String,
    isComplete: Boolean,
    isActive: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) ShelfmatesLightBlue else MaterialTheme.colorScheme.surface
        ),
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
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isComplete) ShelfmatesEmerald else ShelfmatesDeepBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isComplete) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text(stepNumber.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                StatusBadge(
                    text = statusText,
                    containerColor = if (isComplete) ShelfmatesEmerald else if (isActive) ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant,
                    contentColor = if (isComplete || isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isActive || isComplete) {
                Spacer(modifier = Modifier.height(10.dp))
                content()
            }
        }
    }
}

@Composable
fun ApplicationQueueCard(
    application: ArcApplicationEntity,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ShelfmatesDeepBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(application.readerName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(application.readerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${application.pastReviewsCount} past reviews • ${application.appliedAt}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                StatusBadge(
                    text = application.status.name,
                    containerColor = when (application.status) {
                        ApplicationStatus.APPROVED -> ShelfmatesEmerald
                        ApplicationStatus.DECLINED -> ShelfmatesCoral
                        ApplicationStatus.PENDING -> ShelfmatesAmber
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"${application.message}\"",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (application.status == ApplicationStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Text("Approve Slot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShelfmatesCoral),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Text("Decline", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCardItem(review: ArcReviewEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ShelfmatesLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(review.readerName.take(1), color = ShelfmatesNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(review.readerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(review.submittedAt, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                StarRatingDisplay(rating = review.rating)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.reviewText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (review.amazonPosted) {
                    StatusBadge(text = "Amazon Verified ✓", containerColor = ShelfmatesAmber, contentColor = Color.Black)
                }
                if (review.goodreadsPosted) {
                    StatusBadge(text = "Goodreads ✓", containerColor = ShelfmatesLightBlue, contentColor = ShelfmatesNavy)
                }
            }
        }
    }
}
