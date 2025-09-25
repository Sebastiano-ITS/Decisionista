package com.example.decisionista.model

import kotlinx.serialization.Serializable

@Serializable // Added for Kotlinx Serialization
data class OptionData(
    val text: String,
    var weight: Int = 1 // Default weight to 1, can be changed by user
)
