package com.shelfmates.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.PublicClubEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.UserRole
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.viewmodel.MyClubsTab

@Composable
fun MyClubsScreen(
    currentUser: UserEntity?,
    myClubsTab: MyClubsTab,
    joinedPublicClubs: List<PublicClubEntity>,
    myArcReads: List<ArcClubEntity>,
    allArcClubs: List<ArcClubEntity>,
    onSelectMyClubsTab: (MyClubsTab) -> Unit,
    onSelectPublicClub: (String) -> Unit,
    onSelectArcClub: (String) -> Unit,
    onToggleJoinPublicClub: (String, Boolean) -> Unit,
    onCreatePublicClubClick: () -> Unit,
    onCreateArcClubClick: () -> Unit
) {
    val authorArcClubs = allArcClubs.filter { it.authorUserId == currentUser?.id }
    val isAuthor = currentUser?.role == UserRole.AUTHOR

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (myClubsTab == MyClubsTab.AUTHOR_CAMPAIGNS) {
                        onCreateArcClubClick()
                    } else {
                        onCreatePublicClubClick()
                    }
                },
                containerColor = ShelfmatesDeepBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_club_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("my_clubs_screen")
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = when (myClubsTab) {
                    MyClubsTab.JOINED_PUBLIC -> 0
                    MyClubsTab.ARC_READS -> 1
                    MyClubsTab.AUTHOR_CAMPAIGNS -> 2
                },
                containerColor = ShelfmatesDeepBlue,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    val tabIdx = when (myClubsTab) {
                        MyClubsTab.JOINED_PUBLIC -> 0
                        MyClubsTab.ARC_READS -> 1
                        MyClubsTab.AUTHOR_CAMPAIGNS -> 2
                    }
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                        color = ShelfmatesGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = myClubsTab == MyClubsTab.JOINED_PUBLIC,
                    onClick = { onSelectMyClubsTab(MyClubsTab.JOINED_PUBLIC) },
                    text = {
                        Text("Joined Clubs (${joinedPublicClubs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    },
                    modifier = Modifier.testTag("my_joined_clubs_tab")
                )
                Tab(
                    selected = myClubsTab == MyClubsTab.ARC_READS,
                    onClick = { onSelectMyClubsTab(MyClubsTab.ARC_READS) },
                    text = {
                        Text("My ARC Reads (${myArcReads.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    },
                    modifier = Modifier.testTag("my_arc_reads_tab")
                )
                if (isAuthor) {
                    Tab(
                        selected = myClubsTab == MyClubsTab.AUTHOR_CAMPAIGNS,
                        onClick = { onSelectMyClubsTab(MyClubsTab.AUTHOR_CAMPAIGNS) },
                        text = {
                            Text("Author ARCs (${authorArcClubs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        },
                        modifier = Modifier.testTag("my_author_campaigns_tab")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (myClubsTab) {
                    MyClubsTab.JOINED_PUBLIC -> {
                        if (joinedPublicClubs.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    message = "You haven't joined any public book clubs yet.",
                                    actionLabel = "Start a Public Club",
                                    onAction = onCreatePublicClubClick
                                )
                            }
                        } else {
                            items(joinedPublicClubs) { club ->
                                PublicClubDiscoverCard(
                                    club = club,
                                    onClick = { onSelectPublicClub(club.id) },
                                    onToggleJoin = { onToggleJoinPublicClub(club.id, !club.isJoined) }
                                )
                            }
                        }
                    }
                    MyClubsTab.ARC_READS -> {
                        if (myArcReads.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    message = "You have no active ARC reads or applications.",
                                    actionLabel = "Browse Available ARCs",
                                    onAction = {}
                                )
                            }
                        } else {
                            items(myArcReads) { arc ->
                                ArcClubDiscoverCard(
                                    arc = arc,
                                    onClick = { onSelectArcClub(arc.id) }
                                )
                            }
                        }
                    }
                    MyClubsTab.AUTHOR_CAMPAIGNS -> {
                        if (authorArcClubs.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    message = "You haven't created any ARC launch campaigns yet.",
                                    actionLabel = "Create ARC Club Campaign",
                                    onAction = onCreateArcClubClick
                                )
                            }
                        } else {
                            items(authorArcClubs) { arc ->
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
}