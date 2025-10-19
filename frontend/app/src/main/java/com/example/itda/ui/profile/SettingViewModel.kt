package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingViewModel : ViewModel() {

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    private val _alarmEnabled = MutableStateFlow(true)
    val alarmEnabled: StateFlow<Boolean> = _alarmEnabled

    fun toggleDarkMode() {
        _darkMode.value = !_darkMode.value
        // TODO: 실제로 다크모드 적용 로직
    }

    fun toggleAlarm() {
        _alarmEnabled.value = !_alarmEnabled.value
        // TODO: 실제로 알림 설정 변경 로직
    }
}