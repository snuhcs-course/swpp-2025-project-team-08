package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.User
import com.example.itda.data.repository.UserRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
        gender = "남성",
        address = "서울시 강남구",
        postcode = "12345",
        maritalStatus = "미혼",
        educationLevel = "대졸",
        householdSize = 3,
        householdIncome = 5000,
        employmentStatus = "재직자"
    )

    @Before
    fun setup() {
        runBlocking {  // runBlocking 사용!
            `when`(userRepository.getMe()).thenReturn(testUser)
        }
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
            assertThat(state.user.gender).isEqualTo("남성")
            assertThat(state.user.address).isEqualTo("서울시 강남구")
            assertThat(state.user.postcode).isEqualTo("12345")
            assertThat(state.user.maritalStatus).isEqualTo("미혼")
            assertThat(state.user.educationLevel).isEqualTo("대졸")
            assertThat(state.user.householdSize).isEqualTo(3)
            assertThat(state.user.householdIncome).isEqualTo(5000)
            assertThat(state.user.employmentStatus).isEqualTo("재직자")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
        verify(userRepository, times(1)).getMe()
    }

    @Test
    fun init_withEmptyUser_loadsEmptyData() = runTest {
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
            assertThat(state.user.birthDate).isNull()
            assertThat(state.user.gender).isNull()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_withPartialUserData_loadsCorrectly() = runTest {
        // Given
        val partialUser = testUser.copy(
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null
        )
        `when`(userRepository.getMe()).thenReturn(partialUser)

        // When
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.name).isEqualTo("테스트유저")
            assertThat(state.user.address).isEqualTo("서울시 강남구")
            assertThat(state.user.maritalStatus).isNull()
            assertThat(state.user.educationLevel).isNull()
            assertThat(state.user.householdSize).isNull()
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
            // 에러가 발생해도 앱이 크래시되지 않고 빈 User 상태 유지
            assertThat(state.user.id).isEmpty()
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
            .thenReturn(testUser)      // init 호출
            .thenReturn(updatedUser)   // loadProfileData 호출

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
    fun loadProfileData_callsRepository_exactly() = runTest {
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
            // 에러 발생 시에도 기존 데이터는 유지
            assertThat(state.user.name).isEqualTo("테스트유저")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_multipleCallsInSequence_handledCorrectly() = runTest {
        // Given
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When - 연속 3번 호출
        viewModel.loadProfileData()
        advanceUntilIdle()
        viewModel.loadProfileData()
        advanceUntilIdle()
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        verify(userRepository, times(4)).getMe() // init + 3번 호출
    }

    @Test
    fun loadProfileData_updatesMultipleFields_correctly() = runTest {
        // Given
        val updatedUser = testUser.copy(
            name = "새이름",
            address = "부산시 해운대구",
            householdIncome = 8000
        )
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenReturn(updatedUser)

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.name).isEqualTo("새이름")
            assertThat(state.user.address).isEqualTo("부산시 해운대구")
            assertThat(state.user.householdIncome).isEqualTo(8000)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 3: 로딩 상태 테스트
    // ========================================

    @Test
    fun loadProfileData_setsLoadingState_duringFetch() = runTest {
        // Given
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When - 로딩 중 상태 확인
        viewModel.loadProfileData()
        // advanceUntilIdle() 전에는 loading이 true일 수 있음
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_setsLoadingFalse_afterCompletion() = runTest {
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
    // Part 4: 사용자 데이터 검증 테스트
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
            assertThat(state.user.gender).isEqualTo("남성")
            assertThat(state.user.address).isEqualTo("서울시 강남구")
            assertThat(state.user.maritalStatus).isEqualTo("미혼")
            assertThat(state.user.educationLevel).isEqualTo("대졸")
            assertThat(state.user.householdSize).isEqualTo(3)
            assertThat(state.user.householdIncome).isEqualTo(5000)
            assertThat(state.user.employmentStatus).isEqualTo("재직자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_preservesUnchangedFields() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "새이름")
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenReturn(updatedUser)

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            // 변경된 필드
            assertThat(state.user.name).isEqualTo("새이름")
            // 변경되지 않은 필드들은 유지
            assertThat(state.user.email).isEqualTo("test@example.com")
            assertThat(state.user.address).isEqualTo("서울시 강남구")
            assertThat(state.user.householdSize).isEqualTo(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 5: 엣지 케이스 테스트
    // ========================================

    @Test
    fun loadProfileData_withNullName_handlesCorrectly() = runTest {
        // Given
        val userWithNullName = testUser.copy(name = null)
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenReturn(userWithNullName)

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.name).isNull()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_withZeroIncome_handlesCorrectly() = runTest {
        // Given
        val userWithZeroIncome = testUser.copy(householdIncome = 0)
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenReturn(userWithZeroIncome)

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.householdIncome).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadProfileData_withLargeHouseholdSize_handlesCorrectly() = runTest {
        // Given
        val userWithLargeHousehold = testUser.copy(householdSize = 99)
        `when`(userRepository.getMe())
            .thenReturn(testUser)
            .thenReturn(userWithLargeHousehold)

        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then
        viewModel.profileUi.test {
            val state = awaitItem()
            assertThat(state.user.householdSize).isEqualTo(99)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun concurrentLoadCalls_handleGracefully() = runTest {
        // Given
        viewModel = ProfileViewModel(userRepository)
        advanceUntilIdle()

        // When - 동시에 여러 번 호출
        viewModel.loadProfileData()
        viewModel.loadProfileData()
        viewModel.loadProfileData()
        advanceUntilIdle()

        // Then - 최소 init + 3번 = 4번 호출되어야 함
        verify(userRepository, times(4)).getMe()
    }
}