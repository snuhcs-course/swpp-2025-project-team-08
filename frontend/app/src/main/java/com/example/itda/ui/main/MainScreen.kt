package com.example.itda.ui.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.feed.FeedScreen
import com.example.itda.ui.home.HomeScreen
import com.example.itda.ui.navigation.BottomNavBar
import com.example.itda.ui.notification.NotificationScreen
import com.example.itda.ui.profile.ProfileScreen
import com.example.itda.ui.search.SearchScreen

// mainscreen 에서 login 여부 보고 분기 -> homescreen / loginscreen

@Composable
fun MainScreen(
    authViewModel: AuthViewModel
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) } // ✅ 여기서 BottomNavBar 포함
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(
                onFeedClick = { feedId ->
                    navController.navigate("feed/$feedId")
                    Log.d("MainScreen", "Feed ID: $feedId")
                }
            ) }
            composable("search") { SearchScreen() }
            composable("notification") { NotificationScreen() }
            composable("profile") { ProfileScreen(
                authViewModel = authViewModel
            ) }


            composable(
                route = "feed/{feedId}"
            ) { backStackEntry ->
                val feedId = backStackEntry.arguments?.getString("feedId")?.toIntOrNull()
                if (feedId != null) {
                    FeedScreen(
                        feedId = feedId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}