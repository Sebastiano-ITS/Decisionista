package com.example.decisionista.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryColor,
    primaryContainer = PrimaryContainerPurple,
    onPrimaryContainer = OnPrimaryContainerPurple,

    secondary = SecondaryOrange,
    onSecondary = OnSecondaryColor,
    secondaryContainer = SecondaryContainerYellow,
    onSecondaryContainer = OnSecondaryContainerYellow,

    tertiary = TertiaryAccent, // Questo è InfoColor per il tema scuro
    onTertiary = OnTertiaryAccent,
    tertiaryContainer = TertiaryAccentContainer,
    onTertiaryContainer = OnTertiaryAccentContainer,

    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = ErrorContainerColor, 
    onErrorContainer = ErrorColor,   

    background = DarkBackground,
    onBackground = OnDarkBackground,

    surface = DarkSurface,
    onSurface = OnDarkSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,

    outline = DarkOutline,
    outlineVariant = DarkSurfaceVariant, // Nota: DarkSurfaceVariant per il tema scuro

    scrim = ScrimColor
)

// NUOVA PALETTE "LUMINOSA E ARIOSA" (REVISIONATA) PER TEMA CHIARO
private val AppLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = OnPrimaryColor,
    primaryContainer = SkyBluePrimaryContainer,
    onPrimaryContainer = OnSkyBluePrimaryContainer,

    secondary = CoralSecondary,
    onSecondary = OnCoralSecondary,
    secondaryContainer = CoralSecondaryContainer,
    onSecondaryContainer = OnCoralSecondaryContainer,

    tertiary = WarmGrayTertiary,
    onTertiary = OnPrimaryColor,
    tertiaryContainer = WarmGrayTertiaryContainer,
    onTertiaryContainer = OnWarmGrayTertiaryContainer,

    background = BrightBackground,      // Sfondo schermo: Bianco puro
    onBackground = OnBrightBackground,

    surface = BrightSurface,            // Superfici generiche non-card: Bianco puro
    onSurface = OnBrightSurface,

    surfaceVariant = SubtleGraySurfaceVariant,     // Sfondo card: Grigio molto chiaro (da Color.kt)
    onSurfaceVariant = OnSubtleGraySurfaceVariant, // Testo su sfondo card (da Color.kt)

    outline = ModerateOutline,          // Bordo card: Grigio chiaro (da Color.kt, ora 0xFFD1D5DB)
    outlineVariant = ModerateOutline,   // Bordo card variante: stesso grigio chiaro

    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = ErrorContainerColor,
    onErrorContainer = OnErrorContainerColor,

    scrim = ScrimColor
)

@Composable
fun DecisionistaAppTheme(
    useDarkTheme: Boolean = true, 
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.setStatusBarColor(colorScheme.background.toArgb())
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
