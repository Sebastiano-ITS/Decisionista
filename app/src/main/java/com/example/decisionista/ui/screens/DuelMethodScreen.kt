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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelMethodScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    var options by remember { mutableStateOf(mainViewModel.optionsList.value.shuffled()) }
    var winner by remember { mutableStateOf<String?>(null) }

    val duelOptions = if (options.size >= 2) options.subList(0, 2) else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modalità Duello", fontWeight = FontWeight.Bold, color = Color.Black) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (options.size > 1) {
                Text(
                    text = "Scegli l'opzione migliore:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                if (duelOptions != null) {
                    DuelCard(
                        option = duelOptions[0],
                        onClick = {
                            val remaining = options.subList(2, options.size)
                            options = listOf(duelOptions[0]) + remaining
                            if (options.size == 1) {
                                winner = options.first()
                                mainViewModel.incrementDecisionCount()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "vs", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    DuelCard(
                        option = duelOptions[1],
                        onClick = {
                            val remaining = options.subList(2, options.size)
                            options = listOf(duelOptions[1]) + remaining
                            if (options.size == 1) {
                                winner = options.first()
                                mainViewModel.incrementDecisionCount()
                            }
                        }
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "La tua decisione è:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = winner ?: options.firstOrNull() ?: "",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun DuelCard(option: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = option, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
