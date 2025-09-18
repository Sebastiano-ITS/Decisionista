package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.decisionista.R
import com.example.decisionista.ui.theme.DecisionistaTheme

@Composable
fun HomeScreen(navController: NavHostController) {
    val DarkBlue = Color(0xFF261D5A)
    val MediumBlue = Color(0xFF322870)
    val LightBlue = Color(0xFF5E5497)
    val GradientPurple = Brush.linearGradient(
        colors = listOf(Color(0xFF8A2BE2), Color(0xFF4B0082))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Sezione Header
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
            Text(
                text = "Benvenuto, mario",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pronto per prendere la tua prossima decisione?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }

        // Corpo della pagina
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Widget "Punto di Partenza"
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E5F2))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Placeholder per l'immagine centrale
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = GradientPurple,
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

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
                    Button(
                        onClick = { /* TODO: Implementa l'azione */ },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DecisionStatsCard(label = "Decisioni Prese", value = "12")
                DecisionStatsCard(label = "Consulti Oracolo", value = "8")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Attività Recente",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Elenco attività recenti
            ActivityRow(
                label = "Scelta carriera",
                time = "2 ore fa",
                icon = Icons.Outlined.CheckCircle
            )
            Spacer(modifier = Modifier.height(8.dp))
            ActivityRow(
                label = "Consulto Oracolo",
                time = "1 giorno fa",
                icon = Icons.Outlined.Circle
            )
        }
    }
}

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

@Composable
fun ActivityRow(label: String, time: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DecisionistaTheme {
        HomeScreen(rememberNavController())
    }
}
