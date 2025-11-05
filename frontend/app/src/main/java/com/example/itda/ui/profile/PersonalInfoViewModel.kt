package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import retrofit2.HttpException
import com.example.itda.data.repository.UserRepository
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
    private val userRepository: UserRepository,
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
                "ANY" -> "무관"
                else -> ""
            }

            "marital" -> when (enumName) {
                "SINGLE" -> "미혼"
                "MARRIED" -> "기혼"
                "DIVORCED_OR_BEREAVED" -> "이혼/사별"
                "ANY" -> "무관"
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
                "ANY" -> "무관"
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
                "무관" -> "ANY"
                else -> null
            }

            "marital" -> when (korean) {
                "미혼" -> "SINGLE"
                "기혼" -> "MARRIED"
                "이혼/사별" -> "DIVORCED_OR_BEREAVED"
                "무관" -> "ANY"
                else -> null
            }

            "education" -> when (korean) {
                "고졸" -> "HIGHSCHOOL"
                "재학생" -> "STUDENT"
                "휴학생" -> "LEAVE_OF_ABSENCE"
                "졸업예정" -> "EXPECTED_GRADUATE"
                "전문대졸" -> "ASSOCIATE"
                "대졸" -> "BACHELOR"
                "석사" -> "MASTER"
                "박사" -> "PHD"
                "무관" -> "ANY"
                else -> null
            }

            "employment" -> when (korean) {
                "재직자" -> "EMPLOYED"
                "미취업자" -> "UNEMPLOYED"
                "자영업자" -> "SELF_EMPLOYED"
                "무관" -> "ANY"
                else -> null
            }

            else -> null
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _personalInfoUi.update { it.copy(isLoading = true) }

            try {
                val user = userRepository.getMe()

                // 🔧 수정: 서버가 이미 한글 값을 반환하므로 변환 없이 그대로 사용
                _personalInfoUi.update {
                    it.copy(
                        name = user.name ?: "",
                        birthDate = user.birthDate?.replace("-", "") ?: "",
                        gender = user.gender ?: "",
                        address = user.address ?: "",
                        postcode = user.postcode ?: "",
                        maritalStatus = user.maritalStatus ?: "",
                        education = user.educationLevel ?: "",
                        householdSize = user.householdSize?.toString() ?: "",
                        householdIncome = user.householdIncome?.toString() ?: "",
                        employmentStatus = user.employmentStatus ?: "",
                        isLoading = false,
                        nameError = null,
                        birthDateError = null,
                        genderError = null,
                        addressError = null,
                        postcodeError = null,
                        generalError = null
                    )
                }
            } catch (e: Exception) {
                _personalInfoUi.update {
                    it.copy(
                        isLoading = false,
                        generalError = "사용자 정보를 불러오지 못했습니다"
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

        val result = userRepository.updateProfile(
            name = ui.name,
            birthDate = formattedBirthDate,
            gender = genderEnum,
            address = ui.address,
            postcode = ui.postcode,
            maritalStatus = maritalEnum,
            educationLevel = educationEnum,
            householdSize = ui.householdSize.toIntOrNull(),
            householdIncome = ui.householdIncome.toIntOrNull(),
            employmentStatus = employmentEnum
        )

        result.onFailure { exception ->
            if (exception is HttpException) {
                val body = exception.response()?.errorBody()?.string().orEmpty()
                val code = exception.code()
                _personalInfoUi.update {
                    it.copy(generalError = "HTTP $code: $body")
                }
            } else {
                _personalInfoUi.update {
                    it.copy(generalError = "네트워크 오류: ${exception.message}")
                }
            }
        }


        _personalInfoUi.update { it.copy(isLoading = false) }
        return result.isSuccess
    }
}