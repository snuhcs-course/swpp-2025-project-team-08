package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.User
import com.example.itda.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserInfo(
    val userName: String = "User_name",
    val userId: String = "user_id",
    val name: String = "유신청",
    val category: String = "유신청",
    val age: Int = 25,
    val gender: String = "남성",
    val address: String = "서울특별시 광악구 신림동 94-60",
    val maritalStatus: String = "미혼",
    val education: String = "고졸",
    val householdSize: Int = 1,
    val householdIncome: String = "n",
    val excludedKeywords: String = ""
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    data class ProfileUiState(
        val user: User = DummyData.dummyUser[0], // 사용자 정보
        val isLoading: Boolean = false
    )

    private val _profileUi = MutableStateFlow(ProfileUiState())
    val profileUi: StateFlow<ProfileViewModel.ProfileUiState> = _profileUi.asStateFlow()

    init {
        loadProfileData()
    }


    // TODO: Repository에 사용자 정보 저장
    private fun loadProfileData() {
        viewModelScope.launch {
            _profileUi.update { it.copy(isLoading = true) }

            val user =
                userRepository.getMe()


            _profileUi.update {
                it.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    // 나중에 API 연동 시 사용할 함수들
    fun loadUserInfo() {
        // TODO: Repository에서 사용자 정보 불러오기
    }

    fun updateUserInfo(newInfo: UserInfo) {
    }
}