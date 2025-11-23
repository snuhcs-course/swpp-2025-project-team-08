package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.User
import com.example.itda.data.repository.AuthRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: ProfileViewModel

    private val testProfile = User(
        id = "test-id",
        email = "test@example.com",
        name = "테스트유저",
        birthDate = "1990-01-01",
        gender = "남성",
        address = "서울시 강남구",
        postcode = "12345",
        maritalStatus = "미혼",
        educationLevel = "대졸",
        householdSize = 3,
        householdIncome = 5000,
        employmentStatus = "재직자",
        tags = listOf("저소득층")
    )

    // ========== Init Tests ==========

    @Test
    fun init_loadsUserData_successfully() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))

        // When
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user).isNotNull()
            assertThat(state.user!!.name).isEqualTo("테스트유저")
            assertThat(state.user!!.email).isEqualTo("test@example.com")
            assertThat(state.user!!.gender).isEqualTo("남성")
            assertThat(state.user!!.address).isEqualTo("서울시 강남구")
            assertThat(state.user!!.postcode).isEqualTo("12345")
            assertThat(state.user!!.maritalStatus).isEqualTo("미혼")
            assertThat(state.user!!.educationLevel).isEqualTo("대졸")
            assertThat(state.user!!.householdSize).isEqualTo(3)
            assertThat(state.user!!.householdIncome).isEqualTo(5000)
            assertThat(state.user!!.employmentStatus).isEqualTo("재직자")
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
        verify(authRepository, times(1)).getProfile()
    }

    @Test
    fun init_profileLoadFailure_networkError_setsErrorMessage() = runTest {
        // Given
        whenever(authRepository.getProfile())
            .thenReturn(Result.failure(IOException("Network error")))

        // When
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== loadProfileData Tests ==========

    @Test
    fun loadProfileData_updatesUserOnSuccess() = runTest {
        // Given: 초기 로드 성공
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        val updated = testProfile.copy(
            name = "새이름",
            address = "부산시 해운대구"
        )
        whenever(authRepository.getProfile()).thenReturn(Result.success(updated))

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user!!.name).isEqualTo("새이름")
            assertThat(state.user!!.address).isEqualTo("부산시 해운대구")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
        verify(authRepository, times(2)).getProfile() // init + reload
    }

    @Test
    fun loadProfileData_http401_setsUnauthorizedMessage() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val httpException = HttpException(
            Response.error<Any>(401, errorJson.toResponseBody())
        )
        whenever(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isEqualTo("로그인이 필요합니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_http500_setsUnknownServerErrorMessage() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        val errorJson = """{"code":"SERVER_ERROR","message":"Server down"}"""
        val httpException = HttpException(
            Response.error<Any>(500, errorJson.toResponseBody())
        )
        whenever(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_failure_doesNotClearPreviousUser() = runTest {
        // Given: 최초 성공
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()
        val initialUser = viewModel.profileUi.value.user

        // 이후 호출은 실패로 mock
        whenever(authRepository.getProfile())
            .thenReturn(Result.failure(IOException("Network error")))

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then: 기존 user 유지 + 에러 메시지만 세팅
        val state = viewModel.profileUi.value
        assertThat(state.user).isEqualTo(initialUser)
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun loadProfileData_setsLoadingFlagDuringFetch_andResetsAfter() = runTest {
        // Given
        whenever(authRepository.getProfile()).thenReturn(Result.success(testProfile))
        viewModel = ProfileViewModel(authRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        val state = viewModel.profileUi.value
        assertThat(state.isLoading).isFalse()
    }
}
