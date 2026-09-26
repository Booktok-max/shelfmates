package com.shelfmates.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.model.BookManuscript
import com.shelfmates.data.model.LineSpacing
import com.shelfmates.data.model.ReaderBookmark
import com.shelfmates.data.model.ReaderFont
import com.shelfmates.data.model.ReaderHighlight
import com.shelfmates.data.model.ReaderSettings
import com.shelfmates.data.model.ReaderTheme
import com.shelfmates.ui.theme.ShelfmatesCrimson
import com.shelfmates.ui.theme.ShelfmatesDarkCrimson
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesRuby
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppReaderScreen(
    manuscript: BookManuscript,
    initialChapterIndex: Int = 0,
    onBack: () -> Unit,
    onSubmitReviewClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var currentChapterIndex by remember { mutableIntStateOf(initialChapterIndex.coerceIn(0, manuscript.chapters.lastIndex)) }
    val currentChapter = manuscript.chapters.getOrNull(currentChapterIndex) ?: manuscript.chapters.first()

    // Reader UI State
    var showControls by remember { mutableStateOf(true) }
    var isSettingsSheetOpen by remember { mutableStateOf(false) }
    var isTocSheetOpen by remember { mutableStateOf(false) }
    var isNotesSheetOpen by remember { mutableStateOf(false) }
    var isAddNoteDialogOpen by remember { mutableStateOf(false) }
    var selectedParagraphForNote by remember { mutableStateOf<String?>(null) }

    // Settings
    var settings by remember {
        mutableStateOf(
            ReaderSettings(
                theme = ReaderTheme.LIGHT,
                font = ReaderFont.SERIF,
                fontSizeSp = 17f,
                lineSpacing = LineSpacing.STANDARD,
                isJustified = true,
                autoScrollSpeed = 0
            )
        )
    }

    // Local bookmarks and highlights
    val bookmarks = remember { mutableStateListOf<ReaderBookmark>() }
    val highlights = remember { mutableStateListOf<ReaderHighlight>() }

    // Auto-scroll loop
    LaunchedEffect(settings.autoScrollSpeed) {
        if (settings.autoScrollSpeed > 0) {
            while (true) {
                delay(if (settings.autoScrollSpeed == 1) 120L else if (settings.autoScrollSpeed == 2) 75L else 45L)
                try {
                    listState.scrollBy(3f)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    // Scroll to top on chapter change
    LaunchedEffect(currentChapterIndex) {
        listState.scrollToItem(0)
    }

    // Theme colors (Optimized for maximum contrast & readability)
    val (bgColor, textColor, surfaceColor, accentColor) = when (settings.theme) {
        ReaderTheme.LIGHT -> ReaderPalette(
            Color(0xFFFFFFFF),
            Color(0xFF0F172A),
            Color(0xFFFFFFFF),
            ShelfmatesCrimson
        )
        ReaderTheme.SEPIA -> ReaderPalette(
            Color(0xFFFAF4EB),
            Color(0xFF2C2216),
            Color(0xFFF3E9D7),
            ShelfmatesDarkCrimson
        )
        ReaderTheme.DARK -> ReaderPalette(
            Color(0xFF09090B),
            Color(0xFFFAFAFA),
            Color(0xFF18181B),
            ShelfmatesRuby
        )
        ReaderTheme.FROSTED -> ReaderPalette(
            Color(0xFFF1F5F9),
            Color(0xFF09090B),
            Color.White,
            ShelfmatesCrimson
        )
    }

    val selectedFontFamily = when (settings.font) {
        ReaderFont.SERIF -> FontFamily.Serif
        ReaderFont.SANS -> FontFamily.SansSerif
        ReaderFont.MONO -> FontFamily.Monospace
    }

    val overallProgress = ((currentChapterIndex.toFloat() + 0.5f) / manuscript.chapters.size.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Surface(
                    color = surfaceColor,
                    shadowElevation = 4.dp
                ) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = manuscript.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    color = textColor
                                )
                                Text(
                                    text = "Ch. ${currentChapter.number}: ${currentChapter.title}",
                                    fontSize = 11.sp,
                                    color = textColor.copy(alpha = 0.7f),
                                    maxLines = 1
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_button")) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = textColor
                                )
                            }
                        },
                        actions = {
                            // Table of Contents
                            IconButton(onClick = { isTocSheetOpen = true }, modifier = Modifier.testTag("reader_toc_button")) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Table of Contents",
                                    tint = textColor
                                )
                            }

                            // Bookmark toggle
                            val isBookmarked = bookmarks.any { it.chapterIndex == currentChapterIndex }
                            IconButton(
                                onClick = {
                                    if (isBookmarked) {
                                        bookmarks.removeAll { it.chapterIndex == currentChapterIndex }
                                    } else {
                                        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                                        bookmarks.add(
                                            ReaderBookmark(
                                                id = "bm_${System.currentTimeMillis()}",
                                                bookId = manuscript.bookId,
                                                chapterIndex = currentChapterIndex,
                                                chapterTitle = currentChapter.title,
                                                paragraphIndex = listState.firstVisibleItemIndex,
                                                snippet = currentChapter.paragraphs.firstOrNull() ?: "",
                                                timestamp = sdf.format(Date())
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("reader_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) ShelfmatesGold else textColor
                                )
                            }

                            // Highlights & Notes
                            IconButton(onClick = { isNotesSheetOpen = true }, modifier = Modifier.testTag("reader_notes_button")) {
                                Icon(
                                    imageVector = Icons.Default.Highlight,
                                    contentDescription = "Highlights & Notes",
                                    tint = if (highlights.isNotEmpty()) ShelfmatesGold else textColor
                                )
                            }

                            // Display & Typography Settings
                            IconButton(onClick = { isSettingsSheetOpen = true }, modifier = Modifier.testTag("reader_settings_button")) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Reader Settings",
                                    tint = textColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = surfaceColor,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Progress Bar & Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chapter ${currentChapterIndex + 1} of ${manuscript.chapters.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${(overallProgress * 100).toInt()}% • ~${currentChapter.estimatedMinutes} min left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = overallProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = accentColor,
                            trackColor = textColor.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Navigation Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentChapterIndex > 0) currentChapterIndex--
                                },
                                enabled = currentChapterIndex > 0,
                                modifier = Modifier.testTag("reader_prev_chapter_btn")
                            ) {
                                Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prev Chapter", fontSize = 11.sp)
                            }

                            // Auto Scroll Toggle
                            IconButton(
                                onClick = {
                                    val nextSpeed = (settings.autoScrollSpeed + 1) % 4
                                    settings = settings.copy(autoScrollSpeed = nextSpeed)
                                },
                                modifier = Modifier.testTag("reader_autoscroll_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (settings.autoScrollSpeed > 0) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Auto Scroll",
                                        tint = if (settings.autoScrollSpeed > 0) ShelfmatesEmerald else textColor
                                    )
                                    if (settings.autoScrollSpeed > 0) {
                                        Text(
                                            text = "${settings.autoScrollSpeed}x",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShelfmatesEmerald
                                        )
                                    }
                                }
                            }

                            // If ARC manuscript, show review shortcut button
                            if (manuscript.isArc) {
                                Button(
                                    onClick = onSubmitReviewClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesGold),
                                    modifier = Modifier.testTag("reader_submit_arc_review_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Submit ARC Review", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    if (currentChapterIndex < manuscript.chapters.lastIndex) currentChapterIndex++
                                },
                                enabled = currentChapterIndex < manuscript.chapters.lastIndex,
                                modifier = Modifier.testTag("reader_next_chapter_btn")
                            ) {
                                Text("Next Chapter", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .testTag("reader_content_scroll"),
                contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp)
            ) {
                // ARC Watermark / Manuscript Header
                if (manuscript.isArc) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ShelfmatesGold.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = ShelfmatesGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ARC MANUSCRIPT • DUE IN ${manuscript.arcDaysLeft} DAYS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = textColor
                                    )
                                }
                                TextButton(onClick = onSubmitReviewClick) {
                                    Text("Review", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ShelfmatesDeepBlue)
                                }
                            }
                        }
                    }
                }

                // Chapter Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CHAPTER ${currentChapter.number}".uppercase(),
                            fontSize = (settings.fontSizeSp * 0.8f).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor,
                            letterSpacing = 2.sp,
                            fontFamily = selectedFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentChapter.title,
                            fontSize = (settings.fontSizeSp * 1.35f).sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            fontFamily = selectedFontFamily
                        )
                        if (currentChapter.subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentChapter.subtitle,
                                fontSize = (settings.fontSizeSp * 0.85f).sp,
                                fontStyle = FontStyle.Italic,
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = selectedFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            modifier = Modifier
                                .width(60.dp)
                                .height(2.dp),
                            color = accentColor.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Chapter Paragraphs
                itemsIndexed(currentChapter.paragraphs) { pIndex, paragraph ->
                    val isHighlighted = highlights.firstOrNull { it.chapterIndex == currentChapterIndex && it.textSnippet == paragraph }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isHighlighted != null) Color(android.graphics.Color.parseColor(isHighlighted.colorHex)).copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .padding(4.dp)
                            .clickable {
                                selectedParagraphForNote = paragraph
                                isAddNoteDialogOpen = true
                            }
                    ) {
                        Column {
                            Text(
                                text = paragraph,
                                fontSize = settings.fontSizeSp.sp,
                                lineHeight = (settings.fontSizeSp * settings.lineSpacing.multiplier).sp,
                                color = textColor,
                                textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start,
                                fontFamily = selectedFontFamily
                            )

                            if (isHighlighted != null && isHighlighted.note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(surfaceColor, RoundedCornerShape(4.dp))
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.AddComment, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = isHighlighted.note,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }

                // End of chapter card
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "End of Chapter ${currentChapter.number}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentChapterIndex < manuscript.chapters.lastIndex) "Continue to Chapter ${currentChapterIndex + 2}"
                                else "You've reached the end of this ARC preview! Don't forget to submit your review.",
                                fontSize = 12.sp,
                                color = textColor.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (currentChapterIndex < manuscript.chapters.lastIndex) {
                                    Button(
                                        onClick = { currentChapterIndex++ },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                                    ) {
                                        Text("Next Chapter")
                                    }
                                }

                                if (manuscript.isArc) {
                                    Button(
                                        onClick = onSubmitReviewClick,
                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesGold)
                                    ) {
                                        Text("Submit ARC Review", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Table of Contents
    if (isTocSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isTocSheetOpen = false },
            containerColor = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Table of Contents",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(manuscript.chapters) { index, ch ->
                        val isCurrent = index == currentChapterIndex
                        Card(
                            onClick = {
                                currentChapterIndex = index
                                isTocSheetOpen = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) accentColor.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Chapter ${ch.number}: ${ch.title}",
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isCurrent) accentColor else textColor
                                    )
                                    if (ch.subtitle.isNotBlank()) {
                                        Text(
                                            text = ch.subtitle,
                                            fontSize = 11.sp,
                                            color = textColor.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                Text(
                                    text = "~${ch.estimatedMinutes} min",
                                    fontSize = 11.sp,
                                    color = textColor.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Reader Settings (Theme, Font, Size, Spacing)
    if (isSettingsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSettingsSheetOpen = false },
            containerColor = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Reader Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                Spacer(modifier = Modifier.height(16.dp))

                // Theme selection
                Text("BACKGROUND THEME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReaderTheme.values().forEach { theme ->
                        val isSelected = settings.theme == theme
                        val (chipBg, chipBorder, chipText) = when (theme) {
                            ReaderTheme.LIGHT -> Triple(Color(0xFFFAFAFA), Color(0xFFCCCCCC), Color(0xFF1F2937))
                            ReaderTheme.SEPIA -> Triple(Color(0xFFFBF0D9), Color(0xFFD3B88C), Color(0xFF433422))
                            ReaderTheme.DARK -> Triple(Color(0xFF121820), Color(0xFF334155), Color(0xFFE2E8F0))
                            ReaderTheme.FROSTED -> Triple(Color(0xFFE8F1F9), Color(0xFF90CAF9), Color(0xFF0F172A))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chipBg)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ShelfmatesDeepBlue else chipBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    settings = settings.copy(theme = theme)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = theme.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = chipText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Font Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("FONT SIZE (${settings.fontSizeSp.toInt()}sp)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                    Row {
                        TextButton(
                            onClick = { if (settings.fontSizeSp > 12f) settings = settings.copy(fontSizeSp = settings.fontSizeSp - 1f) }
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, color = accentColor)
                        }
                        TextButton(
                            onClick = { if (settings.fontSizeSp < 26f) settings = settings.copy(fontSizeSp = settings.fontSizeSp + 1f) }
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, color = accentColor)
                        }
                    }
                }
                Slider(
                    value = settings.fontSizeSp,
                    onValueChange = { settings = settings.copy(fontSizeSp = it) },
                    valueRange = 12f..26f,
                    steps = 13,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Font Typeface Selection
                Text("TYPOGRAPHY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReaderFont.values().forEach { font ->
                        FilterChip(
                            selected = settings.font == font,
                            onClick = { settings = settings.copy(font = font) },
                            label = { Text(font.displayName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Line Spacing
                Text("LINE SPACING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LineSpacing.values().forEach { spacing ->
                        FilterChip(
                            selected = settings.lineSpacing == spacing,
                            onClick = { settings = settings.copy(lineSpacing = spacing) },
                            label = { Text(spacing.displayName, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Text Alignment Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("JUSTIFY TEXT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                    IconButton(
                        onClick = { settings = settings.copy(isJustified = !settings.isJustified) }
                    ) {
                        Icon(
                            imageVector = if (settings.isJustified) Icons.Default.FormatAlignJustify else Icons.Default.FormatAlignLeft,
                            contentDescription = "Toggle Alignment",
                            tint = accentColor
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet: Bookmarks & Highlights List
    if (isNotesSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isNotesSheetOpen = false },
            containerColor = surfaceColor
        ) {
            var selectedNotesTab by remember { mutableIntStateOf(0) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Saved Notes & Bookmarks", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = selectedNotesTab,
                    containerColor = Color.Transparent,
                    contentColor = accentColor
                ) {
                    Tab(
                        selected = selectedNotesTab == 0,
                        onClick = { selectedNotesTab = 0 },
                        text = { Text("Highlights (${highlights.size})") }
                    )
                    Tab(
                        selected = selectedNotesTab == 1,
                        onClick = { selectedNotesTab = 1 },
                        text = { Text("Bookmarks (${bookmarks.size})") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedNotesTab == 0) {
                    if (highlights.isEmpty()) {
                        Text(
                            "No highlights yet. Tap any paragraph in the reader to highlight text or add reader notes!",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            itemsIndexed(highlights) { idx, hl ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = bgColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(android.graphics.Color.parseColor(hl.colorHex)))
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Chapter ${hl.chapterIndex + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                            IconButton(
                                                onClick = { highlights.removeAt(idx) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${hl.textSnippet}\"",
                                            fontSize = 12.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = textColor,
                                            maxLines = 3
                                        )
                                        if (hl.note.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Note: ${hl.note}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = accentColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (bookmarks.isEmpty()) {
                        Text(
                            "No bookmarks yet. Tap the bookmark icon in the top bar to save your page!",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            itemsIndexed(bookmarks) { idx, bm ->
                                Card(
                                    onClick = {
                                        currentChapterIndex = bm.chapterIndex
                                        isNotesSheetOpen = false
                                    },
                                    colors = CardDefaults.cardColors(containerColor = bgColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Chapter ${bm.chapterIndex + 1}: ${bm.chapterTitle}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor)
                                            Text("Bookmarked on ${bm.timestamp}", fontSize = 10.sp, color = textColor.copy(alpha = 0.6f))
                                        }
                                        IconButton(
                                            onClick = { bookmarks.removeAt(idx) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = textColor.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Highlight & Add Note for Paragraph
    if (isAddNoteDialogOpen && selectedParagraphForNote != null) {
        val paragraph = selectedParagraphForNote!!
        var noteText by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf("#FFD700") } // Gold default
        val colorOptions = listOf("#FFD700", "#2ECC71", "#3498DB", "#E74C3C")

        ModalBottomSheet(
            onDismissRequest = { isAddNoteDialogOpen = false },
            containerColor = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Highlight & Annotate", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${paragraph.take(120)}...\"",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = textColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("SELECT HIGHLIGHT COLOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colorOptions.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = textColor,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Add Reader Note (Optional)") },
                    placeholder = { Text("e.g. Loved this plot twist!") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Copy passage button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Book Quote", paragraph))
                            isAddNoteDialogOpen = false
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Quote", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                            highlights.removeAll { it.chapterIndex == currentChapterIndex && it.textSnippet == paragraph }
                            highlights.add(
                                ReaderHighlight(
                                    id = "hl_${System.currentTimeMillis()}",
                                    bookId = manuscript.bookId,
                                    chapterIndex = currentChapterIndex,
                                    textSnippet = paragraph,
                                    note = noteText,
                                    colorHex = selectedColor,
                                    timestamp = sdf.format(Date())
                                )
                            )
                            isAddNoteDialogOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                    ) {
                        Text("Save Highlight")
                    }
                }
            }
        }
    }
}

private data class ReaderPalette(
    val bgColor: Color,
    val textColor: Color,
    val surfaceColor: Color,
    val accentColor: Color
)
