package com.example.itda.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.itda.ui.auth.LoginScreen
import com.example.itda.ui.auth.PersonalInfoScreen
import com.example.itda.ui.auth.SignUpScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        startDestination = "login",
        route = "auth_graph"
    ) {
        composable("login") {
            LoginScreen(
//                onLoginSuccess = {
//                    navController.navigate("main_graph") {
//                        popUpTo("auth_graph") { inclusive = true }
//                    }
//                },
//                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
//                onNext = { navController.navigate("personal_info") },
//                onBack = { navController.popBackStack() }
            )
        }
        composable("personal_info") {
            PersonalInfoScreen(
//                onFinish = {
//                    navController.navigate("main_graph") {
//                        popUpTo("auth_graph") { inclusive = true }
//                    }
//                }
            )
        }
    }
}