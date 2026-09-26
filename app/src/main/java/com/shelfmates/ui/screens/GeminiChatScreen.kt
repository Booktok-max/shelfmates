package com.shelfmates.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.remote.ChatMessage
import com.shelfmates.data.remote.ChatbotRole
import com.shelfmates.data.remote.GeminiModel
import com.shelfmates.data.remote.MessageSender
import com.shelfmates.ui.theme.*

/**
 * AI chat screen — no provider/model branding visible to users.
 * Internally uses Gemini but surfaces only as "Ask" or "Chat".
 *
 * Rename the nav call site from GeminiChatScreen → ShelfChatScreen.
 */
@Composable
fun ShelfChatScreen(
    currentUser: UserEntity?,
    chatMessages: List<ChatMessage>,
    isAiGenerating: Boolean,
    selectedModel: GeminiModel,
    selectedRole: ChatbotRole,
    onSendMessage: (String) -> Unit,
    onSelectModel: (GeminiModel) -> Unit,
    onSelectRole: (ChatbotRole) -> Unit,
    onClearChat: () -> Unit,
    onOpenVoiceMode: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    // Scroll to bottom on new message
    LaunchedEffect(chatMessages.size, isAiGenerating) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Analyse the character arcs so far",
        "Spoiler-free chapter summary",
        "3 discussion questions for my book club",
        "Review the pacing and tension",
        "Explain the historical context"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("shelf_chat_screen")
    ) {

        // ── HEADER ──────────────────────────────────────────────────────────
        Surface(
            color = ShelfmatesDeepBlue,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ShelfmatesGold, ShelfmatesCoral))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ShelfmatesNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Literary Reading Companion",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedRole.title,   // role label only — no model name
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenVoiceMode,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ShelfmatesGold.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice mode",
                                tint = ShelfmatesGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onClearChat, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear chat",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Mode selector (no model names exposed) ──────────────────
                Text(
                    text = "READING MODE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShelfmatesGold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Map roles to friendly user-facing labels
                val modes = listOf(
                    ChatbotRole.LITERARY_MENTOR  to "Deep Dive",
                    ChatbotRole.ARC_CRITIC       to "Review Helper",
                    ChatbotRole.SPEED_READER     to "Quick Summary",
                    ChatbotRole.MYSTERY_SLEUTH   to "Mystery Tracker"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(modes) { (role, label) ->
                        val isSelected = selectedRole == role
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ShelfmatesGold else Color.White.copy(alpha = 0.12f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                0.5.dp, Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSelectRole(role)
                                    // Internally map to model tiers — hidden from UI
                                    onSelectModel(
                                        when (role) {
                                            ChatbotRole.LITERARY_MENTOR -> GeminiModel.PRO_PREVIEW
                                            ChatbotRole.ARC_CRITIC      -> GeminiModel.FLASH
                                            ChatbotRole.SPEED_READER    -> GeminiModel.FLASH_LITE
                                            ChatbotRole.MYSTERY_SLEUTH  -> GeminiModel.FLASH
                                        }
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = role.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ShelfmatesNavy else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── MESSAGE THREAD ───────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    ShelfChatWelcome(
                        selectedRole = selectedRole,
                        onPromptClick = onSendMessage
                    )
                }
            }

            items(chatMessages, key = { it.id }) { msg ->
                ShelfChatMessageItem(message = msg)
            }

            if (isAiGenerating) {
                item { ShelfChatTypingIndicator() }
            }
        }

        // ── QUICK PROMPTS ────────────────────────────────────────────────────
        if (chatMessages.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.clickable { onSendMessage(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── INPUT BAR ────────────────────────────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Ask anything about your book...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShelfmatesGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isAiGenerating) {
                            val text = inputText.trim()
                            inputText = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isAiGenerating,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isAiGenerating)
                                ShelfmatesDeepBlue
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isAiGenerating)
                            ShelfmatesGold
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun ShelfChatWelcome(
    selectedRole: ChatbotRole,
    onPromptClick: (String) -> Unit
) {
    val starterPrompts = when (selectedRole) {
        ChatbotRole.LITERARY_MENTOR -> listOf(
            "What makes the protagonist's arc memorable?",
            "How does the author use foreshadowing?",
            "Generate 4 deep discussion questions for my club"
        )
        ChatbotRole.ARC_CRITIC -> listOf(
            "Critique the pacing in the first three chapters",
            "How do I write a 5-star spoiler-free review?",
            "Evaluate the dialogue and character voices"
        )
        ChatbotRole.SPEED_READER -> listOf(
            "Give me a 3-bullet summary of the core conflict",
            "List the major characters and their motivations",
            "What are the top 5 key moments?"
        )
        ChatbotRole.MYSTERY_SLEUTH -> listOf(
            "Track the timeline of events so far",
            "What subtle clues point toward the culprit?",
            "Help me spot plot holes or red herrings"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = selectedRole.icon, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = selectedRole.title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = selectedRole.systemPrompt,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "TRY ASKING:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = ShelfmatesDeepBlue,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            starterPrompts.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onPromptClick(prompt) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ShelfmatesGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelfChatMessageItem(message: ChatMessage) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ShelfmatesDeepBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ShelfmatesGold,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd   = if (isUser) 4.dp  else 16.dp
            ),
            color = if (isUser) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(
                0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            ) else null,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Copy button for AI messages — no model attribution shown
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(
                                    ClipData.newPlainText("Shelf Response", message.text)
                                )
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.text,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ShelfChatTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ShelfmatesDeepBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ShelfmatesGold,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = ShelfmatesGold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Thinking...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}