package com.example.itda.ui.profile.component


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.itda.ui.profile.SettingScreen
import com.example.itda.ui.profile.SettingsViewModel

fun NavGraphBuilder.settingNavGraph(
    navController: NavController
) {
    // 설정 메인 화면
    composable(route = "setting") {
        val viewModel: SettingsViewModel = hiltViewModel()
        val uiState by viewModel.settingsUi.collectAsState()

        SettingScreen(
            ui = uiState,
            onBack = { navController.popBackStack() },
            onNavigateToDestination = { destination ->
                navController.navigate(destination.route)
            },
            toggleDarkMode = viewModel::toggleDarkMode,
            toggleAlarm = viewModel::toggleAlarm,
            onLogout = {
                viewModel.logout()
                // 로그아웃 후 로그인 화면으로 이동
                navController.navigate("login") {
                    popUpTo("setting") { inclusive = true }
                }
            }
        )
    }

    // 공지사항
    composable(route = SettingDestination.Notice.route) {
        DefaultScreen(
            title = "공지사항",
            onBack = { navController.popBackStack() }
        )
    }

    // FAQ
    composable(route = SettingDestination.FAQ.route) {
        DefaultScreen(
            title = "자주 묻는 질문",
            onBack = { navController.popBackStack() }
        )
    }

    // 고객 문의
    composable(route = SettingDestination.CustomerSupport.route) {
        DefaultScreen(
            title = "고객 문의",
            onBack = { navController.popBackStack() }
        )
    }

    // 이용약관
    composable(route = SettingDestination.Terms.route) {
        ContentsScreen(
            title = "이용약관",
            content = """
                제1조 (목적)
                이 약관은 회사가 제공하는 서비스의 이용과 관련하여...
                
                (실제로는 여기에 전체 약관 내용이 들어갑니다)
            """.trimIndent(),
            onBack = { navController.popBackStack() }
        )
    }

    // 개인정보 처리방침
    composable(route = SettingDestination.Privacy.route) {
        ContentsScreen(
            title = "개인정보 처리방침",
            content = """
                회사는 개인정보 보호법에 따라...
                
                (실제 개인정보 처리방침 내용)
            """.trimIndent(),
            onBack = { navController.popBackStack() }
        )
    }

    // 나머지도 비슷하게 추가
    composable(route = SettingDestination.PersonalInfo.route) {
        ContentsScreen(
            title = "개인정보 수집/이용 동의",
            content = "개인정보 수집/이용 동의 내용...",
            onBack = { navController.popBackStack() }
        )
    }

    composable(route = SettingDestination.SensitiveInfo.route) {
        ContentsScreen(
            title = "민감정보 수집/이용 동의",
            content = "민감정보 수집/이용 동의 내용...",
            onBack = { navController.popBackStack() }
        )
    }

    composable(route = SettingDestination.Location.route) {
        ContentsScreen(
            title = "위치기반서비스 이용약관",
            content = "위치기반서비스 이용약관 내용...",
            onBack = { navController.popBackStack() }
        )
    }

    composable(route = SettingDestination.Marketing.route) {
        ContentsScreen(
            title = "마케팅 이용동의",
            content = "마케팅 이용동의 내용...",
            onBack = { navController.popBackStack() }
        )
    }
}