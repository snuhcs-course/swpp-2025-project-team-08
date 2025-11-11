package com.example.itda.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.repository.FakeAuthRepository
import com.example.itda.data.repository.FakeProgramRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeProgramRepository: FakeProgramRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeAuthRepository = FakeAuthRepository()
        fakeProgramRepository = FakeProgramRepository()
        viewModel = AuthViewModel(fakeAuthRepository, fakeProgramRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_success_updatesIsLoggedIn() = runTest {
        val email = "test@example.com"
        val password = "password123"
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPasswordChange(password)

        fakeAuthRepository.loginResult = Result.success(Unit)

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeAuthRepository.loginCalled).isTrue()
        assertThat(fakeAuthRepository.lastLoginEmail).isEqualTo(email)
        assertThat(fakeAuthRepository.lastLoginPassword).isEqualTo(password)

        viewModel.isLoggedIn.test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun login_failure_setsGeneralError() = runTest {
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPasswordChange("wrongpassword")

        val errorMessage = "로그인 실패"
        fakeAuthRepository.loginResult = Result.failure(Exception(errorMessage))

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.loginCalled).isTrue()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.isLoading).isFalse()
        }

        viewModel.isLoggedIn.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun login_emptyEmail_setsEmailError() = runTest {
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("password123")

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.loginCalled).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
        }
    }

    @Test
    fun login_emptyPassword_setsPasswordError() = runTest {
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPasswordChange("")

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.loginCalled).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("비밀번호를 입력해주세요")
        }
    }

    @Test
    fun signup_success_callsRepository() = runTest {
        val email = "newuser@example.com"
        val password = "password123"
        viewModel.onSignUpEmailChange(email)
        viewModel.onSignUpPasswordChange(password)
        viewModel.onSignUpConfirmChange(password)
        viewModel.onAgreeTermsChange(true)

        fakeAuthRepository.signupResult = Result.success(Unit)

        val result = viewModel.submitSignUp()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeAuthRepository.signupCalled).isTrue()
        assertThat(fakeAuthRepository.lastSignupEmail).isEqualTo(email)
        assertThat(fakeAuthRepository.lastSignupPassword).isEqualTo(password)
    }

    @Test
    fun signup_passwordMismatch_setsConfirmPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@example.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("differentpassword")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.signupCalled).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.confirmPasswordError).isEqualTo("비밀번호가 일치하지 않습니다")
        }
    }

    @Test
    fun signup_termsNotAgreed_setsGeneralError() = runTest {
        viewModel.onSignUpEmailChange("test@example.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(false)

        val result = viewModel.submitSignUp()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.signupCalled).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("약관에 동의해주세요")
        }
    }

    @Test
    fun updateProfile_success_callsRepository() = runTest {
        val name = "홍길동"
        val birthDate = "20000101"
        val gender = "남성"
        val address = "서울시"
        val postcode = "12345"

        viewModel.onNameChange(name)
        viewModel.onBirthDateChange(birthDate)
        viewModel.onGenderChange(gender)
        viewModel.onAddressChange(address)
        viewModel.onPostCodeChange(postcode)

        fakeAuthRepository.updateProfileResult = Result.success(Unit)

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeAuthRepository.updateProfileCalled).isTrue()
        assertThat(fakeAuthRepository.lastUpdateProfileName).isEqualTo(name)
        assertThat(fakeAuthRepository.lastUpdateProfileBirthDate).isEqualTo("2000-01-01")
    }

    @Test
    fun updateProfile_emptyName_setsNameError() = runTest {
        viewModel.onNameChange("")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.updateProfileCalled).isFalse()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("이름을 입력해주세요")
        }
    }

    @Test
    fun updateProfile_emptyPostcode_setsPostcodeError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.updateProfileCalled).isFalse()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.postcodeError).isEqualTo("우편번호를 입력해주세요")
        }
    }

    @Test
    fun getExamples_success_loadsExamplePrograms() = runTest {
        val examplePrograms = listOf(
            com.example.itda.data.model.ProgramResponse(
                id = 1,
                title = "청년 취업 지원",
                preview = "취업을 준비하는 청년을 위한 프로그램",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                category = "employment",
                categoryValue = "고용, 일자리"
            ),
            com.example.itda.data.model.ProgramResponse(
                id = 2,
                title = "창업 지원금",
                preview = "창업을 준비하는 청년을 위한 프로그램",
                operatingEntity = "중소벤처기업부",
                operatingEntityType = "중앙정부",
                category = "employment",
                categoryValue = "고용, 일자리"
            )
        )

        fakeProgramRepository.getExamplesResult = Result.success(examplePrograms)

        viewModel.getExamples()
        advanceUntilIdle()

        assertThat(fakeProgramRepository.getExamplesCalled).isTrue()

        viewModel.preferenceUi.test {
            val state = awaitItem()
            assertThat(state.examplePrograms).hasSize(2)
            assertThat(state.examplePrograms[0].title).isEqualTo("청년 취업 지원")
            assertThat(state.examplePrograms[1].title).isEqualTo("창업 지원금")
            assertThat(state.preferenceRequestList).hasSize(2)
            assertThat(state.preferenceRequestList[0].id).isEqualTo(1)
            assertThat(state.preferenceRequestList[0].score).isEqualTo(0)
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
        }
    }

    @Test
    fun getExamples_failure_setsGeneralError() = runTest {
        val errorMessage = "예시 프로그램을 불러올 수 없습니다"
        fakeProgramRepository.getExamplesResult = Result.failure(Exception(errorMessage))

        viewModel.getExamples()
        advanceUntilIdle()

        assertThat(fakeProgramRepository.getExamplesCalled).isTrue()

        viewModel.preferenceUi.test {
            val state = awaitItem()
            assertThat(state.examplePrograms).isEmpty()
            assertThat(state.generalError).isNotNull()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun onPreferenceScoreChange_updatesScore() = runTest {
        val examplePrograms = listOf(
            com.example.itda.data.model.ProgramResponse(
                id = 1,
                title = "청년 취업 지원",
                preview = "취업을 준비하는 청년을 위한 프로그램",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                category = "employment",
                categoryValue = "고용, 일자리"
            )
        )

        fakeProgramRepository.getExamplesResult = Result.success(examplePrograms)
        viewModel.getExamples()
        advanceUntilIdle()

        viewModel.onPreferenceScoreChange(programId = 1, newScore = 5)

        viewModel.preferenceUi.test {
            val state = awaitItem()
            assertThat(state.preferenceRequestList).hasSize(1)
            assertThat(state.preferenceRequestList[0].score).isEqualTo(5)
        }
    }

    @Test
    fun updatePreference_success_callsRepository() = runTest {
        val examplePrograms = listOf(
            com.example.itda.data.model.ProgramResponse(
                id = 1,
                title = "청년 취업 지원",
                preview = "취업을 준비하는 청년을 위한 프로그램",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                category = "employment",
                categoryValue = "고용, 일자리"
            )
        )

        fakeProgramRepository.getExamplesResult = Result.success(examplePrograms)
        viewModel.getExamples()
        advanceUntilIdle()

        viewModel.onPreferenceScoreChange(programId = 1, newScore = 5)

        fakeAuthRepository.updatePreferenceResult = Result.success(Unit)

        val result = viewModel.updatePreference()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeAuthRepository.updatePreferenceCalled).isTrue()
        assertThat(fakeAuthRepository.lastPreferenceScores).isNotNull()
        assertThat(fakeAuthRepository.lastPreferenceScores).hasSize(1)
        assertThat(fakeAuthRepository.lastPreferenceScores!![0].id).isEqualTo(1)
        assertThat(fakeAuthRepository.lastPreferenceScores!![0].score).isEqualTo(5)
    }

    @Test
    fun updatePreference_failure_returnsFalse() = runTest {
        val examplePrograms = listOf(
            com.example.itda.data.model.ProgramResponse(
                id = 1,
                title = "청년 취업 지원",
                preview = "취업을 준비하는 청년을 위한 프로그램",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                category = "employment",
                categoryValue = "고용, 일자리"
            )
        )

        fakeProgramRepository.getExamplesResult = Result.success(examplePrograms)
        viewModel.getExamples()
        advanceUntilIdle()

        val errorMessage = "선호도 업데이트 실패"
        fakeAuthRepository.updatePreferenceResult = Result.failure(Exception(errorMessage))

        val result = viewModel.updatePreference()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeAuthRepository.updatePreferenceCalled).isTrue()
    }
}