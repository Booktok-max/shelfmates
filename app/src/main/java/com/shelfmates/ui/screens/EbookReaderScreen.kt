package com.shelfmates.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.model.ReaderFont
import com.shelfmates.data.model.ReaderTheme
import com.shelfmates.data.remote.SupabaseDownloadState
import com.shelfmates.data.remote.SupabaseStorageService
import com.shelfmates.ui.components.reader.ComposePdfViewer
import com.shelfmates.ui.components.reader.EpubChapter
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesBlueAccent
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesNavy
import kotlinx.coroutines.launch
import java.io.File

import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfmates.ui.viewmodel.EbookReaderViewModel
import com.shelfmates.ui.viewmodel.EbookReaderViewModelFactory
import androidx.compose.runtime.collectAsState

/**
 * Controller configuration for FolioReader / In-App EPUB reader instance.
 */
class FolioReaderConfig(
    val theme: ReaderTheme = ReaderTheme.LIGHT,
    val fontSizeSp: Float = 16f,
    val font: ReaderFont = ReaderFont.SERIF,
    val isNightMode: Boolean = false,
    val autoSaveProgress: Boolean = true
)

/**
 * FolioReader session manager that handles lifecycle, configuration, and EPUB/PDF file stream ingestion.
 */
class FolioReaderInstance private constructor(private val context: Context) {
    var isInitialized: Boolean = false
        private set
    var currentFile: File? = null
        private set
    var currentUrl: String? = null
        private set
    var activeConfig: FolioReaderConfig = FolioReaderConfig()
        private set

    fun initialize(config: FolioReaderConfig = FolioReaderConfig()): FolioReaderInstance {
        this.activeConfig = config
        this.isInitialized = true
        return this
    }

    fun openBook(file: File, remoteUrl: String? = null) {
        this.currentFile = file
        this.currentUrl = remoteUrl
    }

    fun close() {
        this.currentFile = null
        this.currentUrl = null
    }

    companion object {
        @Volatile
        private var instance: FolioReaderInstance? = null

        fun get(context: Context): FolioReaderInstance {
            return instance ?: synchronized(this) {
                instance ?: FolioReaderInstance(context.applicationContext).also { instance = it }
            }
        }
    }
}

/**
 * EbookReaderScreen Composable that initializes the FolioReader instance,
 * streams/downloads the manuscript from a provided Supabase Storage URL,
 * and delegates lifecycle management and Room database progress synchronization to [EbookReaderViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbookReaderScreen(
    bookId: String,
    bookTitle: String,
    authorName: String,
    coverUrl: String = "",
    asin: String = "",
    isArc: Boolean = true,
    initialFormat: String = "EPUB",
    supabaseUrl: String? = null,
    viewModel: EbookReaderViewModel = viewModel(
        factory = EbookReaderViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    ),
    onBack: () -> Unit,
    onSubmitReviewClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Collect UI state from EbookReaderViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Initialize session and sync with Room on key parameter changes
    LaunchedEffect(bookId, initialFormat, supabaseUrl) {
        viewModel.initializeSession(
            bookId = bookId,
            bookTitle = bookTitle,
            authorName = authorName,
            coverUrl = coverUrl,
            asin = asin,
            isArc = isArc,
            initialFormat = initialFormat,
            supabaseUrl = supabaseUrl
        )
    }

    // Safely flush progress and dispose FolioReader session on exit / lifecycle teardown
    DisposableEffect(bookId) {
        onDispose {
            viewModel.disposeSession()
        }
    }

    val activeFormat = uiState.activeFormat
    val currentPdfPageIndex = uiState.currentPdfPageIndex
    val totalPdfPages = uiState.totalPdfPages
    val currentChapterIndex = uiState.currentChapterIndex
    val downloadState = uiState.downloadState
    val downloadedFile = uiState.downloadedFile
    val parsedEpub = uiState.parsedEpub
    val settings = uiState.settings
    val showControls = uiState.showControls
    val showResumeBanner = uiState.showResumeBanner
    val resumeMessage = uiState.resumeMessage
    val isSettingsSheetOpen = uiState.isSettingsSheetOpen
    val isTocSheetOpen = uiState.isTocSheetOpen
    val isSupabaseModalOpen = uiState.isSupabaseModalOpen
    val isAddNoteDialogOpen = uiState.isAddNoteDialogOpen
    val selectedParagraphForNote = uiState.selectedParagraphForNote
    val bookmarks = uiState.bookmarks

    val folioReader = remember { FolioReaderInstance.get(context) }
    val chaptersCount = parsedEpub?.chapters?.size ?: 3

    // Theme-based colors (Optimized for maximum contrast & bookish readability)
    val backgroundColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFFFFFFFF)
        ReaderTheme.SEPIA -> Color(0xFFFAF4EB)
        ReaderTheme.DARK -> Color(0xFF09090B)
        ReaderTheme.FROSTED -> Color(0xFFF1F5F9)
    }

    val textColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFF0F172A)
        ReaderTheme.SEPIA -> Color(0xFF2C2216)
        ReaderTheme.DARK -> Color(0xFFFAFAFA)
        ReaderTheme.FROSTED -> Color(0xFF09090B)
    }

    val secondaryTextColor = when (settings.theme) {
        ReaderTheme.LIGHT -> Color(0xFF475569)
        ReaderTheme.SEPIA -> Color(0xFF6B5844)
        ReaderTheme.DARK -> Color(0xFFA1A1AA)
        ReaderTheme.FROSTED -> Color(0xFF475569)
    }

    // Frosted Glass Atmospheric Brush
    val frostedBackgroundBrush = remember(settings.theme) {
        when (settings.theme) {
            ReaderTheme.FROSTED -> Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1E3A5F),
                    Color(0xFF0F1E36),
                    Color(0xFF0A111E)
                ),
                center = Offset(300f, 200f),
                radius = 1800f
            )
            ReaderTheme.DARK -> Brush.radialGradient(
                colors = listOf(
                    Color(0xFF162032),
                    Color(0xFF0E1726),
                    Color(0xFF070B12)
                ),
                center = Offset(400f, 300f),
                radius = 1600f
            )
            ReaderTheme.SEPIA -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFBF8F0),
                    Color(0xFFF3ECE0),
                    Color(0xFFEADBBE)
                )
            )
            ReaderTheme.LIGHT -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF8FAFC),
                    Color(0xFFEFF4F9),
                    Color(0xFFE2EAF4)
                )
            )
        }
    }

    val activeChapterTitle = parsedEpub?.chapters?.getOrNull(currentChapterIndex)?.title
        ?: "Chapter ${currentChapterIndex + 1}"
    val currentParagraphs = parsedEpub?.chapters?.getOrNull(currentChapterIndex)?.plainTextParagraphs
        ?: listOf(
            "The pendulum had stopped swinging precisely three minutes past midnight.",
            "Valen knelt upon the cold obsidian pavers of the Great Spire. Beneath his fingertips, the brass conduits vibrated with a frantic hum.",
            "\"You should not touch that,\" a voice echoed from the archway. Lyra stepped forward, her silver vestments catching the pale moonlight."
        )

    Scaffold(
        modifier = Modifier.testTag("ebook_reader_screen"),
        containerColor = Color.Transparent,
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    color = ShelfmatesNavy.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    shadowElevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
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
                                    Surface(
                                        color = ShelfmatesEmerald.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.5.dp, ShelfmatesEmerald.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = null,
                                                tint = ShelfmatesEmerald,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                "Supabase Folio",
                                                color = ShelfmatesEmerald,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = ShelfmatesGold.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.5.dp, ShelfmatesGold.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Storage,
                                                contentDescription = null,
                                                tint = ShelfmatesGold,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                "Room DB",
                                                color = ShelfmatesGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "$authorName • $activeChapterTitle",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                    maxLines = 1
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .testTag("ebook_reader_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            // Switch between EPUB and PDF
                            Surface(
                                onClick = {
                                    viewModel.setActiveFormat(if (activeFormat == "EPUB") "PDF" else "EPUB")
                                },
                                color = ShelfmatesAmber.copy(alpha = 0.90f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                                modifier = Modifier.padding(end = 4.dp).testTag("format_switch_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (activeFormat == "EPUB") Icons.Default.PictureAsPdf else Icons.Default.AutoStories,
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

                            // Supabase Storage Details
                            IconButton(
                                onClick = { viewModel.setSupabaseModalOpen(true) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .testTag("supabase_storage_info_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Supabase Storage",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(3.dp))

                            // Table of Contents
                            IconButton(
                                onClick = { viewModel.setTocSheetOpen(true) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .testTag("toc_sheet_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "TOC",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(3.dp))

                            // Settings & Styling
                            IconButton(
                                onClick = { viewModel.setSettingsSheetOpen(true) },
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .testTag("reader_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Settings",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Surface(
                        color = ShelfmatesNavy.copy(alpha = 0.85f),
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = ShelfmatesGold.copy(alpha = 0.95f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = if (isArc) "ARC MANUSCRIPT" else "E-BOOK",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (activeFormat == "EPUB") {
                                            "Ch. ${currentChapterIndex + 1}/$chaptersCount • Room Auto-Saved"
                                        } else {
                                            "Page ${currentPdfPageIndex + 1}/$totalPdfPages • Room Auto-Saved"
                                        },
                                        color = Color.White.copy(alpha = 0.88f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Submit ARC Review CTA
                                Button(
                                    onClick = onSubmitReviewClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesCoral.copy(alpha = 0.92f)),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.25f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("reader_submit_review_cta")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RateReview,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Submit Review", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (activeFormat == "EPUB") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (currentChapterIndex > 0) {
                                                viewModel.onEpubChapterChanged(currentChapterIndex - 1, chaptersCount)
                                                scope.launch { listState.scrollToItem(0) }
                                            }
                                        },
                                        enabled = currentChapterIndex > 0,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.10f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Previous Chapter",
                                            tint = if (currentChapterIndex > 0) Color.White else Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Slider(
                                        value = currentChapterIndex.toFloat(),
                                        onValueChange = {
                                            viewModel.onEpubChapterChanged(it.toInt().coerceIn(0, (chaptersCount - 1).coerceAtLeast(0)), chaptersCount)
                                        },
                                        valueRange = 0f..(chaptersCount - 1).coerceAtLeast(1).toFloat(),
                                        steps = (chaptersCount - 2).coerceAtLeast(0),
                                        colors = SliderDefaults.colors(
                                            thumbColor = ShelfmatesGold,
                                            activeTrackColor = ShelfmatesGold,
                                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                            .height(24.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (currentChapterIndex < chaptersCount - 1) {
                                                viewModel.onEpubChapterChanged(currentChapterIndex + 1, chaptersCount)
                                                scope.launch { listState.scrollToItem(0) }
                                            }
                                        },
                                        enabled = currentChapterIndex < chaptersCount - 1,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.10f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                            contentDescription = "Next Chapter",
                                            tint = if (currentChapterIndex < chaptersCount - 1) Color.White else Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
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
                .background(frostedBackgroundBrush)
                .drawBehind {
                    // Subtle ambient glowing frosted glass orbs for atmospheric depth
                    if (settings.theme == ReaderTheme.FROSTED || settings.theme == ReaderTheme.DARK) {
                        drawCircle(
                            color = Color(0xFF1B4F72).copy(alpha = 0.25f),
                            radius = size.width * 0.55f,
                            center = Offset(size.width * 0.85f, size.height * 0.15f)
                        )
                        drawCircle(
                            color = Color(0xFF27AE60).copy(alpha = 0.12f),
                            radius = size.width * 0.45f,
                            center = Offset(size.width * 0.15f, size.height * 0.80f)
                        )
                    }
                }
        ) {
            when (val state = downloadState) {
                is SupabaseDownloadState.Downloading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = if (settings.theme == ReaderTheme.LIGHT) Color.White.copy(alpha = 0.85f) else Color(0xCC1E293B),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                            shadowElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = ShelfmatesBlueAccent,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Streaming from Supabase Storage",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "FolioReader Session • Format: $activeFormat",
                                    fontSize = 13.sp,
                                    color = secondaryTextColor
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                LinearProgressIndicator(
                                    progress = { state.progressPercent / 100f },
                                    color = ShelfmatesEmerald,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth(0.9f).height(8.dp).clip(RoundedCornerShape(4.dp))
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
                    }
                }

                is SupabaseDownloadState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (showResumeBanner) {
                            Surface(
                                color = ShelfmatesDeepBlue.copy(alpha = 0.80f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ShelfmatesGold.copy(alpha = 0.40f)),
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = ShelfmatesGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = resumeMessage.ifBlank { "Resumed from your last saved position in Room DB" },
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    TextButton(
                                        onClick = { viewModel.dismissResumeBanner() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Dismiss", color = ShelfmatesGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        if (activeFormat == "PDF" && downloadedFile != null) {
                            ComposePdfViewer(
                                pdfFile = downloadedFile,
                                readerTheme = settings.theme,
                                initialPage = currentPdfPageIndex,
                                onPageChanged = { page1Based, total ->
                                    val p0 = (page1Based - 1).coerceAtLeast(0)
                                    viewModel.onPdfPageChanged(p0, total)
                                },
                                onTap = { viewModel.toggleControls() }
                            )
                        } else {
                            // EPUB Flow Reader Layout
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.toggleControls()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy((16 * settings.lineSpacing.multiplier).dp)
                            ) {
                                item {
                                    Surface(
                                        color = when (settings.theme) {
                                            ReaderTheme.FROSTED -> Color(0x351E293B)
                                            ReaderTheme.DARK -> Color(0x2B1E293B)
                                            ReaderTheme.SEPIA -> Color(0x65FFFDF7)
                                            ReaderTheme.LIGHT -> Color(0x85FFFFFF)
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (settings.theme == ReaderTheme.LIGHT || settings.theme == ReaderTheme.SEPIA) {
                                                Color.Black.copy(alpha = 0.06f)
                                            } else {
                                                Color.White.copy(alpha = 0.12f)
                                            }
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            if (isArc) {
                                                Surface(
                                                    color = ShelfmatesAmber.copy(alpha = 0.22f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(0.5.dp, ShelfmatesAmber.copy(alpha = 0.5f))
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
                                            }
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
                                }

                                itemsIndexed(currentParagraphs) { _, paragraph ->
                                    Surface(
                                        color = when (settings.theme) {
                                            ReaderTheme.FROSTED -> Color(0x281E293B)
                                            ReaderTheme.DARK -> Color(0x201E293B)
                                            ReaderTheme.SEPIA -> Color(0x50FFFDF7)
                                            ReaderTheme.LIGHT -> Color(0x75FFFFFF)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            0.5.dp,
                                            if (settings.theme == ReaderTheme.LIGHT || settings.theme == ReaderTheme.SEPIA) {
                                                Color.Black.copy(alpha = 0.04f)
                                            } else {
                                                Color.White.copy(alpha = 0.08f)
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setAddNoteDialogOpen(true, paragraph)
                                            }
                                    ) {
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
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (settings.theme == ReaderTheme.LIGHT) {
                                                Color.White.copy(alpha = 0.85f)
                                            } else {
                                                Color(0x50334155)
                                            }
                                        ),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "End of $activeChapterTitle",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = textColor
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                if (currentChapterIndex < chaptersCount - 1) {
                                                    Button(
                                                        onClick = {
                                                            viewModel.onEpubChapterChanged(currentChapterIndex + 1, chaptersCount)
                                                            scope.launch { listState.scrollToItem(0) }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue.copy(alpha = 0.90f)),
                                                        shape = RoundedCornerShape(10.dp),
                                                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
                                                    ) {
                                                        Text("Next Chapter")
                                                    }
                                                }

                                                Button(
                                                    onClick = onSubmitReviewClick,
                                                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesCoral.copy(alpha = 0.90f)),
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
                                                ) {
                                                    Text("Submit ARC Review")
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }

                is SupabaseDownloadState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Unable to stream book from Supabase Storage", color = textColor, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.message, color = secondaryTextColor, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadBookFile() },
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Download")
                        }
                    }
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Initializing FolioReader Instance...", color = textColor)
                    }
                }
            }
        }
    }

    // Modal: Supabase Storage Integration Details
    if (isSupabaseModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setSupabaseModalOpen(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = if (settings.theme == ReaderTheme.LIGHT) Color(0xF2F8FAFC) else Color(0xEB0E2A47)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = ShelfmatesEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Supabase Storage & FolioReader", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Shelfmates streams and parses encrypted ARC manuscripts directly from Supabase Storage into the FolioReader runtime.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (settings.theme == ReaderTheme.LIGHT) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Host: ${SupabaseStorageService.supabaseUrl}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text("• Target Bucket: ${SupabaseStorageService.defaultBucket}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Text("• Active Storage URL: ${supabaseUrl ?: "Supabase Object Stream"}", fontSize = 12.sp, maxLines = 1)
                        Text("• Folio Instance Initialized: ${folioReader.isInitialized}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShelfmatesEmerald)
                        Text("• Local File Cache: ${downloadedFile?.name ?: "In Memory"}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.setSupabaseModalOpen(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal: Reader Settings
    if (isSettingsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setSettingsSheetOpen(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = if (settings.theme == ReaderTheme.LIGHT) Color(0xF2F8FAFC) else Color(0xEB0E2A47)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Folio Reader Typography & Palette", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Theme Palette", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReaderTheme.entries.forEach { theme ->
                        FilterChip(
                            selected = settings.theme == theme,
                            onClick = { viewModel.updateSettings(settings.copy(theme = theme)) },
                            label = { Text(theme.displayName, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Font Family", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReaderFont.entries.forEach { font ->
                        FilterChip(
                            selected = settings.font == font,
                            onClick = { viewModel.updateSettings(settings.copy(font = font)) },
                            label = { Text(font.displayName, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    onValueChange = { viewModel.updateSettings(settings.copy(fontSizeSp = it)) },
                    valueRange = 12f..28f,
                    steps = 7,
                    colors = SliderDefaults.colors(thumbColor = ShelfmatesDeepBlue, activeTrackColor = ShelfmatesDeepBlue)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal: Table of Contents
    if (isTocSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setTocSheetOpen(false) },
            sheetState = rememberModalBottomSheetState(),
            containerColor = if (settings.theme == ReaderTheme.LIGHT) Color(0xF2F8FAFC) else Color(0xEB0E2A47)
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
                                viewModel.onEpubChapterChanged(idx, chapters.size)
                                viewModel.setTocSheetOpen(false)
                                scope.launch { listState.scrollToItem(0) }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ShelfmatesDeepBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
                            border = if (isSelected) BorderStroke(1.5.dp, ShelfmatesGold) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.12f)),
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
                                        color = if (isSelected) ShelfmatesGold else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Est. ~4 min read", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ShelfmatesGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal: Add Note Dialog
    if (isAddNoteDialogOpen && selectedParagraphForNote != null) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.setAddNoteDialogOpen(false) },
            containerColor = if (settings.theme == ReaderTheme.LIGHT) Color(0xF2F8FAFC) else Color(0xEB0E2A47),
            title = { Text("Add Bookmark & Note", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "\"${selectedParagraphForNote.take(120)}...\"",
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
                        viewModel.addBookmark(selectedParagraphForNote, activeChapterTitle)
                        viewModel.setAddNoteDialogOpen(false)
                        Toast.makeText(context, "Note saved to your Shelfmates reader!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setAddNoteDialogOpen(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
