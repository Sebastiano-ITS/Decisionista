package com.example.decisionista.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionista.ui.MainViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InsertOptionsScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    var options by remember { mutableStateOf(listOf("Pizza Margherita", "Sushi", "Hamburger")) }
    var newOptionText by remember { mutableStateOf("") }
    val isButtonEnabled = options.size >= 2
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var overItemIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inserisci Opzioni", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF0F0F0)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Aggiungi le opzioni tra cui vuoi scegliere. Puoi modificare, riordinare o eliminare le opzioni in qualsiasi momento.",
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(options) { index, option ->
                            OptionItem(
                                modifier = Modifier
                                    .animateItemPlacement(tween(500))
                                    .fillMaxWidth(),
                                option = option,
                                onDelete = { options = options.filter { it != option } },
                                onDragStart = { draggingItemIndex = index },
                                onDragEnd = { draggingItemIndex = null; overItemIndex = null },
                                onDragOver = { overItemIndex = index },
                                isDragging = index == draggingItemIndex,
                                isOver = index == overItemIndex && index != draggingItemIndex
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newOptionText,
                            onValueChange = { newOptionText = it },
                            placeholder = { Text("Nuova opzione...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (newOptionText.isNotBlank()) {
                                options = options + newOptionText
                                newOptionText = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Aggiungi")
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = "Suggerimento", tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Tieni premuto su un'opzione per trascinarla e riordinarla. Aggiungi almeno 2 opzioni per procedere.",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Button(
                    onClick = {
                        mainViewModel.setOptions(options)
                        navController.navigate("scegli-metodo")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isButtonEnabled) Color(0xFF673AB7) else Color.Gray),
                    enabled = isButtonEnabled
                ) {
                    Text("Avanti", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    option: String,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragOver: () -> Unit,
    isDragging: Boolean,
    isOver: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                ) { change, _ ->
                    change.consume()
                }
            }
            .background(if (isOver) Color(0xFFE0E0E0) else Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DragHandle, contentDescription = "Trascina", tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = option, modifier = Modifier.weight(1f), fontSize = 16.sp)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color.Gray)
            }
        }
    }
}