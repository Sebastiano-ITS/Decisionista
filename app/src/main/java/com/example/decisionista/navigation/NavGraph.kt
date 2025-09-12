package com.example.decisionista.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.decisionista.ui.screens.SplashScreen
import com.example.decisionista.ui.screens.HomeScreen
import com.example.decisionista.ui.screens.ResultScreen
import com.example.decisionista.ui.screens.SavedScreen
import com.example.decisionista.ui.screens.GroupScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("result") { ResultScreen(navController) }
        composable("saved") { SavedScreen(navController) }
        composable("group") { GroupScreen(navController) }
    }
}
