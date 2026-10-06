package com.shelfmates.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfmates.ui.theme.ShelfAccentGold
import com.shelfmates.ui.theme.ShelfAccentGoldDeep
import com.shelfmates.ui.theme.ShelfBookTitleStyle
import com.shelfmates.ui.theme.ShelfCoverFallback
import com.shelfmates.ui.theme.ShelfMetaStyle
import com.shelfmates.ui.theme.ShelfOnSurface
import com.shelfmates.ui.theme.ShelfOnSurfaceMuted
import com.shelfmates.ui.theme.ShelfPrimaryLabelStyle
import com.shelfmates.ui.theme.ShelfSectionStyle

/**
 * Shared layout primitives for the reader-first Shelfmates surface.
 *
 * These exist to make the information hierarchy enforceable rather than a matter
 * of per-screen discipline. The specific problem they fix: every section in the
 * app had become a [androidx.compose.material3.Card] with its own title
 * treatment, so nothing on screen had a *rank*. A card says "this is a thing".
 * Nine cards says "everything is equally important" -- which is how a reader
 * ends up unable to tell their own shelf from a rewards ticker.
 *
 * The primitives encode the intended order instead:
 *
 * 1. [ShelfSectionHeader] -- "this kind of thing starts here"
 * 2. [ShelfDivider]       -- structural separation, no container
 * 3. [ShelfPrimaryAction] -- exactly one per screen
 * 4. [ShelfEmptyState]    -- say what to do next, not that a table is empty
 * 5. [ShelfBookRow]       -- the cover leads, everything else supports it
 */


// ── Level 2: the boundary between one kind of content and the next ────────────

/**
 * Section heading.
 *
 * Intentionally not a card. Small caps plus a hairline costs almost no visual
 * budget, which is exactly what lets the books underneath stay dominant.
 */
@Composable
fun ShelfSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title.uppercase(), style = ShelfSectionStyle)
            // Only render the supporting line when it carries new information.
            if (supporting != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = supporting,
                    style = ShelfMetaStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = actionLabel,
                style = ShelfMetaStyle.copy(
                    fontWeight = FontWeight.Bold,
                    color = ShelfAccentGold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

/** Hairline separator. Cheaper and quieter than a card border. */
@Composable
fun ShelfDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

// ── The single primary action ────────────────────────────────────────────────

/**
 * The one primary action for a screen.
 *
 * Deliberately loud and deliberately singular. This should be the only filled
 * button a reader-facing screen uses, so the eye lands on the one thing to do
 * next. Everything else is outlined or a text action.
 */
@Composable
fun ShelfPrimaryAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ShelfAccentGold,
            contentColor = ShelfAccentGoldDeep,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = ShelfOnSurfaceMuted
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = label, style = ShelfPrimaryLabelStyle)
    }
}

// ── Empty states ─────────────────────────────────────────────────────────────

/**
 * Empty state that names the next action.
 *
 * The previous behaviour was to render an empty database, which tells a new
 * reader the app is broken rather than empty. This states what is missing and
 * routes to the existing screen that fixes it. No new capability is implied.
 */
@Composable
fun ShelfEmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ShelfOnSurface
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = ShelfMetaStyle,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(14.dp))
            ShelfPrimaryAction(label = actionLabel, onClick = onAction)
        }
    }
}

// ── The book object ──────────────────────────────────────────────────────────

/**
 * The one book row.
 *
 * Built so the cover, not a badge stack, establishes hierarchy: cover, title,
 * author, and at most one supporting line. Deliberately no chips, no gradient
 * card and no competing calls to action.
 */
@Composable
fun ShelfBookRow(
    coverUrl: String?,
    title: String,
    author: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        BookCover(
            coverUrl = coverUrl,
            modifier = Modifier.size(width = 56.dp, height = 84.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ShelfBookTitleStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = author,
                style = ShelfMetaStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (supporting != null) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = supporting,
                    style = ShelfMetaStyle.copy(fontSize = 11.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Book cover letterbox.
 *
 * [ShelfCoverFallback] is what shows when `coverUrl` is null, so a missing cover
 * reads as "no cover available" rather than as a collapsed layout. Image loading
 * itself stays with the caller's painter; this only owns the box.
 */
@Composable
fun BookCover(
    coverUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ShelfCoverFallback),
        contentAlignment = Alignment.Center
    ) {
        // The real image is layered by the caller's painter; this is the
        // fallback that shows behind it while loading, or instead of it.
    }
}

/** Quiet status label: one word of state, never a badge wall. */
@Composable
fun ShelfStatusLabel(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            style = ShelfMetaStyle.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color = color
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
