package com.shelfmates.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.ui.theme.ShelfAccentGold
import com.shelfmates.ui.theme.ShelfBookTitleStyle
import com.shelfmates.ui.theme.ShelfMetaStyle
import com.shelfmates.ui.theme.ShelfOnSurface
import com.shelfmates.ui.theme.ShelfOnSurfaceMuted
import com.shelfmates.ui.theme.ShelfRule

/**
 * Home's identity header: who you are, and where your books are.
 *
 * Replaces the previous 205dp gradient hero carrying a "LITERARY GUILD" badge
 * and a 14-day streak chip. That block was the loudest element on the screen
 * and it described a gamification tier rather than the product — a first-run
 * reader was told they belonged to a guild before they had saved a single book.
 *
 * This block is deliberately short. It answers exactly the two questions a
 * reader arrives with — "is this my account?" and "are my books here?" — and
 * then gets out of the way so the shelf below can establish hierarchy.
 *
 * No new data is read here: the counts come from the same `savedBooks` list the
 * shelf renders, so the header can never disagree with the shelf under it.
 */
@Composable
fun ShelfHomeHeader(
    displayName: String?,
    currentlyReadingCount: Int,
    savedCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
    ) {
        // Level 1: the product says what it is, in one line, every time.
        Text(
            text = "Atomic Shelfmates",
            style = ShelfBookTitleStyle.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        )
        Spacer(Modifier.height(10.dp))

        Text(
            text = if (displayName.isNullOrBlank()) {
                "Discover books, keep your shelves, read."
            } else {
                "Welcome back, ${displayName.substringBefore(' ')}."
            },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ShelfOnSurface
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Discover books, keep your shelves, read, and connect around them.",
            style = ShelfMetaStyle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Reading state as quiet figures, not badges. These are the two numbers
        // a returning reader actually glances for.
        if (savedCount > 0) {
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                ReadingFigure(
                    value = currentlyReadingCount.toString(),
                    label = if (currentlyReadingCount == 1) "Reading" else "Reading now"
                )
                ReadingFigure(
                    value = savedCount.toString(),
                    label = if (savedCount == 1) "Book saved" else "Books saved"
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ShelfRule)
        )
    }
}

/**
 * One figure in the header. No container, no icon, no colour — just a number
 * and what it counts, because decoration here competes with the books.
 */
@Composable
private fun ReadingFigure(
    value: String,
    label: String
) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ShelfAccentGold
            )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            style = ShelfMetaStyle,
            modifier = Modifier.padding(bottom = 1.dp)
        )
    }
}

/**
 * First-run orientation, shown once under the header when the shelf is empty.
 *
 * States what Shelfmates is and what to do next, using the existing routes
 * already wired on this screen. It adds no capability and invents no data — it
 * only routes to search, which the screen already offers.
 */
@Composable
fun ShelfFirstRunNote(
    onFindBooks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Start here",
            style = ShelfMetaStyle.copy(
                fontWeight = FontWeight.Bold,
                color = ShelfAccentGold
            )
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Find a book, save it to your shelf, then read it. " +
                "Clubs, ARCs, reviews and rewards appear once you have books on your shelf.",
            style = ShelfMetaStyle.copy(color = ShelfOnSurfaceMuted)
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ShelfAccentGold.copy(alpha = 0.14f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Find your first book →",
                style = ShelfMetaStyle.copy(
                    fontWeight = FontWeight.Bold,
                    color = ShelfAccentGold
                ),
                modifier = Modifier.clickableNoRipple(onFindBooks)
            )
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)