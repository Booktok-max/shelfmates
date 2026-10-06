package com.shelfmates.ui.components

import android.content.Intent
import android.net.Uri
import com.shelfmates.data.local.BookShelfCategory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfAccentGold
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

/**
 * Reusable stand-alone detail view component to display comprehensive information 
 * retrieved from the Google Books API for a selected book.
 *
 * Can be embedded directly in a Screen, a BottomSheet, a Navigation destination, or a Dialog.
 */
@Composable
fun GoogleBookDetailView(
    book: GoogleBookVolumeItem,
    modifier: Modifier = Modifier,
    currentShelfCategory: String? = null,
    onSaveToShelf: ((category: String) -> Unit)? = null,
    onRemoveFromShelf: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onCreatePublicClub: (() -> Unit)? = null,
    onCreateArcCampaign: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isExpandedDescription by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_book_detail_view_${book.id}")
    ) {
        // Optional Top Bar with Dismiss or Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ShelfmatesLightBlue.copy(alpha = 0.18f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = ShelfmatesDeepBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Book Details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Share Intent
                IconButton(
                    onClick = {
                        val shareText = "Check out \"${book.displayTitle}\" by ${book.displayAuthors} on Shelfmates!\n${book.volumeInfo?.infoLink ?: ""}"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Book Details"))
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_google_book_detail")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Hero Header: Cover Artwork + Primary Metadata
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Book Cover with elevation shadow
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(165.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShelfmatesNavy.copy(alpha = 0.08f))
            ) {
                AsyncImage(
                    model = book.secureCoverUrl,
                    contentDescription = "Cover of ${book.displayTitle}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Meta Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Category Chip
                Surface(
                    color = ShelfmatesEmerald.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = book.displayCategory,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = book.displayTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "by ${book.displayAuthors}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Ratings & Reviews Count
                val rating = book.volumeInfo?.averageRating
                if (rating != null && rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Surface(
                            color = ShelfmatesGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ShelfmatesGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        val count = book.volumeInfo.ratingsCount ?: 0
                        if (count > 0) {
                            Text(
                                text = " ($count ratings)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Publisher info if available
                book.volumeInfo?.publisher?.let { pub ->
                    Text(
                        text = pub,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Badges Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookStatBadge(
                label = "Pages",
                value = book.pageCountText,
                icon = Icons.AutoMirrored.Filled.MenuBook,
                modifier = Modifier.weight(1f)
            )
            BookStatBadge(
                label = "Published",
                value = book.publishedYear,
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
            BookStatBadge(
                label = "Language",
                value = (book.volumeInfo?.language ?: "EN").uppercase(),
                icon = Icons.Default.Language,
                modifier = Modifier.weight(1f)
            )
        }

        // ISBN / Industry Identifier Card
        if (book.isbn13.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ISBN-13",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = book.isbn13,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(14.dp))

        // Synopsis & Description
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Synopsis & Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (book.displayDescription.length > 250) {
                Text(
                    text = if (isExpandedDescription) "Show Less" else "Read More",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShelfmatesDeepBlue,
                    modifier = Modifier.clickable { isExpandedDescription = !isExpandedDescription }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = book.displayDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
            maxLines = if (isExpandedDescription) Int.MAX_VALUE else 6,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bookshelf Categorization Card (Room Database Categories)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("detail_bookshelf_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ShelfmatesLightBlue.copy(alpha = 0.12f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, ShelfmatesLightBlue.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save to My Bookshelf",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesDeepBlue
                        )
                    }

                    if (currentShelfCategory != null) {
                        Surface(
                            color = ShelfmatesEmerald.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "✓ $currentShelfCategory",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Save this book into your local library across reading categories:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ONE primary action, chosen by the book's current state.
                // Previously three equal-weight filled buttons (Reading / To
                // Read / Finished) sat side by side, so nothing on the screen
                // read as "the next thing to do". The full shelf choice is
                // still here -- it just moved below the primary action as a
                // quiet secondary row.
                Spacer(modifier = Modifier.height(12.dp))

                ShelfPrimaryAction(
                    label = when {
                        currentShelfCategory == BookShelfCategory.CURRENTLY_READING -> "Continue Reading"
                        currentShelfCategory != null -> "Saved to $currentShelfCategory"
                        else -> "Add to To Read"
                    },
                    onClick = {
                        onSaveToShelf?.invoke(
                            currentShelfCategory ?: BookShelfCategory.TO_READ
                        )
                    },
                    modifier = Modifier.testTag("btn_save_shelf_primary")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        BookShelfCategory.CURRENTLY_READING to ("currently_reading" to "Reading"),
                        BookShelfCategory.TO_READ to ("to_read" to "To Read"),
                        BookShelfCategory.FINISHED to ("finished" to "Finished")
                    ).forEach { (catKey, meta) ->
                        val (testId, catLabel) = meta
                        val isSelected = currentShelfCategory == catKey
                        OutlinedButton(
                            onClick = { onSaveToShelf?.invoke(catKey) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isSelected) ShelfAccentGold
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, ShelfAccentGold) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_save_shelf_$testId"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                        ) {
                            Text(
                                text = catLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (currentShelfCategory != null && onRemoveFromShelf != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = onRemoveFromShelf,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ShelfmatesCoral
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove from Shelf", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions & Buttons Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Google Books Web Reader Link
            book.webReaderUrl?.let { previewUrl ->
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(previewUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handled safely
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ShelfmatesDeepBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preview Book Online", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            if (onCreatePublicClub != null) {
                Button(
                    onClick = onCreatePublicClub,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShelfmatesDeepBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_create_public_club_from_detail")
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Public Club for this Book", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            if (onCreateArcCampaign != null) {
                OutlinedButton(
                    onClick = onCreateArcCampaign,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_create_arc_from_detail"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ShelfmatesDeepBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch ARC Campaign for this Title", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun BookStatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ShelfmatesDeepBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Dialog wrapper for [GoogleBookDetailView]
 */
@Composable
fun GoogleBookDetailDialog(
    book: GoogleBookVolumeItem,
    currentShelfCategory: String? = null,
    onSaveToShelf: ((category: String) -> Unit)? = null,
    onRemoveFromShelf: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onCreatePublicClub: () -> Unit,
    onCreateArcCampaign: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                GoogleBookDetailView(
                    book = book,
                    currentShelfCategory = currentShelfCategory,
                    onSaveToShelf = onSaveToShelf,
                    onRemoveFromShelf = onRemoveFromShelf,
                    onDismiss = onDismiss,
                    onCreatePublicClub = onCreatePublicClub,
                    onCreateArcCampaign = onCreateArcCampaign
                )
            }
        }
    }
}

/**
 * Bottom Sheet wrapper for [GoogleBookDetailView]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleBookDetailBottomSheet(
    book: GoogleBookVolumeItem?,
    currentShelfCategory: String? = null,
    onSaveToShelf: ((category: String) -> Unit)? = null,
    onRemoveFromShelf: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onCreatePublicClub: (GoogleBookVolumeItem) -> Unit,
    onCreateArcCampaign: (GoogleBookVolumeItem) -> Unit
) {
    if (book != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scrollState = rememberScrollState()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
                    .verticalScroll(scrollState)
            ) {
                GoogleBookDetailView(
                    book = book,
                    currentShelfCategory = currentShelfCategory,
                    onSaveToShelf = onSaveToShelf,
                    onRemoveFromShelf = onRemoveFromShelf,
                    onDismiss = onDismiss,
                    onCreatePublicClub = { onCreatePublicClub(book) },
                    onCreateArcCampaign = { onCreateArcCampaign(book) }
                )
            }
        }
    }
}
