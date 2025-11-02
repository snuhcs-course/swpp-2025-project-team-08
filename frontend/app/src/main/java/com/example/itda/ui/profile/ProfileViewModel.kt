package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.User
import com.example.itda.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    data class ProfileUiState(
        val user: User = User(  // DummyData 제거, 빈 User로 초기화
            id = "",
            email = "",
            name = null,
            birthDate = null,
            gender = null,
            address = null,
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null
        ),
        val isLoading: Boolean = false
    )

    private val _profileUi = MutableStateFlow(ProfileUiState())
    val profileUi: StateFlow<ProfileUiState> = _profileUi.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _profileUi.update { it.copy(isLoading = true) }

            try {
                val user = userRepository.getMe()

                _profileUi.update {
                    it.copy(
                        user = user,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _profileUi.update { it.copy(isLoading = false) }
            }
        }
    }
}