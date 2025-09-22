package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelMethodScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val options by mainViewModel.optionsList.collectAsState()
    var duelOptions by remember { mutableStateOf(options.toMutableList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metodo del Duello", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F0F0)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (options.isEmpty()) {
                Text(
                    text = "Nessuna opzione inserita. Torna indietro e aggiungine almeno due.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                if (duelOptions.size > 1) {
                    val option1 = duelOptions[0]
                    val option2 = duelOptions[1]

                    Text(
                        text = "Quale preferisci?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            duelOptions.remove(option2)
                            if (duelOptions.size == 1) {
                                val winningOption = duelOptions.first()
                                mainViewModel.setFinalDecision(winningOption)
                                mainViewModel.incrementDecisionCount()
                                navController.navigate("risultato")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Text(option1, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("vs", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            duelOptions.remove(option1)
                            if (duelOptions.size == 1) {
                                val winningOption = duelOptions.first()
                                mainViewModel.setFinalDecision(winningOption)
                                mainViewModel.incrementDecisionCount()
                                navController.navigate("risultato")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Text(option2, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (duelOptions.size == 1) {
                    mainViewModel.setFinalDecision(duelOptions.first())
                    mainViewModel.incrementDecisionCount()
                    navController.navigate("risultato")
                }
            }
        }
    }
}
