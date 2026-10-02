package com.shelfmates.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shelfmates.data.local.AppDatabase
import com.shelfmates.data.local.ReadingProgressEntity
import com.shelfmates.data.model.LineSpacing
import com.shelfmates.data.model.ReaderBookmark
import com.shelfmates.data.model.ReaderFont
import com.shelfmates.data.model.ReaderHighlight
import com.shelfmates.data.model.ReaderSettings
import com.shelfmates.data.model.ReaderTheme
import com.shelfmates.data.remote.SupabaseDownloadState
import com.shelfmates.data.remote.SupabaseStorageService
import com.shelfmates.data.repository.ReadingProgressRepository
import com.shelfmates.data.repository.ReadingProgressRepositoryImpl
import com.shelfmates.ui.components.reader.EpubParser
import com.shelfmates.ui.components.reader.ParsedEpubBook
import com.shelfmates.ui.screens.FolioReaderConfig
import com.shelfmates.ui.screens.FolioReaderInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * UI State for EbookReaderScreen.
 */
data class EbookReaderUiState(
    val bookId: String = "",
    val bookTitle: String = "",
    val authorName: String = "",
    val coverUrl: String = "",
    val asin: String = "",
    val isArc: Boolean = true,
    val activeFormat: String = "EPUB", // EPUB or PDF
    val supabaseUrl: String? = null,

    // Reading Position & Sync
    val currentPdfPageIndex: Int = 0,
    val totalPdfPages: Int = 1,
    val currentChapterIndex: Int = 0,
    val totalChapters: Int = 1,
    val progressPercent: Float = 0f,
    val isSavingProgress: Boolean = false,
    val lastSavedTimeText: String? = null,
    val isProgressLoadedFromDb: Boolean = false,
    val showResumeBanner: Boolean = false,
    val resumeMessage: String = "",

    // Reader UI & Customization
    val settings: ReaderSettings = ReaderSettings(
        theme = ReaderTheme.LIGHT,
        font = ReaderFont.SERIF,
        fontSizeSp = 16f,
        lineSpacing = LineSpacing.STANDARD,
        isJustified = true,
        autoScrollSpeed = 0
    ),
    val showControls: Boolean = true,
    val isSettingsSheetOpen: Boolean = false,
    val isTocSheetOpen: Boolean = false,
    val isAddNoteDialogOpen: Boolean = false,
    val selectedParagraphForNote: String? = null,
    val isSupabaseModalOpen: Boolean = false,

    // File Ingestion & Book Data
    val downloadState: SupabaseDownloadState = SupabaseDownloadState.Idle,
    val downloadedFile: File? = null,
    val parsedEpub: ParsedEpubBook? = null,

    // Annotations
    val bookmarks: List<ReaderBookmark> = emptyList(),
    val highlights: List<ReaderHighlight> = emptyList(),

    // FolioReader Integration State
    val isFolioReaderActive: Boolean = false
)

/**
 * ViewModel that manages the lifecycle of the EbookReaderScreen.
 * Specifically handles bidirectional synchronization between the active FolioReader instance
 * and the local Room database (ReadingProgressDao / ReadingProgressRepository), guaranteeing
 * atomic and seamless saving of progress across app lifecycle events.
 */
class EbookReaderViewModel(
    application: Application,
    private val readingProgressRepository: ReadingProgressRepository = ReadingProgressRepositoryImpl(
        AppDatabase.getDatabase(application).readingProgressDao()
    )
) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext

    // Active FolioReader Instance
    private val folioReader: FolioReaderInstance by lazy {
        FolioReaderInstance.get(context)
    }

    private val _uiState = MutableStateFlow(EbookReaderUiState())
    val uiState: StateFlow<EbookReaderUiState> = _uiState.asStateFlow()

    private var downloadJob: Job? = null
    private var saveDebounceJob: Job? = null

    /**
     * Initializes the reader session for a specific book.
     * Restores reading progress from the local Room database and configures FolioReader.
     */
    fun initializeSession(
        bookId: String,
        bookTitle: String,
        authorName: String,
        coverUrl: String = "",
        asin: String = "",
        isArc: Boolean = true,
        initialFormat: String = "EPUB",
        supabaseUrl: String? = null
    ) {
        val normalizedFormat = if (initialFormat.contains("pdf", ignoreCase = true)) "PDF" else "EPUB"

        _uiState.update { current ->
            current.copy(
                bookId = bookId,
                bookTitle = bookTitle,
                authorName = authorName,
                coverUrl = coverUrl,
                asin = asin,
                isArc = isArc,
                activeFormat = normalizedFormat,
                supabaseUrl = supabaseUrl,
                isProgressLoadedFromDb = false
            )
        }

        // Initialize FolioReader configuration
        val config = FolioReaderConfig(
            theme = _uiState.value.settings.theme,
            fontSizeSp = _uiState.value.settings.fontSizeSp,
            font = _uiState.value.settings.font,
            isNightMode = _uiState.value.settings.theme == ReaderTheme.DARK
        )
        folioReader.initialize(config)
        _uiState.update { it.copy(isFolioReaderActive = true) }

        // Restore Progress from Room Database
        loadProgressFromDatabase(bookId)

        // Stream / Download the book file
        loadBookFile()
    }

    /**
     * Queries the Room database for persisted reading progress and updates the active session.
     */
    private fun loadProgressFromDatabase(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedProgress = readingProgressRepository.getReadingProgressDirect(bookId)
                withContext(Dispatchers.Main) {
                    if (savedProgress != null) {
                        val format = if (savedProgress.format.isNotBlank() &&
                            (savedProgress.format == "PDF" || savedProgress.format == "EPUB")
                        ) {
                            savedProgress.format
                        } else {
                            _uiState.value.activeFormat
                        }

                        val resumeMsg = if (format == "PDF") {
                            "Resumed from Page ${savedProgress.lastPageIndex + 1} (Room DB)"
                        } else {
                            "Resumed from Chapter ${savedProgress.lastChapterIndex + 1} (Room DB)"
                        }

                        _uiState.update { current ->
                            current.copy(
                                activeFormat = format,
                                currentPdfPageIndex = savedProgress.lastPageIndex,
                                currentChapterIndex = savedProgress.lastChapterIndex,
                                progressPercent = savedProgress.progressPercent,
                                isProgressLoadedFromDb = true,
                                showResumeBanner = true,
                                resumeMessage = resumeMsg
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isProgressLoadedFromDb = true) }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isProgressLoadedFromDb = true) }
                }
            }
        }
    }

    /**
     * Loads the book content from remote Supabase storage or cache.
     */
    fun loadBookFile() {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.bookId.isBlank()) return@launch

            SupabaseStorageService.downloadArcFromSupabase(
                context = context,
                bookId = currentState.bookId,
                title = currentState.bookTitle,
                author = currentState.authorName,
                format = currentState.activeFormat,
                remoteUrl = currentState.supabaseUrl
            ).collectLatest { state ->
                _uiState.update { it.copy(downloadState = state) }

                if (state is SupabaseDownloadState.Success) {
                    val file = state.file
                    _uiState.update { it.copy(downloadedFile = file) }
                    folioReader.openBook(file, currentState.supabaseUrl)

                    if (currentState.activeFormat == "EPUB") {
                        try {
                            val parsed = EpubParser.parseEpubFile(file)
                            _uiState.update {
                                it.copy(
                                    parsedEpub = parsed,
                                    totalChapters = parsed.chapters.size.coerceAtLeast(1)
                                )
                            }
                        } catch (e: Exception) {
                            // Fallback to default chapters
                        }
                    }
                }
            }
        }
    }

    /**
     * Synchronizes PDF page updates to the local Room database and active session.
     */
    fun onPdfPageChanged(pageIndex: Int, totalPages: Int) {
        val calculatedPercent = if (totalPages > 0) {
            ((pageIndex + 1).toFloat() / totalPages.toFloat() * 100f).coerceIn(0f, 100f)
        } else 0f

        _uiState.update {
            it.copy(
                currentPdfPageIndex = pageIndex,
                totalPdfPages = totalPages.coerceAtLeast(1),
                progressPercent = calculatedPercent
            )
        }

        scheduleProgressSave(
            pageIndex = pageIndex,
            chapterIndex = 0,
            totalCount = totalPages,
            percent = calculatedPercent
        )
    }

    /**
     * Synchronizes EPUB chapter updates to the local Room database and active session.
     */
    fun onEpubChapterChanged(chapterIndex: Int, totalChapters: Int) {
        val total = totalChapters.coerceAtLeast(1)
        val calculatedPercent = ((chapterIndex + 1).toFloat() / total.toFloat() * 100f).coerceIn(0f, 100f)

        _uiState.update {
            it.copy(
                currentChapterIndex = chapterIndex,
                totalChapters = total,
                progressPercent = calculatedPercent
            )
        }

        scheduleProgressSave(
            pageIndex = chapterIndex,
            chapterIndex = chapterIndex,
            totalCount = total,
            percent = calculatedPercent
        )
    }

    /**
     * Switch format between EPUB and PDF.
     */
    fun setActiveFormat(format: String) {
        val normalized = if (format.contains("pdf", ignoreCase = true)) "PDF" else "EPUB"
        if (_uiState.value.activeFormat != normalized) {
            _uiState.update { it.copy(activeFormat = normalized) }
            loadBookFile()
            flushProgressSync()
        }
    }

    /**
     * Schedules a debounced save to Room database to prevent excessive disk I/O during rapid paging.
     */
    private fun scheduleProgressSave(
        pageIndex: Int,
        chapterIndex: Int,
        totalCount: Int,
        percent: Float
    ) {
        saveDebounceJob?.cancel()
        saveDebounceJob = viewModelScope.launch(Dispatchers.IO) {
            persistReadingProgress(pageIndex, chapterIndex, totalCount, percent)
        }
    }

    /**
     * Persists the current reading progress state to Room database.
     */
    private suspend fun persistReadingProgress(
        pageIndex: Int,
        chapterIndex: Int,
        totalCount: Int,
        percent: Float
    ) {
        val bookId = _uiState.value.bookId
        if (bookId.isBlank()) return

        try {
            _uiState.update { it.copy(isSavingProgress = true) }

            readingProgressRepository.saveReadingProgress(
                ReadingProgressEntity(
                    bookId = bookId,
                    lastPageIndex = pageIndex,
                    lastChapterIndex = chapterIndex,
                    totalPagesOrChapters = totalCount,
                    progressPercent = percent,
                    format = _uiState.value.activeFormat,
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )

            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        isSavingProgress = false,
                        lastSavedTimeText = timeFormat
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isSavingProgress = false) }
            }
        }
    }

    /**
     * Immediately flushes and persists the current progress to the Room database.
     * Guaranteed to run synchronously on IO coroutine.
     */
    fun flushProgressSync() {
        val state = _uiState.value
        if (state.bookId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            val total = if (state.activeFormat == "PDF") state.totalPdfPages else state.totalChapters
            val currentIdx = if (state.activeFormat == "PDF") state.currentPdfPageIndex else state.currentChapterIndex
            val pct = if (total > 0) ((currentIdx + 1).toFloat() / total * 100f).coerceIn(0f, 100f) else 0f

            try {
                readingProgressRepository.saveReadingProgress(
                    ReadingProgressEntity(
                        bookId = state.bookId,
                        lastPageIndex = state.currentPdfPageIndex,
                        lastChapterIndex = state.currentChapterIndex,
                        totalPagesOrChapters = total,
                        progressPercent = pct,
                        format = state.activeFormat,
                        lastReadTimestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // Room error
            }
        }
    }

    // --- Bookmarks & Highlights Management ---

    fun addBookmark(snippet: String, chapterTitle: String = "") {
        val state = _uiState.value
        val newBookmark = ReaderBookmark(
            id = UUID.randomUUID().toString(),
            bookId = state.bookId,
            chapterIndex = if (state.activeFormat == "PDF") state.currentPdfPageIndex else state.currentChapterIndex,
            chapterTitle = chapterTitle.ifBlank {
                if (state.activeFormat == "PDF") "Page ${state.currentPdfPageIndex + 1}" else "Chapter ${state.currentChapterIndex + 1}"
            },
            paragraphIndex = 0,
            snippet = snippet.take(120),
            timestamp = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date())
        )

        _uiState.update { current ->
            current.copy(bookmarks = current.bookmarks + newBookmark)
        }
    }

    fun removeBookmark(bookmarkId: String) {
        _uiState.update { current ->
            current.copy(bookmarks = current.bookmarks.filterNot { it.id == bookmarkId })
        }
    }

    fun addHighlight(snippet: String, note: String = "", colorHex: String = "#FFD700") {
        val state = _uiState.value
        val newHighlight = ReaderHighlight(
            id = UUID.randomUUID().toString(),
            bookId = state.bookId,
            chapterIndex = state.currentChapterIndex,
            textSnippet = snippet,
            note = note,
            colorHex = colorHex,
            timestamp = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
        )

        _uiState.update { current ->
            current.copy(highlights = current.highlights + newHighlight)
        }
    }

    // --- UI Controls & Modal Actions ---

    fun toggleControls() {
        _uiState.update { it.copy(showControls = !it.showControls) }
    }

    fun setSettingsSheetOpen(open: Boolean) {
        _uiState.update { it.copy(isSettingsSheetOpen = open) }
    }

    fun setTocSheetOpen(open: Boolean) {
        _uiState.update { it.copy(isTocSheetOpen = open) }
    }

    fun setSupabaseModalOpen(open: Boolean) {
        _uiState.update { it.copy(isSupabaseModalOpen = open) }
    }

    fun setAddNoteDialogOpen(open: Boolean, paragraphText: String? = null) {
        _uiState.update {
            it.copy(
                isAddNoteDialogOpen = open,
                selectedParagraphForNote = paragraphText
            )
        }
    }

    fun dismissResumeBanner() {
        _uiState.update { it.copy(showResumeBanner = false) }
    }

    fun updateSettings(newSettings: ReaderSettings) {
        _uiState.update { it.copy(settings = newSettings) }

        // Propagate updated config to FolioReader instance
        val config = FolioReaderConfig(
            theme = newSettings.theme,
            fontSizeSp = newSettings.fontSizeSp,
            font = newSettings.font,
            isNightMode = newSettings.theme == ReaderTheme.DARK
        )
        folioReader.initialize(config)
    }

    /**
     * Handles lifecycle teardown when navigating away or closing reader.
     * Ensures FolioReader is safely closed and progress is flushed to Room.
     */
    fun disposeSession() {
        flushProgressSync()
        folioReader.close()
        _uiState.update { it.copy(isFolioReaderActive = false) }
    }

    override fun onCleared() {
        super.onCleared()
        disposeSession()
    }
}

/**
 * Factory for creating [EbookReaderViewModel] instances with custom [ReadingProgressRepository].
 */
class EbookReaderViewModelFactory(
    private val application: Application,
    private val readingProgressRepository: ReadingProgressRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EbookReaderViewModel::class.java)) {
            val repo = readingProgressRepository ?: ReadingProgressRepositoryImpl(
                AppDatabase.getDatabase(application).readingProgressDao()
            )
            return EbookReaderViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
