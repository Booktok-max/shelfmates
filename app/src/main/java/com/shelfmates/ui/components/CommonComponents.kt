package com.shelfmates.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.UserEntity
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesNavy
import com.shelfmates.ui.viewmodel.ActiveVoiceRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfmatesTopAppBar(
    currentUser: UserEntity?,
    unreadNotifCount: Int,
    activeVoiceRoom: ActiveVoiceRoom?,
    bookmarkBalance: Int = 740,
    onSwitchUser: (String) -> Unit,
    onBookmarksRewardsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit
) {
    var showUserMenu by remember { mutableStateOf(false) }

    Surface(
        color = ShelfmatesDeepBlue,
        shadowElevation = 6.dp
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✦ SHELFMATES ✦",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.4.sp,
                                    fontFamily = BookDisplayFont
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ShelfmatesGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GUILD",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        Text(
                            text = "A Readers & Authors Guild",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = BookDisplayFont
                            ),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShelfmatesDeepBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    // Bookmarks Points Pill
                    Surface(
                        onClick = onBookmarksRewardsClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFBBF24),
                        modifier = Modifier
                            .testTag("top_bookmarks_pill")
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔖", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$bookmarkBalance",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Persona Switcher Chip
                    Box {
                        Surface(
                            onClick = { showUserMenu = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .testTag("persona_switcher_button")
                                .padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Persona",
                                    tint = ShelfmatesGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Ray",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false }
                        ) {
                            Text(
                                text = "  SWITCH DEMO PERSONA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp)
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Ray K. Vance", fontWeight = FontWeight.Bold)
                                        Text("Indie Author + Reader (ARC Manager)", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    onSwitchUser("user_ray")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Priya Sharma", fontWeight = FontWeight.Bold)
                                        Text("Top ARC Reviewer (4-6 books/mo)", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    onSwitchUser("user_priya")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Jamie Miller", fontWeight = FontWeight.Bold)
                                        Text("Book Club Reader (Casual)", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    onSwitchUser("user_jamie")
                                    showUserMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Elena Vance", fontWeight = FontWeight.Bold)
                                        Text("Sci-Fi Indie Author (Author Pro)", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    onSwitchUser("user_elena")
                                    showUserMenu = false
                                }
                            )
                        }
                    }

                    // Notification Icon with Badge
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.testTag("notifications_icon_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(containerColor = ShelfmatesCoral) {
                                        Text(unreadNotifCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun VoiceRoomLiveBar(
    voiceRoom: ActiveVoiceRoom,
    onToggleMute: () -> Unit,
    onToggleHandRaise: () -> Unit,
    onLeaveRoom: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        color = ShelfmatesNavy,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_room_live_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(ShelfmatesEmerald)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LIVE AUDIO ROOM",
                            color = ShelfmatesEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${voiceRoom.participants.size} inside",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = voiceRoom.clubName,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Hand Raise button
                IconButton(
                    onClick = onToggleHandRaise,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Raise Hand",
                        tint = if (voiceRoom.isHandRaised) ShelfmatesGold else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Mic mute button
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (voiceRoom.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Mute",
                        tint = if (voiceRoom.isMuted) ShelfmatesCoral else ShelfmatesEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Leave room button
                IconButton(
                    onClick = onLeaveRoom,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Leave Voice Room",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GenreChipRow(
    genres: List<String>,
    selectedGenre: String,
    onSelectGenre: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            val isSelected = genre == selectedGenre
            FilterChip(
                selected = isSelected,
                onClick = { onSelectGenre(genre) },
                label = {
                    Text(
                        text = genre,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ShelfmatesDeepBlue,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("genre_chip_$genre")
            )
        }
    }
}

@Composable
fun StarRatingDisplay(
    rating: Int,
    maxRating: Int = 5,
    starSize: Int = 16
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = ShelfmatesGold,
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}

@Composable
fun InteractiveRatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            IconButton(
                onClick = { onRatingChange(i) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = "$i Stars",
                    tint = if (i <= rating) ShelfmatesGold else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color = Color.White
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun BookOrnamentalDivider(
    modifier: Modifier = Modifier,
    ornament: String = "❦   ·   §   ·   ❧",
    color: Color = ShelfmatesDeepBlue.copy(alpha = 0.4f)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color.copy(alpha = 0.25f))
        )
        Text(
            text = "  $ornament  ",
            fontFamily = BookDisplayFont,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color.copy(alpha = 0.25f))
        )
    }
}

@Composable
fun BookishWaxSealBadge(
    symbol: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = ShelfmatesDeepBlue
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color,
        border = BorderStroke(1.dp, ShelfmatesGold),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

