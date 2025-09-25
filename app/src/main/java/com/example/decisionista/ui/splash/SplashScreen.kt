package com.example.decisionista.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Nuova palette di colori fissi, ispirata all'app
private val splashBackgroundStart = Color(0xFF2E0854) // Viola profondo scuro
private val splashBackgroundEnd = Color(0xFF1A0430)   // Viola scurissimo/quasi nero
private val splashIconColor = Color(0xFFFFEB3B)        // Giallo brillante (tipo SecondaryYellow)
private val splashTitleColor = Color(0xFFD0A9F5)       // Viola chiaro e vibrante
private val splashSubtitleColor = Color(0xFFADD8E6)    // Azzurro/Blu chiaro
private val splashSparkleBaseColor1 = Color(0xFFFFEB3B) // Giallo per scintille
private val splashSparkleBaseColor2 = Color(0xFFADD8E6) // Blu chiaro per scintille

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(4000) // Aumentata durata per le nuove animazioni
        isAnimating = false
        delay(600) // Fade out
        onComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite_effects")
    val localConfiguration = LocalConfiguration.current

    // Animazioni per l'icona
    val iconGlow by infiniteTransition.animateFloat(
        initialValue = 15f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "icon_glow"
    )
    val iconRotationY by infiniteTransition.animateFloat(
        initialValue = -12f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "icon_rotation_y"
    )
    val iconOffsetY by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Reverse),
        label = "icon_offset_y"
    )

    // Animazione per l'effetto shimmer sul titolo
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f * localConfiguration.screenWidthDp.toFloat(), // Larghezza del gradiente
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "title_shimmer_translate"
    )
    val shimmerColors = listOf(
        splashTitleColor.copy(alpha = 0.5f),
        splashTitleColor.copy(alpha = 1.0f),
        splashTitleColor.copy(alpha = 0.5f),
    )
    val titleBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerTranslateAnim - (0.5f * localConfiguration.screenWidthDp.toFloat()), 0f),
        end = Offset(shimmerTranslateAnim, 0f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(splashBackgroundStart, splashBackgroundEnd),
                    radius = localConfiguration.screenHeightDp * 1.5f // Raggio ampio
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isAnimating,
                enter = scaleIn(animationSpec = tween(1500)) + fadeIn(animationSpec = tween(1500)),
                exit = scaleOut(animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = "🔮",
                    fontSize = 85.sp, // Leggermente più grande
                    color = splashIconColor,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .graphicsLayer(
                            translationY = iconOffsetY,
                            rotationY = iconRotationY
                        )
                        .shadow(
                            elevation = iconGlow.dp,
                            spotColor = splashIconColor,
                            ambientColor = splashIconColor.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                )
            }

            AnimatedVisibility(
                visible = isAnimating,
                enter = fadeIn(animationSpec = tween(1500, delayMillis = 500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DECISIONISTA",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif, // Scegli un font custom se disponibile
                            fontWeight = FontWeight.ExtraBold, // Più impatto
                            fontSize = 36.sp,
                            letterSpacing = 3.5.sp, // Leggero aumento
                            brush = titleBrush // Applicazione dello shimmer
                           // Non impostare il colore qui quando usi il brush
                        )
                    )
                    Text(
                        text = "Il Mago delle Decisioni",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 19.sp,
                            color = splashSubtitleColor,
                        ),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        // Scintille Cosmiche
        if (isAnimating) {
            repeat(30) { // Numero di scintille
                val randomXFactor = remember { (Random.nextFloat() - 0.5f) * 2.5f }
                val randomYFactor = remember { (Random.nextFloat() - 0.5f) * 2.5f }
                val sparkleAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 0.95f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1300 + Random.nextInt(0, 900) + it * 100),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "sparkle_alpha_enhanced_$it"
                )
                val sparkleSize = remember { (7 + Random.nextInt(0, 10)).sp }
                val sparkleColor = if (Random.nextBoolean()) splashSparkleBaseColor1 else splashSparkleBaseColor2

                Text(
                    text = listOf("✦", "✧", "✨", "◈", ".", "*").random(), // Caratteri per le scintille
                    fontSize = sparkleSize,
                    color = sparkleColor.copy(alpha = sparkleAlpha),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .offset(
                            x = (randomXFactor * (localConfiguration.screenWidthDp / 2.2f)).dp,
                            y = (randomYFactor * (localConfiguration.screenHeightDp / 2.2f)).dp
                        )
                        .alpha(if (Random.nextFloat() > 0.15f) sparkleAlpha else sparkleAlpha * 0.6f)
                )
            }
        }
    }
}
