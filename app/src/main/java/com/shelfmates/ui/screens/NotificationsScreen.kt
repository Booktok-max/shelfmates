package com.shelfmates.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.NotificationEntity
import com.shelfmates.data.model.NotificationType
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesBlue
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesLightBlue

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (NotificationEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_screen")
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications & Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onMarkAllAsRead,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark all read", fontSize = 12.sp, color = ShelfmatesDeepBlue)
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications right now.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notif ->
                    NotificationCard(
                        notification = notif,
                        onClick = { onNotificationClick(notif) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        NotificationType.ARC_APPROVED -> Icons.Default.CheckCircle
        NotificationType.REVIEW_DEADLINE -> Icons.Default.AccessTime
        NotificationType.VOICE_ROOM_STARTED -> Icons.Default.VolumeUp
        NotificationType.AUTHOR_BROADCAST -> Icons.Default.Campaign
        NotificationType.SHELFMATE_REQUEST -> Icons.Default.PersonAdd
        NotificationType.NEW_ARC_GENRE -> Icons.Default.Notifications
        else -> Icons.Default.Notifications
    }

    val iconColor = when (notification.type) {
        NotificationType.ARC_APPROVED -> ShelfmatesEmerald
        NotificationType.REVIEW_DEADLINE -> ShelfmatesCoral
        NotificationType.VOICE_ROOM_STARTED -> ShelfmatesEmerald
        NotificationType.AUTHOR_BROADCAST -> ShelfmatesAmber
        NotificationType.SHELFMATE_REQUEST -> ShelfmatesBlue
        NotificationType.NEW_ARC_GENRE -> ShelfmatesDeepBlue
        else -> ShelfmatesDeepBlue
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else ShelfmatesLightBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.5.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = notification.timeAgo,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
