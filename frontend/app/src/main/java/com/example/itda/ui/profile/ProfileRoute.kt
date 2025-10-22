package com.example.itda.ui.profile


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileRoute(
    onSettingClick : () -> Unit,
    onPersonalInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ProfileViewModel = hiltViewModel()
) {
    val ui by vm.profileUi.collectAsState() // ViewModel의 UI 상태를 구독
    val scope = rememberCoroutineScope()

    ProfileScreen(
        ui = ui, // ProfileScreen에 UI 상태를 통째로 전달
        onPersonalInfoClick = onPersonalInfoClick,
        onSettingClick = onSettingClick,
        modifier = modifier
    )
}

@Composable
fun SettingsRoute(
    onBack : () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val ui by vm.settingsUi.collectAsState()

    SettingScreen(
        ui = ui,
        onBack = onBack,
        toggleDarkMode = { vm::toggleDarkMode },
        toggleAlarm = { vm::toggleAlarm }
    )
}

@Composable
fun PersonalInfoRoute(
    onBack : () -> Unit,
    vm: PersonalInfoViewModel = hiltViewModel()
) {
//    val ui by vm.personalInfoUi.collectAsState()

    PersonalInfoScreen(
        onBack = onBack
//        viewModel = viewModel,
    )
}