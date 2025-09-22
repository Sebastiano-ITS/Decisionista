package com.example.decisionista.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
// import androidx.compose.material3.lightColorScheme // Can be added if a light theme variant is needed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryColor,
    primaryContainer = PrimaryContainerPurple, // Often a lighter shade or for specific accents in dark theme
    onPrimaryContainer = OnPrimaryContainerPurple,

    secondary = SecondaryOrange,
    onSecondary = OnSecondaryColor,
    secondaryContainer = SecondaryContainerYellow, // Often a lighter shade or for specific accents in dark theme
    onSecondaryContainer = OnSecondaryContainerYellow,

    tertiary = TertiaryAccent, // Mapped to InfoColor from Color.kt
    onTertiary = OnTertiaryAccent,
    tertiaryContainer = TertiaryAccentContainer,
    onTertiaryContainer = OnTertiaryAccentContainer,

    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = ErrorContainerColor,
    onErrorContainer = OnErrorContainerColor,

    background = DarkBackground,
    onBackground = OnDarkBackground,

    surface = DarkSurface,
    onSurface = OnDarkSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    scrim = ScrimColor
)

/*
// Placeholder for a potential Light Theme if designed later
private val AppLightColorScheme = lightColorScheme(
    primary = PrimaryBlue, // Example: Use the blue as primary for light theme
    onPrimary = OnPrimaryColor,
    primaryContainer = Color(0xFFE0E0FF), // Example: light blue container
    onPrimaryContainer = Color(0xFF1E3A8A),
    // ... define all other colors for the light theme ...
    background = Color(0xFFF9FAFB), // Example: Light gray background
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF), // Example: White surface
    onSurface = Color(0xFF111827)
)
*/

@Composable
fun DecisionistaAppTheme(
    // darkTheme: Boolean = isSystemInDarkTheme(), // For dynamic light/dark theme switching
    useDarkTheme: Boolean = true, // Default to dark theme as per new design
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        AppDarkColorScheme
    } else {
        // AppLightColorScheme // Future: uncomment and use if light theme is defined
        AppDarkColorScheme // Defaulting to dark for now even if useDarkTheme is false, until light is defined
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar to be transparent or a dark color that matches the app's background
            window.statusBarColor = colorScheme.background.toArgb() // Or a specific dark color like DarkBackground.toArgb()
            // For dark theme, status bar icons should be light
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Typography will be updated next
        content = content
    )
}
