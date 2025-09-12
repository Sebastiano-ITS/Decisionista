// MainActivity.kt
package com.example.decisionista

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit // For SharedPreferences KTX
import com.example.decisionista.data.DecisionRepository
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.model.OptionData
import com.example.decisionista.model.SavedDecision
import com.example.decisionista.model.Screen
import com.example.decisionista.ui.auth.LoginScreen
import com.example.decisionista.ui.auth.RegisterScreen
import com.example.decisionista.ui.decision.MethodSelectionScreen
import com.example.decisionista.ui.decision.OptionsInputScreen
import com.example.decisionista.ui.decision.ResultScreen
import com.example.decisionista.ui.decision.RitualScreen
import com.example.decisionista.ui.glimmerio.GlimmerioScreen
import com.example.decisionista.ui.home.HomeScreen
import com.example.decisionista.ui.oracle.OracleScreen
import com.example.decisionista.ui.profile.ProfileScreen
import com.example.decisionista.ui.splash.SplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.decisionista.navigation.AppNavHost
import com.example.decisionista.ui.theme.DecisionistaAppTheme
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import java.util.Locale

// GUEST_USER_IDENTIFIER is used by MainActivity to identify guest sessions.
const val GUEST_USER_IDENTIFIER = "_decisionista_guest_user_"

// SharedPreferences constants
private const val PREFS_NAME = "DecisionistaUserPrefs"
private const val KEY_REGISTERED_USERS_STRING_V2 = "registered_users_map_v2" // Rinominata per il nuovo formato
private const val KEY_ORACLE_COUNT_PREFIX = "oracle_count_"
private const val KEY_LOGGED_IN_USER_EMAIL = "logged_in_user_email"
private const val ENTRY_DELIMITER = "##USER_ENTRY##"
private const val KV_DELIMITER_PASS = "##EMAIL_PASS_KV##"
private const val KV_DELIMITER_MAGIC_NAME = "##MAGIC_NAME_KV##" // Nuovo delimitatore

// Data class per i dati dell'account utente
private data class UserAccountData(val hashedPassword: String, val magicName: String)

// --- Utility Functions for Hashing and SharedPreferences ---
private fun sha256(input: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        hashBytes.fold("") { str, it -> str + "%02x".format(it) }
    } catch (_: Exception) {
        input.reversed() + "_hash_failed"
    }
}

private fun serializeUserMap(map: Map<String, UserAccountData>): String {
    return map.entries.joinToString(ENTRY_DELIMITER) { (email, accountData) ->
        "${email}${KV_DELIMITER_PASS}${accountData.hashedPassword}${KV_DELIMITER_MAGIC_NAME}${accountData.magicName}"
    }
}

private fun deserializeUserMap(serialized: String?): Map<String, UserAccountData> {
    if (serialized.isNullOrBlank()) {
        return emptyMap()
    }
    val map = mutableMapOf<String, UserAccountData>()
    try {
        val entries = serialized.split(ENTRY_DELIMITER)
        for (entry in entries) {
            if (entry.isBlank()) continue
            // Split prima per password, poi per nome magico
            val partsPass = entry.split(KV_DELIMITER_PASS, limit = 2)
            if (partsPass.size == 2) {
                val email = partsPass[0]
                val rest = partsPass[1]
                val partsMagicName = rest.split(KV_DELIMITER_MAGIC_NAME, limit = 2)
                if (partsMagicName.size == 2) {
                    map[email] = UserAccountData(partsMagicName[0], partsMagicName[1])
                }
            }
        }
    } catch (_: Exception) {
        return emptyMap()
    }
    return map
}

private fun saveRegisteredUsers(context: Context, users: Map<String, UserAccountData>) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val serializedUsers = serializeUserMap(users)
    prefs.edit {
        putString(KEY_REGISTERED_USERS_STRING_V2, serializedUsers)
    }
}

private fun loadRegisteredUsers(context: Context): Map<String, UserAccountData> {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val serializedUsers = prefs.getString(KEY_REGISTERED_USERS_STRING_V2, null)
    return deserializeUserMap(serializedUsers)
}

private fun saveOracleCount(context: Context, userEmail: String, count: Int) {
    if (userEmail.isBlank() || userEmail == GUEST_USER_IDENTIFIER) return
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit { // Using KTX extension
        putInt("${KEY_ORACLE_COUNT_PREFIX}${userEmail}", count)
    }
}

private fun loadOracleCount(context: Context, userEmail: String): Int {
    if (userEmail.isBlank() || userEmail == GUEST_USER_IDENTIFIER) return 0
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getInt("${KEY_ORACLE_COUNT_PREFIX}${userEmail}", 0)
}

private fun saveLoggedInUser(context: Context, email: String) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit {
        putString(KEY_LOGGED_IN_USER_EMAIL, email)
    }
}

private fun loadLoggedInUser(context: Context): String? {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_LOGGED_IN_USER_EMAIL, null)
}

private fun clearLoggedInUser(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit {
        remove(KEY_LOGGED_IN_USER_EMAIL)
    }
}
// --- End Utility Functions ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DecisionistaAppTheme {
                DecisionistaApp()
            }
        }
    }
}

@Composable
fun AppNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Screen.HOME,
        Screen.GLIMMERIO,
        Screen.ORACLE,
        Screen.PROFILE
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        items.forEach { screen ->
            val selected = currentScreen == screen
            val icon = when (screen) {
                Screen.HOME -> Icons.Filled.Home
                Screen.GLIMMERIO -> Icons.Filled.Star
                Screen.ORACLE -> Icons.Filled.Favorite
                Screen.PROFILE -> Icons.Filled.Person
                else -> Icons.Filled.Home
            }
            val label = when (screen) {
                Screen.HOME -> "Home"
                Screen.GLIMMERIO -> "Glimmerio"
                Screen.ORACLE -> "Oracolo"
                Screen.PROFILE -> "Profilo"
                else -> ""
            }

            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selected,
                onClick = { onNavigate(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionistaApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var userEmail by remember { mutableStateOf("") }
    var registeredUsers by remember { mutableStateOf(mapOf<String, UserAccountData>()) }
    var oracleConsultationsCount by remember { mutableIntStateOf(0) }

    // NUOVI STATI PER LE IMPOSTAZIONI DEL PROFILO
    var isDarkMode by remember { mutableStateOf(false) } // Esempio, dovresti caricare/salvare questo valore
    var soundEnabled by remember { mutableStateOf(true) } // Esempio
    var vibrationsEnabled by remember { mutableStateOf(true) } // Esempio
    var showLogoutDialog by remember { mutableStateOf(false) } // Per il dialogo di logout

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        registeredUsers = loadRegisteredUsers(context)
        val loggedInEmail = loadLoggedInUser(context)
        if (!loggedInEmail.isNullOrBlank() && registeredUsers.containsKey(loggedInEmail)) {
            userEmail = loggedInEmail
            currentScreen = Screen.HOME
        } else {
            clearLoggedInUser(context)
        }
    }

    var decisions by remember { mutableStateOf(listOf<SavedDecision>()) }
    val mainNavScreens = listOf(Screen.HOME, Screen.GLIMMERIO, Screen.ORACLE, Screen.PROFILE)

    val navigateToInitialAuthScreen = {
        currentScreen = Screen.LOGIN
    }

    LaunchedEffect(userEmail, context) {
        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            decisions = DecisionRepository.loadDecisions(context, userEmail)
            oracleConsultationsCount = loadOracleCount(context, userEmail)
        } else {
            decisions = listOf()
            oracleConsultationsCount = 0
        }
    }

    LaunchedEffect(currentScreen, userEmail) {
        val isTryingToAccessMainScreenWithoutAuth =
            currentScreen in mainNavScreens && userEmail.isBlank()
        if (isTryingToAccessMainScreenWithoutAuth && currentScreen != Screen.SPLASH) {
            navigateToInitialAuthScreen()
        }
    }

    val updateAndSaveDecisions = { updatedDecisionsList: List<SavedDecision> ->
        decisions = updatedDecisionsList
        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            coroutineScope.launch {
                DecisionRepository.saveDecisions(context, userEmail, updatedDecisionsList)
            }
        }
    }

    var currentOptions by remember { mutableStateOf(listOf<OptionData>()) }
    var selectedMethod by remember { mutableStateOf(DecisionMethod.RANDOM) }
    var currentDecisionResult by remember { mutableStateOf<String?>(null) }

    val showSnackbarMessage = { message: String ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val handleAttemptToRegister: (String, String, String, String) -> Unit = { emailAttempt, passwordAttempt, confirmPasswordAttempt, magicNameAttempt ->
        val normalizedEmail = emailAttempt.lowercase(Locale.ROOT)
        val normalizedMagicName = magicNameAttempt.trim()

        val message: String = if (normalizedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            "Inserisci un\'email valida."
        } else if (passwordAttempt.isBlank()) {
            "La password non può essere vuota."
        } else if (passwordAttempt.length < 6) {
            "La password deve contenere almeno 6 caratteri."
        } else if (passwordAttempt != confirmPasswordAttempt) {
            "Le password non corrispondono."
        } else if (normalizedMagicName.isBlank()) {
            "Il Nome Magico non può essere vuoto."
        } else if (registeredUsers.containsKey(normalizedEmail)) {
            "Questa email è già registrata."
        } else {
            val hashedPassword = sha256(passwordAttempt)
            val accountData = UserAccountData(hashedPassword, normalizedMagicName)
            val updatedUsers = registeredUsers + (normalizedEmail to accountData)
            saveRegisteredUsers(context, updatedUsers)
            registeredUsers = updatedUsers

            userEmail = normalizedEmail
            saveLoggedInUser(context, normalizedEmail)
            currentScreen = Screen.HOME
            "Registrazione completata, Nobile $normalizedMagicName!"
        }
        showSnackbarMessage(message)
    }

    val handleAttemptToLogin: (String, String) -> Unit = { emailAttempt, passwordAttempt ->
        val normalizedEmail = emailAttempt.lowercase(Locale.ROOT)

        val message: String = if (registeredUsers.isEmpty() && normalizedEmail.isNotBlank()) {
            "Nessun account trovato. Per favore, registrati."
        } else if (!registeredUsers.containsKey(normalizedEmail)) {
            "Email non trovata."
        } else {
            val hashedPasswordAttempt = sha256(passwordAttempt)
            if (registeredUsers[normalizedEmail]?.hashedPassword != hashedPasswordAttempt) {
                "Password errata."
            } else {
                userEmail = normalizedEmail
                saveLoggedInUser(context, normalizedEmail)
                currentScreen = Screen.HOME
                val magicName = registeredUsers[normalizedEmail]?.magicName ?: "Utente"
                "Accesso riuscito, Nobile $magicName!"
            }
        }
        showSnackbarMessage(message)
    }

    val handleOracleConsulted = {
        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            oracleConsultationsCount++
            saveOracleCount(context, userEmail, oracleConsultationsCount)
        }
    }

    val handleGuestLogin: () -> Unit = {
        userEmail = GUEST_USER_IDENTIFIER
        oracleConsultationsCount = 0
        clearLoggedInUser(context)
        currentScreen = Screen.HOME
        showSnackbarMessage("Accesso come ospite.")
    }

    val handleLogout: () -> Unit = {
        clearLoggedInUser(context)
        userEmail = ""
        decisions = listOf()
        currentOptions = listOf()
        selectedMethod = DecisionMethod.RANDOM
        currentDecisionResult = null
        oracleConsultationsCount = 0
        showLogoutDialog = false // AGGIUNTO: Chiude il dialogo dopo il logout effettivo
        navigateToInitialAuthScreen()
        showSnackbarMessage("Logout effettuato.")
    }

    val showNavBar = currentScreen in mainNavScreens && userEmail.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showNavBar) {
                AppNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { newScreen -> currentScreen = newScreen }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (showNavBar) {
                val homeScreenUserName = if (userEmail == GUEST_USER_IDENTIFIER) {
                    "Ospite"
                } else {
                    val magicName = registeredUsers[userEmail]?.magicName
                    if (!magicName.isNullOrBlank()) {
                        "Saluti, Nobile $magicName"
                    } else {
                        "Saluti, Nobile ${userEmail.substringBefore("@")}"
                    }
                }

                MainScreensContainer(
                    currentScreen = currentScreen,
                    userEmail = userEmail,
                    homeScreenUserName = homeScreenUserName,
                    registeredUsers = registeredUsers,
                    decisions = decisions,
                    oracleConsultationsCount = oracleConsultationsCount,
                    // Passaggio dei nuovi stati e callback per ProfileScreen
                    isDarkModeActual = isDarkMode,
                    onDarkModeChange = { isDarkMode = it },
                    soundEnabledActual = soundEnabled,
                    onSoundEnabledChange = { soundEnabled = it },
                    vibrationsEnabledActual = vibrationsEnabled,
                    onVibrationsEnabledChange = { vibrationsEnabled = it },
                    showLogoutDialogActual = showLogoutDialog,
                    onShowLogoutDialogChange = { showLogoutDialog = it },
                    onStartDecision = {
                        currentOptions = listOf()
                        selectedMethod = DecisionMethod.RANDOM
                        currentDecisionResult = null
                        currentScreen = Screen.OPTIONS_INPUT
                    },
                    onNavigateToResult = { decision ->
                        currentOptions = decision.options
                        selectedMethod = decision.method
                        currentDecisionResult = decision.result
                        currentScreen = Screen.RESULT
                    },
                    onDeleteDecision = { decisionToDelete ->
                        updateAndSaveDecisions(decisions.filter { it.id != decisionToDelete.id })
                    },
                    onLogout = handleLogout,
                    onBackFromSubScreen = { currentScreen = Screen.HOME },
                    onOracleConsulted = handleOracleConsulted
                )
            } else {
                NonMainScreensContainer(
                    currentScreen = currentScreen,
                    userEmail = userEmail,
                    decisions = decisions,
                    currentOptions = currentOptions,
                    selectedMethod = selectedMethod,
                    currentDecisionResult = currentDecisionResult,
                    onUpdateAndSaveDecisions = updateAndSaveDecisions,
                    onCurrentOptionsChange = { currentOptions = it },
                    onSelectedMethodChange = { selectedMethod = it },
                    onCurrentDecisionResultChange = { currentDecisionResult = it },
                    onNavigate = { newScreen ->
                        currentScreen = newScreen
                    },
                    onLoginAttempt = handleAttemptToLogin,
                    onRegisterAttempt = handleAttemptToRegister,
                    onGuestLogin = handleGuestLogin,
                    onNavigateToInitialAuth = navigateToInitialAuthScreen
                )
            }
        }
    }
}

@Composable
private fun MainScreensContainer(
    currentScreen: Screen,
    userEmail: String,
    homeScreenUserName: String,
    registeredUsers: Map<String, UserAccountData>,
    decisions: List<SavedDecision>,
    oracleConsultationsCount: Int,
    // Definizione dei nuovi parametri ricevuti da DecisionistaApp
    isDarkModeActual: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    soundEnabledActual: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    vibrationsEnabledActual: Boolean,
    onVibrationsEnabledChange: (Boolean) -> Unit,
    showLogoutDialogActual: Boolean,
    onShowLogoutDialogChange: (Boolean) -> Unit,

    onStartDecision: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    onDeleteDecision: (SavedDecision) -> Unit,
    onLogout: () -> Unit,
    onBackFromSubScreen: () -> Unit,
    onOracleConsulted: () -> Unit
) {
    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            userName = homeScreenUserName,
            decisionsMadeCount = decisions.size,
            oracleConsultationsCount = oracleConsultationsCount,
            recentDecisions = decisions.takeLast(3).reversed(),
            onStartDecision = onStartDecision,
            onNavigateToResult = onNavigateToResult
        )
        Screen.GLIMMERIO -> GlimmerioScreen(
            decisions = decisions,
            onDecisionSelect = onNavigateToResult,
            onDelete = onDeleteDecision,
            onBack = onBackFromSubScreen
        )
        Screen.ORACLE -> OracleScreen(
            onBack = onBackFromSubScreen,
            onNavigateToResult = onNavigateToResult,
            onOracleConsulted = onOracleConsulted
        )
        Screen.PROFILE -> {
            val profileDisplayName = if (userEmail == GUEST_USER_IDENTIFIER) "Ospite"
                                     else registeredUsers[userEmail]?.magicName ?: userEmail.substringBefore("@")
            ProfileScreen(
                userEmail = userEmail,
                magicName = profileDisplayName,
                onLogout = onLogout,
                onBack = onBackFromSubScreen,
                decisions = decisions,
                onNavigateToResult = onNavigateToResult,
                // Passaggio degli stati e callback a ProfileScreen
                isDarkModeActual = isDarkModeActual,
                onDarkModeChange = onDarkModeChange,
                soundEnabledActual = soundEnabledActual,
                onSoundEnabledChange = onSoundEnabledChange,
                vibrationsEnabledActual = vibrationsEnabledActual,
                onVibrationsEnabledChange = onVibrationsEnabledChange,
                showLogoutDialog = showLogoutDialogActual, // Corrisponde al parametro di ProfileScreen
                onShowLogoutDialogChange = onShowLogoutDialogChange
            )
        }
        else -> { /* No action */ }
    }
}

@Composable
private fun NonMainScreensContainer(
    currentScreen: Screen,
    userEmail: String,
    decisions: List<SavedDecision>,
    currentOptions: List<OptionData>,
    selectedMethod: DecisionMethod,
    currentDecisionResult: String?,
    onUpdateAndSaveDecisions: (List<SavedDecision>) -> Unit,
    onCurrentOptionsChange: (List<OptionData>) -> Unit,
    onSelectedMethodChange: (DecisionMethod) -> Unit,
    onCurrentDecisionResultChange: (String?) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLoginAttempt: (String, String) -> Unit,
    onRegisterAttempt: (String, String, String, String) -> Unit,
    onGuestLogin: () -> Unit,
    onNavigateToInitialAuth: () -> Unit
) {
    when (currentScreen) {
        Screen.SPLASH -> SplashScreen {
             if (currentScreen == Screen.SPLASH) {
                onNavigateToInitialAuth()
            }
        }
        Screen.LOGIN -> LoginScreen(
            onLogin = { emailVal, passwordVal -> onLoginAttempt(emailVal, passwordVal) },
            onRegister = { onNavigate(Screen.REGISTER) },
            onGuest = onGuestLogin
        )
        Screen.REGISTER -> RegisterScreen(
            onRegister = { emailVal, passwordVal, confirmPasswordVal, magicNameVal ->
                onRegisterAttempt(emailVal, passwordVal, confirmPasswordVal, magicNameVal)
            },
            onBack = { onNavigate(Screen.LOGIN) }
        )
        Screen.OPTIONS_INPUT -> OptionsInputScreen(
            options = currentOptions,
            onOptionsChange = onCurrentOptionsChange,
            onNext = { onNavigate(Screen.METHOD_SELECTION) },
            onBack = {
                if (userEmail.isNotBlank()) onNavigate(Screen.HOME) else onNavigateToInitialAuth()
            }
        )
        Screen.METHOD_SELECTION -> MethodSelectionScreen(
            selectedMethod = selectedMethod,
            onMethodSelect = onSelectedMethodChange,
            onLaunch = { onNavigate(Screen.RITUAL) },
            onBack = { onNavigate(Screen.OPTIONS_INPUT) }
        )
        Screen.RITUAL -> RitualScreen(
            method = selectedMethod,
            options = currentOptions,
            onComplete = { ritualResult ->
                onCurrentDecisionResultChange(ritualResult)
                val newDecision = SavedDecision(
                    id = (decisions.maxOfOrNull { it.id } ?: 0) + 1,
                    options = currentOptions,
                    result = ritualResult,
                    method = selectedMethod,
                    timestamp = System.currentTimeMillis()
                )
                onUpdateAndSaveDecisions(decisions + newDecision)
                onNavigate(Screen.RESULT)
            }
        )
        Screen.RESULT -> ResultScreen(
            result = currentDecisionResult ?: "Errore: Nessun risultato deciso",
            method = selectedMethod,
            options = currentOptions,
            onRetry = {
                onCurrentDecisionResultChange(null)
                onNavigate(Screen.METHOD_SELECTION)
            },
            onGoHome = {
                onNavigate(Screen.HOME)
            }
        )
        else -> { /* No action for screens handled by MainScreensContainer */ }
    }
}

