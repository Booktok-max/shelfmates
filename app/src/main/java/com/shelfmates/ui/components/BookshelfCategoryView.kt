package com.shelfmates.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.shelfmates.data.local.BookShelfCategory
import com.shelfmates.data.local.CustomShelfEntity
import com.shelfmates.data.local.SavedBookEntity
import com.shelfmates.data.model.CloudSyncState
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesCrimson
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

/**
 * Main Bookshelf Component displayed on the Home screen,
 * allowing readers to view and organize saved Google Books metadata into
 * custom shelves ('Currently Reading', 'To Read', 'Finished', plus custom user shelves)
 * with Cloud Firestore synchronization.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookshelfCategorySection(
    savedBooks: List<SavedBookEntity>,
    customShelves: List<CustomShelfEntity> = emptyList(),
    cloudSyncState: CloudSyncState = CloudSyncState.Idle,
    selectedCategoryFilter: String = "All",
    onCategoryFilterChange: (String) -> Unit,
    onUpdateCategory: (String, String, String) -> Unit, // (id, title, newCategory)
    onUpdateNotes: (String, String, Int) -> Unit = { _, _, _ -> },
    onRemoveBook: (String, String) -> Unit, // (id, title)
    onSyncWithCloud: () -> Unit = {},
    onCreateCustomShelf: (String, String) -> Unit = { _, _ -> },
    onDeleteCustomShelf: (String, String) -> Unit = { _, _ -> },
    onNavigateToSearch: () -> Unit,
    onSelectBookGoogleId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreateShelfDialogOpen by remember { mutableStateOf(false) }
    var shelfPendingDelete by remember { mutableStateOf<CustomShelfEntity?>(null) }
    var bookForNotesDialog by remember { mutableStateOf<SavedBookEntity?>(null) }

    // Counts for standard shelves
    val readingCount = savedBooks.count { it.category == BookShelfCategory.CURRENTLY_READING }
    val toReadCount = savedBooks.count {
        it.category == BookShelfCategory.TO_READ || it.category == BookShelfCategory.WANT_TO_READ
    }
    val finishedCount = savedBooks.count { it.category == BookShelfCategory.FINISHED }

    // User-created custom shelves (non-default)
    val userCustomShelves = remember(customShelves) {
        customShelves.filter { shelf ->
            shelf.name != BookShelfCategory.CURRENTLY_READING &&
            shelf.name != BookShelfCategory.TO_READ &&
            shelf.name != BookShelfCategory.WANT_TO_READ &&
            shelf.name != BookShelfCategory.FINISHED
        }
    }

    // All available shelves list (for dropdowns and dialogs)
    val allShelfNames = remember(userCustomShelves) {
        listOf(
            BookShelfCategory.CURRENTLY_READING,
            BookShelfCategory.TO_READ,
            BookShelfCategory.FINISHED
        ) + userCustomShelves.map { it.name }
    }

    val filteredBooks = remember(savedBooks, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            savedBooks
        } else if (selectedCategoryFilter == BookShelfCategory.TO_READ) {
            savedBooks.filter {
                it.category.equals(BookShelfCategory.TO_READ, ignoreCase = true) ||
                it.category.equals(BookShelfCategory.WANT_TO_READ, ignoreCase = true)
            }
        } else {
            savedBooks.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("virtual_bookshelf_section"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title + Cloud Sync Badge + Add Book Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(listOf(ShelfmatesDeepBlue, ShelfmatesNavy)),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Virtual Bookshelf",
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${savedBooks.size} titles",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            // Firestore Cloud Sync Indicator
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (cloudSyncState) {
                                    is CloudSyncState.Syncing -> ShelfmatesAmber.copy(alpha = 0.18f)
                                    is CloudSyncState.Synced -> ShelfmatesEmerald.copy(alpha = 0.15f)
                                    is CloudSyncState.Error -> ShelfmatesCrimson.copy(alpha = 0.15f)
                                    is CloudSyncState.Idle -> ShelfmatesDeepBlue.copy(alpha = 0.12f)
                                },
                                modifier = Modifier.clickable { onSyncWithCloud() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    when (cloudSyncState) {
                                        is CloudSyncState.Syncing -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(9.dp),
                                                strokeWidth = 1.5.dp,
                                                color = ShelfmatesAmber
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Syncing...",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ShelfmatesAmber
                                            )
                                        }
                                        is CloudSyncState.Synced -> {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = "Cloud Synced",
                                                tint = ShelfmatesEmerald,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Synced",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ShelfmatesEmerald
                                            )
                                        }
                                        is CloudSyncState.Error -> {
                                            Icon(
                                                imageVector = Icons.Default.Cloud,
                                                contentDescription = "Cloud Error",
                                                tint = ShelfmatesCrimson,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Tap to Retry",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ShelfmatesCrimson
                                            )
                                        }
                                        is CloudSyncState.Idle -> {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = "Cloud Ready",
                                                tint = ShelfmatesDeepBlue,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Cloud Sync",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ShelfmatesDeepBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSyncWithCloud,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_sync_firestore")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync now",
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedButton(
                        onClick = onNavigateToSearch,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShelfmatesDeepBlue),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_bookshelf_add_book")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category & Custom Shelf Filter Chips + "+ New Shelf"
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. "All"
                item {
                    val isAllSelected = selectedCategoryFilter == "All"
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { onCategoryFilterChange("All") },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "All",
                                    fontSize = 12.sp,
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                ShelfBadgeCount(count = savedBooks.size, isSelected = isAllSelected)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShelfmatesDeepBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 2. Default: Currently Reading
                item {
                    val isSelected = selectedCategoryFilter.equals(BookShelfCategory.CURRENTLY_READING, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChange(BookShelfCategory.CURRENTLY_READING) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📖 Reading", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(5.dp))
                                ShelfBadgeCount(count = readingCount, isSelected = isSelected)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShelfmatesDeepBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 3. Default: To Read
                item {
                    val isSelected = selectedCategoryFilter.equals(BookShelfCategory.TO_READ, ignoreCase = true) ||
                            selectedCategoryFilter.equals(BookShelfCategory.WANT_TO_READ, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChange(BookShelfCategory.TO_READ) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔖 To Read", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(5.dp))
                                ShelfBadgeCount(count = toReadCount, isSelected = isSelected)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShelfmatesDeepBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 4. Default: Finished
                item {
                    val isSelected = selectedCategoryFilter.equals(BookShelfCategory.FINISHED, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChange(BookShelfCategory.FINISHED) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✅ Finished", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(5.dp))
                                ShelfBadgeCount(count = finishedCount, isSelected = isSelected)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShelfmatesDeepBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 5. Custom User Shelves
                items(userCustomShelves) { customShelf ->
                    val isSelected = selectedCategoryFilter.equals(customShelf.name, ignoreCase = true)
                    val count = savedBooks.count { it.category.equals(customShelf.name, ignoreCase = true) }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterChange(customShelf.name) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${customShelf.iconEmoji} ${customShelf.name}", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(5.dp))
                                ShelfBadgeCount(count = count, isSelected = isSelected)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShelfmatesDeepBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 6. Action Chip: "+ New Shelf"
                item {
                    Surface(
                        onClick = { isCreateShelfDialogOpen = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_create_custom_shelf")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ShelfmatesDeepBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New Shelf",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesDeepBlue
                            )
                        }
                    }
                }
            }

            // Banner if currently viewing a custom shelf: option to delete shelf
            val activeCustomShelf = userCustomShelves.firstOrNull { it.name.equals(selectedCategoryFilter, ignoreCase = true) }
            if (activeCustomShelf != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = ShelfmatesLightBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeCustomShelf.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Custom Shelf: ${activeCustomShelf.name}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShelfmatesDeepBlue
                            )
                        }
                        TextButton(
                            onClick = { shelfPendingDelete = activeCustomShelf },
                            colors = ButtonDefaults.textButtonColors(contentColor = ShelfmatesCrimson)
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Shelf", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Books Grid or Empty State
            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedCategoryFilter == "All") "Your virtual bookshelf is empty" else "No books in '$selectedCategoryFilter'",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Search the book catalog to look up book metadata and add titles to your virtual shelves.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToSearch,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find Books in Catalog", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    filteredBooks.forEach { book ->
                        SavedBookItemCard(
                            savedBook = book,
                            allShelfNames = allShelfNames,
                            onUpdateCategory = { newCat -> onUpdateCategory(book.id, book.title, newCat) },
                            onEditNotes = { bookForNotesDialog = book },
                            onRemove = { onRemoveBook(book.id, book.title) },
                            onSelectGoogleId = onSelectBookGoogleId
                        )
                    }
                }
            }
        }
    }

    // --- Create Custom Shelf Dialog ---
    if (isCreateShelfDialogOpen) {
        CreateCustomShelfDialog(
            onDismiss = { isCreateShelfDialogOpen = false },
            onConfirm = { name, emoji ->
                isCreateShelfDialogOpen = false
                onCreateCustomShelf(name, emoji)
            }
        )
    }

    // --- Delete Shelf Confirmation Dialog ---
    if (shelfPendingDelete != null) {
        val targetShelf = shelfPendingDelete!!
        AlertDialog(
            onDismissRequest = { shelfPendingDelete = null },
            title = { Text("Delete Shelf \"${targetShelf.name}\"?") },
            text = {
                Text("Any books currently on this shelf will be safely moved to 'To Read'. This shelf will also be removed from Cloud Firestore.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomShelf(targetShelf.id, targetShelf.name)
                        shelfPendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesCrimson)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { shelfPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Book Notes & Rating Dialog ---
    if (bookForNotesDialog != null) {
        val targetBook = bookForNotesDialog!!
        BookNotesAndRatingDialog(
            book = targetBook,
            onDismiss = { bookForNotesDialog = null },
            onSave = { notes, rating ->
                onUpdateNotes(targetBook.id, notes, rating)
                bookForNotesDialog = null
            }
        )
    }
}

@Composable
private fun ShelfBadgeCount(count: Int, isSelected: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = "$count",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
        )
    }
}

/**
 * Individual Saved Book card on the virtual bookshelf.
 */
@Composable
fun SavedBookItemCard(
    savedBook: SavedBookEntity,
    allShelfNames: List<String>,
    onUpdateCategory: (String) -> Unit,
    onEditNotes: () -> Unit,
    onRemove: () -> Unit,
    onSelectGoogleId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectGoogleId(savedBook.googleBooksId) }
            .testTag("saved_book_card_${savedBook.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Book Cover Image
            Box(
                modifier = Modifier
                    .width(62.dp)
                    .height(90.dp)
                    .shadow(3.dp, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .background(ShelfmatesNavy.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = savedBook.coverUrl,
                    contentDescription = "Cover of ${savedBook.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Shelf Category Badge & Cloud Indicator Row
                val (badgeBg, badgeFg, icon) = when (savedBook.category) {
                    BookShelfCategory.CURRENTLY_READING -> Triple(ShelfmatesEmerald.copy(alpha = 0.15f), ShelfmatesEmerald, Icons.Default.AutoStories)
                    BookShelfCategory.FINISHED -> Triple(ShelfmatesDeepBlue.copy(alpha = 0.15f), ShelfmatesDeepBlue, Icons.Default.CheckCircle)
                    BookShelfCategory.TO_READ, BookShelfCategory.WANT_TO_READ -> Triple(ShelfmatesAmber.copy(alpha = 0.18f), ShelfmatesCoral, Icons.Default.Bookmark)
                    else -> Triple(ShelfmatesNavy.copy(alpha = 0.12f), ShelfmatesNavy, Icons.Default.BookmarkBorder)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = badgeBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = badgeFg, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = savedBook.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeFg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Cloud Synced Status Icon
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Cloud Synced",
                            tint = ShelfmatesEmerald.copy(alpha = 0.8f),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { isMenuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            Text(
                                text = "Move to Shelf:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                            allShelfNames.forEach { shelfName ->
                                if (shelfName != savedBook.category) {
                                    DropdownMenuItem(
                                        text = { Text("Move to $shelfName", fontSize = 13.sp) },
                                        onClick = {
                                            isMenuExpanded = false
                                            onUpdateCategory(shelfName)
                                        }
                                    )
                                }
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Personal Notes & Rating", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onEditNotes()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Remove from Bookshelf", color = ShelfmatesCrimson, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ShelfmatesCrimson, modifier = Modifier.size(16.dp))
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onRemove()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Title
                Text(
                    text = savedBook.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Author
                Text(
                    text = "by ${savedBook.authors}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Metadata tags (Pages, Rating, Genre)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (savedBook.rating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = ShelfmatesGold, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = String.format("%.1f", savedBook.rating), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (savedBook.pageCount > 0) {
                        Text(text = "${savedBook.pageCount} p", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (savedBook.genre.isNotBlank()) {
                            Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (savedBook.genre.isNotBlank()) {
                        Text(
                            text = savedBook.genre,
                            fontSize = 11.sp,
                            color = ShelfmatesDeepBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Personal Notes Preview (if user added notes)
                if (savedBook.notes.isNotBlank() || savedBook.personalRating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (savedBook.personalRating > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(savedBook.personalRating) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = ShelfmatesGold, modifier = Modifier.size(10.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            if (savedBook.notes.isNotBlank()) {
                                Text(
                                    text = "“${savedBook.notes}”",
                                    fontSize = 10.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to create a new custom shelf with custom name and emoji icon.
 */
@Composable
fun CreateCustomShelfDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var shelfName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("📚") }
    val emojiList = listOf("📚", "📖", "🔖", "🚀", "⭐", "💡", "💖", "🏆", "☕", "🎯", "🔮", "🌿")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Custom Shelf",
                    fontFamily = BookDisplayFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Organize books into your own cloud-synced shelves (e.g., 'Favorites', 'Book Club 2026').",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Shelf Name Field
                OutlinedTextField(
                    value = shelfName,
                    onValueChange = { shelfName = it },
                    label = { Text("Shelf Name") },
                    placeholder = { Text("e.g. Sci-Fi Favorites, Summer Reads") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_shelf_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Shelf Icon:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Emoji Picker Grid
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(emojiList) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Surface(
                            onClick = { selectedEmoji = emoji },
                            shape = CircleShape,
                            color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (shelfName.isNotBlank()) {
                                onConfirm(shelfName.trim(), selectedEmoji)
                            }
                        },
                        enabled = shelfName.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                        modifier = Modifier.testTag("btn_confirm_create_shelf")
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create & Sync Shelf")
                    }
                }
            }
        }
    }
}

/**
 * Dialog to edit personal reading notes and 1-5 star rating for a saved book.
 */
@Composable
fun BookNotesAndRatingDialog(
    book: SavedBookEntity,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var notesText by remember { mutableStateOf(book.notes) }
    var rating by remember { mutableIntStateOf(book.personalRating) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personal Notes & Rating",
                    fontFamily = BookDisplayFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = ShelfmatesDeepBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Rating Row
                Text(
                    text = "Your Rating:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        val isFilled = starIndex <= rating
                        IconButton(
                            onClick = { rating = if (rating == starIndex) 0 else starIndex },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$starIndex Stars",
                                tint = if (isFilled) ShelfmatesGold else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Reading Notes / Reflections") },
                    placeholder = { Text("What did you think of the characters, prose, and themes?") },
                    maxLines = 4,
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(notesText.trim(), rating) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                    ) {
                        Text("Save & Sync")
                    }
                }
            }
        }
    }
}
