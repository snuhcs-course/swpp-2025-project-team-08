package com.example.itda.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.itda.ui.auth.AuthViewModel

@Composable
fun AppNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val navController: NavHostController = rememberNavController()

    val isLoadingInitial by authViewModel.isLoadingInitial.collectAsState()
    if (isLoadingInitial) {
        SplashScreen()
        return
    }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState() // 로그인 여부

    val startDestination: String =
        if (isLoggedIn) "main_graph" else "auth_graph" // 로그인 되어있으면 MainScreen, 안되어있으면 AuthScreen

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)
        mainGraph(navController, authViewModel)
    }
}