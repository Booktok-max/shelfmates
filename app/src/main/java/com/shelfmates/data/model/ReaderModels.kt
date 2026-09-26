package com.shelfmates.data.model

enum class ReaderTheme(val displayName: String) {
    LIGHT("Paper"),
    SEPIA("Parchment"),
    DARK("Obsidian"),
    FROSTED("Frosted Ice")
}

enum class ReaderFont(val displayName: String) {
    SERIF("Literary Serif"),
    SANS("Modern Sans"),
    MONO("Monospace")
}

enum class LineSpacing(val displayName: String, val multiplier: Float) {
    COMPACT("Compact", 1.3f),
    STANDARD("Standard", 1.6f),
    RELAXED("Spacious", 1.9f)
}

data class Chapter(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String = "",
    val paragraphs: List<String>,
    val estimatedMinutes: Int = 4
)

data class BookManuscript(
    val bookId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val asin: String = "",
    val genre: String = "Fiction",
    val isArc: Boolean = false,
    val arcDaysLeft: Int = 14,
    val chapters: List<Chapter>
)

data class ReaderBookmark(
    val id: String,
    val bookId: String,
    val chapterIndex: Int,
    val chapterTitle: String,
    val paragraphIndex: Int,
    val snippet: String,
    val timestamp: String
)

data class ReaderHighlight(
    val id: String,
    val bookId: String,
    val chapterIndex: Int,
    val textSnippet: String,
    val note: String = "",
    val colorHex: String = "#FFD700", // Gold, Emerald, Coral, Cyan
    val timestamp: String
)

data class ReaderSettings(
    val theme: ReaderTheme = ReaderTheme.LIGHT,
    val font: ReaderFont = ReaderFont.SERIF,
    val fontSizeSp: Float = 16f,
    val lineSpacing: LineSpacing = LineSpacing.STANDARD,
    val isJustified: Boolean = true,
    val isFullscreen: Boolean = false,
    val autoScrollSpeed: Int = 0 // 0 = off, 1 = slow, 2 = medium, 3 = fast
)
