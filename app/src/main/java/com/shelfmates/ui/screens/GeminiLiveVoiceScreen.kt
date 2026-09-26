package com.shelfmates.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.remote.LiveVoiceSessionState
import com.shelfmates.data.remote.LiveVoiceStatus
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesNavy

@Composable
fun GeminiLiveVoiceScreen(
    sessionState: LiveVoiceSessionState,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onToggleTts: () -> Unit,
    onSendPromptDirect: (String) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(sessionState.turns.size, sessionState.currentResponseText) {
        if (sessionState.turns.isNotEmpty()) {
            listState.animateScrollToItem(sessionState.turns.size - 1)
        }
    }

    val starterVoicePrompts = listOf(
        "Who is your favorite character and why?",
        "What are the major plot twists to expect?",
        "Can you quiz me on Chapter 2?",
        "Explain the emotional subtext of the climax"
    )

    // Pulsing animation for active speech
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (sessionState.status == LiveVoiceStatus.LISTENING || sessionState.status == LiveVoiceStatus.SPEAKING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF07192C),
                        ShelfmatesDeepBlue,
                        Color(0xFF0F365A)
                    )
                )
            )
            .testTag("gemini_live_voice_screen")
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live Voice Companion",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ShelfmatesGold,
                            modifier = Modifier.padding(1.dp)
                        ) {
                            Text(
                                text = "LIVE API",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ShelfmatesNavy,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Model: ${sessionState.modelName}",
                        fontSize = 11.sp,
                        color = ShelfmatesGold.copy(alpha = 0.85f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleTts,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (sessionState.isTtsEnabled) ShelfmatesGold.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = if (sessionState.isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Spoken Audio",
                        tint = if (sessionState.isTtsEnabled) ShelfmatesGold else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onClearHistory,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Conversation",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // --- GLOWING ORB VISUALIZER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f),
            contentAlignment = Alignment.Center
        ) {
            // Ripple background circles
            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .scale(pulseScale)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            when (sessionState.status) {
                                LiveVoiceStatus.LISTENING -> ShelfmatesCoral.copy(alpha = 0.35f)
                                LiveVoiceStatus.SPEAKING -> ShelfmatesGold.copy(alpha = 0.4f)
                                LiveVoiceStatus.PROCESSING -> ShelfmatesAmber.copy(alpha = 0.3f)
                                else -> ShelfmatesGold.copy(alpha = 0.15f)
                            },
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }

            // Core Interactive Audio Orb
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            when (sessionState.status) {
                                LiveVoiceStatus.LISTENING -> listOf(ShelfmatesCoral, Color(0xFFFF8E53))
                                LiveVoiceStatus.SPEAKING -> listOf(ShelfmatesGold, ShelfmatesAmber)
                                LiveVoiceStatus.PROCESSING -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                else -> listOf(ShelfmatesDeepBlue, ShelfmatesGold)
                            }
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(ShelfmatesGold, Color.White)),
                        shape = CircleShape
                    )
                    .clickable {
                        if (sessionState.status == LiveVoiceStatus.LISTENING) {
                            onStopListening()
                        } else {
                            onStartListening()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (sessionState.status) {
                            LiveVoiceStatus.LISTENING -> Icons.Default.Mic
                            LiveVoiceStatus.SPEAKING -> Icons.Default.GraphicEq
                            LiveVoiceStatus.PROCESSING -> Icons.Default.AutoAwesome
                            else -> Icons.Default.RecordVoiceOver
                        },
                        contentDescription = "Voice State",
                        tint = if (sessionState.status == LiveVoiceStatus.SPEAKING) ShelfmatesNavy else Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (sessionState.status) {
                            LiveVoiceStatus.LISTENING -> "LISTENING"
                            LiveVoiceStatus.SPEAKING -> "SPEAKING"
                            LiveVoiceStatus.PROCESSING -> "THINKING"
                            LiveVoiceStatus.ERROR -> "ERROR"
                            LiveVoiceStatus.IDLE -> "TAP TO TALK"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = if (sessionState.status == LiveVoiceStatus.SPEAKING) ShelfmatesNavy else Color.White
                    )
                }
            }
        }

        // --- STATUS & LIVE TRANSCRIPT CARD ---
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Topic bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE CONVERSATION TRANSCRIPT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesGold,
                        letterSpacing = 0.5.sp
                    )
                    if (sessionState.status == LiveVoiceStatus.PROCESSING) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = ShelfmatesGold,
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live audio streaming...", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Turns History
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    if (sessionState.turns.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎙️ Real-time Interactive Literary Voice Session",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tap the glowing microphone orb or select a prompt below to have a live spoken book conversation.",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    items(sessionState.turns, key = { it.id }) { turn ->
                        val isUser = turn.speaker == "You"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isUser) ShelfmatesDeepBlue else Color.White.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    if (isUser) ShelfmatesGold.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = turn.speaker,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUser) ShelfmatesGold else ShelfmatesAmber
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = turn.text,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (sessionState.errorMessage != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShelfmatesCoral.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShelfmatesCoral)
                            ) {
                                Text(
                                    text = "⚠️ ${sessionState.errorMessage}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Voice Starter Prompts
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(starterVoicePrompts) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f)),
                            modifier = Modifier.clickable {
                                onSendPromptDirect(prompt)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ShelfmatesGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
