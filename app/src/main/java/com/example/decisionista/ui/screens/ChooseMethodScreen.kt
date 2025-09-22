package com.example.decisionista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Loop
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionista.ui.MainViewModel

data class DecisionMethod(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseMethodScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    var selectedMethod by remember { mutableStateOf<DecisionMethod?>(null) }
    val methods = listOf(
        DecisionMethod("Casuale", "Scelta completamente random", Icons.Default.Casino, Color(0xFF2196F3), "random-method"),
        DecisionMethod("Eliminazione Progressiva", "Elimina opzioni una alla volta", Icons.Default.Close, Color(0xFFEF5350), "progressive-elimination-method"),
        DecisionMethod("Duel", "Confronta due opzioni alla volta", Icons.Default.QuestionMark, Color(0xFFFF9800), "duel-method"),
        DecisionMethod("Ruota", "Ruota della fortuna visuale", Icons.Default.Loop, Color(0xFF5E35B1), "wheel-method"),
        DecisionMethod("Ponderata", "Basata su pesi e priorità", Icons.Default.Balance, Color(0xFF4CAF50), "weighted-method")
    )
    val options by mainViewModel.optionsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modalità Decisione", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Aggiungi azione impostazioni */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
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
                // Icona centrale
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(100.dp)
                        .background(Color(0xFF8A2BE2), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Dice Icon",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Scegli il Metodo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Seleziona come vuoi prendere la tua decisione",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(methods) { method ->
                        MethodCard(
                            method = method,
                            isSelected = selectedMethod == method,
                            onClick = { selectedMethod = method }
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
            ) {
                Button(
                    onClick = {
                        if (selectedMethod != null) {
                            navController.navigate(selectedMethod!!.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedMethod != null) Color(0xFF673AB7) else Color.Gray),
                    enabled = selectedMethod != null
                ) {
                    Text("Lancia Decisione", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun MethodCard(method: DecisionMethod, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(method.iconColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = method.icon,
                    contentDescription = null,
                    tint = method.iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = method.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = method.description, fontSize = 14.sp, color = Color.Gray)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF673AB7))
            )
        }
    }
}