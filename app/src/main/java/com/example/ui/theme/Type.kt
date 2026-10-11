package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Nunito Font Family configuration matching global design system
val NunitoFontFamily = FontFamily(
    Font(resId = R.font.nunito_regular, weight = FontWeight.Normal),      // 400 - Regular
    Font(resId = R.font.nunito_regular, weight = FontWeight.W400),
    Font(resId = R.font.nunito_medium, weight = FontWeight.Medium),        // 500 - Medium
    Font(resId = R.font.nunito_medium, weight = FontWeight.W500),
    Font(resId = R.font.nunito_semibold, weight = FontWeight.SemiBold),    // 600 - SemiBold
    Font(resId = R.font.nunito_semibold, weight = FontWeight.W600),
    Font(resId = R.font.nunito_bold, weight = FontWeight.Bold),            // 700 - Bold
    Font(resId = R.font.nunito_bold, weight = FontWeight.W700),
    Font(resId = R.font.nunito_extrabold, weight = FontWeight.ExtraBold),  // 800 - ExtraBold
    Font(resId = R.font.nunito_extrabold, weight = FontWeight.W800),
    Font(resId = R.font.nunito_extrabold, weight = FontWeight.Black),      // 900
    Font(resId = R.font.nunito_extrabold, weight = FontWeight.W900),
)

// Complete Material 3 typography with Nunito as the global default font
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold, // 800
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold, // 800
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold, // 800 - Major headings (Recommend, Voice Chat, etc.)
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold, // 700
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold, // 700 - Section headings and prominent text
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold, // 600 - Tabs, cards, usernames
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal, // 400 - Normal body text
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal, // 400 - Descriptions, secondary text
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold, // 600 - Buttons, important labels
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Medium, // 500 - Labels and supporting text
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Medium, // 500 - Small supporting badges
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
