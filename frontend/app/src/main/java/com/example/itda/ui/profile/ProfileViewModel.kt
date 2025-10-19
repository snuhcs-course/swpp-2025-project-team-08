package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

class ProfileViewModel : ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    // 나중에 API 연동 시 사용할 함수들
    fun loadUserInfo() {
        // TODO: Repository에서 사용자 정보 불러오기
    }

    fun updateUserInfo(newInfo: UserInfo) {
        // TODO: Repository에 사용자 정보 저장
        _userInfo.value = newInfo
    }
}