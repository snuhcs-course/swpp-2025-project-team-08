package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
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
    private val authRepository: AuthRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    data class SettingsUiState(
        val darkMode: Boolean = false,
        val fontSize: FontSize = FontSize.MEDIUM,
        val isLoading: Boolean = false,
    )

    enum class FontSize(val displayName: String, val scale: Float) {
        SMALL("작게", 0.9f),
        MEDIUM("보통", 1.0f),
        LARGE("크게", 1.1f),
        EXTRA_LARGE("매우\n크게", 1.2f)
    }

    private val _settingsUi = MutableStateFlow(SettingsUiState())
    val settingsUi: StateFlow<SettingsUiState> = _settingsUi.asStateFlow()

    private val _isLogoutSuccess = MutableStateFlow(false)
    val isLogoutSuccess: StateFlow<Boolean> = _isLogoutSuccess.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // 두 Flow를 combine해서 UI State 업데이트
            combine(
                settingsDataStore.darkModeFlow,
                settingsDataStore.fontSizeFlow
            ) { darkMode, fontSize ->
                SettingsUiState(
                    darkMode = darkMode,
                    fontSize = fontSize,
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
            settingsDataStore.setDarkMode(newValue)
        }
    }

    fun setFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            settingsDataStore.setFontSize(fontSize)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLogoutSuccess.value = true
        }
    }

    fun resetLogoutState() {
        _isLogoutSuccess.value = false
    }
}