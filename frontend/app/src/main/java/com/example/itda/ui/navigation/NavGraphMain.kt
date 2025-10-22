package com.example.itda.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.feed.FeedRoute
import com.example.itda.ui.home.HomeRoute
import com.example.itda.ui.notification.NotificationScreen
import com.example.itda.ui.profile.ProfileScreen
import com.example.itda.ui.search.SearchScreen

// Bottom Navigation 탭의 경로 목록을 정의합니다.
private val MainTabRoutes = listOf(
    "home", "search", "notification", "profile"
)

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = MainTabRoutes.first(), // "home"
        route = "main_graph"
    ) {
        // 1. 메인 탭 화면들 정의
        MainTabRoutes.forEach { route ->
            composable(route) {
                // MainScaffoldWrapper로 감싸서 BottomBar와 Padding을 제공
                MainScaffoldWrapper(navController = navController) { innerPadding ->
                    when (route) {
                        "home" -> HomeRoute(
                            onFeedClick = { feedId ->
                                // 상세 화면은 Wrapper 없이 바로 이동
                                navController.navigate("feed/$feedId")
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "search" -> SearchScreen(
                            //modifier = Modifier.padding(innerPadding)
                         )
                        "notification" -> NotificationScreen(
                            //modifier = Modifier.padding(innerPadding)
                         )
                        "profile" -> ProfileScreen( // 🌟 ProfileRoute로 감싸는 것이 이상적
                            // modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }

        // 2. Bottom Bar가 없는 상세 화면 정의
        composable(
            route = "feed/{feedId}",
            arguments = listOf(navArgument("feedId") { type = NavType.IntType })
        ) { backStackEntry ->
            val feedId = backStackEntry.arguments?.getInt("feedId")
            if (feedId != null) {
                // 이 화면은 Wrapper로 감싸지 않아 BottomBar가 보이지 않습니다.
                FeedRoute(
                    feedId = feedId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

//        composable("profile") {
//            val viewModel: ProfileViewModel = viewModel()
//            ProfileScreen(
//                viewModel = viewModel,
//                navController = navController
//            )
//        }
//
//        composable("settings") {
//            val viewModel: SettingViewModel = viewModel()
//            SettingScreen(
//                viewModel = viewModel,
//                navController = navController,
//                authViewModel = authViewModel
//            )
//        }
//
//        composable("personal_info") {
//            val viewModel: PersonalInfoViewModel = viewModel()
//            PersonalInfoScreen(
//                viewModel = viewModel,
//                navController = navController
//            )
//        }
    }
}

@Composable
fun MainScaffoldWrapper(
    navController: NavController,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    // 현재 경로를 확인하여 Bottom Bar를 숨길지 결정하는 로직을 추가할 수 있습니다.
    // 하지만, NavGraphMain에서 Scaffold Wrapper를 사용할 때,
    // BottomBar가 필요 없는 경로는 Wrapper를 사용하지 않도록 설계하는 것이 더 깔끔합니다.
    // 여기서는 BottomNavBar가 필요한 탭 화면을 감싸는 역할만 수행합니다.

}