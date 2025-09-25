package com.example.decisionista.ui.decision

// --- Import necessari per i componenti dell'interfaccia utente ---
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.decisionista.model.OptionData // Import della classe per i dati dell'opzione.
import com.example.decisionista.ui.theme.PrimaryBlue
import com.example.decisionista.ui.theme.PrimaryPurple

/**
 * Funzione composable per la schermata di inserimento delle opzioni.
 * Permette all'utente di aggiungere e rimuovere opzioni per la decisione.
 *
 * @param options La lista attuale delle opzioni.
 * @param onOptionsChange Callback per aggiornare la lista delle opzioni.
 * @param onNext Callback per procedere alla schermata successiva (se ci sono almeno 2 opzioni).
 * @param onBack Callback per tornare alla schermata precedente.
 * @param onNavigateToHome Callback per navigare alla schermata Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsInputScreen(
    options: List<OptionData>,
    onOptionsChange: (List<OptionData>) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Stato per il testo e il peso della nuova opzione da aggiungere.
    var newOptionText by remember { mutableStateOf("") }
    var newOptionWeightInput by remember { mutableStateOf("1") }

    // Definizione dei colori per il campo di testo.
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = PrimaryPurple,
        focusedBorderColor = PrimaryPurple,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = PrimaryPurple,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Scaffold fornisce la struttura di base della schermata.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scolpisci le Tue Opzioni") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
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
                .padding(16.dp)
        ) {
            // Card per il form di input per le nuove opzioni.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Definisci i Sentieri del Fato",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Campo di testo per la descrizione dell'opzione.
                        OutlinedTextField(
                            value = newOptionText,
                            onValueChange = { newOptionText = it },
                            label = { Text("Descrivi un'opzione...") },
                            modifier = Modifier.weight(0.7f),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Campo di testo per il peso dell'opzione, accetta solo numeri.
                        OutlinedTextField(
                            value = newOptionWeightInput,
                            onValueChange = { newValue ->
                                newOptionWeightInput = newValue.filter { it.isDigit() }.take(2)
                            },
                            label = { Text("Peso") },
                            modifier = Modifier.weight(0.3f),
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Pulsante per aggiungere una nuova opzione.
                    IconButton(
                        onClick = {
                            if (newOptionText.isNotBlank()) {
                                // Converte il peso in intero, assicurandosi che sia almeno 1.
                                val weight = newOptionWeightInput.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                val newOption = OptionData(text = newOptionText.trim(), weight = weight)
                                onOptionsChange(options + newOption) // Aggiunge la nuova opzione alla lista.
                                newOptionText = "" // Azzera il campo di testo.
                                newOptionWeightInput = "1" // Reimposta il peso a 1.
                            }
                        },
                        enabled = newOptionText.isNotBlank(),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            Icons.Filled.AddCircleOutline,
                            contentDescription = "Aggiungi Opzione",
                            tint = if (newOptionText.isNotBlank()) PrimaryPurple else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Messaggio mostrato quando la lista delle opzioni è vuota.
            if (options.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.PlaylistAddCheck,
                        contentDescription = "Nessuna opzione",
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(72.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "L'Oracolo attende le tue proposte.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "Forgia almeno due sentieri perché il destino possa scegliere.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                // Lista delle opzioni inserite.
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // `itemsIndexed` è usato per accedere sia all'elemento che al suo indice.
                    itemsIndexed(options) { index, optionData ->
                        OptionItem(index = index, optionData = optionData, onRemove = {
                            // Rimuove l'opzione in base all'indice.
                            onOptionsChange(options.filterIndexed { i, _ -> i != index })
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pulsante "Prosegui al Rituale".
            val buttonEnabled = options.size >= 2
            val buttonShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp)
                    .shadow(
                        elevation = if (buttonEnabled) 6.dp else 0.dp,
                        shape = buttonShape,
                        spotColor = PrimaryPurple
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (buttonEnabled) listOf(PrimaryPurple, PrimaryBlue) else listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        ),
                        shape = buttonShape
                    )
                    .clip(buttonShape)
                    .clickable(enabled = buttonEnabled) {
                        if (buttonEnabled) {
                            onNext()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Prosegui al Rituale",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (buttonEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Prosegui",
                        tint = if (buttonEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            // Messaggio di avviso se non ci sono abbastanza opzioni.
            if (!buttonEnabled && options.isNotEmpty()) {
                Text(
                    text = "Servono almeno 2 opzioni per svelare il fato!",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

/**
 * Funzione composable che rappresenta una singola riga di opzione nella lista.
 *
 * @param index L'indice dell'opzione nella lista.
 * @param optionData L'oggetto `OptionData` da visualizzare.
 * @param onRemove Callback per la rimozione dell'opzione.
 */
@Composable
fun OptionItem(index: Int, optionData: OptionData, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${index + 1}. ${optionData.text}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Peso: ${optionData.weight}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Pulsante per rimuovere l'opzione.
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Rimuovi Opzione",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
