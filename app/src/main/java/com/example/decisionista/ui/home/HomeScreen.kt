package com.example.decisionista.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.decisionista.R
import com.example.decisionista.model.SavedDecision
import com.example.decisionista.ui.theme.PrimaryBlue
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.statsDecisionMadeBackgroundColor
import com.example.decisionista.ui.theme.statsDecisionMadeLabelValueColor
import com.example.decisionista.ui.theme.statsOracleConsultationBackgroundColor
import com.example.decisionista.ui.theme.statsOracleConsultationLabelValueColor
import com.example.decisionista.utils.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    decisionsMadeCount: Int,
    oracleConsultationsCount: Int,
    recentDecisions: List<SavedDecision>,
    onStartDecision: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val screenBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        topBar = {
            HomeTopAppBar(
                userName = userName,
                onHelpClick = { showHelpDialog = true },
                onNotificationsClick = { showNotificationsDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(screenBackgroundBrush)
                .padding(innerPadding)
                .padding(horizontal = 24.dp) // Leggermente ridotto per più spazio ai lati
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Spazio sotto TopAppBar
            MainDecisionArea(onStartDecision = onStartDecision)
            Spacer(modifier = Modifier.height(24.dp))
            StatsSection(
                decisionsMadeCount = decisionsMadeCount,
                oracleConsultationsCount = oracleConsultationsCount
            )
            Spacer(modifier = Modifier.height(24.dp))
            RecentDecisionsSection(
                recentDecisions = recentDecisions,
                onNavigateToResult = onNavigateToResult
            )
            Spacer(modifier = Modifier.height(24.dp)) // Padding inferiore più generoso
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Guida Mistica") },
            text = { Text("Benvenuto in Decisionista!\n\nEsplora questa schermata per iniziare nuove decisioni, consultare le tue statistiche arcane o rivedere le tue recenti scelte del Fato. Che la fortuna ti assista!") },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Capito!")
                }
            }
        )
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Notifiche Arcane") },
            text = { Text("Al momento, nessun sussurro dal Fato o messaggio dagli astri. Controlla più tardi!") },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Ricevuto")
                }
            }
        )
    }
}

@Composable
fun HomeTopAppBar(
    userName: String,
    onHelpClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryPurple, PrimaryBlue)
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp) // Aumentato padding verticale
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Allineamento verticale icone
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = stringResource(R.string.home_icon_content_description_help),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(35.dp) // Aumentata dimensione icona
                        .clickable { onHelpClick() }
                        .padding(4.dp) // Padding per area cliccabile maggiore
                )
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = stringResource(R.string.home_icon_content_description_notifications),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(35.dp) // Aumentata dimensione icona
                        .clickable { onNotificationsClick() }
                        .padding(4.dp) // Padding per area cliccabile maggiore
                )
            }
            Spacer(modifier = Modifier.height(16.dp)) // Ridotto spacer
            Column(
                modifier = Modifier.fillMaxWidth(), // Rimosso padding(top=8dp) qui, gestito da Spacer
                horizontalAlignment = Alignment.CenterHorizontally // Centra il testo del nome e sottotitolo
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.home_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MainDecisionArea(onStartDecision: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "evoca_button_pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "evocaButtonPulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), // Leggermente ridotto padding superiore se c'è già lo spacer globale
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryPurple.copy(alpha = 0.6f), PrimaryBlue.copy(alpha = 0.6f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Immagine con bordi arrotondati, caricata in modo asincrono con Coil
            AsyncImage(
                model = R.drawable.mago,
                contentDescription = "Punto di Partenza",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .shadow(
                        elevation = 26.dp, // L'altezza "dell'ombra"
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.5f), // Colore dell'ombra
                        spotColor = Color.Black.copy(alpha = 0.5f) // Colore dell'ombra
                    ),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = stringResource(R.string.home_start_point_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.home_start_point_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp) // Aggiunto padding orizzontale al testo descrittivo
        )
        Spacer(modifier = Modifier.height(32.dp))
        val buttonShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(pulseScale)
                .shadow(elevation = 8.dp, shape = buttonShape, spotColor = PrimaryPurple)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryPurple, PrimaryBlue)
                    ),
                    shape = buttonShape
                )
                .clip(buttonShape)
                .clickable { onStartDecision() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.home_start_decision_button_icon_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.home_start_decision_button_text),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}



@Composable
fun StatsSection(decisionsMadeCount: Int, oracleConsultationsCount: Int) {
    Column {
        Text(
            text = stringResource(R.string.home_mystic_path_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold, // Reso Bold per più importanza
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp) // Aumentato padding bottom
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DecisionStatsCard(
                label = stringResource(R.string.home_stats_decisions_made),
                value = decisionsMadeCount.toString(),
                modifier = Modifier.weight(1f),
                labelColor = statsDecisionMadeLabelValueColor,
                valueColor = statsDecisionMadeLabelValueColor,
                backgroundColor = statsDecisionMadeBackgroundColor
            )
            DecisionStatsCard(
                label = stringResource(R.string.home_stats_oracle_consultations),
                value = "$oracleConsultationsCount",
                modifier = Modifier.weight(1f),
                labelColor = statsOracleConsultationLabelValueColor,
                valueColor = statsOracleConsultationLabelValueColor,
                backgroundColor = statsOracleConsultationBackgroundColor
            )
        }
    }
}

@Composable
fun RecentDecisionsSection(
    recentDecisions: List<SavedDecision>,
    onNavigateToResult: (SavedDecision) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.home_recent_activity_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold, // Reso Bold
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp) // Unificato padding bottom
        )
        if (recentDecisions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.HistoryEdu,
                    contentDescription = stringResource(R.string.home_no_recent_activity_icon_content_description),
                    modifier = Modifier.size(60.dp).padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
                Text(
                    text = stringResource(R.string.home_no_recent_activity_message_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.home_no_recent_activity_message_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp) // Aggiunto padding orizzontale
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // Leggermente ridotto spazio tra le card
                recentDecisions.forEach { decision ->
                    RecentDecisionItem(
                        decision = decision,
                        onClick = { onNavigateToResult(decision) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentDecisionItem(
    decision: SavedDecision,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Aggiunta leggera elevazione
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) // Bordo più tenue
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${decision.method.emoji} ${decision.method.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val prefixFromResource = stringResource(R.string.recent_decision_item_result_prefix)
            // Assicura che ci sia " : " (spazio, due punti, spazio) tra il prefisso e il risultato
            val labelText = prefixFromResource.trimEnd(' ', ':') + " : " 

            Text(
                text = labelText + decision.result, // Esempio: "Risultato : pino"
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = formatTimestamp(decision.timestamp), // Now uses the imported function
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DecisionStatsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: Color,
    valueColor: Color,
    backgroundColor: Color
) {
    Card(
        modifier = modifier
            .height(140.dp) // Altezza fissa per uniformità
            .clip(RoundedCornerShape(16.dp)), // Raggio aumentato
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Aggiunta leggera elevazione
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp), // Testo valore più grande
                fontWeight = FontWeight.ExtraBold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp)) // Aumentato spacer
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp) // Padding per evitare testo troppo vicino ai bordi
            )
        }
    }
}
