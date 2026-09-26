package com.shelfmates.data.remote

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * State representing a download or cache retrieval from Supabase Storage.
 */
sealed class SupabaseDownloadState {
    data object Idle : SupabaseDownloadState()
    data class Downloading(val progressPercent: Int, val bytesDownloaded: Long, val totalBytes: Long) : SupabaseDownloadState()
    data class Success(val file: File, val isFromCache: Boolean, val format: String, val fileSizeMb: Double) : SupabaseDownloadState()
    data class Error(val message: String, val fallbackFile: File? = null) : SupabaseDownloadState()
}

/**
 * Metadata for an ARC manuscript hosted in Supabase Storage.
 */
data class SupabaseArcFile(
    val id: String,
    val bookTitle: String,
    val author: String,
    val fileName: String,
    val format: String, // "EPUB", "PDF"
    val bucketName: String = "arc-manuscripts",
    val storagePath: String,
    val fileSizeMb: Double,
    val isWatermarked: Boolean = true,
    val downloadUrl: String
)

/**
 * Service to download, cache, and upload ARC files to/from Supabase Storage buckets.
 */
object SupabaseStorageService {

    private const val TAG = "SupabaseStorage"
    var supabaseUrl: String = "https://rwyuqplqfexjhoxmswvc.supabase.co"
    var supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ3eXVxcGxxZmV4amhveG1zd3ZjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAwMDAwMDAsImV4cCI6MjA1NTU3NjAwMH0.sample"
    var defaultBucket: String = "arc-manuscripts"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Catalog of Supabase-hosted ARC books for instant demonstration and preview.
     */
    val sampleSupabaseArcs = listOf(
        SupabaseArcFile(
            id = "arc_chrono_pdf",
            bookTitle = "The Last Chronomancer",
            author = "Ray K. Vance",
            fileName = "the_last_chronomancer_arc_v1.pdf",
            format = "PDF",
            storagePath = "fantasy/the_last_chronomancer_arc_v1.pdf",
            fileSizeMb = 2.8,
            downloadUrl = "$supabaseUrl/storage/v1/object/public/$defaultBucket/fantasy/the_last_chronomancer_arc_v1.pdf"
        ),
        SupabaseArcFile(
            id = "arc_chrono_epub",
            bookTitle = "The Last Chronomancer",
            author = "Ray K. Vance",
            fileName = "the_last_chronomancer_arc_v1.epub",
            format = "EPUB",
            storagePath = "fantasy/the_last_chronomancer_arc_v1.epub",
            fileSizeMb = 1.4,
            downloadUrl = "$supabaseUrl/storage/v1/object/public/$defaultBucket/fantasy/the_last_chronomancer_arc_v1.epub"
        ),
        SupabaseArcFile(
            id = "arc_quantum_pdf",
            bookTitle = "Whispers in the Quantum Mist",
            author = "Dr. Samantha Reyes",
            fileName = "quantum_mist_arc_unproofed.pdf",
            format = "PDF",
            storagePath = "scifi/quantum_mist_arc_unproofed.pdf",
            fileSizeMb = 3.2,
            downloadUrl = "$supabaseUrl/storage/v1/object/public/$defaultBucket/scifi/quantum_mist_arc_unproofed.pdf"
        ),
        SupabaseArcFile(
            id = "arc_starlight_epub",
            bookTitle = "The Starlight Cartographer",
            author = "Evelyn Thorne",
            fileName = "starlight_cartographer_advance_copy.epub",
            format = "EPUB",
            storagePath = "ya/starlight_cartographer_advance_copy.epub",
            fileSizeMb = 1.9,
            downloadUrl = "$supabaseUrl/storage/v1/object/public/$defaultBucket/ya/starlight_cartographer_advance_copy.epub"
        )
    )

    /**
     * Get or create cache directory for Supabase ARC files.
     */
    private fun getStorageCacheDir(context: Context): File {
        val dir = File(context.cacheDir, "supabase_arc_storage")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Checks if a file is already downloaded and cached locally.
     */
    fun getCachedArcFile(context: Context, bookId: String, format: String): File? {
        val cacheDir = getStorageCacheDir(context)
        val file = File(cacheDir, "${bookId.lowercase().replace(" ", "_")}.${format.lowercase()}")
        return if (file.exists() && file.length() > 0) file else null
    }

    /**
     * Downloads an ARC file from Supabase Storage with reactive progress tracking,
     * falling back to local generated ARC format when offline or in simulated environments.
     */
    fun downloadArcFromSupabase(
        context: Context,
        bookId: String,
        title: String,
        author: String,
        format: String,
        remoteUrl: String? = null
    ): Flow<SupabaseDownloadState> = flow {
        emit(SupabaseDownloadState.Downloading(0, 0, 100))

        val cacheDir = getStorageCacheDir(context)
        val cleanFormat = if (format.contains("pdf", ignoreCase = true)) "pdf" else "epub"
        val localTargetFile = File(cacheDir, "${bookId.lowercase().replace(" ", "_")}.$cleanFormat")

        // 1. Check local cache
        if (localTargetFile.exists() && localTargetFile.length() > 500) {
            val sizeMb = String.format("%.2f", localTargetFile.length() / (1024.0 * 1024.0)).toDoubleOrNull() ?: 1.2
            emit(SupabaseDownloadState.Downloading(100, localTargetFile.length(), localTargetFile.length()))
            emit(SupabaseDownloadState.Success(localTargetFile, isFromCache = true, format = cleanFormat.uppercase(), fileSizeMb = sizeMb))
            return@flow
        }

        // 2. Determine actual Supabase Storage URL
        val targetUrl = remoteUrl?.takeIf { it.isNotBlank() }
            ?: "$supabaseUrl/storage/v1/object/public/$defaultBucket/${bookId.lowercase()}.$cleanFormat"

        var downloadedSuccessfully = false

        // 3. Attempt download via OkHttp if network is available
        try {
            emit(SupabaseDownloadState.Downloading(15, 150_000, 1_000_000))
            val request = Request.Builder()
                .url(targetUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .build()

            val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    val contentLength = body.contentLength()
                    val inputStream: InputStream = body.byteStream()
                    val outputStream = FileOutputStream(localTargetFile)

                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead: Long = 0

                    withContext(Dispatchers.IO) {
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            val progress = if (contentLength > 0) ((totalRead * 100) / contentLength).toInt() else 60
                            // Emit intermediate progress
                        }
                        outputStream.flush()
                        outputStream.close()
                        inputStream.close()
                    }

                    if (localTargetFile.exists() && localTargetFile.length() > 500) {
                        downloadedSuccessfully = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Direct Supabase network fetch for $targetUrl returned: ${e.message}. Using built-in ARC compiler.")
        }

        // 4. If remote is unreachable or empty, handle according to demo vs production mode
        if (!downloadedSuccessfully) {
            if (BuildConfig.DEBUG) {
                emit(SupabaseDownloadState.Downloading(45, 450_000, 1_000_000))
                withContext(Dispatchers.IO) {
                    if (cleanFormat == "pdf") {
                        generateSamplePdfArc(localTargetFile, title, author, bookId)
                    } else {
                        generateSampleEpubArc(localTargetFile, title, author, bookId)
                    }
                }
                emit(SupabaseDownloadState.Downloading(90, 900_000, 1_000_000))
            } else {
                emit(SupabaseDownloadState.Error("Failed to stream ARC from Supabase Storage in production mode. Network error or missing file.", null))
                return@flow
            }
        }

        val sizeMb = String.format("%.2f", localTargetFile.length() / (1024.0 * 1024.0)).toDoubleOrNull() ?: 1.5
        emit(SupabaseDownloadState.Success(
            file = localTargetFile,
            isFromCache = false,
            format = cleanFormat.uppercase(),
            fileSizeMb = sizeMb
        ))
    }

    /**
     * Synthesizes a valid multi-page PDF ARC document with cover, copyright,
     * ARC watermark, and structured chapters using Android's native PdfDocument.
     */
    private fun generateSamplePdfArc(targetFile: File, title: String, author: String, bookId: String) {
        val document = PdfDocument()
        val pageWidth = 595 // A4 standard pt at 72dpi
        val pageHeight = 842

        val titlePaint = Paint().apply {
            color = AndroidColor.rgb(20, 30, 55)
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val authorPaint = Paint().apply {
            color = AndroidColor.rgb(180, 140, 20)
            textSize = 15f
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = AndroidColor.rgb(30, 41, 59)
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = AndroidColor.rgb(51, 65, 85)
            textSize = 12f
            isAntiAlias = true
        }

        val watermarkPaint = Paint().apply {
            color = AndroidColor.argb(45, 220, 38, 38)
            textSize = 34f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = AndroidColor.rgb(148, 163, 184)
            textSize = 10f
            isAntiAlias = true
        }

        // --- Page 1: ARC Cover & Watermark ---
        val page1Info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = document.startPage(page1Info)
        val canvas1 = page1.canvas

        // Decorative background border
        val borderPaint = Paint().apply {
            color = AndroidColor.rgb(226, 232, 240)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas1.drawRect(36f, 36f, (pageWidth - 36).toFloat(), (pageHeight - 36).toFloat(), borderPaint)

        // Watermark diagonal
        canvas1.save()
        canvas1.rotate(-35f, (pageWidth / 2).toFloat(), (pageHeight / 2).toFloat())
        canvas1.drawText("ADVANCE READING COPY — NOT FOR SALE", 60f, (pageHeight / 2).toFloat(), watermarkPaint)
        canvas1.restore()

        canvas1.drawText("SHELFMATES ARC READER", 60f, 100f, authorPaint)
        canvas1.drawText(title, 60f, 150f, titlePaint)
        canvas1.drawText("by $author", 60f, 185f, authorPaint)

        val notice = listOf(
            "This is an uncorrected Advance Reader Copy (ARC).",
            "Content and pagination may vary from the final published edition.",
            "Hosted securely on Supabase Storage for approved reviewers.",
            "Please post your honest review on Amazon and Goodreads by the campaign deadline."
        )
        var noticeY = 240f
        for (line in notice) {
            canvas1.drawText(line, 60f, noticeY, bodyPaint)
            noticeY += 22f
        }

        canvas1.drawText("Page 1 of 4 • Confidential Review Copy", 60f, (pageHeight - 50).toFloat(), footerPaint)
        document.finishPage(page1)

        // --- Pages 2 to 4: Sample ARC Chapters ---
        val sampleChapters = listOf(
            Pair("Chapter 1: The Fractured Hourglass", listOf(
                "The pendulum had stopped swinging precisely three minutes past midnight. In the ancient citadel of Oakhaven, where time was harvested like ore from the deep earth, stillness was not peaceful—it was a catastrophe.",
                "Valen knelt upon the cold obsidian pavers of the Great Spire. Beneath his fingertips, the brass conduits that channeled the Temporal Weave vibrated with an unnatural dissonance. A single grain of golden chronomite rolled across the stone.",
                "\"You should not touch that,\" a voice warned from the archway. Lyra stepped forward, her silver vestments catching the pale lunar glow. In her palm, she held a prism glowing with violet luminescence.",
                "\"If I don't touch it,\" Valen replied without turning, \"the morning will never arrive. The Eastern provinces have already slipped three hours into yesterday.\""
            )),
            Pair("Chapter 2: Subterranean Vaults", listOf(
                "Deep beneath the foundations, sixty fathoms down where the subterranean molten tides churned, giant iron cogs four stories tall turned with ponderous gravity.",
                "Valen held his lodestone aloft. Its violet glow cast dancing shadows across rusted catwalks and ancient copper steam valves. Steam hissed from hairline fissures in the pipes, smelling of ionized copper and ozone.",
                "\"The temporal friction has welded the escapement wheel to the flywheel,\" Lyra whispered. \"Someone sabotaged the equilibrium gate with Void-runes.\""
            )),
            Pair("Chapter 3: The Weaver's Paradox", listOf(
                "The Sentinels lunged forward, clockwork limbs whirring at blinding velocity. Their bladed appendages cleaved through the iron railing as if it were parchment.",
                "Valen focused his willpower through the bracer. In an instant, the local seconds stretched into molasses. Condensation droplets hung frozen mid-air; sparks from the gears turned into suspended diamonds of fire.",
                "\"Hold the barrier for ten heartbeats!\" Valen shouted. \"I'm reversing the main gear train!\""
            ))
        )

        sampleChapters.forEachIndexed { index, (chTitle, paragraphs) ->
            val pageNum = index + 2
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Page Header
            canvas.drawText("$title — $author", 50f, 45f, footerPaint)
            val lineP = Paint().apply { color = AndroidColor.rgb(226, 232, 240); strokeWidth = 1f }
            canvas.drawLine(50f, 55f, (pageWidth - 50).toFloat(), 55f, lineP)

            // Chapter Title
            canvas.drawText(chTitle, 50f, 95f, headerPaint)

            // Body Paragraphs
            var currentY = 135f
            for (p in paragraphs) {
                val words = p.split(" ")
                var currentLine = ""
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (bodyPaint.measureText(testLine) > (pageWidth - 100)) {
                        canvas.drawText(currentLine, 50f, currentY, bodyPaint)
                        currentY += 18f
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, 50f, currentY, bodyPaint)
                    currentY += 28f
                }
            }

            // Footer
            canvas.drawLine(50f, (pageHeight - 55).toFloat(), (pageWidth - 50).toFloat(), (pageHeight - 55).toFloat(), lineP)
            canvas.drawText("Page $pageNum of 4 • Supabase Storage ARC Engine", 50f, (pageHeight - 40).toFloat(), footerPaint)

            document.finishPage(page)
        }

        val out = FileOutputStream(targetFile)
        document.writeTo(out)
        out.flush()
        out.close()
        document.close()
    }

    /**
     * Synthesizes a valid EPUB ZIP-container archive with OPF manifest,
     * NCX table of contents, and XHTML chapter files.
     */
    private fun generateSampleEpubArc(targetFile: File, title: String, author: String, bookId: String) {
        val zipOut = ZipOutputStream(FileOutputStream(targetFile))

        // 1. mimetype (MUST be first and uncompressed in EPUB standard)
        val mimeEntry = ZipEntry("mimetype")
        zipOut.putNextEntry(mimeEntry)
        zipOut.write("application/epub+zip".toByteArray())
        zipOut.closeEntry()

        // 2. META-INF/container.xml
        val containerXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
              <rootfiles>
                <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
              </rootfiles>
            </container>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("META-INF/container.xml"))
        zipOut.write(containerXml.toByteArray())
        zipOut.closeEntry()

        // 3. OEBPS/content.opf
        val opfXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <package xmlns="http://www.idpf.org/2007/opf" unique-identifier="BookId" version="2.0">
              <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
                <dc:title>$title (Advance Reader Copy)</dc:title>
                <dc:creator>$author</dc:creator>
                <dc:identifier id="BookId">$bookId-arc-supabase</dc:identifier>
                <dc:language>en</dc:language>
                <dc:publisher>Shelfmates Supabase Storage</dc:publisher>
              </metadata>
              <manifest>
                <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
                <item id="ch1" href="chapter1.xhtml" media-type="application/xhtml+xml"/>
                <item id="ch2" href="chapter2.xhtml" media-type="application/xhtml+xml"/>
                <item id="ch3" href="chapter3.xhtml" media-type="application/xhtml+xml"/>
              </manifest>
              <spine toc="ncx">
                <itemref idref="ch1"/>
                <itemref idref="ch2"/>
                <itemref idref="ch3"/>
              </spine>
            </package>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("OEBPS/content.opf"))
        zipOut.write(opfXml.toByteArray())
        zipOut.closeEntry()

        // 4. OEBPS/toc.ncx
        val ncxXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
              <head>
                <meta name="dtb:uid" content="$bookId-arc-supabase"/>
              </head>
              <docTitle><text>$title</text></docTitle>
              <navMap>
                <navPoint id="navPoint-1" playOrder="1">
                  <navLabel><text>Chapter 1: The Fractured Hourglass</text></navLabel>
                  <content src="chapter1.xhtml"/>
                </navPoint>
                <navPoint id="navPoint-2" playOrder="2">
                  <navLabel><text>Chapter 2: Subterranean Vaults</text></navLabel>
                  <content src="chapter2.xhtml"/>
                </navPoint>
                <navPoint id="navPoint-3" playOrder="3">
                  <navLabel><text>Chapter 3: The Weaver's Paradox</text></navLabel>
                  <content src="chapter3.xhtml"/>
                </navPoint>
              </navMap>
            </ncx>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("OEBPS/toc.ncx"))
        zipOut.write(ncxXml.toByteArray())
        zipOut.closeEntry()

        // 5. Chapter XHTMLs
        val ch1Html = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head><title>Chapter 1</title></head>
            <body>
              <h2>Chapter 1: The Fractured Hourglass</h2>
              <p class="arc-badge"><b>[ADVANCE READER COPY • HOSTED ON SUPABASE STORAGE]</b></p>
              <p>The pendulum had stopped swinging precisely three minutes past midnight. In the city of Oakhaven, where the passage of time had been traded like grain and iron for six hundred years, stillness was not mere silence—it was an omen.</p>
              <p>Valen knelt upon the cold obsidian pavers of the Great Spire. Beneath his fingertips, the brass conduits that channeled the Temporal Weave vibrated with a dissonant, frantic hum. A single grain of golden chronomite rolled across the stone, glowing with the luminescence of dying suns.</p>
              <p>"You should not touch that," a voice echoed from the archway. Lyra stepped forward, her silver-threaded vestments catching the pale moonlight filtering through the broken stained glass.</p>
            </body>
            </html>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("OEBPS/chapter1.xhtml"))
        zipOut.write(ch1Html.toByteArray())
        zipOut.closeEntry()

        val ch2Html = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head><title>Chapter 2</title></head>
            <body>
              <h2>Chapter 2: Subterranean Vaults</h2>
              <p>Deep beneath the foundations of Oakhaven, sixty fathoms below the bustling markets and the soaring spires, lay the Sub-Terrane Vaults. Here, giant brass cogs four stories tall turned with ponderous gravity.</p>
              <p>Valen held his lodestone aloft. Its violet glow cast long, dancing shadows across rusted catwalks and ancient copper steam pipes. Steam hissed from hairline fissures in the valves, smelling of ionized copper and ozone.</p>
              <p>"Look at the main axle," Lyra whispered. "The temporal friction has welded the escapement wheel to the flywheel."</p>
            </body>
            </html>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("OEBPS/chapter2.xhtml"))
        zipOut.write(ch2Html.toByteArray())
        zipOut.closeEntry()

        val ch3Html = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head><title>Chapter 3</title></head>
            <body>
              <h2>Chapter 3: The Weaver's Paradox</h2>
              <p>The Sentinels lunged forward, clockwork limbs whirring at blinding speed. Their bladed appendages cleaved through the iron railing as if it were parchment.</p>
              <p>Valen focused on his lodestone. In an instant, the world slowed to a molasses crawl. Droplets of condensation hung frozen mid-air; the sparks jumping from the construct's joints turned into suspended diamonds of fire.</p>
              <p>"Hold the barrier for ten heartbeats!" Valen shouted. "I'm reversing the gear train!"</p>
            </body>
            </html>
        """.trimIndent()
        zipOut.putNextEntry(ZipEntry("OEBPS/chapter3.xhtml"))
        zipOut.write(ch3Html.toByteArray())
        zipOut.closeEntry()

        zipOut.flush()
        zipOut.close()
    }
}
