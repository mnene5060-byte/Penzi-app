package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Kenyan Aesthetic Color Palette - Sunset Terracotta & Royal Crimson Luxury Theme
// Replaced all pink with rich Kenyan Sunset Amber, Royal Maasai Crimson, 24K Savannah Gold & Rift Espresso

// Kenyan Royal Crimson & Terracotta (Primary)
val KenyanRed = Color(0xFFB91C1C)       // Royal Crimson
val KenyanRedDark = Color(0xFF7F1D1D)   // Deep Crimson Red
val KenyanRedLight = Color(0xFFFDBA74)  // Warm Sand Amber
val KenyanCrimson = Color(0xFF991B1B)
val KenyanSunsetOrange = Color(0xFFEA580C)
val PassionRose = Color(0xFFB91C1C)
val PassionRoseLight = Color(0xFFFED7AA)
val PassionRoseDark = Color(0xFF7F1D1D)
val PassionRoseBg = Color(0xFFFFF7ED)   // Warm Cream Canvas
val KenyanSunsetAmber = Color(0xFFF59E0B) // 24K Savannah Amber

// 24K Savannah Gold & Safari Amber (Secondary & Highlights)
val SavannahGold = Color(0xFFF59E0B)
val SavannahGoldLight = Color(0xFFFFD166)
val SavannahGoldDark = Color(0xFFB45309)
val SafariTerracotta = Color(0xFFC2410C)
val GoldenAmber = Color(0xFFD97706)

// Kenyan Cultural Accents
val MaasaiBlue = Color(0xFF2563EB)
val MaasaiBeadBlue = Color(0xFF3B82F6)
val MaasaiBlueDark = Color(0xFF1D4ED8)
val MaasaiBlueLight = Color(0xFF93C5FD)
val HighlandGreen = Color(0xFF059669)
val HighlandGreenDark = Color(0xFF047857)
val MpesaGreen = Color(0xFF00A859)
val MpesaGreenDark = Color(0xFF00753E)

// Warm Luxury Neutrals & Card Backgrounds
val RiftCoffeeDark = Color(0xFF130F0C)
val RiftCoffeeCard = Color(0xFF221A15)
val RiftCoffeeElevated = Color(0xFF2E241E)

val WarmSandLight = Color(0xFFFBF9F5)
val WarmSandCard = Color(0xFFFFFFFF)
val WarmSandSubtle = Color(0xFFF4EFE6)
val WarmSandElevated = Color(0xFFECE4D6)

// Text Colors
val TextPrimaryLight = Color(0xFF18130F)
val TextSecondaryLight = Color(0xFF574E47)
val TextTertiaryLight = Color(0xFF9E9187)

val TextPrimaryDark = Color(0xFFFDFCFB)
val TextSecondaryDark = Color(0xFFCBC0B6)
val TextTertiaryDark = Color(0xFF8F8378)

// Borders & Dividers
val BorderLight = Color(0xFFEADBCE)
val BorderDark = Color(0xFF3D2F25)
val BorderGold = Color(0x66F59E0B)

// Premium Gradient Brushes - Warm Sunset & Savannah Orange
val PenziHeaderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFF5722),
        Color(0xFFFF6D3B),
        Color(0xFFFF8A50)
    )
)

val PenziFullScreenGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFF5722),
        Color(0xFFFF7A00),
        Color(0xFFEA580C),
        Color(0xFFC2410C)
    )
)

val PenziPrimaryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEA580C), // Sunset Flame
        Color(0xFFDC2626), // Royal Crimson
        Color(0xFF991B1B)
    )
)

val KenyanSunsetGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFEA580C), Color(0xFFB91C1C))
)

val SafariGoldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFD166), Color(0xFFF59E0B), Color(0xFFB45309))
)

val MpesaGreenGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF10B981), Color(0xFF00A859), Color(0xFF005A36))
)

val MaasaiBlueGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF1D4ED8))
)

val LuxuryDarkGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF2A2019), Color(0xFF130F0C))
)
