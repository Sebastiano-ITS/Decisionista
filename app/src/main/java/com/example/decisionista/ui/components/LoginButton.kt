package com.example.decisionista.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginButton(text: String, onClick: () -> Unit) {
    val LightPurple = Color(0xFF9A86DA)
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
    ) {
        Text(text, color = Color.White, fontSize = 18.sp)
    }
}