package com.decisionista.app.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(navController: NavHostController) {
    // Colori per il tema della splash screen
    val Purple800 = Color(0xFF673AB7)
    val Purple700 = Color(0xFF9C27B0)
    val DarkBlue = Color(0xFF261D5A)
    val MediumBlue = Color(0xFF322870)
    val LightPurple = Color(0xFF9A86DA)
    val Orange = Color(0xFFFFA500)

    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, MediumBlue),
                    startY = 0f,
                    endY = 1000f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(100.dp))
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Purple800, Purple700)
                    ),
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = "Icona App",
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Decisionista", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Risveglia la tua mente", fontSize = 16.sp, color = LightPurple)

        Spacer(Modifier.height(48.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = if (index == 1) Orange else LightPurple)
                        .alpha(animatedAlpha)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Preparando gli enigmi...", color = Color.White, fontSize = 14.sp)
        Spacer(Modifier.height(100.dp))
        Button(
            onClick = {
                // Modifica qui per navigare verso la destinazione "auth"
                navController.navigate("auth")
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
        ) {
            Text("Inizia", color = Color.White, fontSize = 18.sp)
        }
    }
}