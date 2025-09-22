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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.SavedDecision // Keep for onNavigateToResult if Oracle saves decisions
import com.example.decisionista.ui.theme.PrimaryBlue
import com.example.decisionista.ui.theme.PrimaryPurple
import com.example.decisionista.ui.theme.SecondaryBlue // Using SecondaryBlue for gradient diversity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleScreen(
    onBack: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit, // Parameter kept for future use if Oracle generates a savable decision
    modifier: Modifier = Modifier
) {
    var currentProphecy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val prophecies = listOf(
        "Le stelle sussurrano che un grande cambiamento è in arrivo.",
        "La fortuna sorriderà a chi osa fare il primo passo.",
        "Un incontro inaspettato porterà nuove opportunità.",
        "La pazienza sarà la tua alleata più preziosa oggi.",
        "Una decisione coraggiosa aprirà porte mai immaginate.",
        "L'energia positiva che emani attrarrà ciò che desideri.",
        "Un piccolo gesto di gentilezza avrà grandi conseguenze.",
        "La risposta che cerchi si trova più vicina di quanto pensi.",
        "Il destino ha in serbo per te una sorpresa meravigliosa.",
        "La tua intuizione ti guiderà verso la scelta giusta."
        // Add more diverse and thematic prophecies
    )

    val generateProphecy = {
        isGenerating = true
        currentProphecy = prophecies.random() // Simple random for now
    }

    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            delay(2000) // Simulate oracle thinking time
            isGenerating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔮 L'Oracolo Mistico") }, // Thematic title
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Updated
                    titleContentColor = MaterialTheme.colorScheme.primary, // Updated
                    navigationIconContentColor = MaterialTheme.colorScheme.primary // Updated
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
                        radius = 1000f // Adjusted radius
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "oracle_orb_scale")
                val animatedScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f, // Subtle pulse
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
                    color = MaterialTheme.colorScheme.secondary, // Thematic color
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Generating Prophecy Card
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
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f) // Updated
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Updated
                            )
                        }
                    }
                }

                // Prophecy Revealed Card
                AnimatedVisibility(
                    visible = currentProphecy.isNotEmpty() && !isGenerating,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 100)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp), // Adjusted padding
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer // Good choice for revealed info
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

                Spacer(modifier = Modifier.weight(1f)) // Push button to bottom if content is less

                // Custom Gradient Button for New Prophecy
                val buttonShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(vertical = 16.dp) // Margin around button
                        .shadow(
                            elevation = if (isGenerating) 0.dp else 6.dp, // No shadow when disabled
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
                    text = "(O scuoti il tuo artefatto per un responso!) ", // Thematic hint
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }
        }
    }
}
