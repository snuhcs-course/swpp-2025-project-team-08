package com.example.itda.ui.profile.component


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.profile.SettingScreen
import com.example.itda.ui.profile.SettingsViewModel
import com.example.itda.ui.profile.component.FAQScreen
import com.example.itda.ui.profile.component.NoticeScreen
import com.example.itda.ui.profile.component.CustomerSupportScreen
import com.example.itda.ui.profile.component.LegalDocumentScreen
import com.example.itda.ui.profile.component.LegalDocumentFactory

fun NavGraphBuilder.settingNavGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // 설정 메인 화면
    composable(route = "setting") {
        val viewModel: SettingsViewModel = hiltViewModel()
        val uiState by viewModel.settingsUi.collectAsState()
        val isLogoutSuccess by viewModel.isLogoutSuccess.collectAsState()

        // 로그아웃 성공 시 AuthViewModel 상태 업데이트 및 로그인 화면으로 이동
        LaunchedEffect(isLogoutSuccess) {
            if (isLogoutSuccess) {
                // AuthViewModel의 로그인 상태를 false로 설정
                authViewModel.setLoggedOut()
                // 로그인 화면으로 이동
                navController.navigate("login") {
                    popUpTo("setting") { inclusive = true }
                    popUpTo("main_graph") { inclusive = true }
                }
                // 로그아웃 상태 리셋
                viewModel.resetLogoutState()
            }
        }

        SettingScreen(
            ui = uiState,
            onBack = { navController.popBackStack() },
            onNavigateToDestination = { destination ->
                navController.navigate(destination.route)
            },
            toggleDarkMode = viewModel::toggleDarkMode,
            onFontSizeChange = viewModel::setFontSize,
            onLogout = viewModel::logout
        )
    }

    // 공지사항
    composable(route = SettingDestination.Notice.route) {
        NoticeScreen(
            onBack = { navController.popBackStack() }
        )
    }

    // FAQ
    composable(route = SettingDestination.FAQ.route) {
        FAQScreen(
            onBack = { navController.popBackStack() }
        )
    }

    // 고객 문의
    composable(route = SettingDestination.CustomerSupport.route) {
        CustomerSupportScreen(
            onBack = { navController.popBackStack() },
            onNavigateToFAQ = { navController.navigate(SettingDestination.FAQ.route) }
        )
    }

    // 이용약관
    composable(route = SettingDestination.Terms.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createTermsOfService(),
            onBack = { navController.popBackStack() }
        )
    }

    // 개인정보 처리방침
    composable(route = SettingDestination.Privacy.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createPrivacyPolicy(),
            onBack = { navController.popBackStack() }
        )
    }

    // 개인정보 수집/이용 동의
    composable(route = SettingDestination.PersonalInfo.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createPersonalInfoConsent(),
            onBack = { navController.popBackStack() }
        )
    }

    // 민감정보 수집/이용 동의
    composable(route = SettingDestination.SensitiveInfo.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createSensitiveInfoConsent(),
            onBack = { navController.popBackStack() }
        )
    }

    // 위치기반서비스 이용약관
    composable(route = SettingDestination.Location.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createLocationConsent(),
            onBack = { navController.popBackStack() }
        )
    }

    // 마케팅 이용동의
    composable(route = SettingDestination.Marketing.route) {
        LegalDocumentScreen(
            documentType = LegalDocumentFactory.createMarketingConsent(),
            onBack = { navController.popBackStack() }
        )
    }
}