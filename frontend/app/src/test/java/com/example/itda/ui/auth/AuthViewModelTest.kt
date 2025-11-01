package com.example.itda.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.repository.AuthRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        val isLoggedInFlow = MutableStateFlow(false)
        Mockito.`when`(authRepository.isLoggedInFlow).thenReturn(isLoggedInFlow)

        viewModel = AuthViewModel(authRepository)
    }

    // ========================================
    // Part 1: 로그인 입력값 검증 테스트
    // ========================================

    @Test
    fun login_emptyEmail_showsEmailError() = runTest {
        // Given
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("password123")

        // When
        val result = viewModel.submitLogin()

        // Then
        assertThat(result).isFalse()
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
            assertThat(state.passwordError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun login_emptyPassword_showsPasswordError() = runTest {
        // Given
        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("")

        // When
        val result = viewModel.submitLogin()

        // Then
        assertThat(result).isFalse()
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isNull()
            assertThat(state.passwordError).isEqualTo("비밀번호를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun login_bothEmpty_showsEmailErrorFirst() = runTest {
        // Given
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("")

        // When
        val result = viewModel.submitLogin()

        // Then
        assertThat(result).isFalse()
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 2: 회원가입 입력값 검증 테스트
    // ========================================

    @Test
    fun signUp_emptyEmail_showsEmailError() = runTest {
        // Given
        viewModel.onSignUpEmailChange("")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        // When
        val result = viewModel.submitSignUp()

        // Then
        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 3: 개인정보 입력값 검증 테스트
    // ========================================

    @Test
    fun personalInfo_emptyName_showsNameError() = runTest {
        // Given
        viewModel.onNameChange("")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시")

        // When
        val result = viewModel.submitPersonalInfo()

        // Then
        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("이름을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyBirthDate_showsBirthDateError() = runTest {
        // Given
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("서울시")

        // When
        val result = viewModel.submitPersonalInfo()

        // Then
        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDateError).isEqualTo("생년월일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_incompleteBirthDate_showsBirthDateError() = runTest {
        // Given
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("1999")
        viewModel.onGenderChange("M")
        viewModel.onAddressChange("서울시")

        // When
        val result = viewModel.submitPersonalInfo()

        // Then
        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDateError).isEqualTo("8자리를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyGender_showsGenderError() = runTest {
        // Given
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("")
        viewModel.onAddressChange("서울시")

        // When
        val result = viewModel.submitPersonalInfo()

        // Then
        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.genderError).isEqualTo("성별을 선택해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyAddress_showsAddressError() = runTest {
        // Given
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("MALE")
        viewModel.onAddressChange("")

        // When
        val result = viewModel.submitPersonalInfo()

        // Then
        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.addressError).isEqualTo("주소를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 4: StateFlow 변화 테스트
    // ========================================

    @Test
    fun loginEmailChange_clearsErrors() = runTest {
        // Given
        viewModel.onLoginEmailChange("")
        viewModel.submitLogin()

        // When
        viewModel.onLoginEmailChange("test@test.com")

        // Then
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loginPasswordChange_clearsErrors() = runTest {
        // Given
        viewModel.onLoginPasswordChange("")
        viewModel.submitLogin() // 에러 발생

        // When
        viewModel.onLoginPasswordChange("password123")

        // Then: 에러가 사라짐
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}