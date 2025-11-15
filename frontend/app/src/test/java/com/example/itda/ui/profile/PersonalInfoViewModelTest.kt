package com.example.itda.ui.profile

import com.example.itda.data.model.User
import com.example.itda.data.repository.UserRepository
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
import org.mockito.kotlin.*
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class PersonalInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var userRepository: UserRepository

    private lateinit var viewModel: PersonalInfoViewModel

    private val testUser = User(
        id = "test-id",
        email = "test@example.com",
        name = "홍길동",
        birthDate = "1990-01-01",
        gender = "남성",
        address = "서울시 강남구",
        postcode = "12345",
        maritalStatus = "미혼",
        educationLevel = "대졸",
        householdSize = 3,
        householdIncome = 5000,
        employmentStatus = "재직자"
    )

    // @Before 제거 - 각 테스트에서 모킹합니다

    @Test
    fun init_loadsUserData_successfully() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEqualTo("홍길동")
        assertThat(ui.birthDate).isEqualTo("19900101")
        assertThat(ui.gender).isEqualTo("남성")
        assertThat(ui.address).isEqualTo("서울시 강남구")
        assertThat(ui.postcode).isEqualTo("12345")
        assertThat(ui.maritalStatus).isEqualTo("미혼")
        assertThat(ui.education).isEqualTo("대졸")
        assertThat(ui.householdSize).isEqualTo("3")
        assertThat(ui.householdIncome).isEqualTo("5000")
        assertThat(ui.employmentStatus).isEqualTo("재직자")
        assertThat(ui.isLoading).isFalse()
    }

    @Test
    fun init_handlesNullValues_correctly() = runTest {
        val emptyUser = User(
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
            employmentStatus = null
        )
        whenever(userRepository.getMe()).thenReturn(emptyUser)

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEmpty()
        assertThat(ui.birthDate).isEmpty()
        assertThat(ui.gender).isEmpty()
        assertThat(ui.address).isEmpty()
    }

    @Test
    fun init_handlesLoadError_setsGeneralError() = runTest {
        whenever(userRepository.getMe()).thenThrow(RuntimeException("Network error"))

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        assertThat(viewModel.personalInfoUi.value.generalError).isNotNull()
        assertThat(viewModel.personalInfoUi.value.isLoading).isFalse()
    }

    @Test
    fun onNameChange_updatesName_andClearsErrors() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("김철수")

        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEqualTo("김철수")
        assertThat(ui.nameError).isNull()
        assertThat(ui.generalError).isNull()
    }

    @Test
    fun onBirthDateChange_filtersNonDigits_andLimitsTo8Digits() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onBirthDateChange("1990-01-01abc")
        assertThat(viewModel.personalInfoUi.value.birthDate).isEqualTo("19900101")

        viewModel.onBirthDateChange("123456789012")
        assertThat(viewModel.personalInfoUi.value.birthDate).isEqualTo("12345678")
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenNameIsBlank() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("")
        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.nameError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenBirthDateIsBlank() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.birthDateError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenGenderIsBlank() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.genderError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsFalse_whenAddressIsBlank() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.addressError).isNotNull()
    }

    @Test
    fun submitPersonalInfo_returnsTrue_withValidRequiredFields() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        whenever(
            userRepository.updateProfile(
                any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any()
            )
        ).thenReturn(Result.success(Unit))

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시 강남구")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(viewModel.personalInfoUi.value.isLoading).isFalse()
        assertThat(viewModel.personalInfoUi.value.generalError).isNull()
    }

    @Test
    fun submitPersonalInfo_convertsKoreanToEnum_correctly() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        whenever(
            userRepository.updateProfile(
                any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any()
            )
        ).thenReturn(Result.success(Unit))

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("여성")
        viewModel.onAddressChange("서울시 강남구")
        viewModel.onMaritalStatusChange("기혼")
        viewModel.onEducationChange("대졸")
        viewModel.onEmploymentStatusChange("재직자")

        viewModel.submitPersonalInfo()
        advanceUntilIdle()

        verify(userRepository).updateProfile(
            name = eq("홍길동"),
            birthDate = eq("1990-01-01"),
            gender = eq("FEMALE"),
            address = eq("서울시 강남구"),
            postcode = eq("12345"),
            maritalStatus = eq("MARRIED"),
            educationLevel = eq("BACHELOR"),
            householdSize = eq(3),
            householdIncome = eq(5000),
            employmentStatus = eq("EMPLOYED")
        )
    }

    @Test
    fun submitPersonalInfo_returnsFalse_onNetworkException() = runTest {
        whenever(userRepository.getMe()).thenReturn(testUser)
        whenever(
            userRepository.updateProfile(
                any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any()
            )
        ).thenReturn(Result.failure(RuntimeException("Network timeout")))

        viewModel = PersonalInfoViewModel(userRepository)
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19900101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시 강남구")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(viewModel.personalInfoUi.value.generalError).contains("네트워크 연결이 불안정합니다")
        assertThat(viewModel.personalInfoUi.value.isLoading).isFalse()
    }
}