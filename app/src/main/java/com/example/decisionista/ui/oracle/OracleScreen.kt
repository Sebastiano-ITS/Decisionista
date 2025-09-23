package com.example.decisionista.ui.oracle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.SavedDecision
import com.example.decisionista.ui.theme.PrimaryBlue
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.SecondaryBlue
import kotlinx.coroutines.delay

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

// Costanti per il rilevamento dello scuotimento
private const val SHAKE_DETECTION_THRESHOLD = 800 // Valore soglia per la velocità di scuotimento
private const val MIN_TIME_BETWEEN_SHAKES_MS = 1000L // Minimo intervallo tra due scuotimenti validi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleScreen(
    onBack: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    onOracleConsulted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentProphecy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    // Contesto e SensorManager per il rilevamento dello scuotimento
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    var lastShakeTimestamp by remember { mutableLongStateOf(0L) }

    val prophecies = listOf(
        "Le stelle sussurrano che un grande cambiamento è in arrivo.",
        "La fortuna sorriderà a chi osa fare il primo passo.",
        "Un incontro inaspettato porterà nuove opportunità.",
        "La pazienza sarà la tua alleata più preziosa oggi.",
        "Una decisione coraggiosa aprirà porte mai immaginate.",
        "L'energia positiva che emani attrarrà ciò che desideri.",
        "Un piccolo gesto di gentilezza avrà grandi conseguenze.",
        "Un stage arriverà per te..",
        "La fortuna ti sorriderà se linux userai..",
        "Per te arriverà un lavoro sicuro",
        "La risposta che cerchi si trova più vicina di quanto pensi.",
        "Il destino ha in serbo per te una sorpresa meravigliosa.",
        "La tua intuizione ti guiderà verso la scelta giusta."
    )

    val generateProphecy = remember(isGenerating, prophecies, onOracleConsulted) {
        {
            if (!isGenerating) {
                isGenerating = true
                currentProphecy = prophecies.random()
                onOracleConsulted()
            }
        }
    }

    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            delay(2000)
            isGenerating = false
        }
    }

    // Logica per il rilevamento dello scuotimento
    val shakeListener = remember(generateProphecy) {
        object : SensorEventListener {
            private var lastUpdate: Long = 0
            private var lastX: Float = 0f
            private var lastY: Float = 0f
            private var lastZ: Float = 0f

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val currentTime = System.currentTimeMillis()
                    if ((currentTime - lastUpdate) > 100) {
                        val diffTime = (currentTime - lastUpdate)
                        lastUpdate = currentTime

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                        if (speed > SHAKE_DETECTION_THRESHOLD) {
                            val now = System.currentTimeMillis()
                            if ((now - lastShakeTimestamp > MIN_TIME_BETWEEN_SHAKES_MS)) {
                                generateProphecy()
                                if (!isGenerating) {
                                    lastShakeTimestamp = now
                                }
                            }
                        }
                        lastX = x
                        lastY = y
                        lastZ = z
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Non necessario
            }
        }
    }

    // Registra e de-registra il listener del sensore
    DisposableEffect(sensorManager, accelerometer, shakeListener) {
        sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(shakeListener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔮 L'Oracolo Mistico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryPurple.copy(alpha = 0.3f),
                            SecondaryBlue.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        ),
                        radius = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "oracle_orb_scale")
                val animatedScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "orb_scale"
                )

                Text(
                    text = "🔮",
                    fontSize = 120.sp,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .scale(if (isGenerating) animatedScale else 1f)
                        .clickable(enabled = !isGenerating) { generateProphecy() }
                )

                Text(
                    text = "Tocca la Sfera Mistica per svelare una profezia...",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                AnimatedVisibility(
                    visible = isGenerating,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ L'Oracolo sta scrutando i fili del fato... ✨",
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = currentProphecy.isNotEmpty() && !isGenerating,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 100)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌟 Profezia Svelata 🌟",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                text = currentProphecy,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
            // Pulsante e testo spostati fuori dal Column principale
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Allinea in basso
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsante "Nuova Profezia"
                val buttonShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = if (isGenerating) 0.dp else 6.dp,
                            shape = buttonShape,
                            spotColor = PrimaryPurple
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (isGenerating) listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) else listOf(PrimaryPurple, PrimaryBlue)
                            ),
                            shape = buttonShape
                        )
                        .clip(buttonShape)
                        .clickable(enabled = !isGenerating, onClick = generateProphecy),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Nuova Profezia Icona",
                            tint = if (isGenerating) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isGenerating) "Consultando..." else "✨ Nuova Profezia ✨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isGenerating) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Text(
                    text = "(O scuoti il tuo artefatto per un responso!) ",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }
        }
    }
}
