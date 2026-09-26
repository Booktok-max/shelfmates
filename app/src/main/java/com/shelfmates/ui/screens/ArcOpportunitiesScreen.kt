package com.shelfmates.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shelfmates.data.local.ArcApplicationEntity
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.ApplicationStatus
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesBlue
import com.shelfmates.ui.theme.ShelfmatesCoral
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

enum class ArcOpportunityTab {
    BROWSE_OPPORTUNITIES,
    AUTHOR_PORTAL,
    MY_REQUESTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcOpportunitiesScreen(
    currentUser: UserEntity?,
    arcClubs: List<ArcClubEntity>,
    applications: List<ArcApplicationEntity>,
    onBack: () -> Unit,
    onSelectArcOpportunity: (String) -> Unit,
    onRequestArcAccess: (ArcClubEntity) -> Unit,
    onCreateArcOpportunity: () -> Unit,
    onUpdateAppStatus: (ArcApplicationEntity, ApplicationStatus, ArcClubEntity) -> Unit,
    onSendReminders: (ArcClubEntity) -> Unit,
    onReadInApp: (ArcClubEntity) -> Unit,
    onDownloadFile: (ArcClubEntity) -> Unit,
    onSubmitReview: (ArcClubEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(ArcOpportunityTab.BROWSE_OPPORTUNITIES) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val availableGenres = listOf("All", "Fantasy", "Sci-Fi", "Romance", "Mystery", "LitRPG", "Thriller", "Horror", "Non-Fiction", "YA")

    // Derived statistics
    val totalOpportunities = arcClubs.size
    val totalRemainingSlots = arcClubs.sumOf { (it.slotLimit - it.slotsFilled).coerceAtLeast(0) }
    val myAuthorArcs = arcClubs.filter { it.authorUserId == currentUser?.id }
    val myRequestedArcs = arcClubs.filter { it.isApplied || it.isApproved || it.isReviewSubmitted }

    // Applications for this author's books
    val authorBookIds = myAuthorArcs.map { it.id }.toSet()
    val incomingAuthorApplications = applications.filter { it.arcClubId in authorBookIds }

    // Filtered browse list
    val filteredClubs = arcClubs.filter { arc ->
        val matchesGenre = selectedGenre == "All" || arc.genre.equals(selectedGenre, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                arc.bookTitle.contains(searchQuery, ignoreCase = true) ||
                arc.authorName.contains(searchQuery, ignoreCase = true) ||
                arc.blurb.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (selectedStatusFilter) {
            "Open Slots" -> arc.slotsFilled < arc.slotLimit
            "Closing Soon" -> arc.daysRemaining <= 7
            else -> true
        }
        matchesGenre && matchesSearch && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Indie ARC Opportunities",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                fontFamily = BookDisplayFont
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = ShelfmatesGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "AUTHORS & READERS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Request advance reader copies or list upcoming releases",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("arc_opportunities_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onCreateArcOpportunity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShelfmatesGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(34.dp)
                            .testTag("top_list_arc_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("List ARC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShelfmatesDeepBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("arc_opportunities_screen")
        ) {
            // Module Tabs: Browse Opportunities vs Indie Author Portal vs My Requests
            val tabIndex = when (selectedTab) {
                ArcOpportunityTab.BROWSE_OPPORTUNITIES -> 0
                ArcOpportunityTab.AUTHOR_PORTAL -> 1
                ArcOpportunityTab.MY_REQUESTS -> 2
            }

            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = ShelfmatesNavy,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (tabIndex < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                            color = ShelfmatesGold,
                            height = 3.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == ArcOpportunityTab.BROWSE_OPPORTUNITIES,
                    onClick = { selectedTab = ArcOpportunityTab.BROWSE_OPPORTUNITIES },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Browse ARCs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_browse_arcs")
                )
                Tab(
                    selected = selectedTab == ArcOpportunityTab.AUTHOR_PORTAL,
                    onClick = { selectedTab = ArcOpportunityTab.AUTHOR_PORTAL },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Author Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            if (incomingAuthorApplications.any { it.status == ApplicationStatus.PENDING }) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(ShelfmatesCoral, CircleShape)
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("tab_author_portal")
                )
                Tab(
                    selected = selectedTab == ArcOpportunityTab.MY_REQUESTS,
                    onClick = { selectedTab = ArcOpportunityTab.MY_REQUESTS },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("My Requests (${myRequestedArcs.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_my_requests")
                )
            }

            // Tab Content
            when (selectedTab) {
                ArcOpportunityTab.BROWSE_OPPORTUNITIES -> {
                    BrowseOpportunitiesContent(
                        arcClubs = filteredClubs,
                        totalOpportunities = totalOpportunities,
                        totalRemainingSlots = totalRemainingSlots,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedGenre = selectedGenre,
                        onGenreChange = { selectedGenre = it },
                        availableGenres = availableGenres,
                        selectedStatusFilter = selectedStatusFilter,
                        onStatusFilterChange = { selectedStatusFilter = it },
                        onSelectOpportunity = onSelectArcOpportunity,
                        onRequestArcAccess = onRequestArcAccess,
                        onCreateArcClick = onCreateArcOpportunity,
                        onReadInApp = onReadInApp,
                        onDownloadFile = onDownloadFile,
                        onSubmitReview = onSubmitReview
                    )
                }
                ArcOpportunityTab.AUTHOR_PORTAL -> {
                    IndieAuthorPortalContent(
                        currentUser = currentUser,
                        authorArcs = myAuthorArcs,
                        applications = incomingAuthorApplications,
                        allArcClubs = arcClubs,
                        onCreateArcClick = onCreateArcOpportunity,
                        onSelectOpportunity = onSelectArcOpportunity,
                        onUpdateAppStatus = onUpdateAppStatus,
                        onSendReminders = onSendReminders
                    )
                }
                ArcOpportunityTab.MY_REQUESTS -> {
                    MyRequestedArcsContent(
                        requestedArcs = myRequestedArcs,
                        onSelectOpportunity = onSelectArcOpportunity,
                        onReadInApp = onReadInApp,
                        onDownloadFile = onDownloadFile,
                        onSubmitReview = onSubmitReview,
                        onBrowseClick = { selectedTab = ArcOpportunityTab.BROWSE_OPPORTUNITIES }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: BROWSE OPPORTUNITIES (Reader View)
// -----------------------------------------------------------------------------

@Composable
fun BrowseOpportunitiesContent(
    arcClubs: List<ArcClubEntity>,
    totalOpportunities: Int,
    totalRemainingSlots: Int,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedGenre: String,
    onGenreChange: (String) -> Unit,
    availableGenres: List<String>,
    selectedStatusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    onSelectOpportunity: (String) -> Unit,
    onRequestArcAccess: (ArcClubEntity) -> Unit,
    onCreateArcClick: () -> Unit,
    onReadInApp: (ArcClubEntity) -> Unit,
    onDownloadFile: (ArcClubEntity) -> Unit,
    onSubmitReview: (ArcClubEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("browse_arc_opportunities_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Overview Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(ShelfmatesDeepBlue, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Indie ARC Network",
                                    fontFamily = BookDisplayFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ShelfmatesNavy
                                )
                                Text(
                                    text = "Read before release date & post honest reviews",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Call to list
                        OutlinedButton(
                            onClick = onCreateArcClick,
                            border = BorderStroke(1.dp, ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Author? List Book", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatisticMetricPill(label = "Active ARCs", value = "$totalOpportunities")
                        StatisticMetricPill(label = "Available Copies", value = "$totalRemainingSlots")
                        StatisticMetricPill(label = "Formats", value = "EPUB / PDF")
                    }
                }
            }
        }

        // Search Input Field
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by title, author, or pitch...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ShelfmatesDeepBlue)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShelfmatesDeepBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("arc_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Genre Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableGenres) { genre ->
                        val isSelected = selectedGenre.equals(genre, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onGenreChange(genre) },
                            label = { Text(genre, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShelfmatesDeepBlue,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Status Filters
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("All", "Open Slots", "Closing Soon").forEach { filter ->
                        val isSelected = selectedStatusFilter == filter
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ShelfmatesNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { onStatusFilterChange(filter) }
                        ) {
                            Text(
                                text = filter,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ARC Opportunities Feed
        if (arcClubs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = ShelfmatesDeepBlue, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No ARC opportunities found matching criteria.",
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Be the first indie author to list an advance review copy in this category!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onCreateArcClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                        ) {
                            Text("List ARC Opportunity")
                        }
                    }
                }
            }
        } else {
            items(arcClubs) { arc ->
                ArcOpportunityFeedCard(
                    arc = arc,
                    onClick = { onSelectOpportunity(arc.id) },
                    onRequestAccess = { onRequestArcAccess(arc) },
                    onReadInApp = { onReadInApp(arc) },
                    onDownloadFile = { onDownloadFile(arc) },
                    onSubmitReview = { onSubmitReview(arc) }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 2: INDIE AUTHOR PORTAL (Author View)
// -----------------------------------------------------------------------------

@Composable
fun IndieAuthorPortalContent(
    currentUser: UserEntity?,
    authorArcs: List<ArcClubEntity>,
    applications: List<ArcApplicationEntity>,
    allArcClubs: List<ArcClubEntity>,
    onCreateArcClick: () -> Unit,
    onSelectOpportunity: (String) -> Unit,
    onUpdateAppStatus: (ArcApplicationEntity, ApplicationStatus, ArcClubEntity) -> Unit,
    onSendReminders: (ArcClubEntity) -> Unit
) {
    val pendingApplications = applications.filter { it.status == ApplicationStatus.PENDING }
    val approvedApplications = applications.filter { it.status == ApplicationStatus.APPROVED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("author_portal_scroll"),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Author Launchpad Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ShelfmatesDeepBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ShelfmatesGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "INDIE AUTHOR LAUNCH SUITE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "Author: ${currentUser?.displayName ?: "Indie Author"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Build Early Launch Momentum",
                        fontFamily = BookDisplayFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "List ARC copies, review reader credentials, approve trusted reviewers, and automatically push deadline reminders.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCreateArcClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShelfmatesGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("author_portal_list_arc_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("List New ARC Opportunity", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Author Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AuthorStatBox(
                    label = "My Listed Books",
                    value = "${authorArcs.size}",
                    subtext = "Active ARC campaigns",
                    modifier = Modifier.weight(1f)
                )
                AuthorStatBox(
                    label = "Pending Requests",
                    value = "${pendingApplications.size}",
                    subtext = "Waiting for approval",
                    highlight = pendingApplications.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                AuthorStatBox(
                    label = "Approved Readers",
                    value = "${approvedApplications.size}",
                    subtext = "Copies distributed",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Reader Requests Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = ShelfmatesDeepBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reader Access Requests (${pendingApplications.size})",
                        fontFamily = BookDisplayFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (pendingApplications.isNotEmpty()) {
                    Surface(
                        color = ShelfmatesAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "ACTION NEEDED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        if (pendingApplications.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ShelfmatesEmerald, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All reader requests reviewed!",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "When readers request copies of your listed books, their applications will appear here.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        } else {
            items(pendingApplications) { app ->
                val matchedArc = allArcClubs.firstOrNull { it.id == app.arcClubId }
                AuthorApplicationItemCard(
                    application = app,
                    arc = matchedArc,
                    onApprove = {
                        if (matchedArc != null) {
                            onUpdateAppStatus(app, ApplicationStatus.APPROVED, matchedArc)
                        }
                    },
                    onDecline = {
                        if (matchedArc != null) {
                            onUpdateAppStatus(app, ApplicationStatus.DECLINED, matchedArc)
                        }
                    }
                )
            }
        }

        // Section: Author's Active Listings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Listed Opportunities (${authorArcs.size})",
                    fontFamily = BookDisplayFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        if (authorArcs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You haven't listed any ARC opportunities yet.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "List your upcoming manuscript to recruit early launch reviewers.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onCreateArcClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                        ) {
                            Text("Create ARC Listing")
                        }
                    }
                }
            }
        } else {
            items(authorArcs) { arc ->
                AuthorListedArcCard(
                    arc = arc,
                    onClick = { onSelectOpportunity(arc.id) },
                    onSendReminders = { onSendReminders(arc) }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 3: MY REQUESTED ARCS (Reader Shelf)
// -----------------------------------------------------------------------------

@Composable
fun MyRequestedArcsContent(
    requestedArcs: List<ArcClubEntity>,
    onSelectOpportunity: (String) -> Unit,
    onReadInApp: (ArcClubEntity) -> Unit,
    onDownloadFile: (ArcClubEntity) -> Unit,
    onSubmitReview: (ArcClubEntity) -> Unit,
    onBrowseClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_requested_arcs_list"),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ShelfmatesDeepBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "My ARC Bookshelf",
                                fontFamily = BookDisplayFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = ShelfmatesNavy
                            )
                            Text(
                                text = "${requestedArcs.size} active reader copies & applications",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onBrowseClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("+ Browse More", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (requestedArcs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No ARCs requested yet",
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Discover upcoming indie books, request advance copies, and be the first to read and review them!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onBrowseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                        ) {
                            Text("Browse Open ARC Opportunities")
                        }
                    }
                }
            }
        } else {
            items(requestedArcs) { arc ->
                ReaderRequestedArcCard(
                    arc = arc,
                    onClick = { onSelectOpportunity(arc.id) },
                    onReadInApp = { onReadInApp(arc) },
                    onDownloadFile = { onDownloadFile(arc) },
                    onSubmitReview = { onSubmitReview(arc) }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT CARDS
// -----------------------------------------------------------------------------

@Composable
fun ArcOpportunityFeedCard(
    arc: ArcClubEntity,
    onClick: () -> Unit,
    onRequestAccess: () -> Unit,
    onReadInApp: () -> Unit,
    onDownloadFile: () -> Unit,
    onSubmitReview: () -> Unit
) {
    val progress = (arc.slotsFilled.toFloat() / arc.slotLimit.toFloat()).coerceIn(0f, 1f)
    val isFull = arc.slotsFilled >= arc.slotLimit
    val slotsRemaining = (arc.slotLimit - arc.slotsFilled).coerceAtLeast(0)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("arc_card_${arc.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Genre & Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ShelfmatesDeepBlue,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = arc.genre.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (arc.daysRemaining <= 3) ShelfmatesCoral else ShelfmatesAmber,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${arc.daysRemaining}d deadline",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (arc.autoApprove) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = ShelfmatesEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⚡ INSTANT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesEmerald,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Request status pill
                when {
                    arc.isReviewSubmitted -> {
                        StatusChipPill(text = "REVIEWED", color = ShelfmatesEmerald)
                    }
                    arc.isApproved -> {
                        StatusChipPill(text = "ACCESS GRANTED", color = ShelfmatesEmerald)
                    }
                    arc.isApplied -> {
                        StatusChipPill(text = "REQUEST PENDING", color = ShelfmatesAmber)
                    }
                    isFull -> {
                        StatusChipPill(text = "SLOTS FULL", color = Color.Gray)
                    }
                    else -> {
                        StatusChipPill(text = "$slotsRemaining SLOTS LEFT", color = ShelfmatesDeepBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Book Details & Thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cover Thumbnail
                Box(
                    modifier = Modifier
                        .size(width = 65.dp, height = 95.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ShelfmatesNavy)
                ) {
                    if (arc.coverUrl.isNotBlank()) {
                        AsyncImage(
                            model = arc.coverUrl,
                            contentDescription = arc.bookTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = arc.bookTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = BookDisplayFont,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "by ${arc.authorName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShelfmatesDeepBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Indie Author",
                            tint = ShelfmatesGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = arc.blurb,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Format: ${arc.format}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (arc.minimumReviewsRequired > 0) {
                            Text(
                                text = "Req: ${arc.minimumReviewsRequired}+ past reviews",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShelfmatesDeepBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reader Slots: ${arc.slotsFilled} / ${arc.slotLimit} Filled",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isFull) Color.Gray else ShelfmatesDeepBlue,
                    trackColor = ShelfmatesLightBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("View Details", fontSize = 11.sp)
                }

                when {
                    arc.isReviewSubmitted -> {
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    arc.isApproved -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onReadInApp,
                                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Read", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDownloadFile,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    arc.isApplied -> {
                        Surface(
                            color = ShelfmatesAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ShelfmatesAmber.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Request Pending", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            }
                        }
                    }
                    isFull -> {
                        Text(
                            text = "Campaign full",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        Button(
                            onClick = onRequestAccess,
                            colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_request_arc_${arc.id}")
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Request Access", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthorApplicationItemCard(
    application: ArcApplicationEntity,
    arc: ArcClubEntity?,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, ShelfmatesAmber.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_card_${application.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Reader info & Past Reviews badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ShelfmatesLightBlue)
                    ) {
                        if (application.readerAvatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = application.readerAvatarUrl,
                                contentDescription = application.readerName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ShelfmatesDeepBlue,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = application.readerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Applied ${application.appliedAt}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = ShelfmatesEmerald.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "★ ${application.pastReviewsCount} Reviews",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Book target
            Surface(
                color = ShelfmatesLightBlue,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Requested Book: ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = arc?.bookTitle ?: application.bookTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Reader's message / credentials
            Text(
                text = "\"${application.message}\"",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (application.goodreadsUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Profile: ${application.goodreadsUrl}",
                    fontSize = 10.sp,
                    color = ShelfmatesBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1-Tap Decision Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ShelfmatesCoral),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("btn_decline_app_${application.id}")
                ) {
                    Text("Decline", fontSize = 11.sp)
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("btn_approve_app_${application.id}")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve Access", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AuthorListedArcCard(
    arc: ArcClubEntity,
    onClick: () -> Unit,
    onSendReminders: () -> Unit
) {
    val progress = (arc.slotsFilled.toFloat() / arc.slotLimit.toFloat()).coerceIn(0f, 1f)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("author_listed_arc_${arc.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = arc.bookTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = BookDisplayFont
                )
                Surface(
                    color = ShelfmatesDeepBlue,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = arc.genre.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Deadline: ${arc.deadlineDate} (${arc.daysRemaining} days remaining)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Reader Slots: ${arc.slotsFilled} / ${arc.slotLimit}", fontSize = 10.sp)
                Text("${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
            }
            Spacer(modifier = Modifier.height(2.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ShelfmatesDeepBlue,
                trackColor = ShelfmatesLightBlue
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                ) {
                    Text("View Campaign", fontSize = 11.sp)
                }

                Button(
                    onClick = onSendReminders,
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesNavy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("send_reminders_btn_${arc.id}")
                ) {
                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = ShelfmatesGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Push Reminders", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReaderRequestedArcCard(
    arc: ArcClubEntity,
    onClick: () -> Unit,
    onReadInApp: () -> Unit,
    onDownloadFile: () -> Unit,
    onSubmitReview: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_arc_card_${arc.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    arc.isReviewSubmitted -> {
                        StatusChipPill(text = "REVIEW COMPLETED ✓", color = ShelfmatesEmerald)
                    }
                    arc.isApproved -> {
                        StatusChipPill(text = "APPROVED · READY TO READ", color = ShelfmatesEmerald)
                    }
                    else -> {
                        StatusChipPill(text = "REQUEST PENDING AUTHOR", color = ShelfmatesAmber)
                    }
                }

                Text(
                    text = "Due: ${arc.deadlineDate}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (arc.daysRemaining <= 3) ShelfmatesCoral else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Book info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 55.dp, height = 80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ShelfmatesNavy)
                ) {
                    if (arc.coverUrl.isNotBlank()) {
                        AsyncImage(
                            model = arc.coverUrl,
                            contentDescription = arc.bookTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = arc.bookTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = BookDisplayFont
                    )
                    Text(
                        text = "by ${arc.authorName}",
                        fontSize = 11.sp,
                        color = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = arc.blurb,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (arc.isApproved) {
                    Button(
                        onClick = onReadInApp,
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("my_arc_read_in_app_${arc.id}")
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Read", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDownloadFile,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    Button(
                        onClick = onSubmitReview,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (arc.isReviewSubmitted) ShelfmatesEmerald else ShelfmatesGold,
                            contentColor = if (arc.isReviewSubmitted) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("my_arc_submit_review_${arc.id}")
                    ) {
                        Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (arc.isReviewSubmitted) "Review ✓" else "Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                    ) {
                        Text("View Application Details", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HELPER UI PILLS
// -----------------------------------------------------------------------------

@Composable
fun StatisticMetricPill(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = ShelfmatesDeepBlue
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AuthorStatBox(
    label: String,
    value: String,
    subtext: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) ShelfmatesAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = if (highlight) BorderStroke(1.dp, ShelfmatesAmber.copy(alpha = 0.5f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = if (highlight) Color(0xFF92400E) else ShelfmatesDeepBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusChipPill(
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
