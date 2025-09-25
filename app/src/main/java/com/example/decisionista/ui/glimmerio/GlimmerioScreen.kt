package com.example.decisionista.ui.glimmerio

// --- Import per i componenti dell'interfaccia utente ---
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Insights // Icona per lo stato vuoto
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.SavedDecision
import com.example.decisionista.utils.formatTimestamp
// Import della funzione di utilità per formattare la data.

/**
 * La schermata principale che mostra la lista delle decisioni salvate
 *
 * @param decisions La lista delle decisioni da visualizzare.
 * @param onDecisionSelect Callback chiamato quando un utente seleziona una Card.
 * @param onDelete Callback chiamato per eliminare una decisione.
 * @param onBack Callback per tornare alla schermata precedente.
 * @param modifier Modificatore per applicare stili e layout esterni.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlimmerioScreen(
    decisions: List<SavedDecision>,
    onDecisionSelect: (SavedDecision) -> Unit,
    onDelete: (SavedDecision) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scaffold è il layout di base di Material Design, offre una struttura predefinita per Top Bar, Floating Action Button, ecc.
    Scaffold(
        topBar = {
            // La barra in alto della schermata.
            TopAppBar(
                title = { Text("Glimmerio dei Ricordi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                // Colori personalizzati per la Top Bar.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Colore di sfondo della schermata.
    ) { paddingValues ->
        // Controlla se la lista delle decisioni è vuota.
        if (decisions.isEmpty()) {
            // Se la lista è vuota, mostra un messaggio a schermo intero.
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center // Centra il contenuto del Box.
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Insights,
                        contentDescription = "Nessun ricordo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 24.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Nessun Glimmering Ancora!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Prendi una decisione per illuminare il tuo Glimmerio. Ogni scelta è una stella nel tuo firmamento personale.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Se la lista non è vuota, visualizza le decisioni in una colonna scorrevole.
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Spaziatura tra gli elementi.
            ) {
                // `items` è un modo efficiente di visualizzare elementi di una lista in LazyColumn.
                // `.reversed()` mostra le decisioni più recenti in cima.
                items(decisions.reversed()) { decision ->
                    // Per ogni decisione, viene creata una `DecisionCard`.
                    DecisionCard(
                        decision = decision,
                        onClick = { onDecisionSelect(decision) },
                        onDelete = { onDelete(decision) }
                    )
                }
            }
        }
    }
}

/**
 * Componente riutilizzabile che visualizza una singola Card per una decisione.
 *
 * @param decision L'oggetto `SavedDecision` da visualizzare.
 * @param onClick Callback per quando si clicca sulla Card.
 * @param onDelete Callback per quando si clicca sull'icona di eliminazione.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionCard(
    decision: SavedDecision,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Stato per mostrare o nascondere la finestra di dialogo di conferma per l'eliminazione.
    var showDeleteDialog by remember { mutableStateOf(false) }
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        onClick = onClick,
        shape = cardShape,
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape), // Assicura che l'effetto "ripple" rispetti gli angoli arrotondati.
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "${decision.method.emoji} ${decision.method.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Risultato: ${decision.result}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis, // Tronca il testo se troppo lungo con "..."
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                // Pulsante di eliminazione.
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Elimina Decisione",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Il timestamp della decisione, formattato usando la funzione di utilità.
            Text(
                text = "Deciso il: ${formatTimestamp(decision.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }

    // Se lo stato `showDeleteDialog` è true, mostra l'AlertDialog.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Conferma Eliminazione", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Vuoi davvero rimuovere questo ricordo dal Glimmerio? L'eco di questa scelta svanirà.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete() // Esegue la callback di eliminazione.
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}
