package com.example.itda.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.bookmark.BookmarkRoute
import com.example.itda.ui.feed.FeedRoute
import com.example.itda.ui.feed.FeedViewModel
import com.example.itda.ui.home.HomeRoute
import com.example.itda.ui.home.HomeViewModel
import com.example.itda.ui.profile.PersonalInfoRoute
import com.example.itda.ui.profile.ProfileRoute
import com.example.itda.ui.profile.SettingsRoute
import com.example.itda.ui.profile.component.settingNavGraph
import com.example.itda.ui.search.SearchRoute
import com.example.itda.ui.search.SearchViewModel

// Bottom Navigation 탭의 경로 목록을 정의합니다.
private val MainTabRoutes = listOf(
    "home", "search", "bookmark", "profile"
)

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = MainTabRoutes.first(), // "home"
        route = "main_graph"
    ) {
        // 메인 탭 화면
        MainTabRoutes.forEach { route ->
            composable(route) { backStackEntry ->
                // MainScaffoldWrapper로 감싸서 BottomBar와 Padding을 제공
                MainScaffoldWrapper(navController = navController) { innerPadding ->
                    when (route) {
                        "home" -> {
                            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)

                            // BottomNavBar에서 보낸 "refresh_home" 신호를 감지합니다.
                            val refreshTrigger = backStackEntry.savedStateHandle
                                .getLiveData<Boolean>("refresh_home")
                                .observeAsState()

                            // 신호가 true가 되면 데이터 새로고침을 실행합니다.
                            LaunchedEffect(refreshTrigger.value) {
                                if (refreshTrigger.value == true) {
                                    homeViewModel.refreshHomeData()
                                    // 신호를 소비했으므로 다시 false로 돌려놓거나 제거합니다.
                                    backStackEntry.savedStateHandle["refresh_home"] = false
                                }
                            }
                            // 북마크 변경 결과 감지 및 처리
                            LaunchedEffect(backStackEntry) {
                                // Pair<Int, Boolean> 형태의 데이터를 관찰합니다. (ID, 최종 상태)
                                backStackEntry.savedStateHandle.getLiveData<Pair<Int, Boolean>>("bookmark_change_info").observe(
                                    backStackEntry
                                ) { info ->
                                    if (info != null) {
                                        val (id, isBookmarked) = info
                                        homeViewModel.updateBookmarkStatusInList(id, isBookmarked)
                                        backStackEntry.savedStateHandle.remove<Pair<Int, Boolean>>("bookmark_change_info")
                                    }
                                }
                            }

                            HomeRoute(
                                onFeedClick = { feedId -> navController.navigate("feed/$feedId") },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "search" -> {
                            val searchViewModel: SearchViewModel = hiltViewModel(backStackEntry)

                            // 북마크 변경 결과 감지 및 처리
                            LaunchedEffect(backStackEntry) {
                                // Pair<Int, Boolean> 형태 데이터 관찰 (ID, 최종 상태)
                                backStackEntry.savedStateHandle.getLiveData<Pair<Int, Boolean>>("bookmark_change_info").observe(
                                    backStackEntry
                                ) { info ->
                                    if (info != null) {
                                        val (id, isBookmarked) = info
                                        searchViewModel.updateBookmarkStatusInList(id, isBookmarked)
                                        backStackEntry.savedStateHandle.remove<Pair<Int, Boolean>>("bookmark_change_info")
                                    }
                                }
                            }
                            SearchRoute(
                                onFeedClick = { feedId -> navController.navigate("feed/$feedId") },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "bookmark" -> BookmarkRoute(
                            onFeedClick = { feedId -> navController.navigate("feed/$feedId") },
                            modifier = Modifier.padding(innerPadding)
                         )
                        "profile" -> ProfileRoute(
                            onSettingClick = { navController.navigate("settings") },
                            onPersonalInfoClick = { navController.navigate("personal_info") },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }

        // Bottom Bar가 없는 상세 화면 정의
        composable(
            route = "feed/{feedId}",
            arguments = listOf(navArgument("feedId") { type = NavType.IntType })
        ) { backStackEntry ->
            val feedId = backStackEntry.arguments?.getInt("feedId")
            val feedViewModel: FeedViewModel = hiltViewModel(backStackEntry)
            if (feedId != null) {
                FeedRoute(
                    feedId = feedId,
                    onBack = {
                        if (feedViewModel.hasBookmarkChanged.value) {
                            val bookmarkInfo = feedViewModel.savedStateHandle.get<Pair<Int, Boolean>>("bookmark_change_info")

                            if (bookmarkInfo != null) {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("bookmark_change_info", bookmarkInfo)
                            }

                        }
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("settings") {
            SettingsRoute(
                onBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate("auth_graph") {
                        popUpTo("main_graph") { inclusive = true }
                    }
                },
                onNavigateToDestination = { destination ->
                    navController.navigate(destination.route)
                }
            )
        }

        composable("personal_info") {
            PersonalInfoRoute(
                onBack = { navController.popBackStack() },
                onComplete = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("profile_refresh", true)
                    navController.popBackStack()
                }
            )
        }
        settingNavGraph(
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun MainScaffoldWrapper(
    navController: NavController,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        content(innerPadding)
    }
}