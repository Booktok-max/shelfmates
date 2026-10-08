package com.shelfmates.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.PublicClubEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.ui.components.GenreChipRow
import com.shelfmates.ui.components.GoogleBooksCatalogFeed
import com.shelfmates.ui.components.StatusBadge
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfTitleStyle
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy
import com.shelfmates.ui.viewmodel.DiscoverTab

@Composable
fun DiscoverScreen(
    currentUser: UserEntity?,
    discoverTab: DiscoverTab,
    selectedGenre: String,
    searchQuery: String,
    availableGenres: List<String>,
    publicClubs: List<PublicClubEntity>,
    arcClubs: List<ArcClubEntity>,
    // Google Books state & callbacks
    googleBooksQuery: String,
    googleBooksResults: List<GoogleBookVolumeItem>,
    isGoogleBooksLoading: Boolean,
    googleBooksError: String?,
    savedBooksMap: Map<String, String> = emptyMap(),
    onSaveBookToShelf: ((GoogleBookVolumeItem, String) -> Unit)? = null,
    onRemoveBookFromShelf: ((String) -> Unit)? = null,
    onSelectDiscoverTab: (DiscoverTab) -> Unit,
    onSelectGenre: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectPublicClub: (String) -> Unit,
    onSelectArcClub: (String) -> Unit,
    onToggleJoinPublicClub: (String, Boolean) -> Unit,
    onCreatePublicClubClick: () -> Unit,
    onCreateArcClubClick: () -> Unit,
    onOpenArcOpportunitiesHub: () -> Unit = {},
    onGoogleBooksSearchChange: (String) -> Unit,
    onPerformGoogleBooksSearch: (String) -> Unit,
    onGoogleBooksCategorySelect: (String) -> Unit,
    onSelectGoogleBook: (GoogleBookVolumeItem) -> Unit,
    onCreateClubFromGoogleBook: (GoogleBookVolumeItem) -> Unit,
    onCreateArcFromGoogleBook: (GoogleBookVolumeItem) -> Unit
) {
    // Filtered lists
    val filteredPublicClubs = publicClubs.filter { club ->
        val matchesGenre = selectedGenre == "All" || club.genre.equals(selectedGenre, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                club.name.contains(searchQuery, ignoreCase = true) ||
                club.currentBookTitle.contains(searchQuery, ignoreCase = true) ||
                club.description.contains(searchQuery, ignoreCase = true)
        matchesGenre && matchesSearch
    }

    val filteredArcClubs = arcClubs.filter { arc ->
        val matchesGenre = selectedGenre == "All" || arc.genre.equals(selectedGenre, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                arc.bookTitle.contains(searchQuery, ignoreCase = true) ||
                arc.authorName.contains(searchQuery, ignoreCase = true) ||
                arc.blurb.contains(searchQuery, ignoreCase = true)
        matchesGenre && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("discover_screen")
    ) {
        // Screen title. Discover is a way of FINDING books, so it says so
        // rather than naming the backends it happens to query.
        Text(
            text = "Discover",
            style = ShelfTitleStyle,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 2.dp)
                .testTag("discover_title")
        )

        // Tab Row: Public Clubs vs ARC Campaigns vs Google Books API
        // Index follows the TabRow declaration order below.
        val selectedIndex = when (discoverTab) {
            DiscoverTab.GOOGLE_BOOKS -> 0
            DiscoverTab.PUBLIC_CLUBS -> 1
            DiscoverTab.ARC_CLUBS -> 2
        }

        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = ShelfmatesDeepBlue,
            contentColor = Color.White,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = ShelfmatesGold,
                        height = 3.dp
                    )
                }
            }
        ) {
            Tab(
                selected = discoverTab == DiscoverTab.GOOGLE_BOOKS,
                onClick = {
                    onSelectDiscoverTab(DiscoverTab.GOOGLE_BOOKS)
                    if (googleBooksResults.isEmpty()) {
                        onPerformGoogleBooksSearch("popular fiction")
                    }
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Search", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.testTag("google_books_tab")
            )
            Tab(
                selected = discoverTab == DiscoverTab.PUBLIC_CLUBS,
                onClick = { onSelectDiscoverTab(DiscoverTab.PUBLIC_CLUBS) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clubs", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.testTag("public_clubs_tab")
            )
            Tab(
                selected = discoverTab == DiscoverTab.ARC_CLUBS,
                onClick = { onSelectDiscoverTab(DiscoverTab.ARC_CLUBS) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ARCs", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                modifier = Modifier.testTag("arc_clubs_tab")
            )
        }

        if (discoverTab == DiscoverTab.GOOGLE_BOOKS) {
            // Google Books API Live Catalog Feed
            GoogleBooksCatalogFeed(
                searchQuery = googleBooksQuery,
                onSearchChange = onGoogleBooksSearchChange,
                onPerformSearch = onPerformGoogleBooksSearch,
                selectedCategory = selectedGenre,
                onSelectCategory = { cat ->
                    onSelectGenre(cat)
                    onGoogleBooksCategorySelect(cat)
                },
                books = googleBooksResults,
                isLoading = isGoogleBooksLoading,
                errorMessage = googleBooksError,
                savedBooksMap = savedBooksMap,
                onSaveBookToShelf = onSaveBookToShelf,
                onRemoveBookFromShelf = onRemoveBookFromShelf,
                onSelectBook = onSelectGoogleBook,
                onCreateClubForBook = onCreateClubFromGoogleBook,
                onCreateArcForBook = onCreateArcFromGoogleBook
            )
        } else {
            // Search Bar for Local Public / ARC Clubs
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = if (discoverTab == DiscoverTab.PUBLIC_CLUBS)
                                    "Search public clubs or current reads..."
                                else
                                    "Search open ARC campaigns, books, authors...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ShelfmatesDeepBlue)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("discover_search_field")
                    )

                    // Genre Filter Chips
                    GenreChipRow(
                        genres = availableGenres,
                        selectedGenre = selectedGenre,
                        onSelectGenre = onSelectGenre
                    )
                }
            }

            // Content Feed for Local Clubs / ARCs
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("discover_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (discoverTab == DiscoverTab.PUBLIC_CLUBS) {
                    // Public Clubs Feed
                    if (filteredPublicClubs.isEmpty()) {
                        item {
                            EmptyStateCard(
                                message = if (selectedGenre != "All") {
                                    "No ${selectedGenre} clubs yet. Browse another genre or start one."
                                } else {
                                    "No book clubs found matching your search."
                                },
                                actionLabel = "Create New Public Club",
                                onAction = onCreatePublicClubClick
                            )
                        }
                    } else {
                        items(filteredPublicClubs) { club ->
                            PublicClubDiscoverCard(
                                club = club,
                                onClick = { onSelectPublicClub(club.id) },
                                onToggleJoin = { onToggleJoinPublicClub(club.id, !club.isJoined) }
                            )
                        }
                    }
                } else {
                    // ARC Clubs Feed
                    item {
                        ArcBannerHeaderCard(
                            onOpenHub = onOpenArcOpportunitiesHub,
                            onCreateArc = onCreateArcClubClick
                        )
                    }

                    if (filteredArcClubs.isEmpty()) {
                        item {
                            EmptyStateCard(
                                message = "No ARC campaigns found matching your filter.",
                                actionLabel = "Create New ARC Campaign",
                                onAction = onCreateArcClubClick
                            )
                        }
                    } else {
                        items(filteredArcClubs) { arc ->
                            ArcClubDiscoverCard(
                                arc = arc,
                                onClick = { onSelectArcClub(arc.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicClubDiscoverCard(
    club: PublicClubEntity,
    onClick: () -> Unit,
    onToggleJoin: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("public_club_card_${club.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = club.genre.uppercase(), containerColor = ShelfmatesDeepBlue)
                    if (club.isVoiceRoomActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ShelfmatesEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ShelfmatesEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AUDIO LIVE",
                                color = ShelfmatesEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "${club.memberCount} Members",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = club.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = club.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Current Read Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ShelfmatesDeepBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT READ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesDeepBlue
                        )
                        Text(
                            text = club.currentBookTitle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ShelfmatesNavy,
                            maxLines = 1
                        )
                        Text(
                            text = "by ${club.currentBookAuthor}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Admin: ${club.adminName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (club.isJoined) {
                    OutlinedButton(
                        onClick = onToggleJoin,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShelfmatesDeepBlue),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Joined", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onToggleJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Join Club", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ArcBannerHeaderCard(
    onOpenHub: () -> Unit = {},
    onCreateArc: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ShelfmatesNavy),
        modifier = Modifier.fillMaxWidth().testTag("discover_arc_banner_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ShelfmatesGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("INDIE ARC PLATFORM", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Verified Authors",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Indie ARC Opportunities",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = "Indie authors list unreleased manuscripts. Readers request early access copies in exchange for honest launch reviews.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenHub,
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f).height(36.dp).testTag("open_arc_hub_button")
                ) {
                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ARC Opportunities Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                OutlinedButton(
                    onClick = onCreateArc,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp).testTag("list_arc_banner_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("List ARC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ArcClubDiscoverCard(
    arc: ArcClubEntity,
    onClick: () -> Unit
) {
    val progress = (arc.slotsFilled.toFloat() / arc.slotLimit.toFloat()).coerceIn(0f, 1f)
    val isFull = arc.slotsFilled >= arc.slotLimit

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arc_club_card_${arc.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(text = arc.genre.uppercase(), containerColor = ShelfmatesDeepBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(
                        text = "${arc.daysRemaining}d left",
                        containerColor = if (arc.daysRemaining <= 3) ShelfmatesCoral else ShelfmatesAmber
                    )
                }

                if (arc.isReviewSubmitted) {
                    StatusBadge(text = "REVIEWED", containerColor = ShelfmatesEmerald)
                } else if (arc.isApproved) {
                    StatusBadge(text = "APPROVED", containerColor = ShelfmatesEmerald)
                } else if (arc.isApplied) {
                    StatusBadge(text = "APPLIED", containerColor = ShelfmatesAmber)
                } else if (isFull) {
                    StatusBadge(text = "SLOTS FULL", containerColor = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = arc.bookTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Author: ${arc.authorName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ShelfmatesDeepBlue
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = arc.blurb,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Slots Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reviewer Slots: ${arc.slotsFilled} / ${arc.slotLimit} Filled",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isFull) MaterialTheme.colorScheme.outline else ShelfmatesDeepBlue,
                    trackColor = ShelfmatesLightBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Format: ${arc.format}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (arc.isApproved) ShelfmatesEmerald else ShelfmatesDeepBlue
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = when {
                            arc.isReviewSubmitted -> "View Review"
                            arc.isApproved -> "Download ARC"
                            arc.isApplied -> "Request Pending"
                            isFull -> "View Details"
                            else -> "Request Access"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(actionLabel)
            }
        }
    }
}
