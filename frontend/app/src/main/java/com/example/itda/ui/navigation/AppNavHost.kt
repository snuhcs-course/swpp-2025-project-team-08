package com.example.itda.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "auth_graph"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)
        mainGraph(navController)
    }
}