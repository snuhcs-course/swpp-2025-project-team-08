package com.example.itda.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    init {
        viewModelScope.launch {
            val hasToken = authRepository.isLoggedInFlow.first()
            _isLoggedIn.value = hasToken
        }
    }

    // 로그인 상태
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _loginUi = MutableStateFlow(LoginUiState())
    val loginUi: StateFlow<LoginUiState> = _loginUi.asStateFlow()

    fun onLoginEmailChange(v: String) {
        _loginUi.value = _loginUi.value.copy(email = v, error = null)
    }

    fun onLoginPasswordChange(v: String) {
        _loginUi.value = _loginUi.value.copy(password = v, error = null)
    }

    // 로그인
    suspend fun submitLogin(): Boolean {
        val ui = _loginUi.value

        if (ui.email.isBlank() || ui.password.isBlank()) {
            _loginUi.update { it.copy(error = "이메일/비밀번호를 입력하세요.") }
            return false
        }
        _loginUi.update { it.copy(isLoading = true, error = null) }

        val ok = authRepository.login(ui.email, ui.password)
            .onFailure { e -> _loginUi.update { it.copy(error = e.message ?: "로그인 실패") } }
            .isSuccess
        _loginUi.update { it.copy(isLoading = false) }
        if (ok) {
            _isLoggedIn.value = true
        }

        return ok
    }
    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }

    // 회원가입 상태
    data class SignUpUiState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val agreeTerms: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _signUpUi = MutableStateFlow(SignUpUiState())
    val signUpUi: StateFlow<SignUpUiState> = _signUpUi.asStateFlow()

    fun onSignUpEmailChange(v: String) {
        _signUpUi.value = _signUpUi.value.copy(email = v, error = null)
    }
    fun onSignUpPasswordChange(v: String) {
        _signUpUi.value = _signUpUi.value.copy(password = v, error = null)
    }
    fun onSignUpConfirmChange(v: String) {
        _signUpUi.value = _signUpUi.value.copy(confirmPassword = v, error = null)
    }
    fun onAgreeTermsChange(v: Boolean) {
        _signUpUi.value = _signUpUi.value.copy(agreeTerms = v, error = null)
    }

    fun isSignUpFormValid(ui: SignUpUiState = _signUpUi.value): Boolean {
        return ui.email.isNotBlank() &&
                ui.password.isNotBlank() &&
                ui.confirmPassword.isNotBlank() &&
                ui.password == ui.confirmPassword &&
                ui.agreeTerms &&
                !ui.isLoading
    }

    suspend fun submitSignUp(): Boolean {

        val ui = _signUpUi.value

        if (!isSignUpFormValid(ui)) {
            _signUpUi.update { it.copy(error = "입력값을 확인해주세요.") }
            return false
        }

        _signUpUi.update { it.copy(isLoading = true, error = null) }

        val ok = authRepository.signup(ui.email, ui.password)
            .onFailure { e ->
                _signUpUi.update { it.copy(error = e.message ?: "회원가입 실패") }
            }
            .isSuccess

        _signUpUi.update { it.copy(isLoading = false) }


        return ok
    }

    // 기본 개인 정보 입력 상태
    data class PersonalInfoUiState(
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "",
        val address: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _personalInfoUi = MutableStateFlow(PersonalInfoUiState())
    val personalInfoUi: StateFlow<PersonalInfoUiState> = _personalInfoUi.asStateFlow()

    fun onNameChange(v: String) {
        _personalInfoUi.value = _personalInfoUi.value.copy(name = v, error = null)
    }

    fun onBirthDateChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(8)
        _personalInfoUi.value = _personalInfoUi.value.copy(birthDate = filtered, error = null)
    }

    fun onGenderChange(v: String) {
        _personalInfoUi.value = _personalInfoUi.value.copy(gender = v, error = null)
    }

    fun onAddressChange(v: String) {
        _personalInfoUi.value = _personalInfoUi.value.copy(address = v, error = null)
    }

    suspend fun submitPersonalInfo(): Boolean {
        val ui = _personalInfoUi.value

        if (ui.name.isBlank() || ui.birthDate.isBlank() ||
            ui.gender.isBlank() || ui.address.isBlank()) {
            _personalInfoUi.update { it.copy(error = "모든 항목을 입력해주세요.") }
            return false
        }

        _personalInfoUi.update { it.copy(isLoading = true, error = null) }

        val age = calculateAge(ui.birthDate)

        val ok = authRepository.updateProfile(
            name = ui.name,
            age = age,
            gender = ui.gender,
            address = ui.address,
            maritalStatus = "Single",
            educationLevel = "HighSchool",
            householdSize = 1,
            householdIncome = 7500
        )

            .onFailure { e ->
                _personalInfoUi.update { it.copy(error = e.message ?: "프로필 업데이트 실패")
                }
            }
            .isSuccess

        _personalInfoUi.update { it.copy(isLoading = false) }
        if (ok) {
            _isLoggedIn.value = true
        }
        return ok
    }

    private fun calculateAge(birthDate: String): Int {
        if (birthDate.length != 8) return 0

        val birthYear = birthDate.substring(0, 4).toIntOrNull() ?: return 0
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        return currentYear - birthYear
    }

}