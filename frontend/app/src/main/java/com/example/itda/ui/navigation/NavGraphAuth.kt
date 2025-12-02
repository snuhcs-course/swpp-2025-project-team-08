package com.example.itda.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.auth.LoginRoute
import com.example.itda.ui.auth.OnBoardingRoute
import com.example.itda.ui.auth.PersonalInfoRoute
import com.example.itda.ui.auth.SignUpRoute

fun NavGraphBuilder.authGraph(
    navController: NavController,
) {
    navigation(
        startDestination = "login",
        route = "auth_graph"
    ) {
        composable("login") {
            LoginRoute(
                onSignUpClick = {
                    navController.navigate("signup")
                },
                onLoginSuccess = {
                    navController.navigate("main_graph") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignUpRoute(
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate("personal_info") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("personal_info") {
            PersonalInfoRoute(
                onComplete = {
                    navController.navigate("onboarding") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnBoardingRoute(
                onComplete = {
                    navController.navigate("main_graph") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                })
        }
    }
}