package com.example.decisionista.ui.oracle

// --- Import necessari per la schermata dell'Oracolo ---
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Share
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
import kotlin.math.abs

// --- Costanti per il rilevamento dello scuotimento dell'accelerometro ---
// Un valore soglia che, se superato dalla "velocità" dello scuotimento, lo attiva.
private const val SHAKE_DETECTION_THRESHOLD = 800
// Un intervallo di tempo minimo in millisecondi tra due scuotimenti validi, per evitare trigger multipli.
private const val MIN_TIME_BETWEEN_SHAKES_MS = 1000L

/**
 * Funzione composable che rappresenta la schermata dell'Oracolo.
 *
 * @param onBack Funzione per gestire la navigazione all'indietro.
 * @param onNavigateToResult Questo parametro è stato mantenuto ma non è utilizzato nella logica corrente della schermata.
 * @param onOracleConsulted Funzione callback da chiamare quando viene generata una nuova profezia, utile per aggiornare le statistiche o altre logiche.
 * @param modifier Modificatore per lo stile e il layout del componente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleScreen(
    onBack: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    onOracleConsulted: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Gestione dello Stato dell'interfaccia utente ---
    // `remember` mantiene lo stato persistente durante le "recomposition".
    // `mutableStateOf` crea una variabile di stato che, se modificata, scatena il ridisegno dei componenti che la usano.
    var currentProphecy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    // --- Inizializzazione del Sensore di Movimento (Accelerometro) ---
    // `LocalContext.current` ottiene il contesto Android, necessario per accedere ai servizi di sistema.
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    // Ottiene il sensore predefinito dell'accelerometro.
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    // Stato per tracciare il timestamp dell'ultimo scuotimento valido, per prevenire trigger rapidi.
    var lastShakeTimestamp by remember { mutableLongStateOf(0L) }

    // --- Dati: Lista delle profezie ---
    val prophecies = listOf(
        "Le stelle sussurrano che un grande cambiamento è in arrivo.",
        "La fortuna sorriderà a chi osa fare il primo passo.",
        "Non temere il futuro: se va male, c’è sempre un concorso pubblico da qualche parte.",
        "Un incontro inaspettato porterà nuove opportunità.",
        "La pazienza sarà la tua alleata più preziosa oggi.",
        "L'amore della tua vita arriverà",
        "Chi lascia la strada vecchia per la nuova.... rischia di trovarsi senza stipendio a fine mese.",
        "L'energia positiva che emani attrarrà ciò che desideri.",
        "Un piccolo gesto di gentilezza avrà grandi conseguenze.",
        "Uno stage arriverà per te",
        "La fortuna ti sorriderà se linux userai..",
        "Per te arriverà un lavoro sicuro",
        "La risposta che cerchi si trova più vicina di quanto pensi.",
        "Il McDonalds ti aspetta",
        "La felicità è come il posto fisso: tutti la cercano, pochi la trovano, e chi ce l’ha non la molla più",
        "La tua intuizione ti guiderà verso la scelta giusta.",
        "Un bel ragazzo americano occhi azzuri e capelli biondi arriverà"
    )

    // --- Logica per la generazione della profezia ---
    // `remember` con le sue chiavi garantisce che questa funzione lambda venga ricreata solo se `isGenerating`, `prophecies` o `onOracleConsulted` cambiano.
    val generateProphecy = remember(isGenerating, prophecies, onOracleConsulted) {
        {
            if (!isGenerating) { // Impedisce l'avvio di una nuova generazione se una è già in corso.
                isGenerating = true
                currentProphecy = prophecies.random() // Seleziona una profezia casuale dalla lista.
                onOracleConsulted() // Notifica al componente padre che l'oracolo è stato consultato.
            }
        }
    }

    // --- Gestione del "Side Effect"
    // `LaunchedEffect` esegue una coroutine quando il valore della sua chiave (`isGenerating`) cambia.
    // Viene usata per simulare un tempo di "generazione" prima che la profezia appaia.
    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            delay(2000) // Sospende la coroutine per 2 secondi.
            isGenerating = false // Imposta lo stato a false, nascondendo l'animazione di caricamento.
        }
    }

    // --- Listener per l'accelerometro
    val shakeListener = remember(generateProphecy) {
        object : SensorEventListener {
            private var lastUpdate: Long = 0
            private var lastX: Float = 0f
            private var lastY: Float = 0f
            private var lastZ: Float = 0f

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val currentTime = System.currentTimeMillis()
                    // Si aggiorna solo ogni 100ms per evitare calcoli eccessivi e risparmiare batteria.
                    if ((currentTime - lastUpdate) > 100) {
                        val diffTime = (currentTime - lastUpdate)
                        lastUpdate = currentTime

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        // Calcola la velocità di scuotimento basata sul cambio di accelerazione sui tre assi.
                        val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                        if (speed > SHAKE_DETECTION_THRESHOLD) {
                            val now = System.currentTimeMillis()
                            // Controlla che sia passato un tempo minimo dall'ultimo scuotimento valido.
                            if ((now - lastShakeTimestamp > MIN_TIME_BETWEEN_SHAKES_MS)) {
                                generateProphecy() // Chiama la funzione per generare la profezia.
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
                // Non necessario per l'implementazione attuale.
            }
        }
    }

    // --- Gestione del "Side Effect" del sensore ---
    // `DisposableEffect` è ideale per le risorse che richiedono pulizia (cleanup).
    // Viene eseguito quando il composable entra nella composizione e `onDispose` viene chiamato quando ne esce.
    DisposableEffect(sensorManager, accelerometer, shakeListener) {
        // Registra il listener per il sensore con un ritardo `SENSOR_DELAY_UI`, adatto all'interfaccia utente.
        sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        // La funzione `onDispose` viene chiamata quando il componente viene rimosso, per evitare memory leak.
        onDispose {
            sensorManager.unregisterListener(shakeListener)
        }
    }

    // --- Logica per la condivisione della profezia ---
    val shareProphecy = { prophecy: String ->
        // Crea un'Intent per l'azione "SEND" per condividere testo.
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "La mia profezia dall'Oracolo Mistico: \n\n\"$prophecy\" \n \n By Decisionista App")
            type = "text/plain" // Specifica il tipo di dato che viene condiviso.
        }
        // Crea un selettore di app per l'utente, per scegliere dove condividere.
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent) // Avvia l'Intent.
    }

    // --- Struttura principale della schermata ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("L'Oracolo Mistico") },
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
    ) { paddingValuesScaffold ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValuesScaffold) // Applica il padding definito dallo Scaffold per la Top Bar.
                .background(
                    Brush.radialGradient( // Gradiente radiale per un effetto di "bagliore" al centro.
                        colors = listOf(
                            PrimaryPurple.copy(alpha = 0.3f),
                            SecondaryBlue.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        ),
                        radius = 1000f
                    )
                )
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.weight(1f)) // Questo Spacer "spinge" il contenuto sottostante verso il centro.

            // --- Contenuto principale della sfera mistica ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animazione di pulsazione infinita per la sfera.
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

                // La sfera mistica, implementata con un'Emoji.
                Text(
                    text = "🔮",
                    fontSize = 120.sp,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .scale(if (isGenerating) animatedScale else 1f) // Applica l'animazione di pulsazione solo durante la generazione.
                        .clickable(enabled = !isGenerating) { generateProphecy() } // Cliccabile solo se non sta già generando.
                )

                Text(
                    text = "Tocca la Sfera Mistica per svelare una profezia...",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // --- Sezione di caricamento e profezia animata ---
                // `AnimatedVisibility` gestisce la comparsa e la scomparsa animata dei componenti figli.
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
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSystemInDarkTheme()) 4.dp else 0.dp)
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

                // La carta che contiene la profezia, visibile solo se una profezia è disponibile e non si sta generando.
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
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSystemInDarkTheme()) 8.dp else 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Profezia Svelata",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                // Pulsante di condivisione, visibile solo quando c'è una profezia.
                                if (currentProphecy.isNotEmpty() && !isGenerating) {
                                    IconButton(onClick = { shareProphecy(currentProphecy) }) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Condividi profezia",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
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
            }

            Spacer(Modifier.weight(1f)) // Questo Spacer spinge il pulsante in basso.

            // --- Sezione inferiore con il pulsante e il testo esplicativo ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                            brush = Brush.horizontalGradient( // Il colore del pulsante cambia quando è disabilitato.
                                colors = if (isGenerating) listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) else listOf(PrimaryPurple, PrimaryBlue)
                            ),
                            shape = buttonShape
                        )
                        .clip(buttonShape)
                        .clickable(enabled = !isGenerating, onClick = generateProphecy), // Non cliccabile durante la generazione.
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Nuova Profezia Icona",
                            tint = if (isGenerating) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onPrimary, // Il colore dell'icona cambia.
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isGenerating) "Consultando..." else "✨ Nuova Profezia ✨", // Il testo del pulsante cambia.
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isGenerating) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onPrimary // Il colore del testo cambia.
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
