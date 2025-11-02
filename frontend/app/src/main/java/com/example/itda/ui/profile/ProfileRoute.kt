package com.example.itda.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.common.theme.Primary60

@Composable
fun ProfileRoute(
    onSettingClick: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ProfileViewModel = hiltViewModel()
) {
    val ui by vm.profileUi.collectAsState()
    val scope = rememberCoroutineScope()

    ProfileScreen(
        ui = ui,
        onPersonalInfoClick = onPersonalInfoClick,
        onSettingClick = onSettingClick,
        modifier = modifier,
        onRefresh = { vm.loadProfileData() }  // 👈 추가
    )
}

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit,
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val ui by settingsVm.settingsUi.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("정말 로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        settingsVm.logout()
                        onLogoutSuccess()  // 여기서 화면 이동!
                    }
                ) {
                    Text("확인", color = Primary60)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    SettingScreen(
        ui = ui,
        onBack = onBack,
        toggleDarkMode = settingsVm::toggleDarkMode,
        toggleAlarm = settingsVm::toggleAlarm,
        onLogout = { showLogoutDialog = true }
    )
}

@Composable
fun PersonalInfoRoute(
    onBack: () -> Unit,
    vm: PersonalInfoViewModel = hiltViewModel()
) {
    PersonalInfoScreen(
        onBack = onBack
    )
}