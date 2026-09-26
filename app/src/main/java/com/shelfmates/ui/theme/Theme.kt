package com.shelfmates.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = ShelfmatesRuby,
  onPrimary = Color.White,
  primaryContainer = ShelfmatesDarkCrimson,
  onPrimaryContainer = ShelfmatesPaperWhite,
  secondary = ShelfmatesGold,
  onSecondary = Color.Black,
  tertiary = ShelfmatesAmber,
  background = BackgroundDark,
  surface = SurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = BorderDark,
  outlineVariant = Color(0xFF3F3F46)
)

private val LightColorScheme = lightColorScheme(
  primary = ShelfmatesCrimson,
  onPrimary = Color.White,
  primaryContainer = ShelfmatesLightBlue,
  onPrimaryContainer = ShelfmatesDarkCrimson,
  secondary = ShelfmatesInkBlack,
  onSecondary = Color.White,
  tertiary = ShelfmatesGold,
  onTertiary = Color.White,
  background = BackgroundLight,
  surface = SurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderLight,
  outlineVariant = Color(0xFFD4D4D8)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

