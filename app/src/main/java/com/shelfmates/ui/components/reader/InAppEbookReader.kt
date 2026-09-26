package com.shelfmates.ui.components.reader

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.shelfmates.data.model.LineSpacing
import com.shelfmates.data.model.ReaderBookmark
import com.shelfmates.data.model.ReaderFont
import com.shelfmates.data.model.ReaderHighlight
import com.shelfmates.data.model.ReaderSettings
import com.shelfmates.data.model.ReaderTheme
import com.shelfmates.data.remote.SupabaseDownloadState
import com.shelfmates.data.remote.SupabaseStorageService
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesNavy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern InAppEbookReader supporting both EPUB and PDF formats,
 * deeply integrated with Supabase Storage for streaming/caching ARC files.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppEbookReader(
    bookId: String,
    bookTitle: String,
    authorName: String,
    coverUrl: String = "",
    asin: String = "",
    isArc: Boolean = true,
    initialFormat: String = "EPUB",
    supabaseUrl: String? = null,
    onBack: () -> Unit,
    onSubmitReviewClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Active format state (EPUB vs PDF)
    var activeFormat by remember {
        mutableStateOf(if (initialFormat.contains("pdf", ignoreCase = true)) "PDF" else "EPUB")
    }

    // Supabase Download & Storage State
    var downloadState by remember { mutableStateOf<SupabaseDownloadState>(SupabaseDownloadState.Idle) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }
    var parsedEpub by remember { mutableStateOf<ParsedEpubBook?>(null) }
    var isSupabaseModalOpen by remember { mutableStateOf(false) }

    // EPUB & Text Reader UI State
    var currentChapterIndex by remember { mutableIntStateOf(0) }
    var showControls by remember { mutableStateOf(true) }
    var isSettingsSheetOpen by remember { mutableStateOf(false) }
    var isTocSheetOpen by remember { mutableStateOf(false) }
    var isNotesSheetOpen by remember { mutableStateOf(false) }
    var isAddNoteDialogOpen by remember { mutableStateOf(false) }
    var selectedParagraphForNote by remember { mutableStateOf<String?>(null) }

    // Reader Settings
    var settings by remember {
        mutableStateOf(
            ReaderSettings(
                theme = ReaderTheme.LIGHT,
                font = ReaderFont.SERIF,
                fontSizeSp = 16f,
                lineSpacing = LineSpacing.STANDARD,
                isJustified = true,
                autoScrollSpeed = 0
            )
        )
    }

    val bookmarks = remember { mutableStateListOf<ReaderBookmark>() }
    val highlights = remember { mutableStateListOf<ReaderHighlight>() }

    // Fetch or Cache from Supabase Storage
    LaunchedEffect(bookId, activeFormat) {
        SupabaseStorageService.downloadArcFromSupabase(
            context = context,
            bookId = bookId,
            title = bookTitle,
            author = authorName,
            format = activeFormat,
            remoteUrl = supabaseUrl
        ).collectLatest { state ->
            downloadState = state
            if (state is SupabaseDownloadState.Success) {
                downloadedFile = state.file
                if (activeFormat == "EPUB") {
                    try {
                        val parsed = EpubParser.parseEpubFile(state.file)
                        parsedEpub = parsed
                    } catch (e: Exception) {
                        // Keep fallback
                    }
                }
            }
        }
    }

    // Colors according to Theme
    val backgroundColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFFFAF8F5)
        ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
        ReaderTheme.DARK -> Color(0xFF121824)
        ReaderTheme.FROSTED -> Color(0xFF0F172A)
    }

    val textColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFF1E293B)
        ReaderTheme.SEPIA -> Color(0xFF3E2F20)
        ReaderTheme.DARK -> Color(0xFFE2E8F0)
        ReaderTheme.FROSTED -> Color(0xFFF1F5F9)
    }

    val secondaryTextColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFF64748B)
        ReaderTheme.SEPIA -> Color(0xFF7D6B57)
        ReaderTheme.DARK -> Color(0xFF94A3B8)
        ReaderTheme.FROSTED -> Color(0xFF94A3B8)
    }

    val listState = rememberLazyListState()

    // Auto-Scroll Coroutine
    LaunchedEffect(settings.autoScrollSpeed) {
        if (settings.autoScrollSpeed > 0 && activeFormat == "EPUB") {
            while (true) {
                val scrollAmount = when (settings.autoScrollSpeed) {
                    1 -> 3f
                    2 -> 6f
                    3 -> 10f
                    else -> 0f
                }
                listState.scrollBy(scrollAmount)
                delay(50)
            }
        }
    }

    val chaptersCount = parsedEpub?.chapters?.size ?: 3
    val activeChapterTitle = parsedEpub?.chapters?.getOrNull(currentChapterIndex)?.title
        ?: "Chapter ${currentChapterIndex + 1}"
    val currentParagraphs = parsedEpub?.chapters?.getOrNull(currentChapterIndex)?.plainTextParagraphs
        ?: listOf(
            "The pendulum had stopped swinging precisely three minutes past midnight.",
            "Valen knelt upon the cold obsidian pavers of the Great Spire. Beneath his fingertips, the brass conduits vibrated with a frantic hum.",
            "\"You should not touch that,\" a voice echoed from the archway. Lyra stepped forward, her silver vestments catching the pale moonlight."
        )

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = bookTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Supabase Sync Badge
                                Surface(
                                    color = ShelfmatesEmerald.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = ShelfmatesEmerald, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Supabase", color = ShelfmatesEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "$authorName • $activeChapterTitle",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("ebook_reader_back_button")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        // Format Switcher Tab Button
                        Surface(
                            onClick = {
                                activeFormat = if (activeFormat == "EPUB") "PDF" else "EPUB"
                            },
                            color = ShelfmatesAmber,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (activeFormat == "EPUB") Icons.Default.PictureAsPdf else Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (activeFormat == "EPUB") "Switch to PDF" else "Switch to EPUB",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        // Supabase Storage Info Action
                        IconButton(onClick = { isSupabaseModalOpen = true }) {
                            Icon(imageVector = Icons.Default.Storage, contentDescription = "Supabase Storage", tint = Color.White)
                        }

                        // Table of Contents
                        IconButton(onClick = { isTocSheetOpen = true }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = "TOC", tint = Color.White)
                        }

                        // Settings & Styling
                        IconButton(onClick = { isSettingsSheetOpen = true }) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ShelfmatesNavy)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(
                    color = ShelfmatesNavy,
                    shadowElevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // ARC Status & Progress Line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = ShelfmatesGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ARC MANUSCRIPT",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (activeFormat == "EPUB") "Chapter ${currentChapterIndex + 1} of $chaptersCount" else "Rendered PDF Document",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }

                            // Submit ARC Review CTA
                            Button(
                                onClick = onSubmitReviewClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesCoral),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("reader_submit_review_cta")
                            ) {
                                Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit Review", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (activeFormat == "EPUB") {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Chapter progress slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (currentChapterIndex > 0) {
                                            currentChapterIndex--
                                            scope.launch { listState.scrollToItem(0) }
                                        }
                                    },
                                    enabled = currentChapterIndex > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White)
                                }

                                Slider(
                                    value = currentChapterIndex.toFloat(),
                                    onValueChange = {
                                        currentChapterIndex = it.toInt().coerceIn(0, (chaptersCount - 1).coerceAtLeast(0))
                                    },
                                    valueRange = 0f..(chaptersCount - 1).coerceAtLeast(1).toFloat(),
                                    steps = (chaptersCount - 2).coerceAtLeast(0),
                                    colors = SliderDefaults.colors(
                                        thumbColor = ShelfmatesGold,
                                        activeTrackColor = ShelfmatesGold
                                    ),
                                    modifier = Modifier.weight(1f).height(24.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (currentChapterIndex < chaptersCount - 1) {
                                            currentChapterIndex++
                                            scope.launch { listState.scrollToItem(0) }
                                        }
                                    },
                                    enabled = currentChapterIndex < chaptersCount - 1,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Next", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Check download/loading state from Supabase
            when (val state = downloadState) {
                is SupabaseDownloadState.Downloading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Streaming ARC from Supabase Storage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bucket: arc-manuscripts • Format: $activeFormat",
                            fontSize = 13.sp,
                            color = secondaryTextColor
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        LinearProgressIndicator(
                            progress = { state.progressPercent / 100f },
                            color = ShelfmatesEmerald,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth(0.8f).height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.progressPercent}% downloaded",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }

                is SupabaseDownloadState.Success -> {
                    // Render according to active format
                    if (activeFormat == "PDF" && downloadedFile != null) {
                        ComposePdfViewer(
                            pdfFile = downloadedFile!!,
                            readerTheme = settings.theme,
                            onTap = { showControls = !showControls }
                        )
                    } else {
                        // EPUB Flow Reader
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    showControls = !showControls
                                }
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy((16 * settings.lineSpacing.multiplier).dp)
                        ) {
                            // Chapter Heading
                            item {
                                Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
                                    Surface(
                                        color = ShelfmatesAmber.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "CONFIDENTIAL ADVANCE REVIEW COPY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShelfmatesAmber,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = activeChapterTitle,
                                        fontSize = (settings.fontSizeSp + 8).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        fontFamily = when (settings.font) {
                                            ReaderFont.SERIF -> FontFamily.Serif
                                            ReaderFont.SANS -> FontFamily.SansSerif
                                            ReaderFont.MONO -> FontFamily.Monospace
                                        }
                                    )
                                    HorizontalDivider(
                                        color = secondaryTextColor.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }

                            // Paragraphs
                            itemsIndexed(currentParagraphs) { pIndex, paragraph ->
                                Text(
                                    text = paragraph,
                                    fontSize = settings.fontSizeSp.sp,
                                    lineHeight = (settings.fontSizeSp * settings.lineSpacing.multiplier).sp,
                                    textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start,
                                    color = textColor,
                                    fontFamily = when (settings.font) {
                                        ReaderFont.SERIF -> FontFamily.Serif
                                        ReaderFont.SANS -> FontFamily.SansSerif
                                        ReaderFont.MONO -> FontFamily.Monospace
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedParagraphForNote = paragraph
                                            isAddNoteDialogOpen = true
                                        }
                                )
                            }

                            // Chapter End / Navigation Action
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "End of $activeChapterTitle",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            if (currentChapterIndex < chaptersCount - 1) {
                                                Button(
                                                    onClick = {
                                                        currentChapterIndex++
                                                        scope.launch { listState.scrollToItem(0) }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                                                ) {
                                                    Text("Next Chapter")
                                                }
                                            }

                                            Button(
                                                onClick = onSubmitReviewClick,
                                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesCoral)
                                            ) {
                                                Text("Submit ARC Review")
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(60.dp))
                            }
                        }
                    }
                }

                else -> {
                    // Fallback
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Preparing Manuscript...", color = textColor)
                    }
                }
            }
        }
    }

    // --- Modal Sheets: Supabase Storage Info ---
    if (isSupabaseModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSupabaseModalOpen = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = ShelfmatesEmerald, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Supabase Storage Integration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Shelfmates streams and caches encrypted ARC manuscripts directly from Supabase Storage buckets with offline persistence.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Storage Host: ${SupabaseStorageService.supabaseUrl}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text("• Target Bucket: ${SupabaseStorageService.defaultBucket}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text("• Cached Locally: ${downloadedFile?.exists() == true}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShelfmatesEmerald)
                        Text("• File Size: ${downloadedFile?.length()?.let { String.format("%.2f MB", it / (1024.0 * 1024.0)) } ?: "1.4 MB"}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { isSupabaseModalOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // --- Settings & Typography Bottom Sheet ---
    if (isSettingsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSettingsSheetOpen = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Reader Settings & Styling", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Themes
                Text("Theme Palette", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReaderTheme.entries.forEach { theme ->
                        FilterChip(
                            selected = settings.theme == theme,
                            onClick = { settings = settings.copy(theme = theme) },
                            label = { Text(theme.displayName, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Font Family
                Text("Font Family", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReaderFont.entries.forEach { font ->
                        FilterChip(
                            selected = settings.font == font,
                            onClick = { settings = settings.copy(font = font) },
                            label = { Text(font.displayName, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Font Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("${settings.fontSizeSp.toInt()} sp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                }
                Slider(
                    value = settings.fontSizeSp,
                    onValueChange = { settings = settings.copy(fontSizeSp = it) },
                    valueRange = 12f..28f,
                    steps = 7,
                    colors = SliderDefaults.colors(thumbColor = ShelfmatesDeepBlue, activeTrackColor = ShelfmatesDeepBlue)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Auto Scroll Speed
                Text("Hands-Free Auto-Scroll", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Off" to 0, "Slow" to 1, "Medium" to 2, "Fast" to 3).forEach { (label, speed) ->
                        FilterChip(
                            selected = settings.autoScrollSpeed == speed,
                            onClick = { settings = settings.copy(autoScrollSpeed = speed) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // --- Table of Contents Sheet ---
    if (isTocSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isTocSheetOpen = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Table of Contents", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                val chapters = parsedEpub?.chapters ?: (1..3).map {
                    EpubChapter("ch_$it", "Chapter $it", "", "", emptyList(), it)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(chapters) { idx, ch ->
                        val isSelected = idx == currentChapterIndex
                        Surface(
                            onClick = {
                                currentChapterIndex = idx
                                isTocSheetOpen = false
                                scope.launch { listState.scrollToItem(0) }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ShelfmatesDeepBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ShelfmatesDeepBlue) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = ch.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Est. ~4 min read", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ShelfmatesDeepBlue, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // --- Add Note Dialog ---
    if (isAddNoteDialogOpen && selectedParagraphForNote != null) {
        var noteText by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { isAddNoteDialogOpen = false },
            title = { Text("Add Bookmark & Note", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "\"${selectedParagraphForNote!!.take(120)}...\"",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Your private reading note") },
                        placeholder = { Text("e.g. Great character development here...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBm = ReaderBookmark(
                            id = System.currentTimeMillis().toString(),
                            bookId = bookId,
                            chapterIndex = currentChapterIndex,
                            chapterTitle = activeChapterTitle,
                            paragraphIndex = 0,
                            snippet = selectedParagraphForNote!!,
                            timestamp = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())
                        )
                        bookmarks.add(newBm)
                        isAddNoteDialogOpen = false
                        Toast.makeText(context, "Note saved to your Shelfmates reader!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddNoteDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
