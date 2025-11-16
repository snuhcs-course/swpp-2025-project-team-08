package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.source.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.itda.ui.auth.components.formatBirthDate
import com.example.itda.ui.auth.components.isValidBirthDate

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    data class PersonalInfoUiState(
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "",
        val address: String = "",
        val postcode: String = "",
        val maritalStatus: String = "",
        val education: String = "",
        val householdSize: String = "",
        val householdIncome: String = "",
        val employmentStatus: String = "",
        val tags: List<String> = emptyList(),
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

    init {
        loadUserData()
    }

    // Enum 이름 → 한글 (서버값 -> UI표시)
    // 참고: 서버가 이미 한글을 반환하는 경우 이 함수는 사용되지 않음
    private fun convertEnumToKorean(enumName: String?, type: String): String {
        return when (type) {
            "gender" -> when (enumName) {
                "MALE" -> "남성"
                "FEMALE" -> "여성"
                else -> ""
            }

            "marital" -> when (enumName) {
                "SINGLE" -> "미혼"
                "MARRIED" -> "기혼"
                "DIVORCED_OR_BEREAVED" -> "이혼/사별"
                else -> ""
            }

            "education" -> when (enumName) {
                "ELEMENTARY_SCHOOL_STUDENT" -> "초등학생"
                "MIDDLE_SCHOOL_STUDENT" -> "중학생"
                "HIGH_SCHOOL_STUDENT" -> "고등학생"
                "COLLEGE_STUDENT" -> "대학생"
                "ELEMENTARY_SCHOOL" -> "초졸"
                "MIDDLE_SCHOOL" -> "중졸"
                "HIGH_SCHOOL" -> "고졸"
                "ASSOCIATE" -> "전문대졸"
                "BACHELOR" -> "대졸"
                else -> ""
            }

            "employment" -> when (enumName) {
                "EMPLOYED" -> "재직자"
                "UNEMPLOYED" -> "미취업자"
                "SELF_EMPLOYED" -> "자영업자"
                else -> ""
            }

            else -> ""
        }
    }

    // 한글 → Enum 이름 (UI입력 -> 서버전송)
    private fun convertKoreanToEnum(korean: String?, type: String): String? {
        if (korean.isNullOrBlank()) return null

        return when (type) {
            "gender" -> when (korean) {
                "남성" -> "MALE"
                "여성" -> "FEMALE"
                else -> null
            }

            "marital" -> when (korean) {
                "미혼" -> "SINGLE"
                "기혼" -> "MARRIED"
                "이혼/사별" -> "DIVORCED_OR_BEREAVED"
                else -> null
            }

            "education" -> when (korean) {
                "초등학생" -> "ELEMENTARY_SCHOOL_STUDENT"
                "중학생" -> "MIDDLE_SCHOOL_STUDENT"
                "고등학생" -> "HIGH_SCHOOL_STUDENT"
                "대학생" -> "COLLEGE_STUDENT"
                "초졸" -> "ELEMENTARY_SCHOOL"
                "중졸" -> "MIDDLE_SCHOOL"
                "고졸" -> "HIGH_SCHOOL"
                "전문대졸" -> "ASSOCIATE"
                "대졸" -> "BACHELOR"
                else -> null
            }

            "employment" -> when (korean) {
                "재직자" -> "EMPLOYED"
                "미취업자" -> "UNEMPLOYED"
                "자영업자" -> "SELF_EMPLOYED"
                else -> null
            }

            else -> null
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _personalInfoUi.update { it.copy(isLoading = true) }

            authRepository.getProfile()
                .onSuccess { profile ->
                    _personalInfoUi.update {
                        it.copy(
                            name = profile.name ?: "",
                            birthDate = profile.birthDate?.replace("-", "") ?: "",
                            gender = profile.gender ?: "",
                            address = profile.address ?: "",
                            postcode = profile.postcode ?: "",
                            maritalStatus = profile.maritalStatus ?: "",
                            education = profile.educationLevel ?: "",
                            householdSize = profile.householdSize?.toString() ?: "",
                            householdIncome = profile.householdIncome?.toString() ?: "",
                            employmentStatus = profile.employmentStatus ?: "",
                            tags = profile.tags ?: emptyList(),
                            isLoading = false,
                            generalError = null
                        )
                    }
                }
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _personalInfoUi.update {
                        it.copy(
                            isLoading = false,
                            generalError = apiError.message
                        )
                    }
                }
        }
    }

    fun onNameChange(v: String) {
        _personalInfoUi.update {
            it.copy(name = v, nameError = null, generalError = null)
        }
    }

    fun onBirthDateChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(8)
        _personalInfoUi.update { it.copy(birthDate = filtered, birthDateError = null, generalError = null) }
    }

    fun onGenderChange(v: String) {
        _personalInfoUi.update {
            it.copy(gender = v, genderError = null, generalError = null)
        }
    }

    fun onAddressChange(v: String) {
        _personalInfoUi.update { it.copy(address = v, addressError = null, generalError = null) }
    }

    fun onPostCodeChange(v: String) {
        _personalInfoUi.update { it.copy(postcode = v, postcodeError = null, generalError = null) }
    }

    fun onChange(v: String) {
        _personalInfoUi.update {
            it.copy(address = v, addressError = null, generalError = null)
        }
    }

    fun onMaritalStatusChange(v: String) {
        _personalInfoUi.update { it.copy(maritalStatus = v, generalError = null) }
    }

    fun onEducationChange(v: String) {
        _personalInfoUi.update { it.copy(education = v, generalError = null) }
    }

    fun onHouseholdSizeChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(2)
        _personalInfoUi.update { it.copy(householdSize = filtered, generalError = null) }
    }

    fun onHouseholdIncomeChange(v: String) {
        val filtered = v.filter { it.isDigit() }
        _personalInfoUi.update { it.copy(householdIncome = filtered, generalError = null) }
    }

    fun onEmploymentStatusChange(v: String) {
        _personalInfoUi.update { it.copy(employmentStatus = v, generalError = null) }
    }

    fun onTagInputChange(v: String) {
        _personalInfoUi.update { it.copy(tagInput = v) }
    }

    fun onAddTag(tag: String) {
        val trimmedTag = tag.trim()
        if (trimmedTag.isNotEmpty() && trimmedTag !in _personalInfoUi.value.tags) {
            _personalInfoUi.update {
                it.copy(
                    tags = it.tags + trimmedTag,
                    tagInput = "" // 태그 추가 후 입력 필드 클리어
                )
            }
        }
    }

    fun onRemoveTag(tag: String) {
        _personalInfoUi.update {
            it.copy(tags = it.tags.filter { t -> t != tag })
        }
    }

    suspend fun submitPersonalInfo(): Boolean {
        val ui = _personalInfoUi.value

        var hasError = false

        if (ui.name.isBlank()) {
            _personalInfoUi.update { it.copy(nameError = "이름을 입력해주세요") }
            hasError = true
        }

        val birthDateDigits = ui.birthDate.filter { it.isDigit() }
        if (birthDateDigits.isBlank()) {
            _personalInfoUi.update { it.copy(birthDateError = "생년월일을 입력해주세요") }
            hasError = true
        } else if (!isValidBirthDate(birthDateDigits)) {
            _personalInfoUi.update { it.copy(birthDateError = "올바른 생년월일을 입력해주세요 (예: 19990101)") }
            hasError = true
        }

        if (hasError) {
            return false
        }

        if (ui.gender.isBlank()) {
            _personalInfoUi.update { it.copy(genderError = "성별을 선택해주세요") }
            hasError = true
        }

        if (ui.address.isBlank()) {
            _personalInfoUi.update { it.copy(addressError = "주소를 입력해주세요") }
            hasError = true
        }

        if (hasError) {
            return false
        }

        // 검증 통과. 저장 시작.
        _personalInfoUi.update { it.copy(isLoading = true, generalError = null) }

        val formattedBirthDate = formatBirthDate(ui.birthDate)
        val genderEnum = convertKoreanToEnum(ui.gender, "gender")
        val maritalEnum = convertKoreanToEnum(ui.maritalStatus, "marital")
        val educationEnum = convertKoreanToEnum(ui.education, "education")
        val employmentEnum = convertKoreanToEnum(ui.employmentStatus, "employment")

        val result = authRepository.updateProfile(
            name = ui.name,
            birthDate = formattedBirthDate,
            gender = genderEnum,
            address = ui.address,
            postcode = ui.postcode,
            maritalStatus = maritalEnum,
            educationLevel = educationEnum,
            householdSize = ui.householdSize.toIntOrNull(),
            householdIncome = ui.householdIncome.toIntOrNull(),
            employmentStatus = employmentEnum,
            tags = ui.tags.ifEmpty { null }
        )

        result.onFailure { exception ->
            val errorMessage = when {
                exception is retrofit2.HttpException -> {
                    when (exception.code()) {
                        400 -> "입력하신 정보를 다시 확인해주세요"
                        401 -> "로그인이 만료되었습니다. 다시 로그인해주세요"
                        403 -> "접근 권한이 없습니다"
                        404 -> "요청하신 정보를 찾을 수 없습니다"
                        409 -> "이미 등록된 정보입니다"
                        422 -> "입력값이 올바르지 않습니다"
                        500, 502, 503 -> "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요"
                        else -> "정보 저장에 실패했습니다. 다시 시도해주세요"
                    }
                }
                exception.message?.contains("timeout", ignoreCase = true) == true -> {
                    "네트워크 연결이 불안정합니다. 다시 시도해주세요"
                }
                exception.message?.contains("unable to resolve host", ignoreCase = true) == true -> {
                    "인터넷 연결을 확인해주세요"
                }
                else -> "정보 저장 중 오류가 발생했습니다. 다시 시도해주세요"
            }
            _personalInfoUi.update { it.copy(generalError = errorMessage) }
        }

        _personalInfoUi.update { it.copy(isLoading = false) }

        return result.isSuccess
    }
}