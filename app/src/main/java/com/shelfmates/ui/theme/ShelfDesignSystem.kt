package com.shelfmates.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Semantic design roles for the Shelfmates reader-first surface.
 *
 * ## Why this exists
 *
 * The palette in [Color.kt] is large and deliberately so: it is the accumulated
 * brand vocabulary of the app and is referenced by dozens of screens. Those
 * names are NOT self-describing. `ShelfmatesBlue` is crimson, `ShelfmatesNavy`
 * is near-black, `ShelfmatesCoral` is rose and `ShelfmatesRuby` is a vivid red
 * — four near-identical reds with names that promise otherwise. That is exactly
 * how five accent colours end up competing on one screen.
 *
 * Rather than rename or delete brand colours (which would touch every screen and
 * is not what this pass is for), this file adds a thin *semantic* layer on top.
 * Screens that want to read clearly say what a colour MEANS, not what it looks
 * like:
 *
 * ```
 * ShelfOnSurface          -> TextRole.OnSurface
 * ShelfAccentGold         -> AccentRole.Primary
 * ```
 *
 * New work should prefer these roles. Existing `Shelfmates*` references keep
 * working untouched.
 *
 * ## The hierarchy
 *
 * Book covers are the strongest visual objects in this product, so the chrome
 * around them gets out of the way:
 *
 *  - [ShelfOnSurface] / [ShelfOnSurfaceMuted] carry all readable text.
 *  - [ShelfAccentGold] is reserved for ONE thing per screen: the primary action
 *    or the current selection. It is not decoration.
 *  - [ShelfRule] and [ShelfSurfaceAlt] do structural work that a card would
 *    otherwise have to do with a border and a shadow.
 *
 * Status colours ([ShelfStatusLive], [ShelfStatusSuccess], [ShelfStatusWarning],
 * [ShelfStatusDanger]) mean one thing each and must not be used decoratively.
 */

// ── Surfaces ────────────────────────────────────────────────────────────────

/** Page background. Deliberately the plainest colour in the app. */
val ShelfSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background

/** Raised background for a distinct region (a shelf, a sheet, a selected row). */
val ShelfSurfaceAlt: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

/** A hairline separator. Prefer this to a card border. */
val ShelfRule: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant

// ── Type ────────────────────────────────────────────────────────────────────

/** Primary readable text. */
val ShelfOnSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

/** Secondary readable text: metadata, supporting copy. Never below 11sp. */
val ShelfOnSurfaceMuted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * A book title. Serif, because a title is the one piece of text on screen that
 * benefits from editorial character. UI text below never uses this.
 */
val ShelfTitleFont: FontFamily
    @Composable @ReadOnlyComposable get() = BookDisplayFont

/** UI body text. Always sans-serif for legibility at small sizes. */
val ShelfUiFont: FontFamily
    @Composable @ReadOnlyComposable get() = FontFamily.SansSerif

// ── Accents ─────────────────────────────────────────────────────────────────

/**
 * The single accent. One primary action or one selected item per screen.
 *
 * This is antiqued gold rather than a second red on purpose: the app already
 * uses crimson as its brand red on every surface, so making crimson the
 * "selected" colour would mean selection is invisible against the brand.
 */
val ShelfAccentGold: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesGold

/** Accent pressed/pressed-adjacent state. */
val ShelfAccentGoldDeep: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesDarkCrimson

/**
 * Cover letterbox / image background.
 *
 * Book cover URLs are frequently broken, absent, or slow. Rendering the same
 * warm neutral behind every cover means a missing cover reads as "no cover
 * available" instead of as a broken layout.
 */
val ShelfCoverFallback: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesObsidian

// ── Status (one meaning each) ───────────────────────────────────────────────

/** Live / happening right now. */
val ShelfStatusLive: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesCoral

/** Success: synced, saved, completed. */
val ShelfStatusSuccess: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesEmerald

/** Attention: deadline approaching, pending. */
val ShelfStatusWarning: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesAmber

/** Destructive or refused: sign out, delete, admin-only. */
val ShelfStatusDanger: Color
    @Composable @ReadOnlyComposable get() = ShelfmatesRuby

// ── Typography roles ────────────────────────────────────────────────────────

/** Screen title. One per screen. */
val ShelfTitleStyle
    @Composable @ReadOnlyComposable get() = Typography.headlineSmall.copy(
        fontFamily = ShelfTitleFont,
        fontWeight = FontWeight.Bold,
        color = ShelfOnSurface
    )

/**
 * Section heading — the Level 2 boundary in the information hierarchy.
 *
 * Small, uppercase, letterspaced. It separates one kind of content from the
 * next without drawing a card around it.
 */
val ShelfSectionStyle
    @Composable @ReadOnlyComposable get() = Typography.labelMedium.copy(
        fontFamily = ShelfUiFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.1.sp,
        color = ShelfOnSurfaceMuted
    )

/** Book title in a list or grid. Serif so it reads as a title, not as UI. */
val ShelfBookTitleStyle
    @Composable @ReadOnlyComposable get() = Typography.titleSmall.copy(
        fontFamily = ShelfTitleFont,
        fontWeight = FontWeight.SemiBold,
        color = ShelfOnSurface
    )

/** Supporting body copy. Sans-serif for readability. */
val ShelfBodyStyle
    @Composable @ReadOnlyComposable get() = Typography.bodyMedium.copy(
        fontFamily = ShelfUiFont,
        color = ShelfOnSurface
    )

/** Metadata: author, year, shelf name. Small but never below 11sp. */
val ShelfMetaStyle
    @Composable @ReadOnlyComposable get() = Typography.bodySmall.copy(
        fontFamily = ShelfUiFont,
        color = ShelfOnSurfaceMuted
    )

/** The one primary button label on a screen. */
val ShelfPrimaryLabelStyle
    @Composable @ReadOnlyComposable get() = Typography.labelLarge.copy(
        fontFamily = ShelfUiFont,
        fontWeight = FontWeight.Bold
    )