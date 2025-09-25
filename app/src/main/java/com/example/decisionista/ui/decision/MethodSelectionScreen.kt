package com.example.decisionista.ui.decision

// --- Import per i componenti dell'interfaccia utente di Jetpack Compose ---
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.ui.theme.PrimaryBlue
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.SecondaryYellow

/**
 * Funzione composable che rappresenta l'intera schermata di selezione del metodo di decisione.
 *
 * @param selectedMethod Il metodo attualmente selezionato dall'utente.
 * @param onMethodSelect Callback chiamato quando un metodo viene selezionato.
 * @param onLaunch Callback chiamato per avviare il processo di decisione.
 * @param onBack Callback per tornare alla schermata precedente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodSelectionScreen(
    selectedMethod: DecisionMethod,
    onMethodSelect: (DecisionMethod) -> Unit,
    onLaunch: () -> Unit,
    onBack: () -> Unit
) {
    // Scaffold fornisce la struttura base della schermata
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scegli il Metodo Magico") },
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
        containerColor = MaterialTheme.colorScheme.background // Imposta il colore di sfondo.
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Applica il padding per evitare che il contenuto si sovrapponga alla barra superiore.
                .padding(16.dp)
        ) {
            Text(
                text = "Come vuoi che il mago decida?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )

            // LazyColumn è un modo efficiente per visualizzare una lista di elementi.
            // Crea solo i componenti visibili sullo schermo, ottimizzando le performance.
            LazyColumn(
                modifier = Modifier.weight(1f), // Occupa tutto lo spazio verticale disponibile.
                verticalArrangement = Arrangement.spacedBy(16.dp) // Aggiunge uno spazio verticale tra ogni card.
            ) {
                // `items` itera sulla lista dei metodi di decisione.
                // `DecisionMethod.entries.toTypedArray()` ottiene tutti i valori dell'enum `DecisionMethod`.
                items(DecisionMethod.entries.toTypedArray()) { method ->
                    MethodCard(
                        method = method,
                        isSelected = selectedMethod == method, // Passa lo stato di selezione alla card.
                        onClick = { onMethodSelect(method) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Spazio tra la lista e il pulsante.

            // --- Pulsante "Lancia la Decisione Magica" ---
            val buttonShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(elevation = 4.dp, shape = buttonShape)
                    .background(
                        brush = Brush.horizontalGradient( // Gradiente orizzontale per un look più "magico".
                            colors = listOf(PrimaryPurple, PrimaryBlue)
                        ),
                        shape = buttonShape
                    )
                    .clip(buttonShape) // Taglia il pulsante per rispettare la forma.
                    .clickable { onLaunch() }, // Rende il pulsante cliccabile.
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lancia la Decisione Magica",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary // Colore del testo sul gradiente.
                    )
                }
            }
        }
    }
}

/**
 * Funzione composable che disegna una singola Card per un metodo di decisione.
 * Il suo aspetto cambia in base allo stato di selezione.
 *
 * @param method L'oggetto `DecisionMethod` da visualizzare.
 * @param isSelected Booleano che indica se la card è selezionata.
 * @param onClick Callback per il click sulla card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodCard(
    method: DecisionMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp, // L'ombra è più pronunciata se la card è selezionata.
                shape = cardShape
            )
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            // Il colore del container è trasparente se selezionato, altrimenti usa il colore predefinito.
            containerColor = if (isSelected)
                Color.Transparent
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected)
            BorderStroke(2.dp, SecondaryYellow) // Bordo spesso e giallo se selezionato.
        else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Bordo sottile se non selezionato.
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Box è un contenitore che impila i suoi figli.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    // Applica un gradiente di sfondo solo se la card è selezionata.
                    if (isSelected) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(colors = listOf(PrimaryPurple, PrimaryBlue)),
                            shape = cardShape
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = method.emoji,
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = method.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = method.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = (if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // L'icona del "check" è visibile solo se la card è selezionata.
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Metodo Selezionato",
                        tint = SecondaryYellow,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
