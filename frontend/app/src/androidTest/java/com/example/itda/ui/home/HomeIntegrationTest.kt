package com.example.itda.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.Category
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.FakeAuthRepository
import com.example.itda.data.repository.FakeProgramRepository
import com.example.itda.data.source.remote.ProfileResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * HomeViewModel Integration Test
 * Fake Repository를 사용하여 ViewModel이 상태(UiState)를 올바르게 업데이트하는지 검증
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeProgramRepository: FakeProgramRepository

    // ========== 더미 데이터 정의 ==========
    private val dummyProfile = ProfileResponse(
        id = "user123", email = "test@test.com", name = "테스트 사용자",
        birthDate = "2000-01-01", gender = "남성", address = "서울", postcode = "12345",
        maritalStatus = null, educationLevel = null, householdSize = null,
        householdIncome = null, employmentStatus = null
    )
    private val dummyProgramPage0 = ProgramResponse(
        id = 1, title = "Program 1 (Page 0)", preview = "P1",
        operatingEntity = "A", operatingEntityType = "T1", category = "all", categoryValue = "전체"
    )
    private val dummyProgramPage1 = ProgramResponse(
        id = 2, title = "Program 2 (Page 1)", preview = "P2",
        operatingEntity = "B", operatingEntityType = "T2", category = "all", categoryValue = "전체"
    )
    private val dummyProgramCategory = ProgramResponse(
        id = 3, title = "Program 3 (New Cat)", preview = "P3",
        operatingEntity = "C", operatingEntityType = "T3", category = "new_cat", categoryValue = "새 카테고리"
    )
    private val dummyProgramCategoryPage1 = ProgramResponse(
        id = 4, title = "Program 4 (New Cat Page 1)", preview = "P4",
        operatingEntity = "D", operatingEntityType = "T4", category = "new_cat", categoryValue = "새 카테고리"
    )

    private val page0Response = ProgramPageResponse(
        content = listOf(dummyProgramPage0), totalPages = 2, totalElements = 2,
        size = 1, page = 0, isFirst = true, isLast = false // 마지막 아님
    )
    private val page1Response = ProgramPageResponse(
        content = listOf(dummyProgramPage1), totalPages = 2, totalElements = 2,
        size = 1, page = 1, isFirst = false, isLast = true // 마지막
    )
    private val categoryPage0Response = ProgramPageResponse(
        content = listOf(dummyProgramCategory), totalPages = 2, totalElements = 2,
        size = 1, page = 0, isFirst = true, isLast = false // 마지막 아님
    )
    private val categoryPage1Response = ProgramPageResponse(
        content = listOf(dummyProgramCategoryPage1), totalPages = 2, totalElements = 2,
        size = 1, page = 1, isFirst = false, isLast = true // 마지막
    )
    private val lastPageResponse = ProgramPageResponse( // isLast=true인 0페이지
        content = listOf(dummyProgramPage0), totalPages = 1, totalElements = 1,
        size = 1, page = 0, isFirst = true, isLast = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // ViewModel 생성 전에 Fake Repository를 초기화합니다.
        fakeAuthRepository = FakeAuthRepository()
        fakeProgramRepository = FakeProgramRepository()
    }

    // ========== 1. 초기화(init) 테스트 ==========

    @Test
    fun init_success_loadsProfileAndData() = runTest {
        // Given: Repository가 성공적으로 응답하도록 설정
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)

        // When: ViewModel 생성 (init 블록 실행)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle() // init 코루틴 대기

        // Then: Repository 호출 및 상태 검증
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        assertThat(fakeProgramRepository.lastGetProgramsPage).isEqualTo(0)
        assertThat(fakeProgramRepository.lastGetProgramsCategory).isEqualTo("") // 초기값

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.username).isEqualTo("테스트 사용자")
            assertThat(state.feedItems).hasSize(1)
            assertThat(state.feedItems[0].title).isEqualTo("Program 1 (Page 0)")
            assertThat(state.currentPage).isEqualTo(0)
            assertThat(state.isLastPage).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
        }
    }

    @Test
    fun init_profileFails_setsError_andLoadsData() = runTest {
        // Given: 프로필 로드 실패, 데이터 로드 성공
        fakeAuthRepository.getProfileResult = Result.failure(IOException("프로필 로드 실패"))
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)

        // When
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.username).isEqualTo("사용자") // 프로필 로드 실패
            assertThat(state.feedItems).hasSize(1) // 데이터 로드는 성공
        }
    }

    @Test
    fun init_dataFails_setsError_andLoadsProfile() = runTest {
        // Given: 프로필 로드 성공, 데이터 로드 실패
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.failure(IOException("데이터 로드 실패"))

        // When
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()

        // Then
        assertThat(fakeAuthRepository.getProfileCalled).isTrue()
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.username).isEqualTo("테스트 사용자") // 프로필 로드는 성공
            assertThat(state.feedItems).isEmpty() // 데이터 로드 실패
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun init_profileNameIsNull_usesDefaultName() = runTest {
        // Given: 이름이 null인 프로필 응답
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile.copy(name = null))
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)

        // When
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.username).isEqualTo("사용자") // 기본값 "사용자"
        }
    }

    // ========== 2. 페이지네이션(loadNextPage) 테스트 ==========

    @Test
    fun loadNextPage_success_appendsData_andUpdatesPage() = runTest {
        // Given: 초기 로드(Page 0) 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset() // Repository 호출 상태 리셋

        // When: 다음 페이지(Page 1) 로드
        fakeProgramRepository.getProgramsResult = Result.success(page1Response)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: Page 1이 호출되고 데이터가 *추가*되었는지 검증
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        assertThat(fakeProgramRepository.lastGetProgramsPage).isEqualTo(1)

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.feedItems).hasSize(2) // 1 (Page 0) + 1 (Page 1)
            assertThat(state.feedItems[0].title).isEqualTo("Program 1 (Page 0)")
            assertThat(state.feedItems[1].title).isEqualTo("Program 2 (Page 1)")
            assertThat(state.currentPage).isEqualTo(1)
            assertThat(state.isLastPage).isTrue()
            assertThat(state.isPaginating).isFalse()
        }
    }

    @Test
    fun loadNextPage_failure_setsError_andStopsPaginating() = runTest {
        // Given: 초기 로드(Page 0) 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 다음 페이지 로드 실패
        fakeProgramRepository.getProgramsResult = Result.failure(IOException("Paging Error"))
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isNotNull()
            assertThat(state.isPaginating).isFalse() // Paginating 중지
            assertThat(state.feedItems).hasSize(1) // 기존 Page 0 데이터는 유지
        }
    }

    @Test
    fun loadNextPage_whenLastPage_doesNotCallRepository() = runTest {
        // Given: Page 0이 마지막 페이지인 상태로 초기 로드
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(lastPageResponse) // isLast = true
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 마지막 페이지에서 loadNextPage 호출
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: Repository가 호출되지 않음
        assertThat(fakeProgramRepository.getProgramsCalled).isFalse()
    }

    @Test
    fun loadNextPage_whenAlreadyPaginating_doesNotCallRepository() = runTest {
        // Given: 초기 로드 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response) // [dummyProgramPage0]
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle() // init 완료. feedItems = [dummyProgramPage0]
        fakeProgramRepository.reset()

        fakeProgramRepository.getProgramsResult = Result.success(page1Response) // [dummyProgramPage1]

        // When: Paginating 중에 loadNextPage()를 또 호출
        viewModel.loadNextPage() // 1. 첫 번째 호출 (isPaginating = true 업데이트 *스케줄*)

        // 2. 스케줄된 isPaginating = true 업데이트를 즉시 실행
        testDispatcher.scheduler.runCurrent() // <-- 이 라인을 다시 추가 (주석 해제)

        viewModel.loadNextPage() // 3. 두 번째 호출 (이제 isPaginating = true 이므로 즉시 return)

        // 4. 첫 번째 호출에서 스케줄된 나머지 작업(delay, API 호출)을 마저 실행
        advanceUntilIdle()

        // Then: loadNextCount가 1만 증가해야 함 (첫 번째 호출만 인정됨)
        viewModel.homeUi.test {
            val state = awaitItem()
            // (첫 번째 호출만 성공적으로 실행되어 loadNextCount 1)
            assertThat(state.loadNextCount).isEqualTo(1)
            // (첫 번째 호출의 onSuccess가 실행되어 P0 + P1 = 2개)
            assertThat(state.feedItems).hasSize(2)
        }
    }

    // ========== 3. 카테고리 변경(onCategorySelected) 테스트 ==========

    @Test
    fun onCategorySelected_updatesState_andReloadsDataWithCategory() = runTest {
        // Given: 초기 로드(Page 0, 'all' 카테고리) 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 새 카테고리 선택
        val newCategory = Category(category = "new_cat", value = "새 카테고리")
        fakeProgramRepository.getProgramsResult = Result.success(categoryPage0Response)
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        // Then: Repository가 새 카테고리/Page 0으로 호출되었는지 검증
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        assertThat(fakeProgramRepository.lastGetProgramsPage).isEqualTo(0) // 페이지 리셋
        assertThat(fakeProgramRepository.lastGetProgramsCategory).isEqualTo("new_cat")

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(newCategory)
            assertThat(state.feedItems).hasSize(1) // 새 카테고리 데이터로 교체됨
            assertThat(state.feedItems[0].title).isEqualTo("Program 3 (New Cat)")
            assertThat(state.currentPage).isEqualTo(0) // 페이지 리셋
        }
    }

    @Test
    fun onCategorySelected_failure_setsError() = runTest {
        // Given: 초기 로드 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 카테고리 변경 시 데이터 로드 실패
        val newCategory = Category(category = "fail_cat", value = "실패 카테고리")
        fakeProgramRepository.getProgramsResult = Result.failure(IOException("Category Load Fail"))
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(newCategory)
            assertThat(state.generalError).isNotNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.feedItems).isEmpty() // loadHomeData가 기존 데이터를 비움
        }
    }

    @Test
    fun onCategorySelected_then_loadNextPage_usesCorrectCategoryForPaging() = runTest {
        // Given: 카테고리 변경 완료
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(categoryPage0Response) // isLast=false
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        val newCategory = Category(category = "new_cat", value = "새 카테고리")
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 변경된 카테고리에서 다음 페이지 로드
        fakeProgramRepository.getProgramsResult = Result.success(categoryPage1Response)
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: Page 1, 'new_cat' 카테고리로 호출되었는지 검증
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        assertThat(fakeProgramRepository.lastGetProgramsPage).isEqualTo(1)
        assertThat(fakeProgramRepository.lastGetProgramsCategory).isEqualTo("new_cat")

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.feedItems).hasSize(2)
            assertThat(state.feedItems[0].title).isEqualTo("Program 3 (New Cat)")
            assertThat(state.feedItems[1].title).isEqualTo("Program 4 (New Cat Page 1)")
        }
    }

    // ========== 4. 새로고침(refreshHomeData) 테스트 ==========

    @Test
    fun refreshHomeData_reloadsPage0_andResetsState() = runTest {
        // Given: Page 0, Page 1까지 로드된 상태
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.getProgramsResult = Result.success(page1Response)
        viewModel.loadNextPage()
        advanceUntilIdle() // 현재 Page 1, feedItems 2개

        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(1)
        assertThat(viewModel.homeUi.value.feedItems).hasSize(2)
        fakeProgramRepository.reset()

        // When: 새로고침
        fakeProgramRepository.getProgramsResult = Result.success(page0Response) // Page 0 응답 다시 설정
        viewModel.refreshHomeData()
        advanceUntilIdle()

        // Then: Page 0으로 리셋되었는지 검증
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        assertThat(fakeProgramRepository.lastGetProgramsPage).isEqualTo(0)

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.isRefreshing).isFalse()
            assertThat(state.feedItems).hasSize(1) // 데이터가 Page 0으로 리셋됨
            assertThat(state.feedItems[0].title).isEqualTo("Program 1 (Page 0)")
            assertThat(state.currentPage).isEqualTo(0) // 페이지 리셋됨
            assertThat(state.isLastPage).isFalse()
        }
    }

    @Test
    fun refreshHomeData_failure_setsError() = runTest {
        // Given: 초기 로드 성공
        fakeAuthRepository.getProfileResult = Result.success(dummyProfile)
        fakeProgramRepository.getProgramsResult = Result.success(page0Response)
        viewModel = HomeViewModel(fakeAuthRepository, fakeProgramRepository)
        advanceUntilIdle()
        fakeProgramRepository.reset()

        // When: 새로고침 실패
        fakeProgramRepository.getProgramsResult = Result.failure(IOException("Refresh Fail"))
        viewModel.refreshHomeData()
        advanceUntilIdle()

        // Then
        assertThat(fakeProgramRepository.getProgramsCalled).isTrue()
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.isRefreshing).isFalse()
            assertThat(state.generalError).isNotNull()
            assertThat(state.feedItems).isEmpty() // loadHomeData가 기존 데이터를 비움
        }
    }
}