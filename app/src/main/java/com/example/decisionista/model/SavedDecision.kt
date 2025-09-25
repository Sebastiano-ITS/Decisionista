package com.example.decisionista.model

import kotlinx.serialization.Serializable

@Serializable // Added for Kotlinx Serialization
data class SavedDecision(
    val id: Int,
    val options: List<OptionData>, // Changed to List<OptionData>
    val result: String,
    val method: DecisionMethod, // Assuming DecisionMethod is already @Serializable or can be made so
    val timestamp: Long
)
