package com.shelfmates.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shelfmates.ui.components.GoogleBookDetailDialog
import com.shelfmates.ui.components.ShelfmatesTopAppBar
import com.shelfmates.ui.components.VoiceRoomLiveBar
import com.shelfmates.ui.dialogs.AddBookLogDialog
import com.shelfmates.ui.dialogs.AmazonDeepLinkDialog
import com.shelfmates.ui.dialogs.ApplyArcDialog
import com.shelfmates.ui.dialogs.AuthorProDialog
import com.shelfmates.ui.dialogs.BroadcastDialog
import com.shelfmates.ui.dialogs.CreateArcClubDialog
import com.shelfmates.ui.dialogs.CreatePublicClubDialog
import com.shelfmates.ui.dialogs.CsvExportDialog
import com.shelfmates.ui.dialogs.PromoBookingDialog
import com.shelfmates.ui.dialogs.SubmitReviewDialog
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.viewmodel.MainTab
import com.shelfmates.ui.viewmodel.ShelfmatesViewModel

@Composable
fun MainScreen(viewModel: ShelfmatesViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val publicClubs by viewModel.allPublicClubs.collectAsState(initial = emptyList())
    val joinedPublicClubs by viewModel.joinedPublicClubs.collectAsState(initial = emptyList())
    val arcClubs by viewModel.allArcClubs.collectAsState(initial = emptyList())
    val myArcReads by viewModel.myArcReads.collectAsState(initial = emptyList())
    val allArcApplications by viewModel.allArcApplications.collectAsState(initial = emptyList())
    val broadcasts by viewModel.allBroadcasts.collectAsState(initial = emptyList())
    val notifications by viewModel.allNotifications.collectAsState(initial = emptyList())
    val unreadNotifCount by viewModel.unreadNotificationsCount.collectAsState(initial = 0)

    val firebaseAuthState by viewModel.firebaseAuthState.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val selectedChatModel by viewModel.selectedChatModel.collectAsState()
    val selectedChatRole by viewModel.selectedChatRole.collectAsState()
    val liveVoiceSessionState by viewModel.liveVoiceSessionState.collectAsState()
    val bookmarkWallet by viewModel.bookmarkWallet.collectAsState()
    val savedBooks by viewModel.savedBooks.collectAsState(initial = emptyList())
    val savedBooksMap by viewModel.savedBooksMap.collectAsState(initial = emptyMap())
    val customShelves by viewModel.customShelves.collectAsState(initial = emptyList())
    val cloudSyncState by viewModel.cloudSyncState.collectAsState(initial = com.shelfmates.data.model.CloudSyncState.Idle)

    val snackbarHostState = remember { SnackbarHostState() }

    // Initial background Firestore synchronization
    LaunchedEffect(Unit) {
        viewModel.syncBookshelfWithCloud()
    }

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedbackMessage()
        }
    }

    // Detail targets
    val selectedPublicClub = publicClubs.firstOrNull { it.id == uiState.selectedPublicClubId }
    val selectedArcClub = arcClubs.firstOrNull { it.id == uiState.selectedArcClubId }

    val publicClubThreads by if (selectedPublicClub != null) {
        viewModel.getThreadsForClub(selectedPublicClub.id).collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    val selectedThread = publicClubThreads.firstOrNull { it.id == uiState.selectedThreadId }
    val threadReplies by if (selectedThread != null) {
        viewModel.getRepliesForThread(selectedThread.id).collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    val arcApplications by if (selectedArcClub != null) {
        viewModel.getApplicationsForArc(selectedArcClub.id).collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    val arcReviews by if (selectedArcClub != null) {
        viewModel.getReviewsForArc(selectedArcClub.id).collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    val authorFollowers by if (currentUser != null) {
        viewModel.getFollowersForAuthor(currentUser?.id ?: "").collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    val bookLogs by if (currentUser != null) {
        viewModel.getBookLogs(currentUser?.id ?: "").collectAsState(initial = emptyList())
    } else {
        remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    }

    if (uiState.readingBookId != null) {
        val readingId = uiState.readingBookId ?: ""
        EbookReaderScreen(
            bookId = readingId,
            bookTitle = uiState.readingBookTitle.ifBlank { "Advance Reader Copy" },
            authorName = uiState.readingBookAuthor.ifBlank { "Featured Author" },
            coverUrl = uiState.readingBookCover,
            asin = uiState.readingBookAsin,
            isArc = uiState.readingIsArc,
            initialFormat = uiState.readingFormat,
            supabaseUrl = uiState.readingSupabaseUrl,
            onBack = { viewModel.closeInAppReader() },
            onSubmitReviewClick = {
                viewModel.closeInAppReader()
                viewModel.selectArcClub(readingId)
                viewModel.openSubmitReviewDialog()
            }
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedPublicClub == null && selectedArcClub == null && selectedThread == null) {
                Column {
                    ShelfmatesTopAppBar(
                        currentUser = currentUser,
                        unreadNotifCount = unreadNotifCount,
                        activeVoiceRoom = uiState.activeVoiceRoom,
                        bookmarkBalance = bookmarkWallet.balance,
                        onBookmarksRewardsClick = { viewModel.setTab(MainTab.BOOKMARKS_REWARDS) },
                        onNotificationsClick = { viewModel.setTab(MainTab.NOTIFICATIONS) }
                    )
                    AnimatedVisibility(
                        visible = uiState.activeVoiceRoom != null,
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        uiState.activeVoiceRoom?.let { room ->
                            VoiceRoomLiveBar(
                                voiceRoom = room,
                                onToggleMute = { viewModel.toggleVoiceMute() },
                                onToggleHandRaise = { viewModel.toggleHandRaise() },
                                onLeaveRoom = { viewModel.leaveVoiceRoom() }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (selectedPublicClub == null && selectedArcClub == null && selectedThread == null) {
                NavigationBar(
                    containerColor = ShelfmatesDeepBlue,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.HOME,
                        onClick = { viewModel.setTab(MainTab.HOME) },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.DISCOVER,
                        onClick = { viewModel.setTab(MainTab.DISCOVER) },
                        icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Discover") },
                        label = { Text("Discover", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_discover")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.CLUBS,
                        onClick = { viewModel.setTab(MainTab.CLUBS) },
                        icon = { Icon(imageVector = Icons.Default.Groups, contentDescription = "My Clubs") },
                        label = { Text("Clubs", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_clubs")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.AI_CHAT,
                        onClick = { viewModel.setTab(MainTab.AI_CHAT) },
                        icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Mentor") },
                        label = { Text("AI Chat", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_ai_chat")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.LIVE_VOICE,
                        onClick = { viewModel.setTab(MainTab.LIVE_VOICE) },
                        icon = { Icon(imageVector = Icons.Default.Mic, contentDescription = "Live Voice") },
                        label = { Text("Voice Live", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_live_voice")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == MainTab.PROFILE,
                        onClick = { viewModel.setTab(MainTab.PROFILE) },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShelfmatesDeepBlue,
                            selectedTextColor = ShelfmatesGold,
                            indicatorColor = ShelfmatesGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Thread Detail
                selectedThread != null -> {
                    ThreadDetailScreen(
                        thread = selectedThread,
                        replies = threadReplies,
                        currentUser = currentUser,
                        onBack = { viewModel.selectThread(null) },
                        onSendReply = { reply -> currentUser?.let { viewModel.addThreadReply(selectedThread.id, it, reply) } }
                    )
                }

                // Public Club Detail
                selectedPublicClub != null -> {
                    PublicClubDetailScreen(
                        club = selectedPublicClub,
                        currentUser = currentUser,
                        threads = publicClubThreads,
                        activeVoiceRoom = uiState.activeVoiceRoom,
                        onBack = { viewModel.selectPublicClub(null) },
                        onToggleJoin = { join -> viewModel.joinPublicClub(selectedPublicClub.id, join) },
                        onJoinVoiceRoom = { currentUser?.let { viewModel.joinVoiceRoom(selectedPublicClub, it) } },
                        onLeaveVoiceRoom = { viewModel.leaveVoiceRoom() },
                        onToggleMute = { viewModel.toggleVoiceMute() },
                        onToggleHandRaise = { viewModel.toggleHandRaise() },
                        onCreateThread = { title, body, cat -> currentUser?.let { viewModel.createThread(selectedPublicClub.id, "PUBLIC", it, title, body, cat) } },
                        onSelectThread = { viewModel.selectThread(it) },
                        onReadBook = {
                            // Same entity-boundary bug as the Home reader: a club id
                            // was opened as a book, carrying an ASIN that belonged to
                            // neither. Open the club's actual current book instead.
                            if (selectedPublicClub.currentBookId.isNotBlank()) {
                                viewModel.openInAppReader(
                                    bookId = selectedPublicClub.currentBookId,
                                    title = selectedPublicClub.currentBookTitle,
                                    author = selectedPublicClub.currentBookAuthor,
                                    coverUrl = selectedPublicClub.currentBookCover,
                                    asin = "",
                                    isArc = false
                                )
                            }
                        }
                    )
                }

                // ARC Club Detail
                selectedArcClub != null -> {
                    ArcClubDetailScreen(
                        arc = selectedArcClub,
                        currentUser = currentUser,
                        applications = arcApplications,
                        reviews = arcReviews,
                        onBack = { viewModel.selectArcClub(null) },
                        onOpenApplyDialog = { viewModel.openApplyArcDialog() },
                        onDownloadFile = { viewModel.downloadArcFile(selectedArcClub) },
                        onOpenSubmitReviewDialog = { viewModel.openSubmitReviewDialog() },
                        onSendReminders = { viewModel.sendOneTapReviewReminders(selectedArcClub) },
                        onUpdateAppStatus = { app, status -> viewModel.updateApplicationStatus(app, status, selectedArcClub) },
                        onReadInApp = {
                            viewModel.openInAppReader(
                                bookId = selectedArcClub.id,
                                title = selectedArcClub.bookTitle,
                                author = selectedArcClub.authorName,
                                coverUrl = selectedArcClub.coverUrl,
                                asin = selectedArcClub.asin,
                                isArc = true,
                                format = selectedArcClub.format,
                                supabaseUrl = selectedArcClub.fileUrl
                            )
                        }
                    )
                }

                // Primary Tab Content
                else -> {
                    when (uiState.currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                currentUser = currentUser,
                                publicClubs = publicClubs,
                                arcClubs = arcClubs,
                                broadcasts = broadcasts,
                                bookmarkBalance = bookmarkWallet.balance,
                                googleBooksQuery = uiState.googleBooksQuery,
                                googleBooksResults = uiState.googleBooksResults,
                                isGoogleBooksLoading = uiState.isGoogleBooksLoading,
                                googleBooksError = uiState.googleBooksError,
                                savedBooks = savedBooks,
                                savedBooksMap = savedBooksMap,
                                customShelves = customShelves,
                                cloudSyncState = cloudSyncState,
                                selectedShelfCategoryFilter = uiState.selectedShelfCategoryFilter,
                                onShelfCategoryFilterChange = { viewModel.setShelfCategoryFilter(it) },
                                onSaveBookToShelf = { book, cat -> viewModel.saveGoogleBookToShelf(book, cat) },
                                onUpdateSavedBookCategory = { id, title, cat -> viewModel.updateSavedBookCategory(id, title, cat) },
                                onUpdateSavedBookNotes = { id, notes, rating -> viewModel.updateSavedBookNotes(id, notes, rating) },
                                onRemoveSavedBook = { id, title -> viewModel.removeBookFromShelf(id, title) },
                                onSyncWithCloud = { viewModel.syncBookshelfWithCloud() },
                                onCreateCustomShelf = { name, emoji -> viewModel.createCustomShelf(name, emoji) },
                                onDeleteCustomShelf = { id, name -> viewModel.deleteCustomShelf(id, name) },
                                onGoogleBooksSearchChange = { viewModel.setGoogleBooksQuery(it) },
                                onPerformGoogleBooksSearch = { viewModel.searchGoogleBooks(it) },
                                onGoogleBooksCategorySelect = { viewModel.searchGoogleBooksByCategory(it) },
                                onSelectGoogleBook = { viewModel.openGoogleBookDetail(it) },
                                onCreateClubFromGoogleBook = { viewModel.createPublicClubFromGoogleBook(it) },
                                onCreateArcFromGoogleBook = { viewModel.createArcCampaignFromGoogleBook(it) },
                                onSelectPublicClub = { viewModel.selectPublicClub(it) },
                                onSelectArcClub = { viewModel.selectArcClub(it) },
                                onJoinVoiceRoom = { club -> currentUser?.let { viewModel.joinVoiceRoom(club, it) } },
                                onNavigateDiscover = { viewModel.setTab(MainTab.DISCOVER) },
                                onNavigateBookmarks = { viewModel.setTab(MainTab.BOOKMARKS_REWARDS) },
                                onNavigateArcOpportunities = { viewModel.setTab(MainTab.ARC_OPPORTUNITIES) },
                                onReadArc = { arc ->
                                    viewModel.openInAppReader(
                                        bookId = arc.id,
                                        title = arc.bookTitle,
                                        author = arc.authorName,
                                        coverUrl = arc.coverUrl,
                                        asin = arc.asin,
                                        isArc = true,
                                        format = arc.format,
                                        supabaseUrl = arc.fileUrl
                                    )
                                },
                                onReadPublicClub = { club ->
                                    // Reading a club's book must open THAT book. This
                                    // passed club.id as the bookId, so the reader's
                                    // position was keyed to a club row, and carried a
                                    // hardcoded ASIN matching no real book.
                                    if (club.currentBookId.isNotBlank()) {
                                        viewModel.openInAppReader(
                                            bookId = club.currentBookId,
                                            title = club.currentBookTitle,
                                            author = club.currentBookAuthor,
                                            coverUrl = club.currentBookCover,
                                            asin = "",
                                            isArc = false,
                                            format = "EPUB"
                                        )
                                    }
                                }
                            )
                        }

                        MainTab.DISCOVER -> {
                            DiscoverScreen(
                                currentUser = currentUser,
                                discoverTab = uiState.discoverTab,
                                selectedGenre = uiState.selectedGenre,
                                searchQuery = uiState.searchQuery,
                                availableGenres = viewModel.availableGenres,
                                publicClubs = publicClubs,
                                arcClubs = arcClubs,
                                googleBooksQuery = uiState.googleBooksQuery,
                                googleBooksResults = uiState.googleBooksResults,
                                isGoogleBooksLoading = uiState.isGoogleBooksLoading,
                                googleBooksError = uiState.googleBooksError,
                                savedBooksMap = savedBooksMap,
                                onSaveBookToShelf = { book, cat -> viewModel.saveGoogleBookToShelf(book, cat) },
                                onRemoveBookFromShelf = { gId -> viewModel.removeBookFromShelfByGoogleId(gId) },
                                onSelectDiscoverTab = { viewModel.setDiscoverTab(it) },
                                onSelectGenre = { viewModel.setGenreFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onSelectPublicClub = { viewModel.selectPublicClub(it) },
                                onSelectArcClub = { viewModel.selectArcClub(it) },
                                onToggleJoinPublicClub = { id, join -> viewModel.joinPublicClub(id, join) },
                                onCreatePublicClubClick = { viewModel.openCreatePublicClubDialog() },
                                onCreateArcClubClick = { viewModel.openCreateArcDialog() },
                                onOpenArcOpportunitiesHub = { viewModel.setTab(MainTab.ARC_OPPORTUNITIES) },
                                onGoogleBooksSearchChange = { viewModel.setGoogleBooksQuery(it) },
                                onPerformGoogleBooksSearch = { viewModel.searchGoogleBooks(it) },
                                onGoogleBooksCategorySelect = { viewModel.searchGoogleBooksByCategory(it) },
                                onSelectGoogleBook = { viewModel.openGoogleBookDetail(it) },
                                onCreateClubFromGoogleBook = { viewModel.createPublicClubFromGoogleBook(it) },
                                onCreateArcFromGoogleBook = { viewModel.createArcCampaignFromGoogleBook(it) }
                            )
                        }

                        MainTab.CLUBS -> {
                            MyClubsScreen(
                                currentUser = currentUser,
                                myClubsTab = uiState.myClubsTab,
                                joinedPublicClubs = joinedPublicClubs,
                                myArcReads = myArcReads,
                                allArcClubs = arcClubs,
                                onSelectMyClubsTab = { viewModel.setMyClubsTab(it) },
                                onSelectPublicClub = { viewModel.selectPublicClub(it) },
                                onSelectArcClub = { viewModel.selectArcClub(it) },
                                onToggleJoinPublicClub = { id, join -> viewModel.joinPublicClub(id, join) },
                                onCreatePublicClubClick = { viewModel.openCreatePublicClubDialog() },
                                onCreateArcClubClick = { viewModel.openCreateArcDialog() }
                            )
                        }

                        MainTab.AI_CHAT -> {
                            ShelfChatScreen(
                                currentUser = currentUser,
                                chatMessages = chatMessages,
                                isAiGenerating = isAiGenerating,
                                selectedModel = selectedChatModel,
                                selectedRole = selectedChatRole,
                                onSendMessage = { text -> viewModel.sendChatMessage(text) },
                                onSelectModel = { model -> viewModel.selectChatModel(model) },
                                onSelectRole = { role -> viewModel.selectChatRole(role) },
                                onClearChat = { viewModel.clearChat() },
                                onOpenVoiceMode = { viewModel.setTab(MainTab.LIVE_VOICE) }
                            )
                        }

                        MainTab.LIVE_VOICE -> {
                            GeminiLiveVoiceScreen(
                                sessionState = liveVoiceSessionState,
                                onStartListening = { viewModel.startLiveVoiceListening() },
                                onStopListening = { viewModel.stopLiveVoiceListening() },
                                onToggleTts = { viewModel.toggleLiveVoiceTts() },
                                onSendPromptDirect = { prompt -> viewModel.sendLiveVoicePrompt(prompt) },
                                onClearHistory = { viewModel.clearLiveVoiceHistory() },
                                onBack = { viewModel.setTab(MainTab.AI_CHAT) }
                            )
                        }

                        MainTab.BOOKMARKS_REWARDS -> {
                            BookmarksRewardsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.setTab(MainTab.HOME) }
                            )
                        }

                        MainTab.ARC_OPPORTUNITIES -> {
                            ArcOpportunitiesScreen(
                                currentUser = currentUser,
                                arcClubs = arcClubs,
                                applications = allArcApplications,
                                onBack = { viewModel.setTab(MainTab.HOME) },
                                onSelectArcOpportunity = { viewModel.selectArcClub(it) },
                                onRequestArcAccess = { arc ->
                                    viewModel.openApplyArcDialog(arc.id)
                                },
                                onCreateArcOpportunity = { viewModel.openCreateArcDialog() },
                                onUpdateAppStatus = { app, status, arc ->
                                    viewModel.updateApplicationStatus(app, status, arc)
                                },
                                onSendReminders = { arc ->
                                    viewModel.sendOneTapReviewReminders(arc)
                                },
                                onReadInApp = { arc ->
                                    viewModel.openInAppReader(
                                        bookId = arc.id,
                                        title = arc.bookTitle,
                                        author = arc.authorName,
                                        coverUrl = arc.coverUrl,
                                        asin = arc.asin,
                                        isArc = true,
                                        format = arc.format,
                                        supabaseUrl = arc.fileUrl
                                    )
                                },
                                onDownloadFile = { arc -> viewModel.downloadArcFile(arc) },
                                onSubmitReview = { arc ->
                                    viewModel.selectArcClub(arc.id)
                                    viewModel.openSubmitReviewDialog()
                                }
                            )
                        }

                        MainTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
                                onNotificationClick = { notif ->
                                    viewModel.markNotificationRead(notif.id)
                                    if (notif.targetId.startsWith("arc_")) {
                                        viewModel.selectArcClub(notif.targetId)
                                    } else if (notif.targetId.startsWith("club_")) {
                                        viewModel.selectPublicClub(notif.targetId)
                                    }
                                }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentUser,
                                bookLogs = bookLogs,
                                bookmarkWallet = bookmarkWallet,
                                authState = firebaseAuthState,
                                onSignInWithGoogle = { viewModel.signInWithGoogle(context) },
                                onSignInAnonymously = { viewModel.signInAnonymously() },
                                onSignOut = { viewModel.signOutFirebase() },
                                onNavigateToBookmarks = { viewModel.setTab(MainTab.BOOKMARKS_REWARDS) },
                                onNavigateToArcOpportunities = { viewModel.setTab(MainTab.ARC_OPPORTUNITIES) },
                                onOpenCreateArcDialog = { viewModel.openCreateArcDialog() },
                                onOpenAuthorProModal = { viewModel.openAuthorProModal() },
                                onOpenCsvExportModal = { viewModel.openCsvExportModal() },
                                onOpenBroadcastDialog = { viewModel.openBroadcastDialog() },
                                onOpenPromoBookingDialog = { viewModel.openPromoBookingDialog() },
                                onOpenAddBookLogDialog = { viewModel.openAddBookLogDialog() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (uiState.isCreateArcDialogOpen && currentUser != null) {
        CreateArcClubDialog(
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeCreateArcDialog() },
            onCreate = { title, genre, blurb, format, slots, deadline, asin, minRev ->
                viewModel.createArcClub(currentUser!!, title, genre, blurb, format, slots, deadline, asin, minRev)
            }
        )
    }

    if (uiState.isCreatePublicClubDialogOpen && currentUser != null) {
        CreatePublicClubDialog(
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeCreatePublicClubDialog() },
            onCreate = { name, genre, desc, title, author, bDesc ->
                viewModel.createPublicClub(currentUser!!, name, genre, desc, title, author, bDesc)
            }
        )
    }

    if (uiState.isApplyArcDialogOpen && selectedArcClub != null && currentUser != null) {
        ApplyArcDialog(
            arcClub = selectedArcClub,
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeApplyArcDialog() },
            onApply = { msg, goodreads, pastRev ->
                viewModel.applyForArc(selectedArcClub, currentUser!!, msg, goodreads, pastRev)
            }
        )
    }

    if (uiState.isSubmitReviewDialogOpen && selectedArcClub != null && currentUser != null) {
        SubmitReviewDialog(
            arcClub = selectedArcClub,
            onDismiss = { viewModel.closeSubmitReviewDialog() },
            onSubmit = { rating, text, toAmazon, toGoodreads ->
                viewModel.submitArcReview(selectedArcClub, currentUser!!, rating, text, toAmazon, toGoodreads)
            }
        )
    }

    if (uiState.isAmazonDeepLinkPromptOpen) {
        AmazonDeepLinkDialog(
            asin = uiState.lastReviewedAsin,
            reviewId = uiState.lastSubmittedReviewId,
            onMarkDone = { reviewId -> viewModel.markAmazonReviewCompleted(reviewId) },
            onDismiss = { viewModel.dismissAmazonPrompt() }
        )
    }

    if (uiState.isBroadcastDialogOpen && currentUser != null) {
        BroadcastDialog(
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeBroadcastDialog() },
            onSend = { title, msg, type, url ->
                viewModel.sendBroadcast(currentUser!!, title, msg, type, url)
            }
        )
    }

    if (uiState.isPromoBookingDialogOpen) {
        PromoBookingDialog(
            services = viewModel.getPromoServices(),
            onDismiss = { viewModel.closePromoBookingDialog() },
            onBook = { service -> viewModel.bookPromoService(service) }
        )
    }

    if (uiState.isAuthorProModalOpen && currentUser != null) {
        AuthorProDialog(
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeAuthorProModal() },
            onUpgrade = { viewModel.upgradeToAuthorPro(currentUser!!.id) }
        )
    }

    if (uiState.isCsvExportModalOpen && currentUser != null) {
        CsvExportDialog(
            author = currentUser!!,
            followers = authorFollowers,
            onDismiss = { viewModel.closeCsvExportModal() }
        )
    }

    if (uiState.isAddBookLogDialogOpen && currentUser != null) {
        AddBookLogDialog(
            currentUser = currentUser!!,
            onDismiss = { viewModel.closeAddBookLogDialog() },
            onAdd = { title, author, rating, notes, genre ->
                viewModel.addBookLog(currentUser!!.id, title, author, rating, notes, genre)
            }
        )
    }

    if (uiState.isGoogleBookDetailDialogOpen && uiState.selectedGoogleBookDetail != null) {
        val detailBook = uiState.selectedGoogleBookDetail!!
        GoogleBookDetailDialog(
            book = detailBook,
            currentShelfCategory = savedBooksMap[detailBook.id],
            onSaveToShelf = { category ->
                viewModel.saveGoogleBookToShelf(detailBook, category)
            },
            onRemoveFromShelf = {
                viewModel.removeBookFromShelfByGoogleId(detailBook.id, detailBook.displayTitle)
            },
            onDismiss = { viewModel.closeGoogleBookDetail() },
            onCreatePublicClub = {
                viewModel.createPublicClubFromGoogleBook(detailBook)
            },
            onCreateArcCampaign = {
                viewModel.createArcCampaignFromGoogleBook(detailBook)
            }
        )
    }
}
