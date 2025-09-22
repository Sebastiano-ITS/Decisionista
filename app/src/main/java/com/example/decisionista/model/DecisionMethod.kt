package com.example.decisionista.model

import kotlinx.serialization.Serializable

@Serializable // Added for Kotlinx Serialization
enum class DecisionMethod(
    val title: String,
    val description: String,
    val emoji: String
) {
    RANDOM("Scelta Casuale", "Il mago lancia i dadi del destino", "🎲"),
    ELIMINATION("Eliminazione", "Elimina opzioni una alla volta", "⚡️"),
    DUEL("Duello", "Le opzioni si sfidano a coppie", "⚔️"),
    WHEEL("Ruota Magica", "La ruota del destino decide", "🎡"),
    WEIGHTED("Scelta Ponderata", "Considera l'importanza di ogni opzione", "⚖️"),
    CARD_DRAW("Sorteggio con Carte", "Ogni opzione viene trasformata in una “carta magica”. L’app ne estrae una e la rivela con un effetto sorpresa.", "🃏")
}
