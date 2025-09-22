// MainActivity.kt
package com.example.decisionista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

// GUEST_USER_IDENTIFIER is used by MainActivity to identify guest sessions.
// DecisionRepository checks against this exact string to prevent persistence for guests.
const val GUEST_USER_IDENTIFIER = "_decisionista_guest_user_"
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
                else -> Icons.Filled.Home // Default icon
            }
            val label = when (screen) {
                Screen.HOME -> "Home"
                Screen.GLIMMERIO -> "Glimmerio"
                Screen.ORACLE -> "Oracolo"
                Screen.PROFILE -> "Profilo"
                else -> "" // Default label
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
    var userEmail by remember { mutableStateOf("") } // Blank means no user logged in

    val context = LocalContext.current
    var decisions by remember { mutableStateOf(listOf<SavedDecision>()) }
    val coroutineScope = rememberCoroutineScope()

    val mainNavScreens = listOf(Screen.HOME, Screen.GLIMMERIO, Screen.ORACLE, Screen.PROFILE)

    // Effect to load decisions when userEmail changes or context is available
    LaunchedEffect(userEmail, context) {
        decisions = if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            DecisionRepository.loadDecisions(context, userEmail)
        } else {
            listOf() // Clear decisions for guests or logged-out users
        }
    }

    // Navigation Guard: Prevents access to main screens if not authenticated
    LaunchedEffect(currentScreen, userEmail) {
        val isTryingToAccessMainScreenWithoutAuth = 
            currentScreen in mainNavScreens && userEmail.isBlank() // isBlank covers not GUEST_USER_IDENTIFIER as well
        
        if (isTryingToAccessMainScreenWithoutAuth && currentScreen != Screen.LOGIN) { // also check not already going to LOGIN
            currentScreen = Screen.LOGIN
        }
    }
    
    val updateAndSaveDecisions = { updatedDecisionsList: List<SavedDecision> ->
        decisions = updatedDecisionsList // Always update in-memory list first
        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
            coroutineScope.launch { 
                DecisionRepository.saveDecisions(context, userEmail, updatedDecisionsList)
            }
        }
    }

    var currentOptions by remember { mutableStateOf(listOf<OptionData>()) }
    var selectedMethod by remember { mutableStateOf(DecisionMethod.RANDOM) }
    var currentDecisionResult by remember { mutableStateOf<String?>(null) }

    val showNavBar = currentScreen in mainNavScreens && (userEmail.isNotBlank() || userEmail == GUEST_USER_IDENTIFIER)

    if (showNavBar) {
        Scaffold(
            bottomBar = {
                AppNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { newScreen -> currentScreen = newScreen }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            MainScreensContainer(
                currentScreen = currentScreen,
                contentPadding = innerPadding,
                userEmail = userEmail,
                decisions = decisions,
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
                onLogout = {
                    userEmail = "" 
                    currentOptions = listOf()
                    selectedMethod = DecisionMethod.RANDOM
                    currentDecisionResult = null
                    currentScreen = Screen.LOGIN
                },
                onBackFromSubScreen = { currentScreen = Screen.HOME } // Consistent back navigation
            )
        }
    } else {
        NonMainScreensContainer(
            currentScreen = currentScreen,
            userEmail = userEmail, // Pass userEmail here
            decisions = decisions, 
            currentOptions = currentOptions,
            selectedMethod = selectedMethod,
            currentDecisionResult = currentDecisionResult,
            onUpdateAndSaveDecisions = updateAndSaveDecisions,
            onCurrentOptionsChange = { currentOptions = it },
            onSelectedMethodChange = { selectedMethod = it },
            onCurrentDecisionResultChange = { currentDecisionResult = it },
            onNavigate = { newScreen -> currentScreen = newScreen },
            onLoginSuccess = { loggedInEmail ->
                userEmail = loggedInEmail
                currentScreen = Screen.HOME
            },
            onRegisterSuccess = { registeredEmail ->
                userEmail = registeredEmail
                currentScreen = Screen.HOME
            },
             onGuestLogin = {
                userEmail = GUEST_USER_IDENTIFIER 
                currentScreen = Screen.HOME
            }
        )
    }
}

@Composable
fun MainScreensContainer(
    currentScreen: Screen,
    contentPadding: PaddingValues,
    userEmail: String,
    decisions: List<SavedDecision>,
    onStartDecision: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    onDeleteDecision: (SavedDecision) -> Unit,
    onLogout: () -> Unit,
    onBackFromSubScreen: () -> Unit
) {
    // This container should only be reached if userEmail is valid (registered or guest)
    // The navigation guard in DecisionistaApp should prevent userEmail.isBlank() here.
    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            modifier = Modifier.padding(contentPadding),
            userName = if (userEmail == GUEST_USER_IDENTIFIER) "Ospite" else userEmail, // Simplified due to guard
            decisionsMadeCount = decisions.size,
            oracleConsultationsCount = 0, // Placeholder, implement if needed
            recentDecisions = decisions.takeLast(3).reversed(),
            onStartDecision = onStartDecision,
            onNavigateToResult = onNavigateToResult
        )
        Screen.GLIMMERIO -> GlimmerioScreen(
            modifier = Modifier.padding(contentPadding),
            decisions = decisions,
            onDecisionSelect = onNavigateToResult,
            onDelete = onDeleteDecision,
            onBack = onBackFromSubScreen
        )
        Screen.ORACLE -> OracleScreen(
            modifier = Modifier.padding(contentPadding),
            onBack = onBackFromSubScreen,
            onNavigateToResult = onNavigateToResult // Assuming Oracle might lead to a past result
        )
        Screen.PROFILE -> ProfileScreen(
            modifier = Modifier.padding(contentPadding),
            userEmail = if (userEmail == GUEST_USER_IDENTIFIER) "Ospite" else userEmail, // Simplified
            onLogout = onLogout,
            onBack = onBackFromSubScreen,
            decisions = decisions,
            onNavigateToResult = onNavigateToResult
        )
        else -> { /* Exhaustive when: No action needed for other screens handled by NonMainScreensContainer */ }
    }
}

@Composable
fun NonMainScreensContainer(
    currentScreen: Screen,
    userEmail: String, // Added userEmail parameter
    decisions: List<SavedDecision>,
    currentOptions: List<OptionData>,
    selectedMethod: DecisionMethod,
    currentDecisionResult: String?,
    onUpdateAndSaveDecisions: (List<SavedDecision>) -> Unit,
    onCurrentOptionsChange: (List<OptionData>) -> Unit,
    onSelectedMethodChange: (DecisionMethod) -> Unit,
    onCurrentDecisionResultChange: (String?) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLoginSuccess: (String) -> Unit,
    onRegisterSuccess: (String) -> Unit,
    onGuestLogin: () -> Unit
) {
    when (currentScreen) {
        Screen.SPLASH -> SplashScreen {
            onNavigate(Screen.LOGIN) 
        }
        Screen.LOGIN -> LoginScreen(
            onLogin = onLoginSuccess,
            onRegister = { onNavigate(Screen.REGISTER) },
            onGuest = onGuestLogin
        )
        Screen.REGISTER -> RegisterScreen(
            onRegister = onRegisterSuccess,
            onBack = { onNavigate(Screen.LOGIN) }
        )
        Screen.OPTIONS_INPUT -> OptionsInputScreen(
            options = currentOptions,
            onOptionsChange = onCurrentOptionsChange,
            onNext = { onNavigate(Screen.METHOD_SELECTION) },
            onBack = { 
                // Determine intelligent back navigation for OptionsInput
                // If coming from HOME, go HOME. If from Profile/Glimmerio, might need specific logic
                // For now, simple back to HOME if user is authenticated, else LOGIN
                if (userEmail.isNotBlank()) onNavigate(Screen.HOME) else onNavigate(Screen.LOGIN)
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
                onUpdateAndSaveDecisions(decisions + newDecision) // Save decision on ritual completion
                onNavigate(Screen.RESULT)
            }
        )
        Screen.RESULT -> ResultScreen(
            result = currentDecisionResult ?: "Errore: Nessun risultato deciso",
            method = selectedMethod,
            options = currentOptions,
            onRetry = {
                onCurrentDecisionResultChange(null) 
                onNavigate(Screen.METHOD_SELECTION) // Or Screen.RITUAL for a direct retry of the same options
            },
            onGoHome = { // Simplified callback
                onNavigate(Screen.HOME)
            }
        )
        else -> { /* Exhaustive when: No action needed for other screens handled by MainScreensContainer */ }
    }
}
