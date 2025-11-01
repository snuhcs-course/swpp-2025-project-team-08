package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.repository.UserRepository
import com.example.itda.data.source.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    data class PersonalInfoUiState(
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "",
        val address: String = "",
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
        val generalError: String? = null
    )

    private val _personalInfoUi = MutableStateFlow(PersonalInfoUiState())
    val personalInfoUi: StateFlow<PersonalInfoUiState> = _personalInfoUi.asStateFlow()

    init {
        loadUserData()
    }

    // Enum 이름 → 한글 변환 (불러올 때)
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
                "HIGHSCHOOL" -> "고졸"
                "STUDENT" -> "재학생"
                "LEAVE_OF_ABSENCE" -> "휴학생"
                "EXPECTED_GRADUATE" -> "졸업예정"
                "ASSOCIATE" -> "전문대졸"
                "BACHELOR" -> "대졸"
                "MASTER" -> "석사"
                "PHD" -> "박사"
                "ANY" -> "무관"
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

    // 한글 → Enum 이름 변환 (저장할 때)
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
    // PersonalInfoViewModel.kt의 loadUserData()에 로그 추가
    private fun loadUserData() {
        viewModelScope.launch {
            _personalInfoUi.update { it.copy(isLoading = true) }

            try {
                val user = userRepository.getMe()

                android.util.Log.d("PersonalInfoVM", "Loaded user: $user")  // 로그 추가

                val convertedGender = convertEnumToKorean(user.gender, "gender")
                val convertedMarital = convertEnumToKorean(user.maritalStatus, "marital")
                val convertedEducation = convertEnumToKorean(user.educationLevel, "education")
                val convertedEmployment = convertEnumToKorean(user.employmentStatus, "employment")

                android.util.Log.d("PersonalInfoVM", "Converted - Gender: $convertedGender, Marital: $convertedMarital")  // 로그 추가

                _personalInfoUi.update {
                    it.copy(
                        name = user.name ?: "",
                        birthDate = user.birthDate ?: "",
                        gender = convertedGender,
                        address = user.address ?: "",
                        maritalStatus = convertedMarital,
                        education = convertedEducation,
                        householdSize = user.householdSize?.toString() ?: "",
                        householdIncome = user.householdIncome?.toString() ?: "",
                        employmentStatus = convertedEmployment,
                        isLoading = false
                    )
                }

                android.util.Log.d("PersonalInfoVM", "UI State updated: ${_personalInfoUi.value}")  // 로그 추가

            } catch (e: Exception) {
                android.util.Log.e("PersonalInfoVM", "Error loading user data", e)  // 로그 추가
                _personalInfoUi.update {
                    it.copy(
                        isLoading = false,
                        generalError = "정보 불러오기 실패: ${e.message}"
                    )
                }
            }
        }
    }
    fun onNameChange(v: String) {
        _personalInfoUi.update { it.copy(name = v, nameError = null, generalError = null) }
    }

    fun onBirthDateChange(v: String) {
        _personalInfoUi.update { it.copy(birthDate = v, birthDateError = null, generalError = null) }
    }

    fun onGenderChange(v: String) {
        _personalInfoUi.update { it.copy(gender = v, genderError = null, generalError = null) }
    }

    fun onAddressChange(v: String) {
        _personalInfoUi.update { it.copy(address = v, addressError = null, generalError = null) }
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

        if (ui.birthDate.isBlank()) {
            _personalInfoUi.update { it.copy(birthDateError = "생년월일을 입력해주세요") }
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

        if (hasError) {
            Log.d("PersonalInfoVM", "Validation failed")
            return false
        }

        _personalInfoUi.update {
            it.copy(
                isLoading = true,
                nameError = null,
                birthDateError = null,
                genderError = null,
                addressError = null,
                generalError = null
            )
        }

        // 한글 → Enum 이름으로 변환
        val genderEnum = convertKoreanToEnum(ui.gender, "gender")
        val maritalEnum = convertKoreanToEnum(ui.maritalStatus, "marital")
        val educationEnum = convertKoreanToEnum(ui.education, "education")
        val employmentEnum = convertKoreanToEnum(ui.employmentStatus, "employment")

        Log.d("PersonalInfoVM", "Converted enums:")
        Log.d("PersonalInfoVM", "  gender: ${ui.gender} -> $genderEnum")
        Log.d("PersonalInfoVM", "  marital: ${ui.maritalStatus} -> $maritalEnum")
        Log.d("PersonalInfoVM", "  education: ${ui.education} -> $educationEnum")
        Log.d("PersonalInfoVM", "  employment: ${ui.employmentStatus} -> $employmentEnum")

        val result = userRepository.updateProfile(
            name = ui.name,
            birthDate = ui.birthDate,
            gender = genderEnum,
            address = ui.address,
            maritalStatus = maritalEnum,
            educationLevel = educationEnum,
            householdSize = ui.householdSize.toIntOrNull(),
            householdIncome = ui.householdIncome.toIntOrNull(),
            employmentStatus = employmentEnum
        )

        result.onSuccess {
            Log.d("PersonalInfoVM", "Save successful!")
        }

        result.onFailure { exception ->
            Log.e("PersonalInfoVM", "Save failed", exception)
            _personalInfoUi.update {
                it.copy(generalError = "저장 실패: ${exception.message}")
            }
        }

        _personalInfoUi.update { it.copy(isLoading = false) }

        return result.isSuccess
    }
}