package com.decisionista.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val SPLASH_DELAY = 5000L
private const val BUBBLE_COUNT = 15

data class Bubble(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float,
    val scale: Float,
    val animationDelay: Int
)

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    var startAnimation by remember { mutableStateOf(false) }
    val currentNavController by rememberUpdatedState(navController)

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(SPLASH_DELAY)
        currentNavController.navigate("auth") {
            popUpTo("splash") { inclusive = true }
        }
    }

    val bubbles = remember {
        val random = Random(42)
        List(BUBBLE_COUNT) {
            Bubble(
                x = random.nextFloat(),
                y = random.nextFloat(),
                size = random.nextFloat() * 40 + 20,
                alpha = random.nextFloat() * 0.4f + 0.2f,
                scale = random.nextFloat() * 0.5f + 0.5f,
                animationDelay = random.nextInt(1000)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF261D5A)), // Sfondo viola scuro
        contentAlignment = Alignment.Center
    ) {
        bubbles.forEach { bubble ->
            val bubbleAlpha by animateFloatAsState(
                targetValue = if (startAnimation) bubble.alpha else 0f,
                animationSpec = tween(2000 + bubble.animationDelay, easing = LinearEasing),
                label = "bubbleAlpha"
            )
            val bubbleScale by animateFloatAsState(
                targetValue = if (startAnimation) bubble.scale else 0f,
                animationSpec = tween(2000 + bubble.animationDelay, easing = LinearEasing),
                label = "bubbleScale"
            )

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (bubble.x * screenWidthPx - screenWidthPx / 2).toInt(),
                            y = (bubble.y * screenHeightPx - screenHeightPx / 2).toInt()
                        )
                    }
                    .size(bubble.size.dp)
                    .scale(bubbleScale)
                    .alpha(bubbleAlpha)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape) // Bolle bianche trasparenti
            )
        }

        AnimatedVisibility(
            visible = startAnimation,
            enter = fadeIn(tween(1500)) + scaleIn(initialScale = 0f, animationSpec = tween(1000)),
            exit = ExitTransition.None
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                // Icona centrale (simulazione)
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color(0xFF8A2BE2), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = "Neurology Icon",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Decisionista",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preparazione in corso...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(navController = NavHostController(LocalContext.current))
    }
}