package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.source.remote.ApiErrorParser
import com.example.itda.ui.common.enums.EducationLevel
import com.example.itda.ui.common.enums.EmploymentStatus
import com.example.itda.ui.common.enums.Gender
import com.example.itda.ui.common.enums.MaritalStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        val generalError: String? = null,
        val initialHouseholdSize: String = "",
        val initialHouseholdIncome: String = ""
    )

    private val _personalInfoUi = MutableStateFlow(PersonalInfoUiState())
    val personalInfoUi: StateFlow<PersonalInfoUiState> = _personalInfoUi.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _personalInfoUi.update { it.copy(isLoading = true) }

            authRepository.getProfile()
                .onSuccess { profile ->
                    // 서버에서 한글로 받은 값을 serverValue로 변환
                    val genderServerValue = profile.gender?.let {
                        Gender.fromKorean(it)?.serverValue ?: Gender.fromServerValue(it)?.serverValue ?: it
                    } ?: ""

                    val maritalStatusServerValue = profile.maritalStatus?.let {
                        MaritalStatus.fromKorean(it)?.serverValue ?: MaritalStatus.fromServerValue(it)?.serverValue ?: it
                    } ?: ""

                    val educationServerValue = profile.educationLevel?.let {
                        EducationLevel.fromKorean(it)?.serverValue ?: EducationLevel.fromServerValue(it)?.serverValue ?: it
                    } ?: ""

                    val employmentServerValue = profile.employmentStatus?.let {
                        EmploymentStatus.fromKorean(it)?.serverValue ?: EmploymentStatus.fromServerValue(it)?.serverValue ?: it
                    } ?: ""

                    val householdSizeStr = profile.householdSize?.toString() ?: ""
                    val householdIncomeStr = profile.householdIncome?.toString() ?: ""

                    _personalInfoUi.update {
                        it.copy(
                            name = profile.name ?: "",
                            birthDate = profile.birthDate?.replace("-", "") ?: "",
                            gender = genderServerValue,
                            address = profile.address ?: "",
                            postcode = profile.postcode ?: "",
                            maritalStatus = maritalStatusServerValue,
                            education = educationServerValue,
                            householdSize = householdSizeStr,
                            householdIncome = householdIncomeStr,
                            employmentStatus = employmentServerValue,
                            tags = profile.tags ?: emptyList(),
                            isLoading = false,
                            generalError = null,
                            initialHouseholdSize = householdSizeStr,
                            initialHouseholdIncome = householdIncomeStr
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

        val householdSizeToSend = when {
            ui.householdSize.isBlank() && ui.initialHouseholdSize.isNotBlank() -> 0
            ui.householdSize == ui.initialHouseholdSize -> null
            else -> ui.householdSize.toIntOrNull()
        }

        val householdIncomeToSend = when {
            ui.householdIncome.isBlank() && ui.initialHouseholdIncome.isNotBlank() -> 0
            ui.householdIncome == ui.initialHouseholdIncome -> null
            else -> ui.householdIncome.toIntOrNull()
        }

        val requestResult = ProfileUpdateRequest.builder()
            .name(ui.name)
            .birthDate(ui.birthDate)
            .gender(ui.gender.ifBlank { null })
            .address(ui.address)
            .postcode(ui.postcode)
            .maritalStatus(ui.maritalStatus.ifBlank { null })
            .educationLevel(ui.education.ifBlank { null })
            .householdSize(householdSizeToSend)
            .householdIncome(householdIncomeToSend)
            .employmentStatus(ui.employmentStatus.ifBlank { null })
            .tags(ui.tags.ifEmpty { null })
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

        val request = requestResult.getOrThrow()
        val result = authRepository.updateProfile(request)


        result.onFailure { exception ->
            val apiError = ApiErrorParser.parseError(exception)
            _personalInfoUi.update {
                it.copy(generalError = apiError.message)
            }
        }

        _personalInfoUi.update { it.copy(isLoading = false) }

        return result.isSuccess
    }
}