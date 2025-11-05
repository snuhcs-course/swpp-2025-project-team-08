package com.example.itda.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.source.remote.ApiError
import com.example.itda.data.source.remote.ApiErrorParser
import com.example.itda.ui.auth.components.formatBirthDate
import com.example.itda.ui.auth.components.isValidBirthDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _isLoadingInitial = MutableStateFlow(true)
    val isLoadingInitial: StateFlow<Boolean> = _isLoadingInitial.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoadingInitial.value = true

            val hasToken = authRepository.isLoggedInFlow.first()
            _isLoggedIn.value = hasToken

             delay(1000L) // TODO - loading page 보이게 하려면 delay 필요?? 왜 delay 없으면 점만 보이다가 내려갈까..
            _isLoadingInitial.value = false
        }
    }

    // 로그인 상태
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val generalError: String? = null
    )

    private val _loginUi = MutableStateFlow(LoginUiState())
    val loginUi: StateFlow<LoginUiState> = _loginUi.asStateFlow()

    fun onLoginEmailChange(v: String) {
        _loginUi.update { it.copy(email = v, emailError = null, generalError = null) }
    }

    fun onLoginPasswordChange(v: String) {
        _loginUi.update { it.copy(password = v, passwordError = null, generalError = null) }
    }

    suspend fun submitLogin(): Boolean {
        val ui = _loginUi.value

        if (ui.email.isBlank()) {
            _loginUi.update { it.copy(emailError = "이메일을 입력해주세요") }
            return false
        }
        if (ui.password.isBlank()) {
            _loginUi.update { it.copy(passwordError = "비밀번호를 입력해주세요") }
            return false
        }

        _loginUi.update { it.copy(isLoading = true, emailError = null, passwordError = null, generalError = null) }

        val result = authRepository.login(ui.email, ui.password)

        result.onFailure { exception ->
            val apiError = ApiErrorParser.parseError(exception)

            when (apiError) {
                is ApiError.UserNotFound -> {
                    _loginUi.update { it.copy(emailError = apiError.message) }
                }
                is ApiError.WrongPassword -> {
                    _loginUi.update { it.copy(passwordError = apiError.message) }
                }
                is ApiError.NetworkError -> {
                    _loginUi.update { it.copy(generalError = apiError.message) }
                }
                else -> {
                    _loginUi.update { it.copy(generalError = apiError.message) }
                }
            }
        }

        _loginUi.update { it.copy(isLoading = false) }

        if (result.isSuccess) {
            _isLoggedIn.value = true
        }

        return result.isSuccess
    }

    // 회원가입 상태
    data class SignUpUiState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val agreeTerms: Boolean = false,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val generalError: String? = null
    )

    private val _signUpUi = MutableStateFlow(SignUpUiState())
    val signUpUi: StateFlow<SignUpUiState> = _signUpUi.asStateFlow()

    fun onSignUpEmailChange(v: String) {
        _signUpUi.update { it.copy(email = v, emailError = null, generalError = null) }
    }

    fun onSignUpPasswordChange(v: String) {
        _signUpUi.update {
            it.copy(
                password = v,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }
    }

    fun onSignUpConfirmChange(v: String) {
        _signUpUi.update { it.copy(confirmPassword = v, confirmPasswordError = null, generalError = null) }
    }

    fun onAgreeTermsChange(v: Boolean) {
        _signUpUi.update { it.copy(agreeTerms = v, generalError = null) }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    suspend fun submitSignUp(): Boolean {
        val ui = _signUpUi.value

        var hasError = false

        if (ui.email.isBlank()) {
            _signUpUi.update { it.copy(emailError = "이메일을 입력해주세요") }
            hasError = true
        } else if (!isValidEmail(ui.email)) {
            _signUpUi.update { it.copy(emailError = "올바른 이메일 형식이 아닙니다") }
            hasError = true
        }

        if (ui.password.isBlank()) {
            _signUpUi.update { it.copy(passwordError = "비밀번호를 입력해주세요") }
            hasError = true
        } else if (ui.password.length !in 8..16) {
            _signUpUi.update { it.copy(passwordError = "비밀번호는 8~16자여야 합니다") }
            hasError = true
        }

        if (ui.confirmPassword.isBlank()) {
            _signUpUi.update { it.copy(confirmPasswordError = "비밀번호를 다시 입력해주세요") }
            hasError = true
        } else if (ui.password != ui.confirmPassword) {
            _signUpUi.update { it.copy(confirmPasswordError = "비밀번호가 일치하지 않습니다") }
            hasError = true
        }

        if (!ui.agreeTerms) {
            _signUpUi.update { it.copy(generalError = "약관에 동의해주세요") }
            hasError = true
        }

        if (hasError) return false

        _signUpUi.update {
            it.copy(
                isLoading = true,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }

        val result = authRepository.signup(ui.email, ui.password)

        result.onFailure { exception ->
            val apiError = ApiErrorParser.parseError(exception)

            when (apiError) {
                is ApiError.InvalidEmail -> {
                    _signUpUi.update { it.copy(emailError = apiError.message) }
                }
                is ApiError.BadPassword -> {
                    _signUpUi.update { it.copy(passwordError = apiError.message) }
                }
                is ApiError.EmailConflict -> {
                    _signUpUi.update { it.copy(emailError = apiError.message) }
                }
                is ApiError.NetworkError -> {
                    _signUpUi.update { it.copy(generalError = apiError.message) }
                }
                else -> {
                    _signUpUi.update { it.copy(generalError = apiError.message) }
                }
            }
        }

        _signUpUi.update { it.copy(isLoading = false) }

        return result.isSuccess
    }

    // 기본 개인 정보 입력 상태
    data class PersonalInfoUiState(
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "",
        val address: String = "",
        val postcode: String = "",
        val isLoading: Boolean = false,
        val nameError: String? = null,
        val birthDateError: String? = null,
        val genderError: String? = null,
        val addressError: String? = null,
        val postcodeError: String? = null,
        val generalError: String? = null
    )

    private val _personalInfoUi = MutableStateFlow(PersonalInfoUiState())
    val personalInfoUi: StateFlow<PersonalInfoUiState> = _personalInfoUi.asStateFlow()

    fun onNameChange(v: String) {
        _personalInfoUi.update { it.copy(name = v, nameError = null, generalError = null) }
    }

    fun onBirthDateChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(8)
        _personalInfoUi.update { it.copy(birthDate = filtered, birthDateError = null, generalError = null) }
    }

    fun onGenderChange(v: String) {
        _personalInfoUi.update { it.copy(gender = v, genderError = null, generalError = null) }
    }

    fun onAddressChange(v: String) {
        _personalInfoUi.update { it.copy(address = v, addressError = null, generalError = null) }
    }

    fun onPostCodeChange(v: String) {
        _personalInfoUi.update { it.copy(postcode = v, postcodeError = null, generalError = null) }
    }

    suspend fun submitPersonalInfo(): Boolean {
        val ui = _personalInfoUi.value

        var hasError = false

        if (ui.name.isBlank()) {
            _personalInfoUi.update { it.copy(nameError = "이름을 입력해주세요") }
            hasError = true
        }

        if (ui.birthDate.isBlank()) {
            _personalInfoUi.update { it.copy(birthDateError = "생년월일을 입력해주세요") }
            hasError = true
        } else if (ui.birthDate.length != 8) {
            _personalInfoUi.update { it.copy(birthDateError = "8자리를 입력해주세요") }
            hasError = true
        } else if (!isValidBirthDate(ui.birthDate)) {
            _personalInfoUi.update { it.copy(birthDateError = "올바른 생년월일을 입력해주세요") }
            hasError = true
        }

        if (ui.gender.isBlank()) {
            _personalInfoUi.update { it.copy(genderError = "성별을 선택해주세요") }
            hasError = true
        }

        if (ui.address.isBlank()) {
            _personalInfoUi.update { it.copy(addressError = "주소를 입력해주세요") }
            hasError = true
        }

        if (hasError) return false

        _personalInfoUi.update {
            it.copy(
                isLoading = true,
                nameError = null,
                birthDateError = null,
                genderError = null,
                addressError = null,
                postcodeError = null,
                generalError = null
            )
        }

        val formattedBirthDate = formatBirthDate(ui.birthDate)

        val result = authRepository.updateProfile(
            name = ui.name,
            birthDate = formattedBirthDate,
            gender = ui.gender,
            address = ui.address,
            postcode = ui.postcode
        )

        result.onFailure { exception ->
            val apiError = ApiErrorParser.parseError(exception)
            _personalInfoUi.update { it.copy(generalError = apiError.message) }
        }

        _personalInfoUi.update { it.copy(isLoading = false) }

        if (result.isSuccess) {
            _isLoggedIn.value = true
        }

        return result.isSuccess
    }
}