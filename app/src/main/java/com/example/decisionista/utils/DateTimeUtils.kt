package com.example.decisionista.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats a given timestamp (in milliseconds) into a human-readable date and time string.
 * Example: "10/07/2023 15:45"
 */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
    return sdf.format(Date(timestamp))
}
