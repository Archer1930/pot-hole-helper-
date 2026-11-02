package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val DarkAsphalt = Color(0xFF090D12)
val SurfaceDark = Color(0xFF101620)
val CardDark = Color(0xFF17202C)
val CardBorder = Color(0xFF233044)

val CyberCyan = Color(0xFF00E5FF)
val CyberCyanDark = Color(0xFF0097A7)
val ScrapeAmber = Color(0xFFFFB300)
val LipRed = Color(0xFFFF2D55)
val SmoothGreen = Color(0xFF00E676)
val FrostIce = Color(0xFF64B5F6)

val TextPrimary = Color(0xFFF0F4F8)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val DangerOverlay = Color(0x33FF2D55)
val CautionOverlay = Color(0x33FFB300)
val SafeOverlay = Color(0x3300E676)

enum class AppThemeAccent(
    val displayName: String,
    val primaryColor: Color,
    val containerColor: Color
) {
    CYBER_CYAN("Cyber Cyan", Color(0xFF00E5FF), Color(0xFF003844)),
    SOLAR_AMBER("Solar Gold", Color(0xFFFFB300), Color(0xFF443000)),
    BREMBO_RED("Track Crimson", Color(0xFFFF2D55), Color(0xFF4A0B17)),
    PORSCHE_LIME("Acid Lime", Color(0xFF76FF03), Color(0xFF1D4200)),
    MIDNIGHT_PURPLE("Neon Purple", Color(0xFFD500F9), Color(0xFF380042)),
    STEALTH_WHITE("Titanium White", Color(0xFFF0F4F8), Color(0xFF222B38))
}
