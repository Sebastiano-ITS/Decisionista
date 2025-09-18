package com.example.decisionista


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.decisionista.ui.MainViewModel
import com.example.decisionista.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.decisionista.app.ui.SplashScreen
import com.decisionista.app.ui.screens.AuthScreen
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aggiungi qui il tuo tema
            MaterialTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val currentUser by mainViewModel.currentUser.collectAsState()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val bottomNavDestinations = listOf(
                Screen.Home,
                Screen.Glimmerio,
                Screen.Oracolo,
                Screen.Profile
            )
            val showBottomBar = currentUser != null && currentDestination?.route in bottomNavDestinations.map { it.route }

            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFFFFFFFF),
                ) {
                    bottomNavDestinations.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.name) },
                            label = { Text(screen.name) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4B0082),
                                selectedTextColor = Color(0xFF4B0082),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (currentUser != null) "home" else "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("auth") { AuthScreen(navController = navController) }
            composable(Screen.Home.route) { HomeScreen(navController = navController) }
            composable(Screen.Glimmerio.route) { GlimmerioScreen(navController = navController) }
            composable(Screen.Oracolo.route) { OracoloScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        }
    }
}

sealed class Screen(val route: String, val name: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Glimmerio : Screen("glimmerio", "Glimmerio", Icons.Default.Star)
    object Oracolo : Screen("oracolo", "Oracolo", Icons.Default.Person)
    object Profile : Screen("profile", "Profilo", Icons.Default.Person)
}