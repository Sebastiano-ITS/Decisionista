package com.example.decisionista.ui.decision

// Import necessari per l'UI e i componenti di Compose
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.model.OptionData

/**
 * Questa schermata mostra il risultato finale di una decisione.
 * Offre opzioni per ritentare la decisione, scoprire un motivo "magico"
 * per il risultato e tornare alla schermata Home.
 *
 * @param result La stringa che rappresenta l'opzione scelta.
 * @param method Il metodo di decisione utilizzato
 * @param options La lista delle opzioni inizialmente inserite.
 * @param onRetry Callback chiamato per ritentare la decisione.
 * @param onGoHome Callback chiamato per tornare alla schermata Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: String,
    method: DecisionMethod,
    options: List<OptionData>,
    onRetry: () -> Unit,
    onGoHome: () -> Unit
) {
    // Stato per controllare la visibilità del dialogo "Perché"
    var showWhyDialog by remember { mutableStateOf(false) }

    // Lista di ragioni "magiche" per spiegare il risultato
    val magicalReasons = listOf(
        "Le stelle si sono allineate in modo perfetto per questa scelta.",
        "L'energia cosmica ha guidato la decisione verso questa opzione.",
        "Il vento del destino ha soffiato in questa direzione.",
        "Gli spiriti saggi hanno sussurrato questo nome.",
        "La magia antica ha rivelato questa verità nascosta.",
        "Il cristallo della saggezza ha brillato per questa scelta.",
        "Un sussurro arcano ha indicato la via.",
        "Le rune del fato hanno composto questo verdetto."
    )

    // Layout principale con Scaffold per la Top Bar e il contenuto
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Il Destino Svelato") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Card che mostra il risultato
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = tween(800)) + fadeIn(animationSpec = tween(600))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                        hoveredElevation = 12.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Emoji che rappresenta il metodo di decisione
                        Text(
                            text = method.emoji,
                            fontSize = 72.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Titolo e sottotitolo del risultato
                        Text(
                            text = "Il Fato ha Decretato:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        // Il risultato vero e proprio
                        Text(
                            text = result,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 36.sp
                        )
                    }
                }
            }

            // Sezione con i pulsanti d'azione
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pulsanti "Riprova" e "Perché?"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledTonalButton(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Filled.Autorenew, contentDescription = "Riprova")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Riprova")
                    }

                    OutlinedButton(
                        onClick = { showWhyDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Perché", tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Perché?", color = MaterialTheme.colorScheme.secondary)
                    }
                }

                // Pulsante "Torna alla Home"
                TextButton(
                    onClick = onGoHome,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Torna alla Home", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    // Dialogo che si apre quando si clicca "Perché?"
    if (showWhyDialog) {
        AlertDialog(
            onDismissRequest = { showWhyDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "🔮 Segreti dell'Oracolo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    // Mostra una ragione casuale dalla lista
                    text = magicalReasons.random(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showWhyDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Comprendo", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWhyDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Chiudi Portale")
                }
            }
        )
    }
}
