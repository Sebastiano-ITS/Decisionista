package com.example.decisionista.ui.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.model.OptionData // Import OptionData
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.SecondaryBlue
import com.example.decisionista.ui.theme.SecondaryYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RitualScreen(
    method: DecisionMethod,
    options: List<OptionData>, // Changed to List<OptionData>
    onComplete: (String) -> Unit
) {
    var phase by remember { mutableStateOf(0) }
    val phrases = remember(method) {
        listOf(
            "🌀 Il cosmo converge...",
            "✨ Energie arcane si intrecciano...",
            "👁️ Il velo tra i mondi si assottiglia...",
            "📜 Il fato sta per essere scritto..."
        )
    }

    LaunchedEffect(Unit) {
        val ritualSegmentDuration = 1600L
        val finalPauseDuration = 1200L

        for (i in phrases.indices) {
            phase = i
            delay(ritualSegmentDuration)
        }
        phase = phrases.size
        delay(finalPauseDuration)

        val optionTexts = options.map { it.text } // Get a list of texts for methods not using weights directly

        val result = when (method) {
            DecisionMethod.RANDOM -> {
                if (optionTexts.isNotEmpty()) optionTexts.random()
                else "Nessuna opzione fornita"
            }
            DecisionMethod.ELIMINATION -> {
                // TODO: Implementare visualizzazione passo-passo dell'eliminazione.
                // Logica attuale: Simula il risultato finale scegliendo casualmente un'opzione.
                if (optionTexts.isNotEmpty()) optionTexts.random()
                else "Nessuna opzione per Eliminazione"
            }
            DecisionMethod.DUEL -> {
                // TODO: Implementare visualizzazione del torneo a duello.
                // Logica attuale: Simula un torneo scegliendo vincitori casuali per ogni duello.
                fun runDuelTournament(currentOptionData: List<OptionData>): String {
                    if (currentOptionData.isEmpty()) return "Nessuna opzione per Duello"
                    if (currentOptionData.size == 1) return currentOptionData.first().text

                    var roundContenders = currentOptionData.toMutableList()
                    while (roundContenders.size > 1) {
                        val nextRoundWinners = mutableListOf<OptionData>()
                        for (i in 0 until roundContenders.size step 2) {
                            if (i + 1 < roundContenders.size) {
                                val contender1 = roundContenders[i]
                                val contender2 = roundContenders[i + 1]
                                nextRoundWinners.add(if (Random.nextBoolean()) contender1 else contender2)
                            } else {
                                nextRoundWinners.add(roundContenders[i])
                            }
                        }
                        roundContenders = nextRoundWinners
                    }
                    return roundContenders.first().text
                }
                runDuelTournament(options)
            }
            DecisionMethod.WHEEL -> {
                if (options.isEmpty()) {
                    "Nessuna opzione per Ruota Magica"
                } else {
                    // Pondera le opzioni, ma in modo meno estremo rispetto a WEIGHTED.
                    // Qui ogni opzione ha almeno una probabilità di base,
                    // e il peso aggiunge "biglietti" extra nella lotteria.
                    val weightedOptions = mutableListOf<String>()
                    options.forEach { optionData ->
                        weightedOptions.add(optionData.text) // Ogni opzione ha almeno una chance
                        repeat(optionData.weight.coerceAtLeast(0)) { // Aggiungi copie basate sul peso (min 0)
                            weightedOptions.add(optionData.text)
                        }
                    }
                    if (weightedOptions.isNotEmpty()) weightedOptions.random()
                    else "Errore nella generazione della lista per la Ruota" // Fallback
                }
            }
            DecisionMethod.WEIGHTED -> {
                if (options.isEmpty()) {
                    "Nessuna opzione per Scelta Ponderata"
                } else {
                    val expandedList = mutableListOf<String>()
                    options.forEach { optionData ->
                        repeat(optionData.weight.coerceAtLeast(1)) { // Ensure weight is at least 1
                            expandedList.add(optionData.text)
                        }
                    }
                    if (expandedList.isNotEmpty()) expandedList.random()
                    else "Errore nella generazione della lista ponderata" // Fallback
                }
            }
            DecisionMethod.CARD_DRAW -> {
                if (options.isEmpty()) {
                    "Nessuna opzione per Sorteggio Carte"
                } else {
                    // Pondera le opzioni, simile a WHEEL ma con un'interpretazione leggermente diversa
                    // del peso per un sorteggio di "carte".
                    val cardDeck = mutableListOf<String>()
                    options.forEach { optionData ->
                        repeat(optionData.weight.coerceAtLeast(1)) { // Ogni carta ha almeno "1" copia nel mazzo
                            cardDeck.add(optionData.text)
                        }
                    }
                    if (cardDeck.isNotEmpty()) cardDeck.random() else "Errore nel sorteggio carte"
                }
            }
        }
        onComplete(result)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryPurple.copy(alpha = 0.5f),
                        SecondaryBlue.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "ritual_animation")

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val animatedRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "emoji_rotation"
            )
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "emoji_scale"
            )

            Text(
                text = method.emoji,
                fontSize = 120.sp,
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .rotate(animatedRotation)
                    .scale(animatedScale)
            )

            AnimatedVisibility(
                visible = phase < phrases.size,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Text(
                    text = phrases[phase.coerceAtMost(phrases.size - 1)],
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            AnimatedVisibility(
                visible = phase == phrases.size,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                 Text(
                    text = "🎯 Il destino è stato svelato! 🎯",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = SecondaryYellow,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Sparkles effect
        repeat(20) { index ->
            val randomOffset = remember {
                Offset(
                    (Random.nextFloat() - 0.5f) * 700,
                    (Random.nextFloat() - 0.5f) * 700
                )
            }
            val randomDelay = remember { Random.nextInt(0, 500) }

            val animatedAlpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000 + Random.nextInt(0, 1000), delayMillis = randomDelay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sparkle_alpha_$index"
            )

            Text(
                text =listOf("✦", "✧", "✨", "◈").random(),
                fontSize = (14 + Random.nextInt(0,10)).sp,
                color = SecondaryYellow.copy(alpha = animatedAlpha),
                modifier = Modifier
                    .offset(randomOffset.x.dp, randomOffset.y.dp)
                    .alpha(animatedAlpha)
            )
        }
    }
}
