package com.example.decisionista.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Palette (Purples/Blues)
val PrimaryPurple = Color(0xFFA855F7) // Main primary - purple-500
val PrimaryBlue = Color(0xFF6366F1)   // Alternative primary - indigo-500
val OnPrimaryColor = Color(0xFFFFFFFF)    // White for text/icons on primary

val PrimaryContainerPurple = Color(0xFFE9D5FF) // Light purple - purple-200 (for light theme or accents in dark)
val OnPrimaryContainerPurple = Color(0xFF581C87) // Dark purple - purple-900 (text on light purple container)

// Secondary Palette (Yellows/Oranges/Blues)
val SecondaryOrange = Color(0xFFF59E0B)  // Main secondary - amber-500
val SecondaryYellow = Color(0xFFFDE047)  // Alternative secondary - yellow-400
val SecondaryBlue = Color(0xFF3B82F6)    // Added SecondaryBlue (twBlue500)
val OnSecondaryColor = Color(0xFF111827)     // Near-black for text/icons on secondary

val SecondaryContainerYellow = Color(0xFFFEF08A) // Light yellow - yellow-200 (for light theme or accents in dark)
val OnSecondaryContainerYellow = Color(0xFF374151) // Dark gray - gray-700 (text on light yellow container)

// Background & Surface Palette (Dark Theme Focus: Grays/Near-Blacks)
val DarkBackground = Color(0xFF111827)       // gray-900
val OnDarkBackground = Color(0xFFE5E7EB)       // gray-200 (for text/icons on dark background)

val DarkSurface = Color(0xFF1F2937)          // gray-800 (slightly lighter than background)
val OnDarkSurface = Color(0xFFE5E7EB)          // gray-200 (for text/icons on dark surface)

val DarkSurfaceVariant = Color(0xFF374151)      // gray-700 (for cards, distinct sections)
val OnDarkSurfaceVariant = Color(0xFFCED4DA)      // gray-300 (for text/icons on surface variant)

val DarkOutline = Color(0xFF4B5563)           // gray-600 (for borders)
val DarkOutlineVariant = Color(0xFF6B7280)       // gray-500 (for less prominent borders)

// Semantic Colors
val SuccessColor = Color(0xFF10B981)         // green-500
val OnSuccessColor = Color(0xFFFFFFFF)
val SuccessContainerColor = Color(0xFFDCFCE7) // green-100 (for backgrounds of success messages)
val OnSuccessContainerColor = Color(0xFF166534) // green-800

val InfoColor = Color(0xFF06B6D4)             // cyan-500
val OnInfoColor = Color(0xFFFFFFFF)
val InfoContainerColor = Color(0xFFCFFAFE)    // cyan-100 (using a custom light cyan from earlier)
val OnInfoContainerColor = Color(0xFF164E63)   // A darker cyan for text on info container

val WarningColor = Color(0xFFFACC15)         // yellow-500
val OnWarningColor = Color(0xFF111827)       // Near-black for text on warning
val WarningContainerColor = Color(0xFFFEF9C3) // yellow-50 (very light yellow)
val OnWarningContainerColor = Color(0xFF713F12) // Dark brown/yellow

val ErrorColor = Color(0xFFEF4444)           // red-500
val OnErrorColor = Color(0xFFFFFFFF)
val ErrorContainerColor = Color(0xFFFEE2E2)   // red-100 (light red for error message backgrounds)
val OnErrorContainerColor = Color(0xFF991B1B)   // red-800

// Tertiary - Can be mapped to Info or another accent if desired by Material3 theming
val TertiaryAccent = InfoColor
val OnTertiaryAccent = OnInfoColor
val TertiaryAccentContainer = InfoContainerColor
val OnTertiaryAccentContainer = OnInfoContainerColor

val ScrimColor = Color(0x80000000) // Black with 50% alpha

// Specific colors from your list if needed for direct use (Tailwind names for reference)
val twPurple500 = Color(0xFFA855F7)
val twIndigo500 = Color(0xFF6366F1)
val twBlue500 = Color(0xFF3B82F6)
val twYellow400 = Color(0xFFFDE047)
val twAmber500 = Color(0xFFF59E0B)
val twOrange500 = Color(0xFFF97316)
val twGray900 = Color(0xFF111827)
val twGray800 = Color(0xFF1F2937)
val twGray700 = Color(0xFF374151)
val twGreen500 = Color(0xFF10B981)
val twCyan500 = Color(0xFF06B6D4)
val twRed500 = Color(0xFFEF4444)
