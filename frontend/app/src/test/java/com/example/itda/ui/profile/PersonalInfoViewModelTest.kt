package com.example.itda.ui.profile

import com.example.itda.data.model.User
import com.example.itda.data.repository.AuthRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class PersonalInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var authRepository: AuthRepository

    private lateinit var viewModel: PersonalInfoViewModel

    private val testProfile = User(
        id = "test-id",
        email = "test@example.com",
        name = "홍길동",
        birthDate = "1990-01-01",
        gender = "MALE",
        address = "서울시 강남구",
        postcode = "12345",
        maritalStatus = "SINGLE",
        educationLevel = "BACHELOR",
        householdSize = 3,
        householdIncome = 5000,
        employmentStatus = "EMPLOYED",
        tags = listOf("저소득층")
    )

    @Test
    fun init_loadsUserData_successfully() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))

        // When
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        // Then
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEqualTo("홍길동")
        assertThat(ui.birthDate).isEqualTo("19900101")
        assertThat(ui.gender).isEqualTo("MALE")
        assertThat(ui.address).isEqualTo("서울시 강남구")
        assertThat(ui.postcode).isEqualTo("12345")
        assertThat(ui.maritalStatus).isEqualTo("SINGLE")
        assertThat(ui.education).isEqualTo("BACHELOR")
        assertThat(ui.householdSize).isEqualTo("3")
        assertThat(ui.householdIncome).isEqualTo("5000")
        assertThat(ui.employmentStatus).isEqualTo("EMPLOYED")
        assertThat(ui.tags).containsExactly("저소득층")
        assertThat(ui.isLoading).isFalse()
        assertThat(ui.generalError).isNull()
    }

    @Test
    fun init_handlesNullValues_correctly() = runTest {
        // Given
        val emptyProfile = User(
            id = "test-id",
            email = "test@example.com",
            name = null,
            birthDate = null,
            gender = null,
            address = null,
            postcode = null,
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null,
            tags = null
        )
        whenever(authRepository.getProfile()).thenReturn(Result.success(emptyProfile))

        // When
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        // Then
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEmpty()
        assertThat(ui.birthDate).isEmpty()
        assertThat(ui.gender).isEmpty()
        assertThat(ui.address).isEmpty()
        assertThat(ui.postcode).isEmpty()
        assertThat(ui.maritalStatus).isEmpty()
        assertThat(ui.education).isEmpty()
        assertThat(ui.householdSize).isEmpty()
        assertThat(ui.householdIncome).isEmpty()
        assertThat(ui.employmentStatus).isEmpty()
        assertThat(ui.tags).isEmpty()
        assertThat(ui.isLoading).isFalse()
    }

    @Test
    fun init_profileLoadFailure_setsNetworkError() = runTest {
        // Given
        whenever(authRepository.getProfile())
            .thenReturn(Result.failure(IOException("Network error")))

        // When
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        // Then (ApiErrorParser 네트워크 메시지)
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(ui.isLoading).isFalse()
    }

    @Test
    fun onNameChange_updatesStateAndClearsErrors() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("김철수")

        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEqualTo("김철수")
        assertThat(ui.nameError).isNull()
        assertThat(ui.generalError).isNull()
    }

    @Test
    fun onBirthDateChange_filtersNonDigits_andLimitsTo8Digits() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onBirthDateChange("1990-01-01abc")
        assertThat(viewModel.personalInfoUi.value.birthDate).isEqualTo("19900101")

        viewModel.onBirthDateChange("123456789012")
        assertThat(viewModel.personalInfoUi.value.birthDate).isEqualTo("12345678")
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenNameIsBlank() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.nameError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenBirthDateIsBlank() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.birthDateError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenBirthDateIsInvalid() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19901301")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.birthDateError).isNotNull()
        verify(authRepository, never()).updateProfile(any())
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenGenderIsBlank() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.genderError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenAddressIsBlank() = runTest {
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.addressError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsTrue_withValidRequiredFields_andCallsRepository() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        whenever(authRepository.updateProfile(any())).thenReturn(Result.success(Unit))

        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("FEMALE")
        viewModel.onAddressChange("서울시 강남구")
        viewModel.onPostCodeChange("12345")
        viewModel.onMaritalStatusChange("MARRIED")
        viewModel.onEducationChange("BACHELOR")
        viewModel.onHouseholdSizeChange("3")
        viewModel.onHouseholdIncomeChange("5000")
        viewModel.onEmploymentStatusChange("EMPLOYED")
        viewModel.onAddTag("저소득층")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        assertThat(viewModel.personalInfoUi.value.isLoading).isFalse()

        verify(authRepository).updateProfile(any())
    }

    @Test
    fun submitPersonalInfo_returnsFalse_onNetworkTimeout() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        whenever(authRepository.updateProfile(any())).thenReturn(Result.failure(RuntimeException("timeout")))

        viewModel = PersonalInfoViewModel(authRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.generalError).contains("네트워크 연결이 불안정합니다")
        assertThat(ui.isLoading).isFalse()
    }
}
