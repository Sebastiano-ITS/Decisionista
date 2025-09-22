package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ProgressiveEliminationScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val options by mainViewModel.optionsList.collectAsState()
    var remainingOptions by remember { mutableStateOf(options.toMutableList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eliminazione Progressiva", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                Text(
                    text = "Tocca un'opzione per eliminarla:",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(remainingOptions) { option ->
                        Button(
                            onClick = {
                                remainingOptions.remove(option)
                                if (remainingOptions.size == 1) {
                                    val winningOption = remainingOptions.first()
                                    mainViewModel.setFinalDecision(winningOption)
                                    mainViewModel.incrementDecisionCount()
                                    navController.navigate("risultato")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(option, color = Color.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}