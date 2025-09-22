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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel
import androidx.compose.runtime.toMutableStateMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightedMethodScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val options by mainViewModel.optionsList.collectAsState()
    val scores = remember(options) {
        options.map { it to 0 }.toMutableStateMap()
    }
    var winningOption by remember { mutableStateOf<String?>(null) }

    val allOptionsScored = scores.values.all { it > 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Decisione Ponderata", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                .background(Color(0xFFF0F0F0))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (winningOption == null) {
                Text(
                    text = "Assegna un punteggio a ciascuna opzione (1-10):",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(options) { option ->
                        WeightedOptionItem(
                            option = option,
                            score = scores[option] ?: 0,
                            onScoreChange = { newScore ->
                                scores[option] = newScore
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        val maxScore = scores.values.maxOrNull()
                        winningOption = scores.filter { it.value == maxScore }.keys.firstOrNull()
                        mainViewModel.incrementDecisionCount()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (allOptionsScored) Color(0xFF4CAF50) else Color.Gray),
                    enabled = allOptionsScored
                ) {
                    Text("Prendi la decisione", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "La tua decisione è:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = winningOption!!,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun WeightedOptionItem(option: String, score: Int, onScoreChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(text = option, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = score.toFloat(),
                onValueChange = { onScoreChange(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = "Punteggio: $score", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
