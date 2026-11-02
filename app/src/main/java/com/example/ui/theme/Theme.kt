package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

fun getCustomColorScheme(accent: AppThemeAccent) = darkColorScheme(
    primary = accent.primaryColor,
    onPrimary = DarkAsphalt,
    primaryContainer = CardDark,
    onPrimaryContainer = accent.primaryColor,
    secondary = ScrapeAmber,
    onSecondary = DarkAsphalt,
    secondaryContainer = CardDark,
    onSecondaryContainer = ScrapeAmber,
    tertiary = LipRed,
    onTertiary = TextPrimary,
    background = DarkAsphalt,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = LipRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    accent: AppThemeAccent = AppThemeAccent.CYBER_CYAN,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getCustomColorScheme(accent),
        typography = Typography,
        content = content
    )
}
