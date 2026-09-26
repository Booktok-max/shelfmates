package com.shelfmates.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.BookmarkRedemptionEntity
import com.shelfmates.data.local.BookmarkTransactionEntity
import com.shelfmates.data.model.BookmarkQuest
import com.shelfmates.data.model.BookmarkRewardCategory
import com.shelfmates.data.model.BookmarkRewardItem
import com.shelfmates.data.model.BookmarkTier
import com.shelfmates.data.model.BookmarkWallet
import com.shelfmates.data.model.QuestType
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesDarkCrimson
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesInkBlack
import com.shelfmates.ui.viewmodel.ShelfmatesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class BookmarksSectionTab(val title: String, val icon: String) {
    REWARDS_STORE("Rewards Store", "🎁"),
    EARN_QUESTS("Earn & Quests", "🎯"),
    MY_VAULT("My Vault & History", "📜")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksRewardsScreen(
    viewModel: ShelfmatesViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val uiState by viewModel.uiState.collectAsState()
    val wallet by viewModel.bookmarkWallet.collectAsState()
    val quests by viewModel.bookmarkQuests.collectAsState()
    val transactions by viewModel.bookmarkTransactions.collectAsState(initial = emptyList())
    val redemptions by viewModel.bookmarkRedemptions.collectAsState(initial = emptyList())

    var selectedSection by remember { mutableStateOf(BookmarksSectionTab.REWARDS_STORE) }
    var showTierInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Bookmarks",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ShelfmatesAmber
                        ) {
                            Text(
                                text = "REWARDS",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showTierInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tier Guide",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShelfmatesDeepBlue
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Wallet & Tier Header
            item {
                BookmarkWalletHeroCard(
                    wallet = wallet,
                    hasClaimedToday = uiState.hasClaimedDailyCheckInToday,
                    onDailyCheckIn = { viewModel.performDailyCheckIn() },
                    onTierClick = { showTierInfoDialog = true }
                )
            }

            // Hub Navigation Tabs
            item {
                BookmarksSectionSelector(
                    selectedSection = selectedSection,
                    onSectionSelected = { selectedSection = it }
                )
            }

            // Section Specific Content
            when (selectedSection) {
                BookmarksSectionTab.REWARDS_STORE -> {
                    item {
                        RewardCategoriesFilter(
                            selectedCategory = uiState.selectedBookmarkRewardCategory,
                            onSelectCategory = { viewModel.setBookmarkRewardCategory(it) }
                        )
                    }

                    val filteredRewards = viewModel.allBookmarkRewards.filter {
                        uiState.selectedBookmarkRewardCategory == BookmarkRewardCategory.ALL ||
                                it.category == uiState.selectedBookmarkRewardCategory
                    }

                    if (filteredRewards.isEmpty()) {
                        item {
                            EmptyRewardsPlaceholder()
                        }
                    } else {
                        items(filteredRewards, key = { it.id }) { reward ->
                            RewardItemCard(
                                reward = reward,
                                currentBalance = wallet.balance,
                                userTier = wallet.tier,
                                onRedeemClick = {
                                    viewModel.setSelectedRewardForRedeem(reward)
                                }
                            )
                        }
                    }
                }

                BookmarksSectionTab.EARN_QUESTS -> {
                    item {
                        QuestsOverviewHeader(
                            completedCount = quests.count { it.isCompleted },
                            totalCount = quests.size
                        )
                    }

                    items(quests, key = { it.id }) { quest ->
                        QuestItemCard(
                            quest = quest,
                            multiplier = wallet.multiplier,
                            onClaim = { viewModel.claimQuestReward(quest.id) }
                        )
                    }
                }

                BookmarksSectionTab.MY_VAULT -> {
                    item {
                        VaultSummaryHeader(
                            redemptionsCount = redemptions.size,
                            lifetimeEarned = wallet.lifetimeEarned
                        )
                    }

                    if (redemptions.isNotEmpty()) {
                        item {
                            Text(
                                text = "Active Rewards & Codes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        items(redemptions, key = { it.id }) { redemption ->
                            RedeemedVoucherCard(
                                redemption = redemption,
                                onCopyCode = { code ->
                                    clipboardManager.setText(AnnotatedString(code))
                                    Toast.makeText(context, "Code copied: $code", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Point Activity Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (transactions.isEmpty()) {
                        item {
                            EmptyHistoryPlaceholder()
                        }
                    } else {
                        items(transactions, key = { it.id }) { tx ->
                            TransactionLedgerRow(transaction = tx)
                        }
                    }
                }
            }
        }
    }

    // Modal: Confirm Redemption Dialog
    uiState.selectedRewardForRedeem?.let { reward ->
        AlertDialog(
            onDismissRequest = { viewModel.setSelectedRewardForRedeem(null) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(reward.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Redeem Reward", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = reward.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reward.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = ShelfmatesAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cost:", fontWeight = FontWeight.Medium)
                            Text(
                                "${reward.costBookmarks} Bookmarks 🔖",
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesDeepBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Your Balance: ${wallet.balance} 🔖",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Remaining: ${wallet.balance - reward.costBookmarks} 🔖",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (wallet.balance >= reward.costBookmarks) Color(0xFF2E7D32) else Color.Red
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.redeemBookmarkReward(reward) },
                    enabled = wallet.balance >= reward.costBookmarks,
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
                ) {
                    Text("Confirm Redemption")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setSelectedRewardForRedeem(null) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Redemption Success Voucher Dialog
    uiState.lastRedemptionSuccess?.let { redemption ->
        AlertDialog(
            onDismissRequest = { viewModel.closeRedemptionSuccessDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎉", fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reward Unlocked!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = redemption.rewardTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        color = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Use this code to claim your perk:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = redemption.redemptionCode,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = ShelfmatesDeepBlue
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(redemption.redemptionCode))
                                    Toast.makeText(context, "Copied code: ${redemption.redemptionCode}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = ShelfmatesDeepBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Saved in your 'My Vault' tab. You can view or copy it anytime.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.closeRedemptionSuccessDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Awesome!")
                }
            }
        )
    }

    // Modal: Daily Check-In Success
    if (uiState.isDailyCheckInSuccessDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDailyCheckInDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daily Reading Streak!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "+${uiState.earnedBookmarksAnimationAmount ?: 25} Bookmarks Added",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = ShelfmatesAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You're on a 14-day reading streak! Keep checking in every day to maximize your tier points multiplier.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.closeDailyCheckInDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep Reading")
                }
            }
        )
    }

    // Modal: Tier Guide & Benefits
    if (showTierInfoDialog) {
        AlertDialog(
            onDismissRequest = { showTierInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ShelfmatesGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bookmark Tiers", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BookmarkTier.entries.forEach { tier ->
                        val isCurrent = wallet.tier == tier
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurrent) ShelfmatesAmber.copy(alpha = 0.2f) else Color(0xFFF8FAFC),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, ShelfmatesAmber) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(tier.badgeSymbol, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tier.tierName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Text(
                                        "${tier.multiplierText} (≥${tier.minPoints} pts)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ShelfmatesDeepBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    tier.perkDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTierInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
fun BookmarkWalletHeroCard(
    wallet: BookmarkWallet,
    hasClaimedToday: Boolean,
    onDailyCheckIn: () -> Unit,
    onTierClick: () -> Unit
) {
    val nextTier = BookmarkTier.nextTier(wallet.tier)
    val nextTierTarget = nextTier?.minPoints ?: wallet.tier.minPoints
    val prevTierMin = wallet.tier.minPoints
    val progressInTier = if (nextTier != null && (nextTierTarget - prevTierMin) > 0) {
        ((wallet.lifetimeEarned - prevTierMin).toFloat() / (nextTierTarget - prevTierMin)).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progressInTier,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tierProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShelfmatesGold.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ShelfmatesDarkCrimson,
                            ShelfmatesDeepBlue,
                            ShelfmatesInkBlack
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Header row: Tier Badge & Streak
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onTierClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(wallet.tier.badgeSymbol, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = wallet.tier.tierName,
                                color = Color.White,
                                fontFamily = BookDisplayFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ShelfmatesAmber.copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFF7A3500),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${wallet.activeStreakDays}d Streak (${wallet.tier.multiplierText})",
                                color = Color(0xFF421D00),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Balance Display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${wallet.balance}",
                                fontSize = 42.sp,
                                fontFamily = BookDisplayFont,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🔖", fontSize = 28.sp)
                        }
                    }

                    Button(
                        onClick = onDailyCheckIn,
                        enabled = !hasClaimedToday,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShelfmatesGold,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.2f),
                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        if (hasClaimedToday) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Claimed Today", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Daily +25 🔖", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tier Progress Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lifetime: ${wallet.lifetimeEarned} pts",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        if (nextTier != null) {
                            Text(
                                text = "${nextTierTarget - wallet.lifetimeEarned} pts to ${nextTier.tierName} ${nextTier.badgeSymbol}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesAmber
                            )
                        } else {
                            Text(
                                text = "Max Tier Achieved 👑",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ShelfmatesAmber,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarksSectionSelector(
    selectedSection: BookmarksSectionTab,
    onSectionSelected: (BookmarksSectionTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BookmarksSectionTab.entries.forEach { section ->
            val isSelected = selectedSection == section
            Surface(
                onClick = { onSectionSelected(section) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) ShelfmatesDeepBlue else Color.Transparent,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(section.icon, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = section.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RewardCategoriesFilter(
    selectedCategory: BookmarkRewardCategory,
    onSelectCategory: (BookmarkRewardCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BookmarkRewardCategory.entries.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onSelectCategory(category) },
                label = { Text(category.displayName, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ShelfmatesDeepBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun RewardItemCard(
    reward: BookmarkRewardItem,
    currentBalance: Int,
    userTier: BookmarkTier,
    onRedeemClick: () -> Unit
) {
    val canAfford = currentBalance >= reward.costBookmarks
    val isTierLocked = userTier.minPoints < reward.tierRequirement.minPoints

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Reward Icon Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(ShelfmatesDeepBlue.copy(alpha = 0.08f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(reward.iconEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Titles and category
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ShelfmatesDeepBlue.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = reward.category.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShelfmatesDeepBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (reward.isFeatured) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ShelfmatesAmber
                            ) {
                                Text(
                                    text = "FEATURED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (reward.stockRemaining != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${reward.stockRemaining} left",
                                fontSize = 11.sp,
                                color = Color(0xFFC05621),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reward.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = reward.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = reward.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action row with cost and button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${reward.costBookmarks}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bookmarks 🔖", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShelfmatesAmber)
                }

                if (isTierLocked) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Requires ${reward.tierRequirement.tierName}", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onRedeemClick,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShelfmatesDeepBlue,
                            disabledContainerColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (canAfford) "Redeem Perk" else "Need ${reward.costBookmarks - currentBalance} more",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestItemCard(
    quest: BookmarkQuest,
    multiplier: Float,
    onClaim: () -> Unit
) {
    val effectivePoints = (quest.rewardBookmarks * multiplier).toInt()
    val progressFraction = (quest.currentProgress.toFloat() / quest.targetProgress.coerceAtLeast(1)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(quest.iconEmoji, fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (quest.type) {
                                QuestType.DAILY -> Color(0xFFE0F2FE)
                                QuestType.WEEKLY -> Color(0xFFFEF3C7)
                                QuestType.MILESTONE -> Color(0xFFF3E8FF)
                            }
                        ) {
                            Text(
                                text = quest.type.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (quest.type) {
                                    QuestType.DAILY -> Color(0xFF0369A1)
                                    QuestType.WEEKLY -> Color(0xFFB45309)
                                    QuestType.MILESTONE -> Color(0xFF7E22CE)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = quest.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Reward Tag
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ShelfmatesAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "+$effectivePoints 🔖",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quest.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar and claim button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${quest.currentProgress} / ${quest.targetProgress}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesDeepBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) Color(0xFF16A34A) else ShelfmatesDeepBlue,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (quest.isClaimed) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Claimed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                } else if (quest.isCompleted) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesAmber, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Claim +$effectivePoints", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                } else {
                    Text(
                        text = "In Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RedeemedVoucherCard(
    redemption: BookmarkRedemptionEntity,
    onCopyCode: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(redemption.iconEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = redemption.rewardTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Cost: ${redemption.costBookmarks} 🔖",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (redemption.status == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = redemption.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (redemption.status == "ACTIVE") Color(0xFF15803D) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = redemption.redemptionCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ShelfmatesDeepBlue
                    )
                    IconButton(
                        onClick = { onCopyCode(redemption.redemptionCode) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionLedgerRow(transaction: BookmarkTransactionEntity) {
    val isEarned = transaction.type == "EARNED"
    val dateStr = remember(transaction.timestamp) {
        SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(transaction.timestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isEarned) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(transaction.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = transaction.note,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Text(
                text = if (isEarned) "+${transaction.bookmarksAmount} 🔖" else "${transaction.bookmarksAmount} 🔖",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isEarned) Color(0xFF15803D) else Color(0xFFB91C1C)
            )
        }
    }
}

@Composable
fun QuestsOverviewHeader(completedCount: Int, totalCount: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = ShelfmatesDeepBlue.copy(alpha = 0.06f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, tint = ShelfmatesDeepBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Daily & Milestone Quests", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Complete reading and club activities to earn Bookmarks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(shape = RoundedCornerShape(8.dp), color = ShelfmatesDeepBlue) {
                Text(
                    text = "$completedCount / $totalCount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VaultSummaryHeader(redemptionsCount: Int, lifetimeEarned: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF1F5F9)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Vouchers Claimed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$redemptionsCount Active", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ShelfmatesDeepBlue)
            }
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF1F5F9)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Lifetime Points", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$lifetimeEarned 🔖", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ShelfmatesAmber)
            }
        }
    }
}

@Composable
fun EmptyRewardsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎁", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("No rewards in this category yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Check back soon for new physical swag and author boosts.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyHistoryPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📜", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("No activity yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Earn Bookmarks by reading, reviewing, and checking in daily.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
