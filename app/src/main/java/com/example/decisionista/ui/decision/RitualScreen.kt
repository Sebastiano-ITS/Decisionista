package com.example.decisionista.ui.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.model.OptionData
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.SecondaryBlue
import com.example.decisionista.ui.theme.SecondaryYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RitualScreen(
    method: DecisionMethod,
    options: List<OptionData>,
    onComplete: (String) -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }
    val phrases = remember(method) {
        listOf(
            "Il cosmo converge...",
            "Energie arcane si intrecciano...",
            "Il velo tra i mondi si assottiglia...",
            "Il fato sta per essere scritto..."
        )
    }

    // General UI states
    var winnerMessageText by remember { mutableStateOf("") }

    // RANDOM specific states
    var showRandomUI by remember { mutableStateOf(false) }
    var randomStatusMessage by remember { mutableStateOf("") }
    var currentRandomTextDisplay by remember { mutableStateOf("🎲") }

    // DUEL specific states
    var showDuelUI by remember { mutableStateOf(false) }
    var tournamentContenders by remember { mutableStateOf<List<OptionData>>(emptyList()) }
    var currentDuelPair by remember { mutableStateOf<Pair<OptionData, OptionData>?>(null) }
    var duelMessageText by remember { mutableStateOf("") }

    // ELIMINATION specific states
    var showEliminationUI by remember { mutableStateOf(false) }
    val eliminationItemsList = remember { mutableStateListOf<Pair<OptionData, Boolean>>() }
    var eliminationRoundMessage by remember { mutableStateOf("") }

    // WHEEL specific states
    var showWheelUI by remember { mutableStateOf(false) }
    var wheelItemsForDisplay by remember { mutableStateOf<List<OptionData>>(emptyList()) }
    var highlightedWheelItemIndex by remember { mutableIntStateOf(-1) }
    var wheelStatusMessage by remember { mutableStateOf("") }

    // WEIGHTED specific states
    var showWeightedUI by remember { mutableStateOf(false) }
    var weightedItemsForDisplay by remember { mutableStateOf<List<OptionData>>(emptyList()) }
    var highlightedWeightedOptionText by remember { mutableStateOf<String?>(null) }
    var weightedStatusMessage by remember { mutableStateOf("") }

    // CARD_DRAW specific states
    var showCardDrawUI by remember { mutableStateOf(false) }
    var cardDrawStatusMessage by remember { mutableStateOf("") }
    var currentCardTextToDisplay by remember { mutableStateOf<String?>("🃏") }
    var isShufflingCards by remember { mutableStateOf(false) }


    LaunchedEffect(method, options) {
        // Reset all UI states
        winnerMessageText = ""; phase = 0
        showRandomUI = false; randomStatusMessage = ""; currentRandomTextDisplay = "🎲"
        showDuelUI = false; tournamentContenders = emptyList(); currentDuelPair = null; duelMessageText = ""
        showEliminationUI = false; eliminationItemsList.clear(); eliminationRoundMessage = ""
        showWheelUI = false; wheelItemsForDisplay = emptyList(); highlightedWheelItemIndex = -1; wheelStatusMessage = ""
        showWeightedUI = false; weightedItemsForDisplay = emptyList(); highlightedWeightedOptionText = null; weightedStatusMessage = ""
        showCardDrawUI = false; cardDrawStatusMessage = ""; currentCardTextToDisplay = "🃏"; isShufflingCards = false;

        val ritualSegmentDuration = 1600L
        val stepDelay = 2000L
        val messageDelay = 1500L
        val shortAnimDuration = 75L
        val mediumAnimDuration = 150L
        val longAnimDuration = 300L

        for (i in phrases.indices) {
            phase = i; delay(ritualSegmentDuration)
        }
        phase = phrases.size

        if (method == DecisionMethod.RANDOM || method == DecisionMethod.DUEL || method == DecisionMethod.ELIMINATION || method == DecisionMethod.WHEEL || method == DecisionMethod.WEIGHTED || method == DecisionMethod.CARD_DRAW) {
            delay(messageDelay)
        } else {
            delay(1200L) // Fallback for any future methods
        }

        when (method) {
            DecisionMethod.RANDOM -> {
                showRandomUI = true
                randomStatusMessage = "Il Destino mescola le opzioni..."
                delay(messageDelay)

                val winningRandomResult = if (options.isNotEmpty()) options.random().text else "Ness. opzione"
                val displayOptions = if (options.isNotEmpty()) options.map { it.text } else listOf("✨", "❔", "⏳")

                if (options.isNotEmpty()) {
                    val randomAnimations = 15 + options.size.coerceAtMost(10)
                    for (i in 0 until randomAnimations) {
                        currentRandomTextDisplay = displayOptions.random()
                        delay(shortAnimDuration)
                    }
                }

                currentRandomTextDisplay = winningRandomResult
                randomStatusMessage = "Il Fato ha Decretato:"
                delay(stepDelay + messageDelay / 2)

                showRandomUI = false
                winnerMessageText = "🎯 Il destino è svelato! 🎯"; delay(100)
                onComplete(winningRandomResult)
            }
            DecisionMethod.ELIMINATION -> {
                showEliminationUI = true
                eliminationItemsList.addAll(options.map { it to false })
                if (options.isEmpty()) { eliminationRoundMessage = "No opz. Elim."; delay(stepDelay); showEliminationUI = false; winnerMessageText = "Nessuna scelta."; onComplete("No opz. Elim."); return@LaunchedEffect }
                if (options.size == 1) { eliminationRoundMessage = "${options.first().text} è l\'unica scelta!"; delay(stepDelay + messageDelay); showEliminationUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(options.first().text); return@LaunchedEffect }
                eliminationRoundMessage = "Inizia Eliminazione..."; delay(messageDelay)
                while (eliminationItemsList.count { !it.second } > 1) {
                    val active = eliminationItemsList.withIndex().filter { !it.value.second }; if (active.isEmpty()) break
                    val idx = active[Random.nextInt(active.size)].index; eliminationRoundMessage = "Eliminando: ${eliminationItemsList[idx].first.text}..."; delay(stepDelay)
                    eliminationItemsList[idx] = eliminationItemsList[idx].copy(second = true); delay(messageDelay) }
                val winner = eliminationItemsList.firstOrNull { !it.second }?.first
                if (winner != null) { eliminationRoundMessage = "Prescelta: ${winner.text}!"; delay(stepDelay + messageDelay); showEliminationUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(winner.text) }
                else { eliminationRoundMessage = "Errore."; delay(stepDelay); showEliminationUI = false; winnerMessageText = "Qualcosa storto..."; onComplete("Errore Elim.") }
            }
            DecisionMethod.DUEL -> {
                showDuelUI = true; tournamentContenders = options.toList(); var contenders = options.toMutableList()
                if (contenders.isEmpty()) { duelMessageText = "No opz. Duel."; delay(stepDelay); showDuelUI = false; winnerMessageText = "Nessun duello."; onComplete("No opz. Duel"); return@LaunchedEffect }
                if (contenders.size == 1) { duelMessageText = "Vincitore: ${contenders.first().text}"; delay(stepDelay); showDuelUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(contenders.first().text); return@LaunchedEffect }
                duelMessageText = "Torneo a Duello inizia!"; delay(messageDelay); var round = 1
                while (contenders.size > 1) {
                    duelMessageText = "Round $round!"; tournamentContenders = contenders.toList(); delay(messageDelay)
                    val winners = mutableListOf<OptionData>(); val pairs = contenders.windowed(2, 2, false)
                    for (p in pairs) { currentDuelPair = p[0] to p[1]; duelMessageText = """${p[0].text} \n ⚔️ VS ⚔️${p[1].text} \n""" ; delay(stepDelay); val w = if (Random.nextBoolean()) p[0] else p[1]; winners.add(w); duelMessageText = "Vince: ${w.text}!"; currentDuelPair = null; delay(stepDelay) }
                    if (contenders.size % 2 != 0) { val last = contenders.last(); winners.add(last); duelMessageText = "${last.text} avanza!"; delay(messageDelay) }
                    contenders = winners.toMutableList(); tournamentContenders = contenders.toList()
                    if (contenders.size > 1) { round++; duelMessageText = "Fine Round. Pronti per $round!"; delay(messageDelay) } }
                if (contenders.isNotEmpty()) {
                    duelMessageText = "Vincitore: ${contenders.first().text}" // Coppe rimosse
                    delay(stepDelay + 1000L);
                    showDuelUI = false;
                    winnerMessageText = "🎯 Destino svelato! 🎯";
                    onComplete(contenders.first().text)
                }
                else { duelMessageText = "Errore torneo."; delay(stepDelay); showDuelUI = false; winnerMessageText = "Qualcosa storto..."; onComplete("Errore Torneo") }
            }
            DecisionMethod.WHEEL -> {
                showWheelUI = true; wheelItemsForDisplay = options.toList()
                if (options.isEmpty()) { wheelStatusMessage = "No opz. Ruota."; delay(stepDelay); showWheelUI = false; winnerMessageText = "Nessuna scelta."; onComplete("No opz. Ruota"); return@LaunchedEffect }
                if (options.size == 1) { wheelStatusMessage = "Ruota sceglie: ${options.first().text}!"; highlightedWheelItemIndex = 0; delay(stepDelay + messageDelay); showWheelUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(options.first().text); return@LaunchedEffect }

                wheelStatusMessage = "Ruota Magica si prepara..."; delay(messageDelay)

                val winOpt = options.random()
                val winIdx = options.indexOf(winOpt)

                wheelStatusMessage = "Ruota gira, gira, gira!"; val spins = 30; var spinDelay = shortAnimDuration
                for (i in 0 until spins) {
                    highlightedWheelItemIndex = (highlightedWheelItemIndex + 1) % options.size
                    if (i > spins - 5) spinDelay = mediumAnimDuration
                    if (i > spins - 3) spinDelay = longAnimDuration
                    delay(spinDelay)
                }
                highlightedWheelItemIndex = winIdx
                wheelStatusMessage = "Ruota si ferma su: ${winOpt.text}!"; delay(stepDelay + messageDelay)
                showWheelUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(winOpt.text)
            }
            DecisionMethod.WEIGHTED -> {
                showWeightedUI = true; weightedItemsForDisplay = options.toList()
                if (options.isEmpty()) { weightedStatusMessage = "No opz. Pond."; delay(stepDelay); showWeightedUI = false; winnerMessageText = "Nessuna scelta."; onComplete("No opz. Pond."); return@LaunchedEffect }
                if (options.size == 1) { weightedStatusMessage = "Ponderata sceglie: ${options.first().text}!"; highlightedWeightedOptionText = options.first().text; delay(stepDelay + messageDelay); showWeightedUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(options.first().text); return@LaunchedEffect }
                weightedStatusMessage = "Valutazione Pesi Divini..."; delay(messageDelay)
                val expList = mutableListOf<String>(); options.forEach { o -> repeat(o.weight.coerceAtLeast(1)) { expList.add(o.text) } }
                if (expList.isEmpty()) { weightedStatusMessage = "Errore pesi."; delay(stepDelay); showWeightedUI = false; winnerMessageText = "Errore Pesi."; onComplete("Errore Pesi"); return@LaunchedEffect }
                val winTxt = expList.random(); val winOpt = options.find { it.text == winTxt } ?: options.first()
                weightedStatusMessage = "Energie si concentrano..."; val consid = 20 + options.size * 2; var animDelay = shortAnimDuration; val shuffledDisplayList = if (expList.isNotEmpty()) expList.shuffled() else options.map { it.text }.shuffled()
                if (shuffledDisplayList.isNotEmpty()) {
                    for (i in 0 until consid) {
                        highlightedWeightedOptionText = shuffledDisplayList[i % shuffledDisplayList.size]
                        if (i > consid * 0.7) animDelay = mediumAnimDuration
                        if (i > consid * 0.9) animDelay = longAnimDuration
                        delay(animDelay)
                    }
                }
                highlightedWeightedOptionText = winOpt.text; weightedStatusMessage = "Scelta Ponderata indica: ${winOpt.text}!"; delay(stepDelay + messageDelay)
                showWeightedUI = false; winnerMessageText = "🎯 Destino svelato! 🎯"; onComplete(winOpt.text)
            }
            DecisionMethod.CARD_DRAW -> {
                showCardDrawUI = true
                if (options.isEmpty()) {
                    cardDrawStatusMessage = "No carte nel mazzo.";
                    delay(stepDelay);
                    showCardDrawUI = false;
                    winnerMessageText = "Nessuna scelta.";
                    onComplete("No opz. Carte");
                    return@LaunchedEffect
                }
                // MODIFIED: Simplified condition for single option, weight no longer relevant
                if (options.size == 1) {
                    cardDrawStatusMessage = "Carta singola: ${options.first().text}";
                    currentCardTextToDisplay = options.first().text;
                    delay(stepDelay + messageDelay);
                    showCardDrawUI = false;
                    winnerMessageText = "🎯 Destino svelato! 🎯";
                    onComplete(options.first().text);
                    return@LaunchedEffect
                }

                cardDrawStatusMessage = "Preparazione Mazzo del Destino...";
                delay(messageDelay)
                // MODIFIED: Create deck with unique option texts, ignoring weight
                val actualDeck = options.map { it.text }.toMutableList()

                // Note: The 'if (actualDeck.isEmpty())' check previously here is now redundant
                // because 'options.isEmpty()' is checked above, and 'options.map' on a non-empty list
                // will produce a non-empty list of texts.

                val winningCardText = actualDeck.random() // Pick a random card text

                cardDrawStatusMessage = "Mescolando le carte...";
                isShufflingCards = true;
                delay(messageDelay / 2)

                val shuffleAnimations = 20 + actualDeck.size.coerceAtMost(10)
                // Display texts during shuffle should also be from the unweighted deck
                val displayTextsDuringShuffle = actualDeck.shuffled()

                for (i in 0 until shuffleAnimations) {
                    currentCardTextToDisplay = displayTextsDuringShuffle[i % displayTextsDuringShuffle.size]
                    var currentShuffleDelay = shortAnimDuration
                    if (i > shuffleAnimations * 0.7) currentShuffleDelay = mediumAnimDuration
                    delay(currentShuffleDelay)
                }
                isShufflingCards = false

                currentCardTextToDisplay = winningCardText
                cardDrawStatusMessage = "La Carta Estratta è:"
                delay(stepDelay + messageDelay)

                showCardDrawUI = false;
                winnerMessageText = "🎯 Il destino è stato svelato! 🎯";
                onComplete(winningCardText)
            }
        }
    }

    val showGeneralUI = !showRandomUI && !showDuelUI && !showEliminationUI && !showWheelUI && !showWeightedUI && !showCardDrawUI

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.radialGradient(colors = listOf(PrimaryPurple.copy(alpha = 0.5f), SecondaryBlue.copy(alpha = 0.3f), MaterialTheme.colorScheme.background), radius = 900f)),
        contentAlignment = Alignment.Center
    ) {
        val infiniteBgTransition = rememberInfiniteTransition(label = "ritual_bg_anim_main")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val emojiRotation by infiniteBgTransition.animateFloat(initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "emoji_main_rot")
            val emojiScale by infiniteBgTransition.animateFloat(initialValue = 1f, targetValue = 1.15f, animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "emoji_main_scale")

            if (showGeneralUI) {
                Text(method.emoji, fontSize = 120.sp, modifier = Modifier.padding(bottom = 32.dp).rotate(emojiRotation).scale(emojiScale))
            }

            AnimatedVisibility(visible = phase < phrases.size && showGeneralUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) {
                Text(phrases[phase.coerceAtMost(phrases.size - 1)], style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = SecondaryYellow, fontFamily = FontFamily.Serif, modifier = Modifier.padding(horizontal = 24.dp)) // MODIFIED HERE
            }

            // --- UI Sections for each method ---
            AnimatedVisibility(visible = showRandomUI, enter = fadeIn(tween(300)), exit = fadeOut(tween(300))) { /* RANDOM UI */
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp).fillMaxHeight(0.6f)) {
                    Text(randomStatusMessage, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 24.dp))
                    val cardScale by animateFloatAsState(targetValue = 1.1f, animationSpec = tween(600), label = "random_reveal_scale")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(180.dp)
                            .scale(cardScale)
                            .border(2.dp, SecondaryYellow, CardDefaults.shape),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text(
                                text = currentRandomTextDisplay,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showDuelUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { /* Duel UI */
                 Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
                    Text(duelMessageText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 16.dp))
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(tournamentContenders, key = { it.text }) { opt ->
                            val isDuel = currentDuelPair?.first?.text == opt.text || currentDuelPair?.second?.text == opt.text
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(2.dp, if (isDuel) MaterialTheme.colorScheme.tertiary else Color.Transparent, CardDefaults.shape), colors = CardDefaults.cardColors(containerColor = if (isDuel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                                Text(opt.text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.titleMedium, color = if (isDuel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showEliminationUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { /* Elimination UI */
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
                    Text(eliminationRoundMessage, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 16.dp))
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(eliminationItemsList, key = { it.first.text }) { item ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(if (item.second) 0.5f else 1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if(item.second) 0.3f else 0.8f))) {
                                Text(item.first.text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.titleMedium, textDecoration = if (item.second) TextDecoration.LineThrough else TextDecoration.None, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showWheelUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { /* Wheel UI */
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
                    Text(wheelStatusMessage, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 16.dp))
                    if (options.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                            itemsIndexed(wheelItemsForDisplay, key = { _, option -> option.text }) { index, option ->
                                val isHighlighted = index == highlightedWheelItemIndex
                                val scaleFactor by animateFloatAsState(targetValue = if (isHighlighted) 1.1f else 1.0f, animationSpec = tween(durationMillis = 200), label = "wheel_item_scale_anim_idx_${index}")
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).scale(scaleFactor).border(if (isHighlighted) 3.dp else 1.dp, if (isHighlighted) SecondaryYellow else MaterialTheme.colorScheme.outlineVariant, CardDefaults.shape), colors = CardDefaults.cardColors(containerColor = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))) {
                                    Text(option.text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = if(isHighlighted) FontWeight.Bold else FontWeight.Normal, color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showWeightedUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { /* Weighted UI */
                 Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
                    Text(weightedStatusMessage, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 16.dp))
                    if (options.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                            items(weightedItemsForDisplay, key = { option -> option.text }) { option ->
                                val isHighlighted = option.text == highlightedWeightedOptionText
                                val scaleFactor by animateFloatAsState(targetValue = if (isHighlighted) 1.1f else 1.0f, animationSpec = tween(durationMillis = 200), label = "weighted_item_scale_anim_opt_${option.text}")
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).scale(scaleFactor).border(if (isHighlighted) 3.dp else 1.dp, if (isHighlighted) SecondaryYellow else MaterialTheme.colorScheme.outlineVariant, CardDefaults.shape), colors = CardDefaults.cardColors(containerColor = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(option.text, style = MaterialTheme.typography.titleMedium, fontWeight = if(isHighlighted) FontWeight.Bold else FontWeight.Normal, color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("(Peso: ${option.weight.coerceAtLeast(1)})", style = MaterialTheme.typography.bodySmall, color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showCardDrawUI, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { /* Card Draw UI */
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp).fillMaxHeight(0.6f)) {
                    Text(cardDrawStatusMessage, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Serif, modifier = Modifier.padding(bottom = 24.dp))

                    val infiniteShuffleTransition = rememberInfiniteTransition(label = "card_shuffle_visual_transition")
                    val shuffleRotation by infiniteShuffleTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(150, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "card_shuffle_rotation_effect"
                    )

                    val cardRotationActual = if (isShufflingCards) shuffleRotation else 0f
                    val cardRevealScaleActual by animateFloatAsState(
                        targetValue = if (isShufflingCards) 1.0f else 1.2f,
                        animationSpec = tween(300),
                        label = "card_draw_reveal_scale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(180.dp)
                            .scale(cardRevealScaleActual)
                            .rotate(cardRotationActual) // Applicare rotazione qui
                            .border(2.dp, if (!isShufflingCards) SecondaryYellow else MaterialTheme.colorScheme.outline, CardDefaults.shape),
                        colors = CardDefaults.cardColors(containerColor = if (!isShufflingCards) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text(
                                text = currentCardTextToDisplay ?: "🃏",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = if (!isShufflingCards) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Final Winner Message
            AnimatedVisibility(visible = winnerMessageText.isNotBlank() && showGeneralUI && phase == phrases.size, enter = fadeIn(tween(800)), exit = fadeOut(tween(500))) {
                 Text(winnerMessageText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = SecondaryYellow, fontFamily = FontFamily.Serif, modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
            }
        }

        if (showGeneralUI) {
            repeat(20) { index ->
                val randomOffset = remember { Offset((Random.nextFloat() - 0.5f) * 700, (Random.nextFloat() - 0.5f) * 700) }
                val randomDelay = remember { Random.nextInt(0, 500) }
                val animatedAlphaSparkle by infiniteBgTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(1000 + Random.nextInt(0, 1000), delayMillis = randomDelay, easing = LinearEasing), RepeatMode.Reverse), label = "sparkle_alpha_bg_eff_detail_$index")
                Text(listOf("✦", "✧", "✨", "◈").random(), fontSize = (14 + Random.nextInt(0,10)).sp, color = SecondaryYellow.copy(alpha = animatedAlphaSparkle), modifier = Modifier.offset(randomOffset.x.dp, randomOffset.y.dp).alpha(animatedAlphaSparkle))
            }
        }
    }
}
