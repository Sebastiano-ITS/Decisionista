package com.example.decisionista.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedResult(result: String) {
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500) // piccolo delay prima di mostrare
        showResult = true
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showResult,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut()
        ) {
            Text(text = result, fontSize = 32.sp)
        }
    }
}
