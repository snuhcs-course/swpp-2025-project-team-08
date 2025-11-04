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
import org.mockito.kotlin.anyOrNull
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

    @Test
    fun login_emptyEmail_showsEmailError() = runTest {
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("password123")

        val result = viewModel.submitLogin()

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
        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("")

        val result = viewModel.submitLogin()

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
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()
        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_emptyEmail_showsEmailError() = runTest {
        viewModel.onSignUpEmailChange("")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_invalidEmail_showsEmailError() = runTest {
        viewModel.onSignUpEmailChange("invalidemail")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("올바른 이메일 형식이 아닙니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_emptyPassword_showsPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("")
        viewModel.onSignUpConfirmChange("")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("비밀번호를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_shortPassword_showsPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("short")
        viewModel.onSignUpConfirmChange("short")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("비밀번호는 8~16자여야 합니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_longPassword_showsPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("verylongpassword12345")
        viewModel.onSignUpConfirmChange("verylongpassword12345")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("비밀번호는 8~16자여야 합니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_emptyConfirmPassword_showsConfirmPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.confirmPasswordError).isEqualTo("비밀번호를 다시 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_passwordMismatch_showsConfirmPasswordError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("differentpass")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.confirmPasswordError).isEqualTo("비밀번호가 일치하지 않습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun signUp_termsNotAgreed_showsGeneralError() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(false)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()
        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("약관에 동의해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyName_showsNameError() = runTest {
        viewModel.onNameChange("")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("이름을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyBirthDate_showsBirthDateError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDateError).isEqualTo("생년월일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_incompleteBirthDate_showsBirthDateError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("1999")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDateError).isEqualTo("8자리를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_invalidBirthDate_showsBirthDateError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("99991301")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDateError).isEqualTo("올바른 생년월일을 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyGender_showsGenderError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.genderError).isEqualTo("성별을 선택해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_emptyAddress_showsAddressError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.addressError).isEqualTo("주소를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loginEmailChange_clearsErrors() = runTest {
        viewModel.onLoginEmailChange("")
        viewModel.submitLogin()

        viewModel.onLoginEmailChange("test@test.com")

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loginPasswordChange_clearsErrors() = runTest {
        viewModel.onLoginPasswordChange("")
        viewModel.submitLogin()

        viewModel.onLoginPasswordChange("password123")

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun login_success_callsRepositoryAndUpdatesState() = runTest {
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.success(Unit))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("password123")

        val result = viewModel.submitLogin()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).login("test@test.com", "password123")

        assertThat(viewModel.isLoggedIn.value).isTrue()
    }

    @Test
    fun login_failure_showsError() = runTest {
        val exception = Exception("Login failed")
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(exception))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("wrongpassword")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(viewModel.isLoggedIn.value).isFalse()
    }

    @Test
    fun signUp_success_callsRepository() = runTest {
        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.success(Unit))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).signup("test@test.com", "password123")
    }

    @Test
    fun signUp_failure_showsError() = runTest {
        val exception = Exception("Signup failed")
        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(exception))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun personalInfo_success_callsRepository() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )).thenReturn(Result.success(Unit))

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updateProfile(
            name = "홍길동",
            birthDate = "1999-01-01",
            gender = "남성",
            address = "서울시",
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null
        )
    }

    @Test
    fun personalInfo_failure_showsError() = runTest {
        val exception = Exception("Update failed")
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )).thenReturn(Result.failure(exception))

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}