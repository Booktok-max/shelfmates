package com.shelfmates.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.R
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.BroadcastEntity
import com.shelfmates.data.local.CustomShelfEntity
import com.shelfmates.data.local.PublicClubEntity
import com.shelfmates.data.local.SavedBookEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.BroadcastType
import com.shelfmates.data.model.CloudSyncState
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.data.model.NytBestsellerData
import com.shelfmates.ui.components.BookOrnamentalDivider
import com.shelfmates.ui.components.BookSearchSuggestionsView
import com.shelfmates.ui.components.BookshelfCategorySection
import com.shelfmates.ui.components.GoogleBookCard
import com.shelfmates.ui.components.NytRotatingShowcase
import com.shelfmates.ui.components.StatusBadge
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesBlue
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesCrimson
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesInkBlack
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    publicClubs: List<PublicClubEntity>,
    arcClubs: List<ArcClubEntity>,
    broadcasts: List<BroadcastEntity>,
    bookmarkBalance: Int = 740,
    googleBooksQuery: String = "",
    googleBooksResults: List<GoogleBookVolumeItem> = emptyList(),
    isGoogleBooksLoading: Boolean = false,
    googleBooksError: String? = null,
    savedBooks: List<SavedBookEntity> = emptyList(),
    savedBooksMap: Map<String, String> = emptyMap(),
    customShelves: List<CustomShelfEntity> = emptyList(),
    cloudSyncState: CloudSyncState = CloudSyncState.Idle,
    selectedShelfCategoryFilter: String = "All",
    onShelfCategoryFilterChange: (String) -> Unit = {},
    onSaveBookToShelf: (GoogleBookVolumeItem, String) -> Unit = { _, _ -> },
    onUpdateSavedBookCategory: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateSavedBookNotes: (String, String, Int) -> Unit = { _, _, _ -> },
    onRemoveSavedBook: (String, String) -> Unit = { _, _ -> },
    onSyncWithCloud: () -> Unit = {},
    onCreateCustomShelf: (String, String) -> Unit = { _, _ -> },
    onDeleteCustomShelf: (String, String) -> Unit = { _, _ -> },
    onGoogleBooksSearchChange: (String) -> Unit = {},
    onPerformGoogleBooksSearch: (String) -> Unit = {},
    onGoogleBooksCategorySelect: (String) -> Unit = {},
    onSelectGoogleBook: (GoogleBookVolumeItem) -> Unit = {},
    onCreateClubFromGoogleBook: (GoogleBookVolumeItem) -> Unit = {},
    onCreateArcFromGoogleBook: (GoogleBookVolumeItem) -> Unit = {},
    onSelectPublicClub: (String) -> Unit,
    onSelectArcClub: (String) -> Unit,
    onJoinVoiceRoom: (PublicClubEntity) -> Unit,
    onNavigateDiscover: () -> Unit,
    onNavigateBookmarks: () -> Unit = {},
    onNavigateArcOpportunities: () -> Unit = {},
    onReadArc: (ArcClubEntity) -> Unit = {},
    onReadPublicClub: (PublicClubEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val liveVoiceClubs = publicClubs.filter { it.isVoiceRoomActive }
    val joinedClubs = publicClubs.filter { it.isJoined }
    val activeArcs = arcClubs.filter { it.isApplied || it.isApproved }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header Banner with Book Aesthetic Velvet Crimson Gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_bookclub),
                    contentDescription = "Shelfmates Community",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ShelfmatesInkBlack.copy(alpha = 0.65f),
                                    ShelfmatesDeepBlue.copy(alpha = 0.95f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ShelfmatesGold)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LITERARY GUILD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 14-DAY STREAK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Welcome back, ${currentUser?.displayName ?: "Reader"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect with authors, join live audio read-alongs, and read launch ARCs.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = BookDisplayFont
                        ),
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // The New York Times Best Sellers - Rotating Showcase on Front Page
        item {
            NytRotatingShowcase(
                books = NytBestsellerData.rotatingFrontPageBooks,
                onSelectBook = onSelectGoogleBook,
                onSaveToShelf = onSaveBookToShelf,
                onCreateClub = onCreateClubFromGoogleBook,
                onSearchTopic = { topic ->
                    onGoogleBooksSearchChange(topic)
                    onPerformGoogleBooksSearch(topic)
                }
            )
        }

        // Book Catalog Global Search Input Field with Suggestions
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("home_google_books_search_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                .size(36.dp)
                                .background(ShelfmatesLightBlue.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = ShelfmatesDeepBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Book Search & Catalog",
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Search millions of titles, authors, and genres",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = ShelfmatesEmerald.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "GLOBAL CATALOG",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ShelfmatesEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search Input Field
                    OutlinedTextField(
                        value = googleBooksQuery,
                        onValueChange = onGoogleBooksSearchChange,
                        placeholder = {
                            Text(
                                "Search books by title, author, or genre...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = ShelfmatesDeepBlue
                            )
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                if (googleBooksQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            onGoogleBooksSearchChange("")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (googleBooksQuery.isNotBlank()) {
                                            onPerformGoogleBooksSearch(googleBooksQuery)
                                        } else {
                                            onPerformGoogleBooksSearch("The New York Times bestsellers")
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ShelfmatesDeepBlue,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("btn_submit_google_books_search")
                                ) {
                                    Text("Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShelfmatesDeepBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_view_google_books_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Topic / Suggestion Quick Chips
                    val quickTopics = listOf(
                        "NYT Bestsellers", "Trending", "Sci-Fi", "Fantasy", "Mystery", "Romance", "Award Winners", "Non-Fiction"
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickTopics) { topic ->
                            val isSelected = googleBooksQuery.equals(topic, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable {
                                    val query = when (topic) {
                                        "NYT Bestsellers" -> "The New York Times bestsellers"
                                        "Trending" -> "bestsellers"
                                        "Award Winners" -> "Pulitzer prize fiction"
                                        else -> topic
                                    }
                                    onGoogleBooksSearchChange(query)
                                    onPerformGoogleBooksSearch(query)
                                }
                            ) {
                                Text(
                                    text = topic,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Dynamic Book Search Suggestions
                    Spacer(modifier = Modifier.height(10.dp))
                    BookSearchSuggestionsView(
                        query = googleBooksQuery,
                        onSelectSuggestion = { suggestion ->
                            onGoogleBooksSearchChange(suggestion)
                            onPerformGoogleBooksSearch(suggestion)
                        }
                    )

                    // Loading Indicator
                    if (isGoogleBooksLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = ShelfmatesDeepBlue,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Searching book catalog...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Error Message & Retry
                    if (!isGoogleBooksLoading && googleBooksError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ShelfmatesCrimson.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = googleBooksError,
                                    fontSize = 12.sp,
                                    color = ShelfmatesCrimson,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { onPerformGoogleBooksSearch(googleBooksQuery.ifBlank { "bestsellers" }) }) {
                                    Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShelfmatesCrimson)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Results List in Main View
        if (googleBooksResults.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Books Found (${googleBooksResults.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateDiscover) {
                        Text("Open in Discover", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                    }
                }
            }

            items(googleBooksResults, key = { "home_gbook_${it.id}" }) { book ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    GoogleBookCard(
                        book = book,
                        savedCategory = savedBooksMap[book.id],
                        onClick = { onSelectGoogleBook(book) },
                        onSaveToCategory = { cat -> onSaveBookToShelf(book, cat) },
                        onRemoveFromShelf = {
                            val saved = savedBooks.firstOrNull { it.googleBooksId == book.id }
                            if (saved != null) {
                                onRemoveSavedBook(saved.id, book.displayTitle)
                            }
                        },
                        onCreateClub = { onCreateClubFromGoogleBook(book) },
                        onCreateArc = { onCreateArcFromGoogleBook(book) }
                    )
                }
            }
        }

        // Room Database Bookshelf Section: Categories ('Currently Reading', 'To Read', 'Finished' + Custom Shelves)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            BookshelfCategorySection(
                savedBooks = savedBooks,
                customShelves = customShelves,
                cloudSyncState = cloudSyncState,
                selectedCategoryFilter = selectedShelfCategoryFilter,
                onCategoryFilterChange = onShelfCategoryFilterChange,
                onUpdateCategory = onUpdateSavedBookCategory,
                onUpdateNotes = onUpdateSavedBookNotes,
                onRemoveBook = onRemoveSavedBook,
                onSyncWithCloud = onSyncWithCloud,
                onCreateCustomShelf = onCreateCustomShelf,
                onDeleteCustomShelf = onDeleteCustomShelf,
                onNavigateToSearch = {
                    onGoogleBooksSearchChange("bestsellers")
                    onPerformGoogleBooksSearch("bestsellers")
                },
                onSelectBookGoogleId = { gId ->
                    val matchedBook = googleBooksResults.firstOrNull { it.id == gId }
                    if (matchedBook != null) {
                        onSelectGoogleBook(matchedBook)
                    } else {
                        onPerformGoogleBooksSearch(gId)
                    }
                }
            )
        }

        // Gamified Bookmarks & Quest Shortcut Card
        item {
            Card(
                onClick = onNavigateBookmarks,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ShelfmatesGold.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(ShelfmatesLightBlue, RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.2f)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔖", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bookmarks Vault",
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ShelfmatesDeepBlue)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "1.25x MULTIPLIER",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "Chapter Champion Tier · Tap to claim daily reading quests",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ShelfmatesAmber.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, ShelfmatesAmber.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$bookmarkBalance",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("🔖", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Live Voice Rooms Section
        if (liveVoiceClubs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ShelfmatesEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Audio Salons",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = "${liveVoiceClubs.size} live now",
                            fontSize = 12.sp,
                            color = ShelfmatesEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(liveVoiceClubs) { club ->
                            LiveVoiceClubCard(
                                club = club,
                                onJoinVoice = { onJoinVoiceRoom(club) },
                                onClick = { onSelectPublicClub(club.id) }
                            )
                        }
                    }
                }
            }
        }

        // Bookish Ornamental Divider
        item {
            BookOrnamentalDivider(modifier = Modifier.padding(horizontal = 32.dp))
        }

        // Author Broadcasts & Announcements
        if (broadcasts.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = ShelfmatesDeepBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Author Dispatches",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        broadcasts.take(3).forEach { broadcast ->
                            BroadcastItemCard(broadcast = broadcast)
                        }
                    }
                }
            }
        }

        // Indie Author ARC Opportunities Hub Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigateArcOpportunities() }
                    .testTag("home_arc_opportunities_hub_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                border = BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ShelfmatesDeepBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "INDIE AUTHOR NETWORK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = ShelfmatesGold,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${arcClubs.size} Open ARCs",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Advance Reader Copies (ARCs)",
                        fontFamily = BookDisplayFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = ShelfmatesNavy
                    )
                    Text(
                        text = "Indie authors list unreleased manuscripts. Readers request early digital copies in exchange for honest launch reviews.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateArcOpportunities,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(38.dp)
                                .testTag("home_browse_arcs_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse & Request ARCs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateArcOpportunities,
                            border = BorderStroke(1.dp, ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("home_author_portal_btn")
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ShelfmatesDeepBlue, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Author Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                        }
                    }
                }
            }
        }

        // Current ARC Reads / Deadlines
        if (activeArcs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = ShelfmatesCoral,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active ARC Deadlines",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeArcs.forEach { arc ->
                            ActiveArcItemCard(
                                arc = arc,
                                onClick = { onSelectArcClub(arc.id) },
                                onRead = { onReadArc(arc) }
                            )
                        }
                    }
                }
            }
        }

        // Quick Jump to Joined Clubs
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Book Clubs",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    TextButton(onClick = onNavigateDiscover) {
                        Text(
                            text = "Explore All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesDeepBlue
                        )
                    }
                }

                if (joinedClubs.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "You haven't joined any public clubs yet.",
                                fontFamily = BookDisplayFont,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onNavigateDiscover,
                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                            ) {
                                Text("Discover Clubs by Genre")
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        joinedClubs.forEach { club ->
                            JoinedClubCard(
                                club = club,
                                onClick = { onSelectPublicClub(club.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveVoiceClubCard(
    club: PublicClubEntity,
    onJoinVoice: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ShelfmatesNavy),
        modifier = Modifier
            .width(260.dp)
            .testTag("live_voice_card_${club.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(text = "LIVE AUDIO", containerColor = ShelfmatesEmerald)
                Text(
                    text = "${club.activeVoiceListeners} tuned in",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = club.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Discussing: ${club.currentBookTitle}",
                fontSize = 11.sp,
                color = ShelfmatesGold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onJoinVoice,
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Join Voice Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BroadcastItemCard(broadcast: BroadcastEntity) {
    val context = LocalContext.current
    val badgeColor = when (broadcast.type) {
        BroadcastType.RELEASE -> ShelfmatesEmerald
        BroadcastType.ARC_OPENING -> ShelfmatesDeepBlue
        BroadcastType.PRICE_DROP -> ShelfmatesAmber
        BroadcastType.COVER_REVEAL -> ShelfmatesCoral
        BroadcastType.ANNOUNCEMENT -> ShelfmatesBlue
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (broadcast.type) {
                                BroadcastType.PRICE_DROP -> Icons.Default.LocalOffer
                                BroadcastType.ARC_OPENING -> Icons.Default.MenuBook
                                else -> Icons.Default.Campaign
                            },
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = broadcast.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = broadcast.sentAt,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(
                    text = broadcast.type.name.replace("_", " "),
                    containerColor = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = broadcast.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = broadcast.message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (broadcast.actionUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(broadcast.actionUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Link",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = ShelfmatesDeepBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveArcItemCard(
    arc: ArcClubEntity,
    onClick: () -> Unit,
    onRead: () -> Unit = onClick
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = if (arc.isReviewSubmitted) "REVIEW SUBMITTED" else "DUE IN ${arc.daysRemaining} DAYS",
                        containerColor = if (arc.isReviewSubmitted) ShelfmatesEmerald else ShelfmatesCoral
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = arc.genre,
                        fontSize = 11.sp,
                        color = ShelfmatesNavy
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = arc.bookTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ShelfmatesNavy
                )
                Text(
                    text = "Author: ${arc.authorName}",
                    fontSize = 11.sp,
                    color = ShelfmatesDeepBlue
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onRead,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ShelfmatesDeepBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Read in App",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun JoinedClubCard(
    club: PublicClubEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShelfmatesDeepBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = club.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (club.isVoiceRoomActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ShelfmatesEmerald)
                                .size(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Current Read: ${club.currentBookTitle}",
                    fontSize = 11.sp,
                    color = ShelfmatesDeepBlue,
                    maxLines = 1
                )
                Text(
                    text = "${club.memberCount} Shelfmates",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
