package com.example.itda.ui.auth

import android.R.attr.data
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // 1. 화면 상태 관리
    sealed class AuthState {
        object Login : AuthState()           // 로그인 화면
        object SignUp : AuthState()          // 회원가입 Step 1
        object PersonalInfo : AuthState()    // 회원가입 Step 2
        object Home : AuthState()            // 회원가입 완료 후 홈
    }


    private val _isLoggedIn = MutableStateFlow(false) // viewmodel 내에서 로그인 여부 저장하는 Writable StateFlow
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow() // 외부 접근 가능한 Read-Only StateFlow

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { _isLoggedIn.value = it }
        }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { loggedIn ->
                _isLoggedIn.value = loggedIn
            }
        }
    }


    private val _currentScreen = MutableStateFlow<AuthState>(AuthState.Login)
    val currentScreen: StateFlow<AuthState> = _currentScreen.asStateFlow()


    // 2. 회원가입 Step 1 데이터 임시 저장
    private var signUpEmail: String = ""
    private var signUpPassword: String = ""

    // 로그인 → 회원가입 Step 1
    fun navigateToSignUp() {
        _currentScreen.value = AuthState.SignUp
    }

    // 회원가입 Step 1 → 로그인
    fun navigateToLogin() {
        _currentScreen.value = AuthState.Login

        _isLoggedIn.value = true
    }

    // 회원가입 Step 1 → Step 2 (이메일/비밀번호 저장)
    fun saveSignUpData(email: String, password: String) {
        signUpEmail = email
        signUpPassword = password
        _currentScreen.value = AuthState.PersonalInfo
    }

    // 회원가입 Step 2 → 회원가입 완료 (API 호출)
    fun savePersonalInfo(
        name: String,
        nickname: String,
        phoneNumber: String,
        address: String,
        birthday: String,
    ) { /*TODO: API 연결*/
        _currentScreen.value = AuthState.Home}

    // 4️⃣ 로그인 처리
    fun login(email: String, password: String) {
        /*TODO: API 연결*/
//        _currentScreen.value = AuthState.Home

        Log.d("login", "LoginButtonClicked: $data")
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _isLoggedIn.value = true
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }

}