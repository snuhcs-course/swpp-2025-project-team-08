package com.example.itda.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.main.MainScreen


fun NavGraphBuilder.mainGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "main_screen",
        route = "main_graph"
    ) {
        composable("main_screen") {
            MainScreen(authViewModel = authViewModel)
        }
    }
}