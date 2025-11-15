package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.source.remote.ApiErrorParser
import com.example.itda.data.source.remote.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    data class ProfileUiState(
        val user: ProfileResponse? = null,
        val isLoading: Boolean = false,
        val generalError: String? = null
    )

    private val _profileUi = MutableStateFlow(ProfileUiState())
    val profileUi: StateFlow<ProfileUiState> = _profileUi.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _profileUi.update { it.copy(isLoading = true) }

            authRepository.getProfile()
                .onSuccess { profile ->
                    _profileUi.update {
                        it.copy(
                            user = profile,
                            isLoading = false,
                            generalError = null
                        )
                    }
                }
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _profileUi.update {
                        it.copy(
                            isLoading = false,
                            generalError = apiError.message
                        )
                    }
                }
        }
    }
}