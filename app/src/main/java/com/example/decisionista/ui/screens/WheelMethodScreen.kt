package com.example.decisionista.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.decisionista.ui.MainViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelMethodScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val options by mainViewModel.optionsList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var targetRotation by remember { mutableStateOf(0f) }
    var resultOption by remember { mutableStateOf<String?>(null) }
    val animateSpin = remember { Animatable(0f) }

    // Ottieni la densità prima del blocco remember
    val density = LocalDensity.current

    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = with(density) { 16.sp.toPx() }
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ruota della Fortuna", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                .background(Color(0xFFF0F0F0)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (resultOption == null) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepAngle = 360f / options.size
                        val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray, Color.DarkGray)
                        var startAngle = animateSpin.value

                        // Disegna la ruota
                        options.forEachIndexed { index, option ->
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweepAngle
                        }

                        // Disegna il testo
                        startAngle = animateSpin.value
                        drawIntoCanvas { canvas ->
                            options.forEachIndexed { index, option ->
                                val angle = startAngle + sweepAngle / 2
                                val textX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * size.width / 2.5f
                                val textY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * size.height / 2.5f
                                canvas.nativeCanvas.drawText(option, textX, textY, textPaint)
                                startAngle += sweepAngle
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val randomOffset = Random.nextFloat() * 360f
                            val newTargetRotation = 360f * 5 + randomOffset
                            targetRotation = newTargetRotation

                            animateSpin.animateTo(
                                targetValue = targetRotation,
                                animationSpec = tween(
                                    durationMillis = 3000,
                                    easing = FastOutSlowInEasing
                                )
                            )

                            // Calcola l'opzione vincente
                            val normalizedRotation = (360 - (targetRotation % 360)) % 360
                            val winningIndex = (normalizedRotation / (360f / options.size)).toInt()
                            resultOption = options[winningIndex]

                            mainViewModel.incrementDecisionCount()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                ) {
                    Text("Gira la ruota", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "La tua decisione è:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = resultOption!!,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF673AB7)
                )
            }
        }
    }
}
