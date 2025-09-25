// MainActivity.kt
package com.example.decisionista

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MenuBook // NUOVA ICONA
import androidx.compose.material.icons.filled.AutoAwesome // NUOVA ICONA
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit // For SharedPreferences KTX
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit as dsEdit // Alias to avoid conflict
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
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
import com.example.decisionista.ui.theme.DecisionistaAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import java.util.Locale

// GUEST_USER_IDENTIFIER is used by MainActivity to identify guest sessions.
const val GUEST_USER_IDENTIFIER = "_decisionista_guest_user_"

// SharedPreferences constants (for user accounts and oracle count)
private const val PREFS_NAME = "DecisionistaUserPrefs"
private const val KEY_REGISTERED_USERS_STRING_V2 = "registered_users_map_v2"
private const val KEY_ORACLE_COUNT_PREFIX = "oracle_count_"
private const val KEY_LOGGED_IN_USER_EMAIL = "logged_in_user_email"
private const val ENTRY_DELIMITER = "##USER_ENTRY##"
private const val KV_DELIMITER_PASS = "##EMAIL_PASS_KV##"
private const val KV_DELIMITER_MAGIC_NAME = "##MAGIC_NAME_KV##"

// DataStore constants (for theme settings)
private const val THEME_PREFERENCES_NAME = "decisionista_theme_prefs"
private val Context.themeSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = THEME_PREFERENCES_NAME)
private val DARK_MODE_ENABLED_KEY = booleanPreferencesKey("dark_mode_enabled")

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
    prefs.edit { 
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
// --- End SharedPreferences Utility Functions ---

// --- Utility Functions for Theme DataStore ---
fun observeDarkModeSetting(context: Context): Flow<Boolean> {
    return context.themeSettingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DARK_MODE_ENABLED_KEY] ?: true // Default to dark mode if no preference set
        }
}

suspend fun saveDarkModeSetting(context: Context, isDarkMode: Boolean) {
    context.themeSettingsDataStore.dsEdit { settings -> // Using alias dsEdit
        settings[DARK_MODE_ENABLED_KEY] = isDarkMode
    }
}
// --- End Theme DataStore Utility Functions ---


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DecisionistaApp()
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
                Screen.GLIMMERIO -> Icons.Filled.MenuBook // ICONA MODIFICATA
                Screen.ORACLE -> Icons.Filled.AutoAwesome // ICONA MODIFICATA
                Screen.PROFILE -> Icons.Filled.Person
                else -> Icons.Filled.Home // Default icon
            }
            val label = when (screen) {
                Screen.HOME -> "Home"
                Screen.GLIMMERIO -> "Glimmerio"
                Screen.ORACLE -> "Oracolo"
                Screen.PROFILE -> "Profilo"
                else -> ""
            }

            val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1.0f, label = "navItemScale")
            val alpha by animateFloatAsState(targetValue = if (selected) 1f else 0.7f, label = "navItemAlpha")

            NavigationBarItem(
                icon = {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
                    )
                },
                label = {
                    Text(
                        label,
                        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
                    )
                },
                selected = selected,
                onClick = { onNavigate(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent // Make default indicator transparent
                ),
                alwaysShowLabel = true
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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val systemIsInDarkTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemIsInDarkTheme) }

    LaunchedEffect(key1 = context) {
        observeDarkModeSetting(context).collect { darkModeFromStore ->
            isDarkMode = darkModeFromStore
        }
    }

    val onActualDarkModeChange: (Boolean) -> Unit = { newSetting ->
        isDarkMode = newSetting
        coroutineScope.launch {
            saveDarkModeSetting(context, newSetting)
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) } 
    var showTermsDialog by remember { mutableStateOf(false) } 
    var showResetDialog by remember { mutableStateOf(false) } // NUOVO: Stato per il dialogo azzeramento dati

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        registeredUsers = loadRegisteredUsers(context)
        val loggedInEmail = loadLoggedInUser(context)
        if (!loggedInEmail.isNullOrBlank() && registeredUsers.containsKey(loggedInEmail)) {
            userEmail = loggedInEmail
            // currentScreen = Screen.HOME // Splash screen will handle this if user is logged in
        } else {
            clearLoggedInUser(context)
            // if (currentScreen != Screen.SPLASH) currentScreen = Screen.LOGIN // Splash will handle this
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
    
    LaunchedEffect(userEmail, currentScreen) {
        if (currentScreen == Screen.SPLASH && userEmail.isNotBlank()) {
            currentScreen = Screen.HOME
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
    
    // NUOVO: Handler per la conferma dell'azzeramento dati
    val handleResetDataConfirm: () -> Unit = {
        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            decisions = listOf()
            coroutineScope.launch {
                DecisionRepository.saveDecisions(context, userEmail, listOf())
            }
            oracleConsultationsCount = 0
            saveOracleCount(context, userEmail, 0)
            showSnackbarMessage("Cronologia decisioni e conteggio Oracolo azzerati.")
        }
        showResetDialog = false // Chiudi il dialogo indipendentemente da successo/fallimento interno
    }

    val handleAttemptToRegister: (String, String, String, String) -> Unit = { emailAttempt, passwordAttempt, confirmPasswordAttempt, magicNameAttempt ->
        val normalizedEmail = emailAttempt.lowercase(Locale.ROOT)
        val normalizedMagicName = magicNameAttempt.trim()

        val message: String = if (normalizedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
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

    val handleAttemptToLogin: (String, String) -> Unit = { identifierAttempt, passwordAttempt ->
        // identifierAttempt può essere un'email o un Nome Magico
        val normalizedIdentifier = identifierAttempt.lowercase(Locale.ROOT).trim()

        var actualUserEmail: String? = null
        var userAccountData: UserAccountData? = null

        // 1. Prova a trovare l'utente per email (se l'identifier ha un formato email valido)
        if (Patterns.EMAIL_ADDRESS.matcher(normalizedIdentifier).matches() && registeredUsers.containsKey(normalizedIdentifier)) {
            actualUserEmail = normalizedIdentifier
            userAccountData = registeredUsers[actualUserEmail]
        } else {
            // 2. Se non è un'email valida o non trovato per email, prova per Nome Magico (case-insensitive e trimmato)
            // Questo blocco else viene eseguito se l'identifier non è un email valida OPPURE se è un email valida ma non trovata come chiave.
            // In entrambi i casi, tentiamo la ricerca per Nome Magico.
            for ((emailKey, accData) in registeredUsers) {
                if (accData.magicName.lowercase(Locale.ROOT).trim() == normalizedIdentifier) {
                    actualUserEmail = emailKey // Trovato! actualUserEmail è l'email reale dell'utente
                    userAccountData = accData
                    break
                }
            }
        }

        val message: String = if (registeredUsers.isEmpty() && normalizedIdentifier.isNotBlank()) {
            "Nessun account trovato. Per favore, registrati."
        } else if (actualUserEmail == null || userAccountData == null) {
            "Credenziali non valide. Controlla l'Email/Nome Magico."
        } else {
            val hashedPasswordAttempt = sha256(passwordAttempt)
            if (userAccountData.hashedPassword != hashedPasswordAttempt) {
                "Password Magica errata."
            } else {
                // Login effettuato con successo
                userEmail = actualUserEmail // Usa l'email reale dell'utente per lo stato interno
                saveLoggedInUser(context, actualUserEmail)
                currentScreen = Screen.HOME
                val magicNameToDisplay = userAccountData.magicName // Usa il Nome Magico per il saluto
                "Accesso riuscito, Nobile $magicNameToDisplay!"
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
        showLogoutDialog = false
        navigateToInitialAuthScreen()
        showSnackbarMessage("Logout effettuato.")
    }

    val showNavBar = currentScreen in mainNavScreens && userEmail.isNotBlank()

    DecisionistaAppTheme(useDarkTheme = isDarkMode) {
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
                        isDarkModeActual = isDarkMode,
                        onDarkModeChange = onActualDarkModeChange,
                        showLogoutDialogActual = showLogoutDialog, 
                        onShowLogoutDialogChange = { showLogoutDialog = it },
                        showTermsDialogActual = showTermsDialog,
                        onShowTermsDialogChange = { showTermsDialog = it },
                        showResetDialogActual = showResetDialog, // NUOVO
                        onShowResetDialogChange = { showResetDialog = it }, // NUOVO
                        onResetDataConfirmActual = handleResetDataConfirm, // NUOVO
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
}

@Composable
private fun MainScreensContainer(
    currentScreen: Screen,
    userEmail: String,
    homeScreenUserName: String,
    registeredUsers: Map<String, UserAccountData>,
    decisions: List<SavedDecision>,
    oracleConsultationsCount: Int,
    isDarkModeActual: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    showLogoutDialogActual: Boolean,
    onShowLogoutDialogChange: (Boolean) -> Unit,
    showTermsDialogActual: Boolean,
    onShowTermsDialogChange: (Boolean) -> Unit,
    showResetDialogActual: Boolean, // NUOVO
    onShowResetDialogChange: (Boolean) -> Unit, // NUOVO
    onResetDataConfirmActual: () -> Unit, // NUOVO
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
            onNavigateToResult = onNavigateToResult, // Consider removal if not used by OracleScreen
            onOracleConsulted = onOracleConsulted 
        )
        Screen.PROFILE -> {
            val profileDisplayName = if (userEmail == GUEST_USER_IDENTIFIER) "Ospite" 
                                     else registeredUsers[userEmail]?.magicName ?: userEmail.substringBefore("@")
            ProfileScreen(
                userEmail = if (userEmail == GUEST_USER_IDENTIFIER) "" else userEmail,
                magicName = profileDisplayName,
                onLogout = onLogout,
                onBack = onBackFromSubScreen,
                decisions = decisions, 
                onNavigateToResult = onNavigateToResult,
                isDarkModeActual = isDarkModeActual,
                onDarkModeChange = onDarkModeChange,
                showLogoutDialog = showLogoutDialogActual, 
                onShowLogoutDialogChange = onShowLogoutDialogChange,
                showTermsDialog = showTermsDialogActual, 
                onShowTermsDialogChange = onShowTermsDialogChange,
                showResetDialog = showResetDialogActual, // NUOVO
                onShowResetDialogChange = onShowResetDialogChange, // NUOVO
                onResetDataConfirm = onResetDataConfirmActual // NUOVO
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
                if (userEmail.isNotBlank()) {
                    onNavigate(Screen.HOME)
                } else {
                    onNavigateToInitialAuth()
                }
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
            },
            onNavigateToHome = { onNavigate(Screen.HOME) } 
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
