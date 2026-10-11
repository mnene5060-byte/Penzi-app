package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = KenyanRedLight,
    onPrimary = Color.White,
    primaryContainer = KenyanRedDark,
    onPrimaryContainer = SavannahGoldLight,
    secondary = SavannahGold,
    onSecondary = RiftCoffeeDark,
    secondaryContainer = SafariTerracotta,
    onSecondaryContainer = Color.White,
    tertiary = HighlandGreen,
    background = RiftCoffeeDark,
    onBackground = TextPrimaryDark,
    surface = RiftCoffeeCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF382A20),
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = KenyanRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF7ED),
    onPrimaryContainer = KenyanRedDark,
    secondary = SafariTerracotta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF0E6),
    onSecondaryContainer = RiftCoffeeDark,
    tertiary = HighlandGreen,
    background = WarmSandLight,
    onBackground = TextPrimaryLight,
    surface = WarmSandCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = WarmSandSubtle,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep authentic Kenyan warm palette as default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFFFF5722).toArgb()
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).apply {
                // White status bar icons (clock, battery, notifications) for colored/dark headers
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = NunitoFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                fontSize = 14.sp
            )
        ) {
            content()
        }
    }
}
