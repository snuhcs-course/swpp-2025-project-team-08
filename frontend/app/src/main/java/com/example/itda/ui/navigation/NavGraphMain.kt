package com.example.itda.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.main.MainScreen
import com.example.itda.ui.profile.PersonalInfoScreen
import com.example.itda.ui.profile.PersonalInfoViewModel
import com.example.itda.ui.profile.ProfileScreen
import com.example.itda.ui.profile.ProfileViewModel
import com.example.itda.ui.profile.SettingScreen
import com.example.itda.ui.profile.SettingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


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

        composable("profile") {
            val viewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("settings") {
            val viewModel: SettingViewModel = viewModel()
            SettingScreen(
                viewModel = viewModel,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("personal_info") {
            val viewModel: PersonalInfoViewModel = viewModel()
            PersonalInfoScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}