package com.example.decisionista.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OracoloScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val oracleState by mainViewModel.oracleState.collectAsState()
    val showAnswer by remember { derivedStateOf { oracleState != null } }

    val oracleMessages = remember {
        listOf(
            "Il futuro è plasmato dalle tue mani.",
            "Una nuova opportunità si presenterà presto.",
            "L'amore è la tua più grande forza.",
            "La pazienza ti guiderà verso la vittoria.",
            "Non aver paura di cambiare strada.",
            "Un segreto ti sarà rivelato.",
            "Non aver paura un lavoro troverai.",
            "La fortuna è dalla tua parte.",
            "Ricordati che il McDonald c'è sempre..",
            "Non aver paura uno stage troverai..",
            "Ascolta il tuo cuore, ti condurrà alla verità.",
            "Il successo arriverà con la perseveranza.",
            "Non cercare risposte, cerca te stesso."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A),
                        Color(0xFF4A148C)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sfera dell'Oracolo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE040FB).copy(alpha = 0.8f),
                                Color(0xFF673AB7).copy(alpha = 0.5f),
                                Color(0xFF4A148C).copy(alpha = 0.2f)
                            )
                        )
                    )
                    .clickable {
                        // Genera una risposta casuale e la salva nel ViewModel
                        mainViewModel.setOracleState(oracleMessages.random())
                    }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Sezione testo e risultato
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedContent(
                            targetState = showAnswer,
                            transitionSpec = {
                                fadeIn() with fadeOut()
                            }
                        ) { targetShowAnswer ->
                            if (targetShowAnswer) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "IL DESTINO",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = oracleState ?: "",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "IL DESTINO",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "SUSSURRA...",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "TOCCA LA SFERA MAGICA PER\nRIVELARE IL TUO DESTINO",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (showAnswer) {
                Button(
                    onClick = {
                        oracleState?.let {
                            mainViewModel.addOracleConsultation(it)
                            navController.navigate("glimmerio")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                ) {
                    Text(text = "Mostra Glimmerio", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        mainViewModel.setOracleState(null) // Resetta lo stato per generare un nuovo oracolo
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color.White),
                ) {
                    Text(text = "Genera un nuovo oracolo", color = Color.White)
                }
            }
        }
    }
}