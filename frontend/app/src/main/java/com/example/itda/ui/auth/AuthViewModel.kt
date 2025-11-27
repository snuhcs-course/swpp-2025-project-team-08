package com.example.itda.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.PreferenceRequest
import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.source.remote.ApiError
import com.example.itda.data.source.remote.ApiErrorParser
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
    private val authRepository: AuthRepository,
    private val programRepository: ProgramRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _isLoadingInitial = MutableStateFlow(true)
    val isLoadingInitial: StateFlow<Boolean> = _isLoadingInitial.asStateFlow()

    // 로그인 상태
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val rememberEmail: Boolean = false,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val generalError: String? = null
    )

    private val _loginUi = MutableStateFlow(LoginUiState())
    val loginUi: StateFlow<LoginUiState> = _loginUi.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoadingInitial.value = true

            val startTime = System.currentTimeMillis()

            val refreshToken = authRepository.getRefreshToken()

            if (refreshToken != null) {
                val refreshResult = authRepository.refreshToken()

                if (refreshResult.isSuccess) {
                    _isLoggedIn.value = true
                } else {
                    authRepository.logout()
                    _isLoggedIn.value = false
                }
            } else {
                _isLoggedIn.value = false
            }

            // 저장된 이메일 불러오기
            val savedEmail = authRepository.getSavedEmail()
            if (!savedEmail.isNullOrBlank()) {
                _loginUi.update { it.copy(email = savedEmail, rememberEmail = true) }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = (2500L - elapsedTime).coerceAtLeast(0L)
            if (remainingTime > 0) {
                delay(remainingTime)
            }

            _isLoadingInitial.value = false
        }
    }

    fun onLoginEmailChange(v: String) {
        _loginUi.update { it.copy(email = v, emailError = null, generalError = null) }
    }

    fun onLoginPasswordChange(v: String) {
        _loginUi.update { it.copy(password = v, passwordError = null, generalError = null) }
    }

    fun onRememberEmailChange(v: Boolean) {
        _loginUi.update { it.copy(rememberEmail = v) }
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

            if (ui.rememberEmail) {
                authRepository.saveEmail(ui.email)
            } else {
                authRepository.clearSavedEmail()
            }
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
    fun setLoggedOut() {
        _isLoggedIn.value = false
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
        // 필수
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "",
        val address: String = "",
        val postcode: String = "",

        // 선택
        val maritalStatus: String? = null,
        val educationLevel: String? = null,
        val householdSize: String = "",
        val householdIncome: String = "",
        val employmentStatus: String? = null,
        val selectedTags: List<String> = emptyList(),
        val tagInput: String = "",

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

    fun onMaritalStatusChange(v: String?) {
        _personalInfoUi.update { it.copy(maritalStatus = v, generalError = null) }
    }

    fun onEducationLevelChange(v: String?) {
        _personalInfoUi.update { it.copy(educationLevel = v, generalError = null) }
    }

    fun onHouseholdSizeChange(v: String) {
        _personalInfoUi.update { it.copy(householdSize = v, generalError = null) }
    }

    fun onHouseholdIncomeChange(v: String) {
        _personalInfoUi.update { it.copy(householdIncome = v, generalError = null) }
    }

    fun onEmploymentStatusChange(v: String?) {
        _personalInfoUi.update { it.copy(employmentStatus = v, generalError = null) }
    }

    fun onTagInputChange(input: String) {
        _personalInfoUi.update {
            it.copy(tagInput = input)
        }
    }
    fun addTag(tag: String) {
        val trimmedTag = tag.trim()

        if (trimmedTag.isEmpty()) return
        if (trimmedTag in _personalInfoUi.value.selectedTags) return

        _personalInfoUi.update {
            it.copy(
                selectedTags = it.selectedTags + trimmedTag,
                tagInput = ""
            )
        }
    }

    fun removeTag(tag: String) {
        _personalInfoUi.update {
            it.copy(selectedTags = it.selectedTags - tag)
        }
    }

    suspend fun submitPersonalInfo(): Boolean {
        val ui = _personalInfoUi.value

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

        // Builder 구현
        val householdSizeInt = ui.householdSize.toIntOrNull()
        val householdIncomeInt = ui.householdIncome.toIntOrNull()

        val requestResult = com.example.itda.data.model.ProfileUpdateRequest.builder()
            .name(ui.name)
            .birthDate(ui.birthDate)
            .gender(ui.gender)
            .address(ui.address)
            .postcode(ui.postcode)
            .maritalStatus(ui.maritalStatus)
            .educationLevel(ui.educationLevel)
            .householdSize(householdSizeInt)
            .householdIncome(householdIncomeInt)
            .employmentStatus(ui.employmentStatus)
            .tags(ui.selectedTags.ifEmpty { null })
            .build()

        // 에러 처리
        requestResult.onFailure { exception ->
            val errorMessage = exception.message ?: "유효성 검사 실패"

            when {
                errorMessage.contains("성함") -> {
                    _personalInfoUi.update { it.copy(nameError = errorMessage, isLoading = false) }
                }
                errorMessage.contains("생년월일") || errorMessage.contains("8자리") -> {
                    _personalInfoUi.update { it.copy(birthDateError = errorMessage, isLoading = false) }
                }
                errorMessage.contains("성별") -> {
                    _personalInfoUi.update { it.copy(genderError = errorMessage, isLoading = false) }
                }
                errorMessage.contains("주소") -> {
                    _personalInfoUi.update { it.copy(addressError = errorMessage, isLoading = false) }
                }
                errorMessage.contains("우편번호") -> {
                    _personalInfoUi.update { it.copy(postcodeError = errorMessage, isLoading = false) }
                }
                else -> {
                    _personalInfoUi.update { it.copy(generalError = errorMessage, isLoading = false) }
                }
            }
            return false
        }

        // 유효성 검사 성공 시 API 호출
        val request = requestResult.getOrThrow()
        val result = authRepository.updateProfile(request)

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


    data class PreferenceUIState(
        val examplePrograms : List<ProgramResponse> = emptyList<ProgramResponse>(),
        val preferenceRequestList : PreferenceRequestList = emptyList<PreferenceRequest>(),
        val exampleProgramDetail : ProgramDetailResponse? = null,
        val isLoading: Boolean = false,
        val generalError: String? = null
    )
    private val _preferenceUi = MutableStateFlow(PreferenceUIState())
    val preferenceUi: StateFlow<PreferenceUIState> = _preferenceUi.asStateFlow()

    init {
        viewModelScope.launch {
            getExamples()
        }
    }

    suspend fun getExamples() {
        _preferenceUi.update { it.copy(isLoading = true) }

        val examples = programRepository.getExamples()
        examples
            .onFailure { exception ->
                val apiError = ApiErrorParser.parseError(exception)
                _preferenceUi.update {
                    it.copy(
                        generalError = apiError.message,
                        isLoading = false
                    )
                }
            }
        examples
            .onSuccess { examples ->
                _preferenceUi.update {
                    it.copy(
                        examplePrograms = examples,
                        preferenceRequestList = examples.map { p -> PreferenceRequest(id = p.id, score = 0)},
                        isLoading = false
                    )
                }
            }
    }

    fun onPreferenceScoreChange(programId : Int, newScore : Int) {
        _preferenceUi.update { ui ->
            val updatedList = ui.preferenceRequestList.map {
                if (it.id == programId) it.copy(score = newScore) else it
            }
            ui.copy(preferenceRequestList = updatedList)
        }
    }


    suspend fun updatePreference(): Boolean {
        val ui = _preferenceUi.value
        _preferenceUi.update { it.copy(isLoading = true) }

        val result = authRepository.updatePreference(ui.preferenceRequestList)

        result.onFailure { exception ->
            val apiError = ApiErrorParser.parseError(exception)
            _personalInfoUi.update { it.copy(generalError = apiError.message, isLoading = false) }
            return false
        }

        result.onSuccess {
            _preferenceUi.update { it.copy(isLoading = false) }
            return true
        }

        return false
    }

    suspend fun onFeedExampleClick(exampleId : Int) {
        _preferenceUi.update { it.copy(isLoading = true) }

        val result = programRepository.getExampleDetails(exampleId = exampleId)

        result
            .onFailure { exception ->
                val apiError = ApiErrorParser.parseError(exception)
                _preferenceUi.update {
                    it.copy(
                        generalError = apiError.message,
                        isLoading = false,
                        exampleProgramDetail = null
                    )
                }
            }
            .onSuccess { detail ->
                _preferenceUi.update {
                    it.copy(
                        isLoading = false,
                        exampleProgramDetail = detail
                    )
                }
            }
    }
    fun onDismissExampleDetail() {
        _preferenceUi.update { it.copy(exampleProgramDetail = null) }
    }

}