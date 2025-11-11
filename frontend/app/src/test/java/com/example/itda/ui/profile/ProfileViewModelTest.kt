package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.User
import com.example.itda.data.repository.UserRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: ProfileViewModel

    private val testUser = User(
        id = "test-id",
        email = "test@example.com",
        name = "테스트유저",
        birthDate = "1990-01-01",
        gender = "MALE",
        address = "서울시 강남구",
        postcode = "12345",
        maritalStatus = "SINGLE",
        educationLevel = "BACHELOR",
        householdSize = 3,
        householdIncome = 5000,
        employmentStatus = "EMPLOYED"
    )

    @Before
    fun setup() = runTest {
        `when`(userRepository.getMe()).thenReturn(testUser)
    }

    // ========================================
    // Part 1: 초기화 및 데이터 로딩 테스트
    // ========================================

    @Test
    fun init_loadsUserData_successfully() = runTest {
        // When
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.name).isEqualTo("테스트유저")
            assertThat(state.user.email).isEqualTo("test@example.com")
            assertThat(state.user.gender).isEqualTo("MALE")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
        verify(userRepository, times(1)).getMe()
    }

    @Test
    fun init_emptyUser_whenNoDataSaved() = runTest {
        // Given
        val emptyUser = User(
            id = "",
            email = "",
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
        `when`(userRepository.getMe()).thenReturn(emptyUser)

        // When
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.id).isEmpty()
            assertThat(state.user.name).isNull()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_handlesError_gracefully() = runTest {
        // Given
        `when`(userRepository.getMe()).thenThrow(RuntimeException("Network error"))

        // When
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 2: 데이터 새로고침 테스트
    // ========================================

    @Test
    fun loadProfileData_updatesUserData_successfully() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "업데이트된유저")
        `when`(userRepository.getMe())
            .thenReturn(testUser)  // init 호출
            .thenReturn(updatedUser)  // loadProfileData 호출

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()  // init 완료 대기

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()  // loadProfileData 완료 대기

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.name).isEqualTo("업데이트된유저")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
        verify(userRepository, times(2)).getMe()
    }

    @Test
    fun loadProfileData_callsRepository_correctly() = runTest {
        // Given
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        verify(userRepository, times(2)).getMe() // init + 1번 호출
    }

    @Test
    fun loadProfileData_handlesError_setsLoadingFalse() = runTest {
        // Given
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenThrow(RuntimeException("Error"))

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_multipleCallsInSequence_handledCorrectly() = runTest {
        // Given
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()
        viewModel.loadProfileData()
        advanceUntilIdle()
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        verify(userRepository, times(4)).getMe() // init + 3번 호출
    }

    // ========================================
    // Part 3: 사용자 데이터 검증 테스트
    // ========================================

    @Test
    fun init_loadsAllUserFields_correctly() = runTest {
        // When
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.id).isEqualTo("test-id")
            assertThat(state.user.email).isEqualTo("test@example.com")
            assertThat(state.user.name).isEqualTo("테스트유저")
            assertThat(state.user.birthDate).isEqualTo("1990-01-01")
            assertThat(state.user.gender).isEqualTo("MALE")
            assertThat(state.user.address).isEqualTo("서울시 강남구")
            assertThat(state.user.maritalStatus).isEqualTo("SINGLE")
            assertThat(state.user.educationLevel).isEqualTo("BACHELOR")
            assertThat(state.user.householdSize).isEqualTo(3)
            assertThat(state.user.householdIncome).isEqualTo(5000)
            assertThat(state.user.employmentStatus).isEqualTo("EMPLOYED")
            cancelAndIgnoreRemainingEvents()
        }
    }
}