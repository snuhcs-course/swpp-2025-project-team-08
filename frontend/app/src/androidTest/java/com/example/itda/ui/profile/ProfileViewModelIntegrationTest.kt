package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.repository.FakeAuthRepository
import com.example.itda.data.source.remote.ProfileResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: ProfileViewModel

    private fun createViewModel() {
        viewModel = ProfileViewModel(fakeAuthRepository)
    }

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

    @Test
    fun init_success_loadsProfileFromRepository() = runTest {
        // Given
        val profile = ProfileResponse(
            id = "1",
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
            tags = listOf("저소득층", "당뇨")
        )
        fakeAuthRepository.getProfileResult = Result.success(profile)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()

        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user).isNotNull()
            assertThat(state.user!!.name).isEqualTo("테스트유저")
            assertThat(state.user!!.email).isEqualTo("test@example.com")
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_networkFailure_setsNetworkError() = runTest {
        // Given
        fakeAuthRepository.getProfileResult = Result.failure(IOException("Network error"))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()

        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user).isNull()
            assertThat(state.isLoading).isFalse()
            // ApiErrorParser 가 IO 예외를 네트워크 에러 메시지로 매핑
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_success_updatesUser() = runTest {
        // Given: 초기 로드는 기본 더미 프로필로
        createViewModel()
        advanceUntilIdle()

        val updatedProfile = fakeAuthRepository.getProfileResult.getOrThrow().copy(
            name = "새이름",
            address = "부산시 해운대구"
        )
        fakeAuthRepository.getProfileResult = Result.success(updatedProfile)
        fakeAuthRepository.getProfileCalled = false

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()

        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user!!.name).isEqualTo("새이름")
            assertThat(state.user!!.address).isEqualTo("부산시 해운대구")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_http401_setsUnauthorizedMessage() = runTest {
        // Given
        createViewModel()
        advanceUntilIdle()

        val errorJson = """{"code":"UNAUTHORIZED","message":"Unauthorized"}"""
        val httpException = HttpException(
            Response.error<Any>(401, errorJson.toResponseBody())
        )
        fakeAuthRepository.getProfileResult = Result.failure(httpException)
        fakeAuthRepository.getProfileCalled = false

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()

        val state = viewModel.profileUi.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.generalError).isEqualTo("로그인이 필요합니다")
    }

    @Test
    fun loadProfileData_failure_doesNotClearPreviousUser() = runTest {
        // Given: 최초 성공
        createViewModel()
        advanceUntilIdle()
        val initialUser = viewModel.profileUi.value.user

        fakeAuthRepository.getProfileResult = Result.failure(IOException("Network error"))
        fakeAuthRepository.getProfileCalled = false

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        val state = viewModel.profileUi.value
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()
        assertThat(state.user).isEqualTo(initialUser)
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(state.isLoading).isFalse()
    }
}
