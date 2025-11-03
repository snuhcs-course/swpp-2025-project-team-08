package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.UserRepository
import com.example.itda.data.source.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val settingsDataStore: SettingsDataStore  // 👈 추가
) : ViewModel() {

    data class SettingsUiState(
        val darkMode: Boolean = false,
        val alarmEnabled: Boolean = true,
        val isLoading: Boolean = false,
    )

    private val _settingsUi = MutableStateFlow(SettingsUiState())
    val settingsUi: StateFlow<SettingsUiState> = _settingsUi.asStateFlow()

    init {
        loadSettings()  // 👈 초기 로드
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // 두 Flow를 combine해서 UI State 업데이트
            combine(
                settingsDataStore.darkModeFlow,
                settingsDataStore.alarmEnabledFlow
            ) { darkMode, alarmEnabled ->
                SettingsUiState(
                    darkMode = darkMode,
                    alarmEnabled = alarmEnabled,
                    isLoading = false
                )
            }.collect { state ->
                _settingsUi.value = state
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_settingsUi.value.darkMode
            settingsDataStore.setDarkMode(newValue)  // 👈 저장
            // Flow가 자동으로 UI 업데이트
        }
    }

    fun toggleAlarm() {
        viewModelScope.launch {
            val newValue = !_settingsUi.value.alarmEnabled
            settingsDataStore.setAlarmEnabled(newValue)  // 👈 저장
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            userRepository.clearUser()
        }
    }
}