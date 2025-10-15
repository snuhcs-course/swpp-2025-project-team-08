package com.example.itda.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.itda.ui.common.theme.Neutral10
import com.example.itda.ui.common.theme.Neutral100
import com.example.itda.ui.common.theme.Primary20
import com.example.itda.ui.common.theme.Primary40

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Notification : BottomNavItem("notification", Icons.Default.Notifications, "Notification")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Notification,
        BottomNavItem.Profile
    )
    NavigationBar (
        containerColor = Neutral100,
        tonalElevation = 5.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination.isCurrentRoute(item.route),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    // 선택된 아이콘/텍스트 색상
                    selectedIconColor = Neutral100,
                    selectedTextColor = Primary20,

                    // 선택되지 않은 아이콘/텍스트 색상
                    unselectedIconColor = Neutral10,
                    unselectedTextColor = Neutral10,

                    // 아이템 배경에 표시되는 물결 효과의 배경색 (투명하게 하려면 투명색 사용)
                    indicatorColor = Primary40
                ),
            )
        }
    }
}

private fun NavDestination?.isCurrentRoute(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
