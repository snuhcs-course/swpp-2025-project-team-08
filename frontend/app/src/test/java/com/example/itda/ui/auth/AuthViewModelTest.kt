package com.example.itda.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var programRepository: ProgramRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() = runBlocking {
        val isLoggedInFlow = MutableStateFlow(false)
        Mockito.`when`(authRepository.isLoggedInFlow).thenReturn(isLoggedInFlow)
        Mockito.`when`(authRepository.getSavedEmail()).thenReturn(null)
        Mockito.`when`(programRepository.getExamples()).thenReturn(Result.success(emptyList()))

        viewModel = AuthViewModel(authRepository, programRepository)
    }

    // ========== Init Block Tests ==========

    @Test
    fun init_loadsLoggedInState() = runTest {
        val isLoggedInFlow = MutableStateFlow(true)
        Mockito.`when`(authRepository.isLoggedInFlow).thenReturn(isLoggedInFlow)
        Mockito.`when`(authRepository.getSavedEmail()).thenReturn(null)
        Mockito.`when`(programRepository.getExamples()).thenReturn(Result.success(emptyList()))

        val vm = AuthViewModel(authRepository, programRepository)
        advanceUntilIdle()

        assertThat(vm.isLoggedIn.value).isTrue()
    }

    @Test
    fun init_loadsSavedEmail() = runTest {
        val isLoggedInFlow = MutableStateFlow(false)
        Mockito.`when`(authRepository.isLoggedInFlow).thenReturn(isLoggedInFlow)
        Mockito.`when`(authRepository.getSavedEmail()).thenReturn("saved@test.com")
        Mockito.`when`(programRepository.getExamples()).thenReturn(Result.success(emptyList()))

        val vm = AuthViewModel(authRepository, programRepository)
        advanceUntilIdle()

        vm.loginUi.test {
            val state = awaitItem()
            assertThat(state.email).isEqualTo("saved@test.com")
            assertThat(state.rememberEmail).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_noSavedEmail() = runTest {
        val isLoggedInFlow = MutableStateFlow(false)
        Mockito.`when`(authRepository.isLoggedInFlow).thenReturn(isLoggedInFlow)
        Mockito.`when`(authRepository.getSavedEmail()).thenReturn(null)
        Mockito.`when`(programRepository.getExamples()).thenReturn(Result.success(emptyList()))

        val vm = AuthViewModel(authRepository, programRepository)
        advanceUntilIdle()

        vm.loginUi.test {
            val state = awaitItem()
            assertThat(state.email).isEmpty()
            assertThat(state.rememberEmail).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Login Tests ==========

    @Test
    fun onLoginEmailChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onLoginEmailChange("test@test.com")

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.email).isEqualTo("test@test.com")
            assertThat(state.emailError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onLoginPasswordChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onLoginPasswordChange("password123")

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.password).isEqualTo("password123")
            assertThat(state.passwordError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onRememberEmailChange_updatesState() = runTest {
        viewModel.onRememberEmailChange(true)

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.rememberEmail).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitLogin_emptyEmail_showsEmailError() = runTest {
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
    fun submitLogin_emptyPassword_showsPasswordError() = runTest {
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
    fun submitLogin_bothEmpty_showsEmailErrorFirst() = runTest {
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
    fun submitLogin_success_callsRepositoryAndUpdatesState() = runTest {
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
    fun submitLogin_successWithRememberEmail_savesEmail() = runTest {
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.success(Unit))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("password123")
        viewModel.onRememberEmailChange(true)

        val result = viewModel.submitLogin()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).saveEmail("test@test.com")
    }

    @Test
    fun submitLogin_successWithoutRememberEmail_clearsEmail() = runTest {
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.success(Unit))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("password123")
        viewModel.onRememberEmailChange(false)

        val result = viewModel.submitLogin()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).clearSavedEmail()
    }

    @Test
    fun submitLogin_userNotFoundError_showsEmailError() = runTest {
        val errorJson = """{"code":"USER_NOT_FOUND","message":"User not found"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(404, errorResponse))

        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(httpException))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("wrongpassword")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("존재하지 않는 계정입니다")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(viewModel.isLoggedIn.value).isFalse()
    }

    @Test
    fun submitLogin_wrongPasswordError_showsPasswordError() = runTest {
        val errorJson = """{"code":"WRONG_PASSWORD","message":"Wrong password"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))

        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(httpException))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("wrongpassword")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("비밀번호가 틀렸습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitLogin_networkError_showsGeneralError() = runTest {
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("password123")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitLogin_unknownError_showsGeneralError() = runTest {
        val exception = Exception("Unknown error")
        Mockito.`when`(authRepository.login(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(exception))

        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPasswordChange("password123")

        val result = viewModel.submitLogin()

        assertThat(result).isFalse()

        viewModel.loginUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
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

    // ========== SignUp Tests ==========

    @Test
    fun onSignUpEmailChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onSignUpEmailChange("test@test.com")

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.email).isEqualTo("test@test.com")
            assertThat(state.emailError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSignUpPasswordChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onSignUpPasswordChange("password123")

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.password).isEqualTo("password123")
            assertThat(state.passwordError).isNull()
            assertThat(state.confirmPasswordError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSignUpConfirmChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onSignUpConfirmChange("password123")

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.confirmPassword).isEqualTo("password123")
            assertThat(state.confirmPasswordError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onAgreeTermsChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onAgreeTermsChange(true)

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.agreeTerms).isTrue()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitSignUp_emptyEmail_showsEmailError() = runTest {
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
    fun submitSignUp_invalidEmail_showsEmailError() = runTest {
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
    fun submitSignUp_emptyPassword_showsPasswordError() = runTest {
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
    fun submitSignUp_shortPassword_showsPasswordError() = runTest {
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
    fun submitSignUp_longPassword_showsPasswordError() = runTest {
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
    fun submitSignUp_emptyConfirmPassword_showsConfirmPasswordError() = runTest {
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
    fun submitSignUp_passwordMismatch_showsConfirmPasswordError() = runTest {
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
    fun submitSignUp_termsNotAgreed_showsGeneralError() = runTest {
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
    fun submitSignUp_success_callsRepository() = runTest {
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
    fun submitSignUp_invalidEmailError_showsEmailError() = runTest {
        val errorJson = """{"code":"INVALID_EMAIL","message":"Invalid email format"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(400, errorResponse))

        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(httpException))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이메일 형식이 올바르지 않습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitSignUp_badPasswordError_showsPasswordError() = runTest {
        val errorJson = """{"code":"BAD_PASSWORD","message":"Password's length should be between 8 and 16"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(400, errorResponse))

        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(httpException))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
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
    fun submitSignUp_emailConflictError_showsEmailError() = runTest {
        val errorJson = """{"code":"EMAIL_CONFLICT","message":"Email conflict"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(409, errorResponse))

        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(httpException))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("이미 사용 중인 이메일입니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitSignUp_networkError_showsGeneralError() = runTest {
        Mockito.`when`(authRepository.signup(anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.onSignUpEmailChange("test@test.com")
        viewModel.onSignUpPasswordChange("password123")
        viewModel.onSignUpConfirmChange("password123")
        viewModel.onAgreeTermsChange(true)

        val result = viewModel.submitSignUp()

        assertThat(result).isFalse()

        viewModel.signUpUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitSignUp_unknownError_showsGeneralError() = runTest {
        val exception = Exception("Unknown error")
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

    // ========== PersonalInfo Tests ==========

    @Test
    fun onNameChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onNameChange("홍길동")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.name).isEqualTo("홍길동")
            assertThat(state.nameError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onBirthDateChange_filtersNonDigitsAndLimitsTo8Chars() = runTest {
        viewModel.onBirthDateChange("1999abc01def01xyz")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDate).isEqualTo("19990101")
            assertThat(state.birthDateError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onBirthDateChange_limitsTo8Digits() = runTest {
        viewModel.onBirthDateChange("199901011234567890")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.birthDate).isEqualTo("19990101")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onGenderChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onGenderChange("남성")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.gender).isEqualTo("남성")
            assertThat(state.genderError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onAddressChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onAddressChange("서울시 강남구")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.address).isEqualTo("서울시 강남구")
            assertThat(state.addressError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onPostCodeChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onPostCodeChange("12345")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.postcode).isEqualTo("12345")
            assertThat(state.postcodeError).isNull()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onMaritalStatusChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onMaritalStatusChange("기혼")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.maritalStatus).isEqualTo("기혼")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onEducationLevelChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onEducationLevelChange("대졸")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.educationLevel).isEqualTo("대졸")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onHouseholdSizeChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onHouseholdSizeChange("4")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.householdSize).isEqualTo("4")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onHouseholdIncomeChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onHouseholdIncomeChange("5000000")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.householdIncome).isEqualTo("5000000")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onEmploymentStatusChange_updatesStateAndClearsErrors() = runTest {
        viewModel.onEmploymentStatusChange("재직 중")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.employmentStatus).isEqualTo("재직 중")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitPersonalInfo_emptyName_showsNameError() = runTest {
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
    fun submitPersonalInfo_emptyBirthDate_showsBirthDateError() = runTest {
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
    fun submitPersonalInfo_incompleteBirthDate_showsBirthDateError() = runTest {
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
    fun submitPersonalInfo_invalidBirthDate_showsBirthDateError() = runTest {
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
    fun submitPersonalInfo_emptyGender_showsGenderError() = runTest {
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
    fun submitPersonalInfo_emptyAddress_showsAddressError() = runTest {
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
    fun submitPersonalInfo_emptyPostcode_showsPostcodeError() = runTest {
        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()
        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.postcodeError).isEqualTo("우편번호를 입력해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitPersonalInfo_success_callsRepository() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
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
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updateProfile(
            name = "홍길동",
            birthDate = "1999-01-01",
            gender = "남성",
            address = "서울시",
            postcode = "12345",
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null,
            tags = null
        )
    }

    @Test
    fun submitPersonalInfo_successWithOptionalFields_callsRepositoryWithAllFields() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
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
        viewModel.onPostCodeChange("12345")
        viewModel.onMaritalStatusChange("기혼")
        viewModel.onEducationLevelChange("대졸")
        viewModel.onHouseholdSizeChange("4")
        viewModel.onHouseholdIncomeChange("5000000")
        viewModel.onEmploymentStatusChange("재직 중")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updateProfile(
            name = "홍길동",
            birthDate = "1999-01-01",
            gender = "남성",
            address = "서울시",
            postcode = "12345",
            maritalStatus = "기혼",
            educationLevel = "대졸",
            householdSize = 4,
            householdIncome = 5000000,
            employmentStatus = "재직 중",
            tags = null
        )
    }

    @Test
    fun submitPersonalInfo_successSetsLoggedInTrue() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
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
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        assertThat(viewModel.isLoggedIn.value).isTrue()
    }

    @Test
    fun submitPersonalInfo_failure_showsError() = runTest {
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
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )).thenReturn(Result.failure(exception))

        viewModel.onNameChange("홍길동")
        viewModel.onBirthDateChange("19990101")
        viewModel.onGenderChange("남성")
        viewModel.onAddressChange("서울시")
        viewModel.onPostCodeChange("12345")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isFalse()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Tag Tests ==========

    @Test
    fun onTagInputChange_updatesState() = runTest {
        viewModel.onTagInputChange("독거노인")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.tagInput).isEqualTo("독거노인")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_validTag_addsToList() = runTest {
        viewModel.addTag("독거노인")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("독거노인")
            assertThat(state.tagInput).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_trimmedTag_addsToList() = runTest {
        viewModel.addTag("  독거노인  ")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("독거노인")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_emptyTag_doesNotAdd() = runTest {
        viewModel.addTag("")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_blankTag_doesNotAdd() = runTest {
        viewModel.addTag("   ")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_duplicateTag_doesNotAddAgain() = runTest {
        viewModel.addTag("독거노인")
        viewModel.addTag("독거노인")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("독거노인")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addTag_multipleTags_addsAll() = runTest {
        viewModel.addTag("독거노인")
        viewModel.addTag("저소득층")
        viewModel.addTag("기초생활수급자")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("독거노인", "저소득층", "기초생활수급자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeTag_existingTag_removesFromList() = runTest {
        viewModel.addTag("독거노인")
        viewModel.addTag("저소득층")

        viewModel.removeTag("독거노인")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("저소득층")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeTag_nonExistingTag_doesNothing() = runTest {
        viewModel.addTag("독거노인")

        viewModel.removeTag("저소득층")

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.selectedTags).containsExactly("독거노인")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitPersonalInfo_withTags_callsRepositoryWithTags() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
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
        viewModel.onPostCodeChange("12345")
        viewModel.addTag("독거노인")
        viewModel.addTag("저소득층")

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updateProfile(
            name = "홍길동",
            birthDate = "1999-01-01",
            gender = "남성",
            address = "서울시",
            postcode = "12345",
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null,
            tags = listOf("독거노인", "저소득층")
        )
    }

    @Test
    fun submitPersonalInfo_withEmptyTags_callsRepositoryWithNullTags() = runTest {
        Mockito.`when`(authRepository.updateProfile(
            anyOrNull(),
            anyOrNull(),
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
        viewModel.onPostCodeChange("12345")
        // No tags added

        val result = viewModel.submitPersonalInfo()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updateProfile(
            name = "홍길동",
            birthDate = "1999-01-01",
            gender = "남성",
            address = "서울시",
            postcode = "12345",
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null,
            tags = null
        )
    }

    // ========== Preference Tests ==========

    @Test
    fun getExamples_success_updatesState() = runTest {
        val examples = listOf(
            ProgramResponse(id = 1, title = "Program 1", preview = "미리보기1", operatingEntity = "기관1", operatingEntityType = "타입1", category = "카테고리1", categoryValue = "value1"),
            ProgramResponse(id = 2, title = "Program 2", preview = "미리보기2", operatingEntity = "기관2", operatingEntityType = "타입2", category = "카테고리2", categoryValue = "value2")
        )

        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.success(examples))

        viewModel.getExamples()

        viewModel.preferenceUi.test {
            val state = awaitItem()
            assertThat(state.examplePrograms).hasSize(2)
            assertThat(state.preferenceRequestList).hasSize(2)
            assertThat(state.preferenceRequestList[0].id).isEqualTo(1)
            assertThat(state.preferenceRequestList[0].score).isEqualTo(0)
            assertThat(state.preferenceRequestList[1].id).isEqualTo(2)
            assertThat(state.preferenceRequestList[1].score).isEqualTo(0)
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getExamples_failure_showsError() = runTest {
        val exception = Exception("Failed to load examples")
        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.failure(exception))

        viewModel.getExamples()

        viewModel.preferenceUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onPreferenceScoreChange_updatesScore() = runTest {
        val examples = listOf(
            ProgramResponse(id = 1, title = "Program 1", preview = "미리보기1", operatingEntity = "기관1", operatingEntityType = "타입1", category = "카테고리1", categoryValue = "value1"),
            ProgramResponse(id = 2, title = "Program 2", preview = "미리보기2", operatingEntity = "기관2", operatingEntityType = "타입2", category = "카테고리2", categoryValue = "value2")
        )

        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.success(examples))

        viewModel.getExamples()

        viewModel.onPreferenceScoreChange(1, 5)

        viewModel.preferenceUi.test {
            val state = awaitItem()
            val updatedPreference = state.preferenceRequestList.find { it.id == 1 }
            assertThat(updatedPreference?.score).isEqualTo(5)

            val unchangedPreference = state.preferenceRequestList.find { it.id == 2 }
            assertThat(unchangedPreference?.score).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updatePreference_success_returnsTrue() = runTest {
        val examples = listOf(
            ProgramResponse(id = 1, title = "Program 1", preview = "미리보기1", operatingEntity = "기관1", operatingEntityType = "타입1", category = "카테고리1", categoryValue = "value1")
        )

        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.success(examples))
        Mockito.`when`(authRepository.updatePreference(any()))
            .thenReturn(Result.success(Unit))

        viewModel.getExamples()
        val result = viewModel.updatePreference()

        assertThat(result).isTrue()
        Mockito.verify(authRepository).updatePreference(any())
    }

    @Test
    fun updatePreference_failure_returnsFalse() = runTest {
        val examples = listOf(
            ProgramResponse(id = 1, title = "Program 1", preview = "미리보기1", operatingEntity = "기관1", operatingEntityType = "타입1", category = "카테고리1", categoryValue = "value1")
        )
        val exception = Exception("Failed to update preference")

        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.success(examples))
        Mockito.`when`(authRepository.updatePreference(any()))
            .thenReturn(Result.failure(exception))

        viewModel.getExamples()
        val result = viewModel.updatePreference()

        assertThat(result).isFalse()
    }

    @Test
    fun updatePreference_failure_showsError() = runTest {
        val examples = listOf(
            ProgramResponse(id = 1, title = "Program 1", preview = "미리보기1", operatingEntity = "기관1", operatingEntityType = "타입1", category = "카테고리1", categoryValue = "value1")
        )
        val exception = Exception("Failed to update preference")

        Mockito.`when`(programRepository.getExamples())
            .thenReturn(Result.success(examples))
        Mockito.`when`(authRepository.updatePreference(any()))
            .thenReturn(Result.failure(exception))

        viewModel.getExamples()
        viewModel.updatePreference()

        viewModel.personalInfoUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
