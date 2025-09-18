package com.example.decisionista.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.decisionista.app.ui.SplashScreen
import com.decisionista.app.ui.screens.AuthScreen
import com.example.decisionista.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("saved") { SavedScreen(navController) }
        composable("group") { GroupScreen(navController) }
        composable("result") { ResultScreen(navController) }
        composable("splash") { SplashScreen(navController) }
    }
}