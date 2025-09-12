package com.example.decisionista.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    val methods = listOf("Random", "Weighted", "Elimination")

    Column {
        methods.forEach { method ->
            Text(
                text = method,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMethodSelected(method) }
                    .padding(8.dp)
            )
        }
    }
}
