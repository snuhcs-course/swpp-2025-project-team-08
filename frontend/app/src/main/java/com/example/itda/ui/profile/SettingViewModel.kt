package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class SettingsUiState(
        val darkMode : Boolean = false,
        val alarmEnabled : Boolean = true,
        val isLoading : Boolean = false,
    )

    private val _settingsUi = MutableStateFlow(SettingsUiState())
    val settingsUi: StateFlow<SettingsUiState> = _settingsUi.asStateFlow()

    fun toggleDarkMode() {
        viewModelScope.launch {
            _settingsUi.update { it.copy(isLoading = true) }
            val darkMode = !_settingsUi.value.darkMode
            _settingsUi.update {
                it.copy(
                    darkMode = darkMode,
                    isLoading = false
                )
            }
        }
    }

    fun toggleAlarm() {
        viewModelScope.launch {
            _settingsUi.update { it.copy(isLoading = true) }
            val alarmEnabled = !_settingsUi.value.alarmEnabled
            _settingsUi.update {
                it.copy(
                    alarmEnabled = alarmEnabled,  // 👈 수정
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()       // 토큰 제거
            userRepository.clearUser()    // 캐시 제거
        }
    }
}