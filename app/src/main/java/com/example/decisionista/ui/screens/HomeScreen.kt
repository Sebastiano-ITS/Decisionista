package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionista.ui.MainViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Schermata principale dell'applicazione, mostra le statistiche dell'utente e le opzioni principali.
 */
@Composable
fun HomeScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    // Definizione dei colori e dei gradienti personalizzati
    val DarkBlue = Color(0xFF261D5A)
    val MediumBlue = Color(0xFF322870)
    val LightBlue = Color(0xFF5E5497)
    val GradientPurple = Brush.linearGradient(
        colors = listOf(Color(0xFF8A2BE2), Color(0xFF4B0082))
    )

    // Osservazione dello stato dell'utente e dei dati dal ViewModel
    val currentUser by mainViewModel.currentUser.collectAsState()
    val decisionCount by mainViewModel.decisionCount.collectAsState()

    // Per il momento uso una lista statica, dato che recentActivities non è definito nel tuo ViewModel
    val recentActivities = listOf(
        ActivityItem("Decisione finale presa", "2 ore fa", Icons.Outlined.CheckCircle),
        ActivityItem("Oracolo consultato", "Ieri", Icons.Outlined.CheckCircle)
    )

    // Ottiene il nome utente dall'email o usa un valore di default
    val userName = currentUser?.email?.substringBefore('@') ?: "ospite"
    val scrollState = rememberScrollState()

    // Contenitore principale della schermata
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Contenuto scrollabile che si adatta allo spazio rimanente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp) // Spazio per l'header fisso
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                // Card principale per l'opzione "Punto di Partenza"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E5F2))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Contenitore per l'immagine circolare con sfondo sfumato

                            // Immagine con bordi arrotondati, caricata in modo asincrono con Coil
                            AsyncImage(
                                model = "https://picsum.photos/200/200",
                                contentDescription = "Punto di Partenza",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )


                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Punto di Partenza",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Lascia che ti guidiamo verso la scelta giusta. Ogni decisione è un passo verso il tuo futuro.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // Bottone per navigare alla schermata di inserimento opzioni
                        Button(
                            onClick = { navController.navigate("inserisci-opzioni") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                        ) {
                            Text("Inizia Decisione", color = Color.White, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                // Riga per mostrare le statistiche
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DecisionStatsCard(label = "Decisioni Prese", value = decisionCount.toString())
                    DecisionStatsCard(label = "Consulti Oracolo", value = "8")
                }

                Spacer(modifier = Modifier.height(24.dp))
                // Titolo della sezione "Attività Recente"
                Text(
                    text = "Attività Recente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Elenco delle attività recenti, se presenti
                if (recentActivities.isEmpty()) {
                    Text("Nessuna attività recente", color = Color.Gray)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        recentActivities.forEach { activity ->
                            ActivityRow(
                                label = activity.label,
                                time = activity.time,
                                icon = activity.icon
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // Header fisso in alto, non scorre con il contenuto
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(MediumBlue, LightBlue)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icone di navigazione
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = "Storia",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifiche",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Testo di benvenuto personalizzato
            Text(
                text = "Benvenuto, $userName",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pronto per prendere la tua prossima decisione?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Modello di dati per rappresentare una singola attività recente.
 */
data class ActivityItem(
    val label: String,
    val time: String,
    val icon: ImageVector
)

/**
 * Componente riutilizzabile per mostrare una singola statistica.
 */
@Composable
fun DecisionStatsCard(label: String, value: String) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EBF5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A5ACD))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/**
 * Componente riutilizzabile per mostrare una singola riga di attività recente.
 */
@Composable
fun ActivityRow(label: String, time: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF5E5497),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = time, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
