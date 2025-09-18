package com.example.decisionista.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel

@Composable
fun ProfileScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val currentUser by mainViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentUser != null) {
            Text(text = "Sei loggato come: ${currentUser?.email}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    mainViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            ) {
                Text("Logout")
            }
        } else {
            Text("Non sei loggato.")
        }
    }
}
