package com.shelfmates.ui.components.reader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.model.ReaderTheme
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesGold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/**
 * High-performance PDF Rendering Engine using Android's native PdfRenderer with memory-efficient LRU cache.
 */
class PdfDocumentHolder(private val file: File) {
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null

    val pageCount: Int
        get() = pdfRenderer?.pageCount ?: 0

    // 16 MB bitmap cache for smooth page navigation
    private val bitmapCache = object : LruCache<Int, Bitmap>(16 * 1024 * 1024) {
        override fun sizeOf(key: Int, value: Bitmap): Int = value.byteCount
    }

    fun open(): Boolean {
        return try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            fileDescriptor?.let {
                pdfRenderer = PdfRenderer(it)
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("PdfDocumentHolder", "Failed to open PDF file: ${e.message}")
            false
        }
    }

    suspend fun renderPage(pageIndex: Int, targetWidth: Int = 1080): Bitmap? = withContext(Dispatchers.IO) {
        if (pdfRenderer == null || pageIndex < 0 || pageIndex >= pageCount) return@withContext null

        bitmapCache.get(pageIndex)?.let { return@withContext it }

        try {
            val renderer = pdfRenderer ?: return@withContext null
            val page = synchronized(renderer) {
                renderer.openPage(pageIndex)
            }

            val aspectRatio = page.height.toFloat() / page.width.toFloat()
            val targetHeight = (targetWidth * aspectRatio).toInt().coerceAtLeast(100)

            val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(android.graphics.Color.WHITE)

            synchronized(renderer) {
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
            }

            bitmapCache.put(pageIndex, bitmap)
            bitmap
        } catch (e: Exception) {
            Log.e("PdfDocumentHolder", "Error rendering PDF page $pageIndex: ${e.message}")
            null
        }
    }

    fun close() {
        try {
            pdfRenderer?.close()
            fileDescriptor?.close()
            bitmapCache.evictAll()
        } catch (e: Exception) {
            Log.e("PdfDocumentHolder", "Error closing renderer: ${e.message}")
        }
    }
}

/**
 * Jetpack Compose PDF Reader Component supporting zoom, page swipe, thumbnail navigation,
 * and theme filter overlays (Light, Sepia, Night).
 */
@Composable
fun ComposePdfViewer(
    pdfFile: File,
    readerTheme: ReaderTheme,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageChanged: (Int, Int) -> Unit = { _, _ -> },
    onTap: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var holder by remember(pdfFile.absolutePath) { mutableStateOf<PdfDocumentHolder?>(null) }
    var totalPages by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(initialPage) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }
    var isThumbnailStripOpen by remember { mutableStateOf(false) }

    // Zoom & Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.8f, 4.0f)
        if (scale > 1f) {
            offset += panChange
        } else {
            offset = Offset.Zero
        }
    }

    // Initialize PDF Holder
    DisposableEffect(pdfFile.absolutePath) {
        val newHolder = PdfDocumentHolder(pdfFile)
        if (newHolder.open()) {
            holder = newHolder
            totalPages = newHolder.pageCount
        }
        onDispose {
            newHolder.close()
        }
    }

    LaunchedEffect(initialPage, totalPages) {
        if (totalPages > 0 && initialPage in 0 until totalPages) {
            currentPage = initialPage
        } else if (initialPage > 0) {
            currentPage = initialPage
        }
    }

    // Render current page when page index changes
    LaunchedEffect(currentPage, holder) {
        holder?.let { h ->
            isLoadingPage = true
            val bmp = h.renderPage(currentPage, targetWidth = 1200)
            currentBitmap = bmp
            isLoadingPage = false
            onPageChanged(currentPage + 1, totalPages)
        }
    }

    // Theme filter matrix
    val colorFilter = remember(readerTheme) {
        when (readerTheme) {
            ReaderTheme.SEPIA -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        0.95f, 0f, 0f, 0f, 15f,
                        0f, 0.88f, 0f, 0f, 10f,
                        0f, 0f, 0.75f, 0f, -5f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            ReaderTheme.DARK -> {
                // Invert colors for night reading
                val matrix = ColorMatrix(
                    floatArrayOf(
                        -0.85f, 0f, 0f, 0f, 220f,
                        0f, -0.85f, 0f, 0f, 220f,
                        0f, 0f, -0.85f, 0f, 220f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            ReaderTheme.FROSTED -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        0.85f, 0f, 0f, 0f, 5f,
                        0f, 0.92f, 0f, 0f, 15f,
                        0f, 0f, 0.98f, 0f, 25f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            ReaderTheme.LIGHT -> null
        }
    }

    val backgroundColor = when (readerTheme) {
        ReaderTheme.LIGHT -> Color(0xFFE2E8F0)
        ReaderTheme.SEPIA -> Color(0xFFEFE6D5)
        ReaderTheme.DARK -> Color(0xFF0F172A)
        ReaderTheme.FROSTED -> Color(0xFF1E293B)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onTap() }
    ) {
        // Main PDF Page View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState),
            contentAlignment = Alignment.Center
        ) {
            if (currentBitmap != null) {
                Image(
                    bitmap = currentBitmap!!.asImageBitmap(),
                    contentDescription = "PDF Page ${currentPage + 1}",
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .shadow(8.dp, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.FillWidth,
                    colorFilter = colorFilter
                )
            }

            if (isLoadingPage) {
                CircularProgressIndicator(
                    color = ShelfmatesDeepBlue,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Floating Zoom / Quick Reset Tool
        if (scale > 1.1f) {
            Surface(
                onClick = {
                    scale = 1f
                    offset = Offset.Zero
                },
                shape = RoundedCornerShape(20.dp),
                color = ShelfmatesDeepBlue,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.FitScreen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Zoom (${(scale * 100).toInt()}%)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bottom PDF Navigation Bar (Page Jump Slider + Next/Prev Buttons)
        Surface(
            color = if (readerTheme == ReaderTheme.DARK) Color(0xFF1E293B) else Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Page slider & counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (currentPage > 0) {
                                currentPage--
                                scale = 1f
                                offset = Offset.Zero
                            }
                        },
                        enabled = currentPage > 0
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous Page")
                    }

                    Text(
                        text = "Page ${currentPage + 1} of ${totalPages.coerceAtLeast(1)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (readerTheme == ReaderTheme.DARK) Color.White else Color(0xFF1E293B)
                    )

                    IconButton(
                        onClick = {
                            if (currentPage < totalPages - 1) {
                                currentPage++
                                scale = 1f
                                offset = Offset.Zero
                            }
                        },
                        enabled = currentPage < totalPages - 1
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next Page")
                    }
                }

                if (totalPages > 1) {
                    Slider(
                        value = currentPage.toFloat(),
                        onValueChange = {
                            currentPage = it.roundToInt().coerceIn(0, totalPages - 1)
                            scale = 1f
                            offset = Offset.Zero
                        },
                        valueRange = 0f..(totalPages - 1).toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = ShelfmatesDeepBlue,
                            activeTrackColor = ShelfmatesDeepBlue
                        ),
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }

                // Quick page jump thumbnails toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { scale = (scale + 0.25f).coerceAtMost(4f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { scale = (scale - 0.25f).coerceAtLeast(0.8f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                        }
                    }

                    Surface(
                        onClick = { isThumbnailStripOpen = !isThumbnailStripOpen },
                        color = if (isThumbnailStripOpen) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = if (isThumbnailStripOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Thumbnails",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isThumbnailStripOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Expandable Horizontal Thumbnail Strip
                AnimatedVisibility(visible = isThumbnailStripOpen) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(totalPages) { pageIdx ->
                            val isSelected = pageIdx == currentPage
                            Surface(
                                onClick = {
                                    currentPage = pageIdx
                                    scale = 1f
                                    offset = Offset.Zero
                                },
                                shape = RoundedCornerShape(6.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ShelfmatesGold) else null,
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(width = 54.dp, height = 72.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "P.${pageIdx + 1}",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
