package com.shelfmates.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.data.local.ArcClubEntity
import com.shelfmates.data.local.FollowEntity
import com.shelfmates.data.local.UserEntity
import com.shelfmates.data.model.BroadcastType
import com.shelfmates.data.model.PromoServiceItem
import com.shelfmates.ui.components.InteractiveRatingBar
import com.shelfmates.ui.theme.ShelfmatesAmber
import com.shelfmates.ui.theme.ShelfmatesBlue
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesLightBlue
import com.shelfmates.ui.theme.ShelfmatesNavy

@Composable
fun CreateArcClubDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onCreate: (title: String, genre: String, blurb: String, format: String, slots: Int, deadline: String, asin: String, minReviews: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Fantasy") }
    var blurb by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("EPUB & PDF") }
    var slotsText by remember { mutableStateOf("50") }
    var deadlineDate by remember { mutableStateOf("Oct 15, 2026") }
    var asin by remember { mutableStateOf("") }
    var minReviewsText by remember { mutableStateOf("1") }

    val genreList = listOf("Fantasy", "Sci-Fi", "Romance", "Mystery", "Thriller", "LitRPG", "Horror", "Non-Fiction")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "List ARC Opportunity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ShelfmatesGold,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "INDIE AUTHOR",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Distribute advance review copies & gather launch reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title *") },
                    placeholder = { Text("e.g., The Obsidian Crown") },
                    modifier = Modifier.fillMaxWidth().testTag("arc_title_input")
                )

                Column {
                    Text("Genre *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(genreList) { g ->
                            val isSelected = genre == g
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ShelfmatesDeepBlue else ShelfmatesLightBlue,
                                modifier = Modifier.clickable { genre = g }
                            ) {
                                Text(
                                    text = g,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else ShelfmatesDeepBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = blurb,
                    onValueChange = { blurb = it },
                    label = { Text("Hook / Synopsis Blurb *") },
                    placeholder = { Text("Give early reviewers a compelling reason to read your book...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("arc_blurb_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = slotsText,
                        onValueChange = { slotsText = it },
                        label = { Text("Reviewer Slots *") },
                        modifier = Modifier.weight(1f).testTag("arc_slots_input")
                    )
                    OutlinedTextField(
                        value = deadlineDate,
                        onValueChange = { deadlineDate = it },
                        label = { Text("Review Deadline *") },
                        modifier = Modifier.weight(1f).testTag("arc_deadline_input")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = format,
                        onValueChange = { format = it },
                        label = { Text("Format (e.g., EPUB, PDF)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = asin,
                        onValueChange = { asin = it },
                        label = { Text("Amazon ASIN (Optional)") },
                        placeholder = { Text("B09...") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = minReviewsText,
                    onValueChange = { minReviewsText = it },
                    label = { Text("Min Past Reviews Required (Reader criteria)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ShelfmatesDeepBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Readers must request access. You can review their Goodreads/Amazon profile before approving.",
                            fontSize = 11.sp,
                            color = ShelfmatesNavy
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val slots = slotsText.toIntOrNull() ?: 50
                    val minReviews = minReviewsText.toIntOrNull() ?: 0
                    if (title.isNotBlank()) {
                        onCreate(title, genre, blurb, format, slots, deadlineDate, asin, minReviews)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                modifier = Modifier.testTag("publish_arc_button").testTag("submit_create_arc_button")
            ) {
                Text("List ARC Opportunity")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreatePublicClubDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onCreate: (name: String, genre: String, desc: String, bookTitle: String, bookAuthor: String, bookDesc: String) -> Unit,
    genreOptions: List<String> = listOf("Fantasy", "Sci-Fi", "Romance", "Mystery", "LitRPG", "Thriller", "Horror", "Non-Fiction", "YA", "Other")
) {
    var name by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf(genreOptions.firstOrNull() ?: "Fantasy") }
    var description by remember { mutableStateOf("") }
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookDesc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Public Book Club",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Club Name *") },
                    placeholder = { Text("e.g. Midnight Fantasy Guild") },
                    modifier = Modifier.fillMaxWidth().testTag("public_club_name_input")
                )

                // Genre is drawn from the same taxonomy the Discover clubs feed
                // filters by, so a club created here is always reachable from
                // its genre chip. Free-text genre tags silently orphaned clubs
                // from genre navigation.
                Column {
                    Text(
                        text = "Genre *",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(genreOptions) { option ->
                            val isSelected = genre == option
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable { genre = option }
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Club Description *") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "First Current Read",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ShelfmatesDeepBlue,
                    modifier = Modifier.padding(top = 6.dp)
                )

                OutlinedTextField(
                    value = bookTitle,
                    onValueChange = { bookTitle = it },
                    label = { Text("Book Title *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bookAuthor,
                    onValueChange = { bookAuthor = it },
                    label = { Text("Author Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bookDesc,
                    onValueChange = { bookDesc = it },
                    label = { Text("Book Summary") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && bookTitle.isNotBlank()) {
                        onCreate(name, genre, description, bookTitle, bookAuthor, bookDesc)
                    }
                },
                enabled = name.isNotBlank() && bookTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
            ) {
                Text("Create Club")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ApplyArcDialog(
    arcClub: ArcClubEntity,
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onApply: (message: String, goodreadsUrl: String, pastReviews: Int) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var goodreadsUrl by remember { mutableStateOf("https://goodreads.com/${currentUser.displayName.lowercase().replace(" ", "")}") }
    var pastReviewsText by remember { mutableStateOf("8") }
    var preferredFormat by remember { mutableStateOf("EPUB") }
    var willReviewAmazon by remember { mutableStateOf(true) }
    var willReviewGoodreads by remember { mutableStateOf(true) }
    var willReviewBookTok by remember { mutableStateOf(false) }
    var agreeToDeadline by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Request ARC Access",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ShelfmatesDeepBlue,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "EARLY COPY",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = arcClub.bookTitle,
                    color = ShelfmatesBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Author note info card
                Surface(
                    color = ShelfmatesLightBlue,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Indie Author: ${arcClub.authorName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ShelfmatesDeepBlue
                        )
                        Text(
                            text = "Honest review requested on or before ${arcClub.deadlineDate} (${arcClub.daysRemaining} days left).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Format Preference
                Column {
                    Text(
                        text = "Preferred Reading Format",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("EPUB", "PDF", "MOBI").forEach { fmt ->
                            val isSelected = preferredFormat == fmt
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.clickable { preferredFormat = fmt }
                            ) {
                                Text(
                                    text = fmt,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Platforms to Review On
                Column {
                    Text(
                        text = "Where will you post your review?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = willReviewAmazon,
                            onCheckedChange = { willReviewAmazon = it }
                        )
                        Text("Amazon", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Checkbox(
                            checked = willReviewGoodreads,
                            onCheckedChange = { willReviewGoodreads = it }
                        )
                        Text("Goodreads", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Checkbox(
                            checked = willReviewBookTok,
                            onCheckedChange = { willReviewBookTok = it }
                        )
                        Text("BookTok", fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Note to Author (Why you want to read) *") },
                    placeholder = { Text("I read in this genre regularly and love supporting indie launches...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("arc_application_message_input")
                )

                OutlinedTextField(
                    value = goodreadsUrl,
                    onValueChange = { goodreadsUrl = it },
                    label = { Text("Reviewer Profile (Goodreads / Amazon link)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pastReviewsText,
                    onValueChange = { pastReviewsText = it },
                    label = { Text("Past Reviews Posted") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreeToDeadline,
                        onCheckedChange = { agreeToDeadline = it }
                    )
                    Text(
                        text = "I commit to reading this copy and posting an honest review before the deadline.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = pastReviewsText.toIntOrNull() ?: 0
                    val platforms = buildList {
                        if (willReviewAmazon) add("Amazon")
                        if (willReviewGoodreads) add("Goodreads")
                        if (willReviewBookTok) add("BookTok")
                    }.joinToString(", ")
                    val fullMessage = "[$preferredFormat | Platforms: $platforms] $message"
                    onApply(fullMessage, goodreadsUrl, count)
                },
                enabled = message.isNotBlank() && agreeToDeadline,
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                modifier = Modifier
                    .testTag("submit_arc_request_button")
                    .testTag("submit_arc_application_button")
            ) {
                Text("Submit Access Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SubmitReviewDialog(
    arcClub: ArcClubEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, text: String, postToAmazon: Boolean, postToGoodreads: Boolean) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var postToAmazon by remember { mutableStateOf(true) }
    var postToGoodreads by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Submit ARC Review",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = arcClub.bookTitle,
                    color = ShelfmatesBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Your Rating:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                InteractiveRatingBar(
                    rating = rating,
                    onRatingChange = { rating = it }
                )

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Review Text *") },
                    placeholder = { Text("Write your honest thoughts, character impressions, pacing, and recommendations...") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("arc_review_text_input")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { postToAmazon = !postToAmazon }
                ) {
                    Checkbox(
                        checked = postToAmazon,
                        onCheckedChange = { postToAmazon = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Prompt me to Copy to Amazon Review on launch", fontSize = 12.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { postToGoodreads = !postToGoodreads }
                ) {
                    Checkbox(
                        checked = postToGoodreads,
                        onCheckedChange = { postToGoodreads = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Include Goodreads link after submitting", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reviewText.isNotBlank()) {
                        onSubmit(rating, reviewText, postToAmazon, postToGoodreads)
                    }
                },
                enabled = reviewText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesEmerald),
                modifier = Modifier.testTag("submit_review_action_button")
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AmazonDeepLinkDialog(
    asin: String,
    reviewId: String?,
    onMarkDone: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val amazonReviewUrl = "https://amazon.com/review/create-review?asin=${asin.ifBlank { "B09XRAY101" }}"
    val goodreadsUrl = "https://www.goodreads.com/review/edit"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ShelfmatesEmerald,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Review Posted in Shelfmates!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Now amplify your support for the indie author! Copy your review and paste it on Amazon and Goodreads.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Amazon Deep Link Target:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesDeepBlue
                        )
                        Text(
                            text = amazonReviewUrl,
                            fontSize = 11.sp,
                            color = ShelfmatesNavy,
                            maxLines = 1
                        )
                    }
                }

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(amazonReviewUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesAmber),
                    modifier = Modifier.fillMaxWidth().testTag("open_amazon_review_button")
                ) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy & Open Amazon Review Page", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(goodreadsUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Goodreads Review Page")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    reviewId?.let { onMarkDone(it) } ?: onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
            ) {
                Text("Done / Mark Complete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Later") }
        }
    )
}

@Composable
fun BroadcastDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onSend: (title: String, message: String, type: BroadcastType, actionUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(BroadcastType.RELEASE) }
    var actionUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Broadcast to Followers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reaches all ${currentUser.followersCount} readers who follow you",
                    fontSize = 12.sp,
                    color = ShelfmatesBlue
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Broadcast Type:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        BroadcastType.RELEASE to "Release",
                        BroadcastType.ARC_OPENING to "ARC Open",
                        BroadcastType.PRICE_DROP to "Sale",
                        BroadcastType.COVER_REVEAL to "Cover"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        Surface(
                            onClick = { selectedType = type },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Broadcast Title *") },
                    placeholder = { Text("e.g. Flash Sale or Cover Reveal!") },
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input")
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Body *") },
                    placeholder = { Text("Write your update to readers...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_message_input")
                )

                OutlinedTextField(
                    value = actionUrl,
                    onValueChange = { actionUrl = it },
                    label = { Text("Action URL / Amazon Link (Optional)") },
                    placeholder = { Text("https://amazon.com/dp/...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title, message, selectedType, actionUrl)
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                modifier = Modifier.testTag("send_broadcast_button")
            ) {
                Text("Send Broadcast")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PromoBookingDialog(
    services: List<PromoServiceItem>,
    onDismiss: () -> Unit,
    onBook: (PromoServiceItem) -> Unit
) {
    var selectedService by remember { mutableStateOf(services.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Atomic Shelf Promo Services",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Curated distribution for indie book launches",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                services.forEach { service ->
                    val isSelected = selectedService?.id == service.id
                    Card(
                        onClick = { selectedService = service },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ShelfmatesLightBlue else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = service.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) ShelfmatesNavy else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = service.price,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = ShelfmatesDeepBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = service.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ShelfmatesEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = service.reachEstimate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ShelfmatesEmerald
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedService?.let { onBook(it) }
                },
                enabled = selectedService != null,
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
            ) {
                Text("Book Placement (${selectedService?.price ?: ""})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AuthorProDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    var billingPlan by remember { mutableStateOf("monthly") } // "monthly" or "annual"
    var paymentMethod by remember { mutableStateOf("play_billing") } // "play_billing" or "pesapal"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ShelfmatesGold)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Shelfmates Author Pro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Professional toolkit for self-published indie authors",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Feature checklist
                listOf(
                    "Create unlimited ARC Clubs (up to 500 readers per club)",
                    "Direct reader list building & follower broadcasts",
                    "Export consented follower emails to CSV (GDPR compliant)",
                    "ARC Review tracking dashboard with 1-tap push reminders",
                    "Priority placement in genre discovery feeds",
                    "Atomic Shelf launch campaign discounts"
                ).forEach { feature ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ShelfmatesEmerald,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feature, fontSize = 12.sp)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Plan selection
                Text("Select Billing Cycle:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        onClick = { billingPlan = "monthly" },
                        colors = CardDefaults.cardColors(
                            containerColor = if (billingPlan == "monthly") ShelfmatesLightBlue else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.weight(1f).border(
                            width = if (billingPlan == "monthly") 2.dp else 1.dp,
                            color = if (billingPlan == "monthly") ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Monthly", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("$19.99 / mo", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ShelfmatesDeepBlue)
                            Text("Cancel anytime", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        onClick = { billingPlan = "annual" },
                        colors = CardDefaults.cardColors(
                            containerColor = if (billingPlan == "annual") ShelfmatesLightBlue else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.weight(1f).border(
                            width = if (billingPlan == "annual") 2.dp else 1.dp,
                            color = if (billingPlan == "annual") ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Annual", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(3.dp)).background(ShelfmatesGold).padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("SAVE 20%", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("$189.99 / yr", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ShelfmatesDeepBlue)
                            Text("$15.83 / mo billed yearly", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Payment provider
                Text("Payment Provider:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).clickable { paymentMethod = "play_billing" }
                    ) {
                        RadioButton(selected = paymentMethod == "play_billing", onClick = { paymentMethod = "play_billing" })
                        Text("Store Billing", fontSize = 12.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).clickable { paymentMethod = "pesapal" }
                    ) {
                        RadioButton(selected = paymentMethod == "pesapal", onClick = { paymentMethod = "pesapal" })
                        Text("Pesapal (Africa)", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                modifier = Modifier.testTag("subscribe_pro_button")
            ) {
                Text(if (billingPlan == "monthly") "Subscribe • $19.99/mo" else "Subscribe • $189.99/yr")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CsvExportDialog(
    author: UserEntity,
    followers: List<FollowEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val consentedFollowers = followers.filter { it.emailConsent }
    val csvContent = buildString {
        append("Name,Email,EmailConsent,FollowedDate\n")
        consentedFollowers.forEach { f ->
            append("\"${f.followerName}\",\"${f.followerEmail}\",true,\"${f.createdAt}\"\n")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Export Reader List (CSV)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Author Pro Feature • List Building",
                    fontSize = 12.sp,
                    color = ShelfmatesDeepBlue
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ShelfmatesLightBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Followers:", fontSize = 12.sp)
                            Text("${followers.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("GDPR/CAN-SPAM Consented:", fontSize = 12.sp, color = ShelfmatesEmerald)
                            Text("${consentedFollowers.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ShelfmatesEmerald)
                        }
                    }
                }

                Text(
                    text = "CSV Preview:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = csvContent,
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = "Readers have explicitly consented to receiving author newsletter updates under GDPR and CAN-SPAM regulations.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Shelfmates Reader List", csvContent)
                    clipboard.setPrimaryClip(clip)

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, csvContent)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Export Shelfmates CSV")
                    context.startActivity(shareIntent)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue),
                modifier = Modifier.testTag("export_csv_share_button")
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy & Share CSV")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddBookLogDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onAdd: (title: String, author: String, rating: Int, notes: String, genre: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Fantasy") }
    var rating by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add to Books Read Log",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title *") },
                    modifier = Modifier.fillMaxWidth().testTag("book_log_title_input")
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Your Rating:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                InteractiveRatingBar(
                    rating = rating,
                    onRatingChange = { rating = it }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reading Notes / Highlights") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, author, rating, notes, genre)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ShelfmatesDeepBlue)
            ) {
                Text("Save to Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
