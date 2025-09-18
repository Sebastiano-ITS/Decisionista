package com.decisionista.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel
import com.example.decisionista.ui.components.LoginButton
import com.example.decisionista.ui.components.LoginInput

@Composable
fun AuthScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val DarkBlue = Color(0xFF261D5A)
    val MediumBlue = Color(0xFF322870)
    val LightPurple = Color(0xFF9A86DA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, MediumBlue)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLoginMode) "Login" else "Registrazione",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(48.dp))

            LoginInput(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            Spacer(Modifier.height(16.dp))
            LoginInput(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(32.dp))

            LoginButton(
                text = if (isLoginMode) "Accedi" else "Registrati",
                onClick = {
                    isLoading = true
                    if (email.isNotBlank() && password.isNotBlank()) {
                        mainViewModel.login(email)
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Inserisci email e password.", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = LightPurple
                )
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(
                    text = if (isLoginMode) "Non hai un account? Registrati" else "Hai già un account? Accedi",
                    color = LightPurple,
                    fontSize = 14.sp
                )
            }
        }
    }
}