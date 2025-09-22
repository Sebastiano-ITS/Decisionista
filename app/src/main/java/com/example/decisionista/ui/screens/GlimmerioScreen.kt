package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlimmerioScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val glimmerioList by mainViewModel.glimmerioList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Glimmerio", fontWeight = FontWeight.Bold, color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F0F0))
                .padding(16.dp)
        ) {
            if (glimmerioList.isEmpty()) {
                Text(
                    text = "Ancora nessun consulto dell'oracolo. Torna alla schermata dell'oracolo per crearne uno.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(glimmerioList.reversed()) { consultation ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = consultation.message,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Generato il: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(consultation.timestamp))}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
