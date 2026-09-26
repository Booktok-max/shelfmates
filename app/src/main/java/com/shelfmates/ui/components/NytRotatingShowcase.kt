package com.shelfmates.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.data.model.NytBestsellerBook
import com.shelfmates.data.model.NytBestsellerData
import com.shelfmates.ui.theme.BookDisplayFont
import com.shelfmates.ui.theme.ShelfmatesDeepBlue
import com.shelfmates.ui.theme.ShelfmatesEmerald
import com.shelfmates.ui.theme.ShelfmatesGold
import com.shelfmates.ui.theme.ShelfmatesNavy
import kotlinx.coroutines.delay

/**
 * An interactive rotating showcase of The New York Times Best Sellers for the front page.
 * Includes smooth auto-rotation, NYT external linking, and instant reading shelf integration.
 */
@Composable
fun NytRotatingShowcase(
    modifier: Modifier = Modifier,
    books: List<NytBestsellerBook> = NytBestsellerData.rotatingFrontPageBooks,
    onSelectBook: (GoogleBookVolumeItem) -> Unit,
    onSaveToShelf: (GoogleBookVolumeItem, String) -> Unit,
    onCreateClub: (GoogleBookVolumeItem) -> Unit,
    onSearchTopic: (String) -> Unit
) {
    if (books.isEmpty()) return

    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    var isUserPaused by remember { mutableStateOf(false) }

    // Auto-advance every 5 seconds unless user paused
    LaunchedEffect(isUserPaused, books.size) {
        if (!isUserPaused && books.size > 1) {
            while (true) {
                delay(5000)
                currentIndex = (currentIndex + 1) % books.size
            }
        }
    }

    val currentBook = books[currentIndex.coerceIn(0, books.lastIndex)]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("nyt_rotating_showcase_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ShelfmatesGold.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar: NYT Branding & Link to Official List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "NYT",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "The New York Times",
                            fontFamily = BookDisplayFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Rotating Front Page Best Sellers",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Official Link to NYT Best Sellers
                Surface(
                    color = ShelfmatesGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ShelfmatesGold.copy(alpha = 0.6f)),
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(NytBestsellerData.NYT_BESTSELLERS_URL))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Visit NYT List",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShelfmatesNavy
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open NYT Bestsellers in browser",
                            modifier = Modifier.size(11.dp),
                            tint = ShelfmatesNavy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Book Showcase Body
            AnimatedContent(
                targetState = currentBook,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 3 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                },
                label = "nyt_book_crossfade"
            ) { book ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isUserPaused = true
                            onSelectBook(book.toGoogleBookVolumeItem())
                        },
                    verticalAlignment = Alignment.Top
                ) {
                    // Book Cover with ranking ribbon
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .height(144.dp)
                            .shadow(6.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(ShelfmatesNavy.copy(alpha = 0.1f))
                    ) {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = "Cover of ${book.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Rank Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(bottomEnd = 8.dp))
                                .background(Color.Black.copy(alpha = 0.85f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#${book.rank} NYT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ShelfmatesGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Details Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Category & Weeks on List
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = ShelfmatesEmerald.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${book.weeksOnList} WEEKS ON LIST",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShelfmatesEmerald,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = book.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Title
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = BookDisplayFont,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Author
                        Text(
                            text = "by ${book.author}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShelfmatesDeepBlue
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // NYT Review quote / praise
                        Text(
                            text = book.quoteOrPraise,
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Synopsis
                        Text(
                            text = book.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Details, Shelf, Club
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        isUserPaused = true
                        onSelectBook(currentBook.toGoogleBookVolumeItem())
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShelfmatesDeepBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read / Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        isUserPaused = true
                        onSaveToShelf(currentBook.toGoogleBookVolumeItem(), "To Read")
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ShelfmatesDeepBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ShelfmatesDeepBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("To Read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShelfmatesDeepBlue)
                }

                IconButton(
                    onClick = {
                        isUserPaused = true
                        onCreateClub(currentBook.toGoogleBookVolumeItem())
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(ShelfmatesGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Create club for this book",
                        tint = ShelfmatesNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Carousel Controls & Indicator Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev / Next manual controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isUserPaused = true
                            currentIndex = if (currentIndex > 0) currentIndex - 1 else books.lastIndex
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = "Previous NYT Bestseller",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            isUserPaused = true
                            currentIndex = (currentIndex + 1) % books.size
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Next NYT Bestseller",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${currentIndex + 1} of ${books.size}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    books.forEachIndexed { index, _ ->
                        val isSelected = index == currentIndex
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ShelfmatesDeepBlue else MaterialTheme.colorScheme.outlineVariant
                                )
                                .clickable {
                                    isUserPaused = true
                                    currentIndex = index
                                }
                        )
                    }
                }

                // NYT Article Link for current book
                TextButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.nytUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "NYT Best Sellers ↗",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShelfmatesDeepBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Book Selector Strip
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(books) { index, book ->
                    val isSelected = index == currentIndex
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ShelfmatesNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.clickable {
                            isUserPaused = true
                            currentIndex = index
                        }
                    ) {
                        Text(
                            text = "#${book.rank} ${book.title}",
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ShelfmatesGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
