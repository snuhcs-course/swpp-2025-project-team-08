package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.itda.data.model.User
import com.example.itda.data.repository.FakeAuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalInfoViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: PersonalInfoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        fakeAuthRepository.reset()
    }

    private fun createViewModel() {
        viewModel = PersonalInfoViewModel(fakeAuthRepository)
    }

    @Test
    fun init_success_loadsProfileIntoUiState() = runTest {
        // Given
        val profile = User(
            id = "1",
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
            tags = listOf("저소득층", "당뇨")
        )
        fakeAuthRepository.getProfileResult = Result.success(profile)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()

        val ui = viewModel.personalInfoUi.value
        assertThat(ui.name).isEqualTo("홍길동")
        assertThat(ui.birthDate).isEqualTo("19900101") // '-' 제거
        assertThat(ui.gender).isEqualTo("MALE")
        assertThat(ui.address).isEqualTo("서울시 강남구")
        assertThat(ui.postcode).isEqualTo("12345")
    }

    @Test
    fun init_failure_setsNetworkError() = runTest {
        // Given
        fakeAuthRepository.getProfileResult =
            Result.failure(IOException("Network error"))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(ui.isLoading).isFalse()
    }

    @Test
    fun submitPersonalInfo_success_callsUpdateProfile() = runTest {
        // Given
        fakeAuthRepository.getProfileResult = Result.success(
            User(
                id = "1",
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
        )

        fakeAuthRepository.updateProfileResult = Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        // 필수 필드 채우기
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeAuthRepository.updateProfileCalled).isTrue()
        assertThat(fakeAuthRepository.lastUpdateProfileRequest).isNotNull()
        assertThat(fakeAuthRepository.lastUpdateProfileRequest!!.name).isEqualTo("홍길동")
        // yyyyMMdd -> yyyy-MM-dd 변환 확인
        assertThat(fakeAuthRepository.lastUpdateProfileRequest!!.birthDate)
            .isEqualTo("2000-01-01")
    }

    @Test
    fun submitPersonalInfo_validationError_doesNotCallRepository() = runTest {
        // Given
        fakeAuthRepository.getProfileResult = Result.success(
            fakeAuthRepository.getProfileResult.getOrThrow()
        )
        createViewModel()
        advanceUntilIdle()

        // 이름 비워서 유효성 검사 실패 유도
        viewModel.onNameChange("")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.updateProfileCalled).isFalse()
        assertThat(viewModel.personalInfoUi.value.nameError)
            .isEqualTo("성함을 입력해주세요")
    }

    @Test
    fun submitPersonalInfo_updateFailure_setsGeneralError() = runTest {
        // Given
        fakeAuthRepository.getProfileResult = Result.success(
            fakeAuthRepository.getProfileResult.getOrThrow()
        )
        fakeAuthRepository.updateProfileResult =
            Result.failure(IOException("timeout"))

        createViewModel()
        advanceUntilIdle()

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.updateProfileCalled).isTrue()
        val ui = viewModel.personalInfoUi.value
        assertThat(ui.generalError)
            .isEqualTo("네트워크 연결이 불안정합니다. 다시 시도해주세요")
        assertThat(ui.isLoading).isFalse()
    }
}
