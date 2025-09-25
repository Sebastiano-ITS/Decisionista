package com.example.decisionista.ui.theme

import androidx.compose.ui.graphics.Color

// Original Primary Palette (Purples/Blues) - MANTENUTI PER IL TEMA SCURO
val PrimaryPurple = Color(0xFFA855F7)
val PrimaryBlue = Color(0xFF6366F1)
val OnPrimaryColor = Color(0xFFFFFFFF) // Usato da entrambi i temi per "on" colori primari/secondari chiari

val PrimaryContainerPurple = Color(0xFFE9D5FF)
val OnPrimaryContainerPurple = Color(0xFF581C87)

// Original Secondary Palette (Yellows/Oranges) - MANTENUTI PER IL TEMA SCURO
val SecondaryOrange = Color(0xFFF59E0B)
val SecondaryYellow = Color(0xFFFDE047)
val OnSecondaryColor = Color(0xFF111827) // Usato dal tema scuro

val SecondaryContainerYellow = Color(0xFFFEF08A)
val OnSecondaryContainerYellow = Color(0xFF374151)

// --- NUOVA PALETTE "LUMINOSA E ARIOSA" (REVISIONATA) PER TEMA CHIARO ---
// Primario (Azzurro Cielo Vivace)
val SkyBluePrimary = Color(0xFF50A6FF)
val SkyBluePrimaryContainer = Color(0xFFD6E7FF)
val OnSkyBluePrimaryContainer = Color(0xFF001E3C)

// Secondario (Corallo Tenue)
val CoralSecondary = Color(0xFFFF8A65)
val OnCoralSecondary = Color(0xFF000000)
val CoralSecondaryContainer = Color(0xFFFFE0D9)
val OnCoralSecondaryContainer = Color(0xFF3E0000)

// Terziario (Grigio Caldo Tenue)
val WarmGrayTertiary = Color(0xFFB0AFA9)
val WarmGrayTertiaryContainer = Color(0xFFF0EFEC)
val OnWarmGrayTertiaryContainer = Color(0xFF282826)

// Sfondi e Superfici per il tema "Luminoso e Arioso" (REVISIONATO)
val BrightBackground = Color(0xFFFFFFFF)       // Sfondo principale dello schermo: Bianco puro
val OnBrightBackground = Color(0xFF1C1C1E)

val BrightSurface = Color(0xFFFFFFFF)           // Superfici generiche non-card: Bianco puro
val OnBrightSurface = Color(0xFF1C1C1E)

// NUOVO: Colore per lo sfondo delle card nel tema chiaro per farle staccare
val SubtleGraySurfaceVariant = Color(0xFFF0F2F5) // Grigio molto chiaro per le card
val OnSubtleGraySurfaceVariant = Color(0xFF48474A) // Testo su questo grigio (simile a OnBrightSurfaceVariant)

// Bordi per il tema "Luminoso e Arioso" (REVISIONATO)
val ModerateOutline = Color(0xFFD1D5DB)         // Grigio chiaro per il bordo delle card (contrasto con SubtleGraySurfaceVariant)

// --- FINE PALETTE "LUMINOSA E ARIOSA" (REVISIONATA) ---


// Sfondi e Superfici Originali Tema Scuro
val DarkBackground = Color(0xFF111827)
val OnDarkBackground = Color(0xFFE5E7EB)
val DarkSurface = Color(0xFF1F2937)
val OnDarkSurface = Color(0xFFE5E7EB)
val DarkSurfaceVariant = Color(0xFF374151)
val OnDarkSurfaceVariant = Color(0xFFCED4DA)
val DarkOutline = Color(0xFF4B5563)

// Colori Semantici (Mantenuti per entrambi i temi dove appropriato)
val SuccessColor = Color(0xFF10B981)
val OnSuccessColor = Color(0xFFFFFFFF)
val SuccessContainerColor = Color(0xFFDCFCE7)
val OnSuccessContainerColor = Color(0xFF166534)

val InfoColor = Color(0xFF06B6D4) // Usato come TertiaryAccent nel tema scuro
val OnInfoColor = Color(0xFFFFFFFF)
val InfoContainerColor = Color(0xFFCFFAFE)
val OnInfoContainerColor = Color(0xFF164E63)

val WarningColor = Color(0xFFFACC15)
val OnWarningColor = Color(0xFF111827)
val WarningContainerColor = Color(0xFFFEF9C3)
val OnWarningContainerColor = Color(0xFF713F12)

val ErrorColor = Color(0xFFEF4444)
val OnErrorColor = Color(0xFFFFFFFF)
val ErrorContainerColor = Color(0xFFFEE2E2)
val OnErrorContainerColor = Color(0xFF991B1B)

// Terziario Tema Scuro (usa InfoColor)
val TertiaryAccent = InfoColor
val OnTertiaryAccent = OnInfoColor
val TertiaryAccentContainer = InfoContainerColor
val OnTertiaryAccentContainer = OnInfoContainerColor

val ScrimColor = Color(0x80000000)

// Altri colori specifici
val SecondaryBlue = Color(0xFF3B82F6) // DECOMMENTATO
val twIndigo500 = PrimaryBlue
val twCyan500 = InfoColor
val twRed500 = ErrorColor
val twGreen500 = SuccessColor

// Colors for DecisionStatsCard
val statsDecisionMadeLabelValueColor = Color(0xFF6372E5)
val statsDecisionMadeBackgroundColor = Color(0xFFE5F1FF)
val statsOracleConsultationLabelValueColor = Color(0xFFE996FE)
val statsOracleConsultationBackgroundColor = Color(0xFFF6E7FF)
