package com.example.itda.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.repository.FakeAuthRepository
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
    private lateinit var fakeRepository: FakeAuthRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeAuthRepository()
        viewModel = AuthViewModel(fakeRepository)
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

        fakeRepository.loginResult = Result.success(Unit)

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeRepository.loginCalled).isTrue()
        assertThat(fakeRepository.lastLoginEmail).isEqualTo(email)
        assertThat(fakeRepository.lastLoginPassword).isEqualTo(password)

        viewModel.isLoggedIn.test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun login_failure_setsGeneralError() = runTest {
        viewModel.onLoginEmailChange("test@example.com")
        viewModel.onLoginPasswordChange("wrongpassword")

        val errorMessage = "로그인 실패"
        fakeRepository.loginResult = Result.failure(Exception(errorMessage))

        val result = viewModel.submitLogin()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeRepository.loginCalled).isTrue()

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
        assertThat(fakeRepository.loginCalled).isFalse()

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
        assertThat(fakeRepository.loginCalled).isFalse()

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

        fakeRepository.signupResult = Result.success(Unit)

        val result = viewModel.submitSignUp()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeRepository.signupCalled).isTrue()
        assertThat(fakeRepository.lastSignupEmail).isEqualTo(email)
        assertThat(fakeRepository.lastSignupPassword).isEqualTo(password)
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
        assertThat(fakeRepository.signupCalled).isFalse()

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
        assertThat(fakeRepository.signupCalled).isFalse()

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

        viewModel.onNameChange(name)
        viewModel.onBirthDateChange(birthDate)
        viewModel.onGenderChange(gender)
        viewModel.onAddressChange(address)

        fakeRepository.updateProfileResult = Result.success(Unit)

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isTrue()
        assertThat(fakeRepository.updateProfileCalled).isTrue()
        assertThat(fakeRepository.lastUpdateProfileName).isEqualTo(name)
        assertThat(fakeRepository.lastUpdateProfileBirthDate).isEqualTo("2000-01-01")
    }

    @Test
    fun updateProfile_emptyName_setsNameError() = runTest {
        viewModel.onNameChange("")
        viewModel.onBirthDateChange("20000101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()
        advanceUntilIdle()

        assertThat(result).isFalse()
        assertThat(fakeRepository.updateProfileCalled).isFalse()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("이름을 입력해주세요")
        }
    }
}