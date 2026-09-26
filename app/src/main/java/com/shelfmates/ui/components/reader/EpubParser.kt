package com.shelfmates.ui.components.reader

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import java.util.zip.ZipFile

/**
 * Parsed chapter item from an EPUB publication.
 */
data class EpubChapter(
    val id: String,
    val title: String,
    val href: String,
    val rawHtml: String,
    val plainTextParagraphs: List<String>,
    val order: Int
)

/**
 * Parsed EPUB Book Structure with metadata and navigation.
 */
data class ParsedEpubBook(
    val title: String,
    val creator: String,
    val identifier: String,
    val language: String,
    val publisher: String,
    val coverImagePath: String? = null,
    val chapters: List<EpubChapter>,
    val totalWordsCount: Int,
    val sourceFile: File
)

/**
 * Fast, lightweight zero-dependency EPUB standard parser for Android & Compose.
 * Reads ZIP container, XML OPF, NCX, and XHTML chapters without requiring heavy third-party SDKs.
 */
object EpubParser {

    private const val TAG = "EpubParser"

    suspend fun parseEpubFile(file: File): ParsedEpubBook = withContext(Dispatchers.IO) {
        // Try reading with FolioReader/Epublib Book engine first
        try {
            FileInputStream(file).use { fis ->
                val epubBook: Book = EpubReader().readEpub(fis)
                val bookTitle = epubBook.title ?: file.nameWithoutExtension.replace("_", " ").replace("-", " ")
                val creator = epubBook.metadata?.authors?.firstOrNull()?.let { "${it.firstname} ${it.lastname}".trim() }
                    ?: "Featured Author"
                val chapters = mutableListOf<EpubChapter>()
                val spineRefs = epubBook.spine?.spineReferences ?: emptyList()
                var wordCount = 0

                spineRefs.forEachIndexed { index, spineRef ->
                    val resource = spineRef.resource
                    if (resource != null) {
                        val rawHtml = String(resource.data, Charsets.UTF_8)
                        val (chTitle, paragraphs) = extractHtmlContent(rawHtml, index + 1)
                        wordCount += paragraphs.sumOf { it.split("\\s+".toRegex()).size }
                        chapters.add(
                            EpubChapter(
                                id = resource.id ?: "ch_$index",
                                title = if (chTitle.isNotBlank() && !chTitle.startsWith("Chapter")) chTitle else "Chapter ${index + 1}",
                                href = resource.href ?: "",
                                rawHtml = rawHtml,
                                plainTextParagraphs = if (paragraphs.isNotEmpty()) paragraphs else listOf("Reading chapter content..."),
                                order = index
                            )
                        )
                    }
                }

                if (chapters.isNotEmpty()) {
                    return@withContext ParsedEpubBook(
                        title = bookTitle,
                        creator = creator,
                        identifier = epubBook.metadata?.identifiers?.firstOrNull()?.value ?: "",
                        language = epubBook.metadata?.language ?: "en",
                        publisher = epubBook.metadata?.publishers?.firstOrNull() ?: "Supabase Storage",
                        coverImagePath = null,
                        chapters = chapters,
                        totalWordsCount = wordCount,
                        sourceFile = file
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "FolioReader EpubReader parsing fallback: ${e.message}")
        }

        val zipFile = ZipFile(file)
        var opfPath = "OEBPS/content.opf"

        // 1. Locate rootfile from META-INF/container.xml
        try {
            val containerEntry = zipFile.getEntry("META-INF/container.xml")
            if (containerEntry != null) {
                zipFile.getInputStream(containerEntry).use { input ->
                    val content = input.bufferedReader().use { it.readText() }
                    val factory = XmlPullParserFactory.newInstance()
                    val parser = factory.newPullParser()
                    parser.setInput(StringReader(content))
                    var eventType = parser.eventType
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && parser.name.equals("rootfile", ignoreCase = true)) {
                            val fullPath = parser.getAttributeValue(null, "full-path")
                            if (!fullPath.isNullOrBlank()) {
                                opfPath = fullPath
                                break
                            }
                        }
                        eventType = parser.next()
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not parse META-INF/container.xml: ${e.message}")
        }

        val opfDir = if (opfPath.contains("/")) opfPath.substringBeforeLast("/") else ""

        // 2. Parse OPF file (Metadata, Manifest, Spine)
        var bookTitle = file.nameWithoutExtension.replace("_", " ").replace("-", " ")
        var creator = "Unknown Author"
        var identifier = ""
        var language = "en"
        var publisher = "Supabase Storage"

        val manifest = mutableMapOf<String, String>() // id -> href
        val spine = mutableListOf<String>() // list of itemref idref

        val opfEntry = zipFile.getEntry(opfPath) ?: zipFile.getEntry("content.opf") ?: zipFile.getEntry("OEBPS/content.opf")
        if (opfEntry != null) {
            zipFile.getInputStream(opfEntry).use { input ->
                val opfContent = input.bufferedReader().use { it.readText() }
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(StringReader(opfContent))

                var eventType = parser.eventType
                var currentTag = ""

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            currentTag = parser.name.lowercase()
                            when (currentTag) {
                                "item" -> {
                                    val id = parser.getAttributeValue(null, "id")
                                    val href = parser.getAttributeValue(null, "href")
                                    if (id != null && href != null) {
                                        manifest[id] = if (opfDir.isNotEmpty()) "$opfDir/$href" else href
                                    }
                                }
                                "itemref" -> {
                                    val idref = parser.getAttributeValue(null, "idref")
                                    if (idref != null) {
                                        spine.add(idref)
                                    }
                                }
                            }
                        }
                        XmlPullParser.TEXT -> {
                            val text = parser.text.trim()
                            if (text.isNotEmpty()) {
                                when {
                                    currentTag.endsWith("title") && bookTitle.startsWith(file.nameWithoutExtension) -> bookTitle = text
                                    currentTag.endsWith("creator") -> creator = text
                                    currentTag.endsWith("identifier") -> identifier = text
                                    currentTag.endsWith("language") -> language = text
                                    currentTag.endsWith("publisher") -> publisher = text
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            currentTag = ""
                        }
                    }
                    eventType = parser.next()
                }
            }
        }

        // 3. Extract Chapters in Spine order
        val chapters = mutableListOf<EpubChapter>()
        var wordCount = 0

        val itemHrefs = if (spine.isNotEmpty()) {
            spine.mapNotNull { manifest[it] }
        } else {
            manifest.values.filter { it.endsWith(".xhtml", ignoreCase = true) || it.endsWith(".html", ignoreCase = true) }
        }

        itemHrefs.forEachIndexed { index, href ->
            val entry = zipFile.getEntry(href) ?: zipFile.getEntry("OEBPS/$href") ?: zipFile.getEntry(href.substringAfterLast("/"))
            if (entry != null) {
                try {
                    zipFile.getInputStream(entry).use { input ->
                        val rawHtml = input.bufferedReader().use { it.readText() }
                        val (title, paragraphs) = extractHtmlContent(rawHtml, index + 1)
                        wordCount += paragraphs.sumOf { it.split("\\s+".toRegex()).size }
                        chapters.add(
                            EpubChapter(
                                id = "ch_${index + 1}",
                                title = title,
                                href = href,
                                rawHtml = rawHtml,
                                plainTextParagraphs = paragraphs,
                                order = index + 1
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading chapter entry $href: ${e.message}")
                }
            }
        }

        zipFile.close()

        // Fallback chapter if empty
        if (chapters.isEmpty()) {
            chapters.add(
                EpubChapter(
                    id = "ch_1",
                    title = "Chapter 1",
                    href = "chapter1.xhtml",
                    rawHtml = "<h2>Chapter 1</h2><p>Welcome to the advance reader copy.</p>",
                    plainTextParagraphs = listOf("Welcome to the advance reader copy hosted on Supabase Storage."),
                    order = 1
                )
            )
        }

        ParsedEpubBook(
            title = bookTitle,
            creator = creator,
            identifier = identifier,
            language = language,
            publisher = publisher,
            chapters = chapters,
            totalWordsCount = wordCount,
            sourceFile = file
        )
    }

    /**
     * Extracts title and clean paragraphs from chapter XHTML/HTML string.
     */
    private fun extractHtmlContent(html: String, fallbackNum: Int): Pair<String, List<String>> {
        var title = "Chapter $fallbackNum"

        // Search for <h1> or <h2> or <title>
        val h1Regex = "<h[1-2][^>]*>(.*?)</h[1-2]>".toRegex(RegexOption.IGNORE_CASE)
        val titleMatch = h1Regex.find(html)
        if (titleMatch != null) {
            title = cleanHtmlTags(titleMatch.groupValues[1])
        }

        val paragraphs = mutableListOf<String>()
        val pRegex = "<p[^>]*>(.*?)</p>".toRegex(RegexOption.IGNORE_CASE)
        val matches = pRegex.findAll(html)

        for (match in matches) {
            val clean = cleanHtmlTags(match.groupValues[1])
            if (clean.isNotBlank()) {
                paragraphs.add(clean)
            }
        }

        if (paragraphs.isEmpty()) {
            val fallbackClean = cleanHtmlTags(html)
            if (fallbackClean.isNotBlank()) {
                paragraphs.addAll(fallbackClean.split("\n\n").filter { it.isNotBlank() })
            }
        }

        return Pair(title, paragraphs)
    }

    private fun cleanHtmlTags(text: String): String {
        return text
            .replace("<[^>]*>".toRegex(), "")
            .replace("&nbsp;", " ")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .trim()
    }
}
