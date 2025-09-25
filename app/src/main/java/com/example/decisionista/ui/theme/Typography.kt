package com.example.decisionista.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
// Import Font if you are defining custom font families from res/font
// import androidx.compose.ui.text.font.Font
// import com.example.decisionista.R // Assuming your R file is here for font resources
import androidx.compose.ui.unit.sp

// Placeholder for custom fonts - you will need to add these font files to res/font
// Example:
// val cinzelFontFamily = FontFamily(
//     Font(R.font.cinzel_regular, FontWeight.Normal),
//     Font(R.font.cinzel_bold, FontWeight.Bold)
// )
// val interFontFamily = FontFamily(
//     Font(R.font.inter_regular, FontWeight.Normal),
//     Font(R.font.inter_medium, FontWeight.Medium),
//     Font(R.font.inter_semibold, FontWeight.SemiBold)
// )

val AppTypography = Typography(
    // Display, Headline, Title styles -  using FontFamily.Serif as per plan for "magic/elegant" feel
    // TODO: Replace FontFamily.Serif with your actual serif/display font family (e.g., cinzelFontFamily, playfairDisplayFontFamily)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder, replace with your custom serif font
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal, // Or FontWeight.Bold if desired for headings
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal, // Or FontWeight.Bold
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal, // Or FontWeight.Bold
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Placeholder
        fontWeight = FontWeight.Normal, // Or FontWeight.Bold for larger titles
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // TitleMedium, TitleSmall, Body, Label styles - using FontFamily.SansSerif for readability
    // TODO: Replace FontFamily.SansSerif with your actual sans-serif font family (e.g., interFontFamily, robotoFontFamily)
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder, replace with your custom sans-serif font
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif, // Placeholder
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
