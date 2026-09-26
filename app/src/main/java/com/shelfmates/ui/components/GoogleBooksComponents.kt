package com.shelfmates.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import com.shelfmates.data.local.BookShelfCategory
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesCrimson
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

@Composable
fun GoogleBookCard(
    book: GoogleBookVolumeItem,
    onClick: () -> Unit,
    onCreateClub: () -> Unit,
    onCreateArc: () -> Unit,
    savedCategory: String? = null,
    onSaveToCategory: ((String) -> Unit)? = null,
    onRemoveFromShelf: (() -> Unit)? = null
) {
    var isShelfMenuOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("google_book_card_${book.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Book Cover Image with shadow
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(124.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShelfmatesNavy.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = book.secureCoverUrl,
                    contentDescription = "Cover of ${book.displayTitle}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Book Information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Header tags row (Category + Saved Shelf Status Badge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Surface(
                        color = ShelfmatesLightBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = book.displayCategory,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ShelfmatesDeepBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Shelf Status Indicator
                    if (savedCategory != null) {
                        val (sBg, sFg, sIcon) = when (savedCategory) {
                            BookShelfCategory.CURRENTLY_READING -> Triple(ShelfmatesEmerald.copy(alpha = 0.18f), ShelfmatesEmerald, Icons.Default.AutoStories)
                            BookShelfCategory.FINISHED -> Triple(ShelfmatesDeepBlue.copy(alpha = 0.18f), ShelfmatesDeepBlue, Icons.Default.CheckCircle)
                            else -> Triple(ShelfmatesAmber.copy(alpha = 0.2f), ShelfmatesCoral, Icons.Default.Bookmark)
                        }
                        Surface(
                            color = sBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = sIcon, contentDescription = null, tint = sFg, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = savedCategory,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sFg
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = book.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Author
                Text(
                    text = "by ${book.displayAuthors}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Meta row (Rating, Pages, Year)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rating = book.volumeInfo?.averageRating
                    if (rating != null && rating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ShelfmatesGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", rating),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = book.pageCountText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (book.publishedYear != "Unknown") {
                        Text(
                            text = "• ${book.publishedYear}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Row: Details + Shelf Saver + Club
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bookshelf Dropdown Action
                    Box(modifier = Modifier.weight(1.1f)) {
                        if (savedCategory == null) {
                            OutlinedButton(
                                onClick = { isShelfMenuOpen = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = ShelfmatesDeepBlue
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+ Shelf", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            FilledTonalButton(
                                onClick = { isShelfMenuOpen = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ShelfmatesEmerald.copy(alpha = 0.15f),
                                    contentColor = ShelfmatesEmerald
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("On Shelf", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = isShelfMenuOpen,
                            onDismissRequest = { isShelfMenuOpen = false }
                        ) {
                            Text(
                                text = "Save to Virtual Shelf:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            HorizontalDivider()
                            BookShelfCategory.DEFAULT_SHELVES.forEach { category ->
                                val isCurrent = savedCategory == category || (category == BookShelfCategory.TO_READ && savedCategory == BookShelfCategory.WANT_TO_READ)
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isCurrent) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = ShelfmatesEmerald,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                            Text(category, fontSize = 13.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    },
                                    onClick = {
                                        isShelfMenuOpen = false
                                        onSaveToCategory?.invoke(category)
                                    }
                                )
                            }
                            if (savedCategory != null && onRemoveFromShelf != null) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Remove from Bookshelf", color = ShelfmatesCrimson, fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ShelfmatesCrimson, modifier = Modifier.size(16.dp))
                                    },
                                    onClick = {
                                        isShelfMenuOpen = false
                                        onRemoveFromShelf()
                                    }
                                )
                            }
                        }
                    }

                    // Details Action
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(34.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Details", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Club Action
                    Button(
                        onClick = onCreateClub,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(34.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShelfmatesDeepBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("+ Club", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleBooksCatalogFeed(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onPerformSearch: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    books: List<GoogleBookVolumeItem>,
    isLoading: Boolean,
    errorMessage: String?,
    savedBooksMap: Map<String, String> = emptyMap(),
    onSaveBookToShelf: ((GoogleBookVolumeItem, String) -> Unit)? = null,
    onRemoveBookFromShelf: ((String) -> Unit)? = null,
    onSelectBook: (GoogleBookVolumeItem) -> Unit,
    onCreateClubForBook: (GoogleBookVolumeItem) -> Unit,
    onCreateArcForBook: (GoogleBookVolumeItem) -> Unit
) {
    val categories = listOf(
        "All",
        "Fiction",
        "Sci-Fi",
        "Fantasy",
        "Mystery",
        "Thriller",
        "Romance",
        "Non-Fiction",
        "Self-Help",
        "Biography"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("google_books_catalog_feed")
    ) {
        // Search & Category Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
                // Search Input Field with Instant Catalog Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text("Search millions of titles, authors, genres...", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    onSearchChange("")
                                    onPerformSearch("The New York Times bestsellers")
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                            Button(
                                onClick = { onPerformSearch(searchQuery) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShelfmatesDeepBlue,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .height(36.dp)
                            ) {
                                Text("Search", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("google_books_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Google Books Category Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category.equals(selectedCategory, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCategory(category) },
                            label = { Text(category, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShelfmatesDeepBlue,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // Dynamic Search Suggestions Bar
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    BookSearchSuggestionsView(
                        query = searchQuery,
                        onSelectSuggestion = { suggestion ->
                            onSearchChange(suggestion)
                            onPerformSearch(suggestion)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Search Content & List
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = ShelfmatesDeepBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching book catalog...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fetching live edition details, covers, and ISBNs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                books.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ShelfmatesLightBlue.copy(alpha = 0.2f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = ShelfmatesDeepBlue,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "Search Book Catalog",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Type any book title, author, or genre above, or tap suggestions to explore editions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onSearchChange("The New York Times bestsellers")
                                    onPerformSearch("The New York Times bestsellers")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                            ) {
                                Text("NYT Bestsellers")
                            }
                            OutlinedButton(
                                onClick = {
                                    onSearchChange("James Percival Everett")
                                    onPerformSearch("James Percival Everett")
                                }
                            ) {
                                Text("Trending Fiction")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 14.dp,
                            bottom = 90.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Books Found (${books.size})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    color = ShelfmatesCoral.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "GLOBAL CATALOG",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShelfmatesCoral,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        items(books, key = { it.id }) { book ->
                            GoogleBookCard(
                                book = book,
                                savedCategory = savedBooksMap[book.id],
                                onSaveToCategory = { cat -> onSaveBookToShelf?.invoke(book, cat) },
                                onRemoveFromShelf = { onRemoveBookFromShelf?.invoke(book.id) },
                                onClick = { onSelectBook(book) },
                                onCreateClub = { onCreateClubForBook(book) },
                                onCreateArc = { onCreateArcForBook(book) }
                            )
                        }
                    }
                }
            }
        }
    }
}



