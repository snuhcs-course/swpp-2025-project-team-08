package com.example.itda.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.Category
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.source.remote.ProfileResponse
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var programRepository: ProgramRepository

    private lateinit var viewModel: HomeViewModel

    // 테스트용 더미 데이터
    private val dummyUser = ProfileResponse(
        id = "user123", email = "test@test.com", name = "테스트유저",
        birthDate = null, gender = null, address = null, postcode = null,
        maritalStatus = null, educationLevel = null, householdSize = null,
        householdIncome = null, employmentStatus = null, tags = null
    )

    private val dummyProgramResponse = ProgramResponse(
        id = 1, title = "Test Program", preview = "Preview",
        operatingEntity = "Entity", operatingEntityType = "Type",
        category = "cat1", categoryValue = "Val1"
    )

    private val dummyPage1 = ProgramPageResponse(
        content = List(20) { i -> dummyProgramResponse.copy(id = i, title = "Program $i") },
        page = 0, size = 20, totalPages = 2, totalElements = 40, isFirst = true, isLast = false
    )

    private val dummyPage2 = ProgramPageResponse(
        content = List(20) { i -> dummyProgramResponse.copy(id = i + 20, title = "Program ${i + 20}") },
        page = 1, size = 20, totalPages = 2, totalElements = 40, isFirst = false, isLast = true
    )

    private val lastPage = ProgramPageResponse(
        content = List(10) { i -> dummyProgramResponse.copy(id = i + 40, title = "Program ${i + 40}") },
        page = 2, size = 10, totalPages = 3, totalElements = 50, isFirst = false, isLast = true
    )

    private fun mockInitSuccess() = runTest {
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))

        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()
    }

    @Before
    fun setup() = runTest {
        mockInitSuccess()
    }

    // ========== Init Block Tests (3) ==========

    @Test
    fun init_loadsProfileAndHomeData_success() = runTest {
        // setup() 에서 이미 init 이 성공적으로 실행되었음
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.userId).isEqualTo(dummyUser.id)
            assertThat(state.username).isEqualTo(dummyUser.name)
            assertThat(state.feedItems).isEqualTo(dummyPage1.content)
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * @수정사항: 빈 응답 본문(`""`) 대신 "UNAUTHORIZED" 코드를 포함하는 JSON 응답 본문을 사용하여
     * `ApiErrorParser`가 `ApiError.Unauthorized()`를 올바르게 반환하는지 테스트합니다.
     */
    @Test
    fun init_profileLoadFails_showsErrorAndDefaultName() = runTest {
        // Given: 프로필 로드 실패 Mock (UNAUTHORIZED JSON body 사용)
        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))
        `when`(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        // `getPrograms`는 성공했다고 가정
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))

        // When: ViewModel 재생성
        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("로그인이 필요합니다") // 올바른 에러 메시지 확인
            assertThat(state.username).isEqualTo("사용자")
            assertThat(state.feedItems).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_homeDataLoadFails_showsErrorAndEmptyFeed() = runTest {
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(state.feedItems).isEmpty()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Profile Loading Tests (3) ==========

    @Test
    fun loadMyProfile_success_updatesUsername() = runTest {
        val updatedUser = dummyUser.copy(name = "업데이트된 유저")
        `when`(authRepository.getProfile()).thenReturn(Result.success(updatedUser))

        viewModel.loadMyProfile()
        advanceUntilIdle()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.username).isEqualTo("업데이트된 유저")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadMyProfile_networkFailure_setsErrorAndDefaultName() = runTest {
        `when`(authRepository.getProfile()).thenReturn(Result.failure(IOException("Network error")))

        viewModel.loadMyProfile()
        advanceUntilIdle()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(state.username).isEqualTo("사용자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * @수정사항: 빈 응답 본문 대신 처리되지 않은 코드/메시지를 가진 JSON 응답 본문을 사용하여
     * `ApiErrorParser`가 `ApiError.Unknown()`으로 폴백하는지 테스트합니다.
     */
    @Test
    fun loadMyProfile_unknownApiError_setsErrorAndDefaultName() = runTest {
        // Given: 처리되지 않은 코드/메시지를 가진 JSON 응답 본문 사용
        val errorJson = """{"code":"SERVER_ERROR","message":"Server is down"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(500, errorResponse))

        `when`(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        viewModel.loadMyProfile()
        advanceUntilIdle()

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
            assertThat(state.username).isEqualTo("사용자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Data Refreshing Tests (5) ==========

    @Test
    fun refreshHomeData_success_reloadsDataAndResetsPage() = runTest {
        val refreshedPage = dummyPage1.copy(totalElements = 50)
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(refreshedPage))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.refreshHomeData()

            // 상태 변화의 순서는 비동기 실행 순서에 따라 달라질 수 있으므로,
            // isRefreshing=true, isLoading=true, isRefreshing=false, isLoading=false 상태 변화를 모두 소비합니다.

            // 1. isRefreshing = true (첫 번째 업데이트)
            assertThat(awaitItem().isRefreshing).isTrue()

            // 2. isRefreshing=false 와 isLoading=true 상태는 순서에 상관없이 발생할 수 있습니다.
            //    여기서는 총 3개의 추가 상태 업데이트가 발생합니다.
            //    (isLoading=true, isRefreshing=false, 최종 결과(isLoading=false))
            //    이 3개의 상태를 모두 소비합니다.

            val stateUpdates = mutableListOf<HomeViewModel.HomeUiState>()
            stateUpdates.add(awaitItem())
            stateUpdates.add(awaitItem())
            val finalState = awaitItem() // 최종 상태

            // 최종 상태 검증
            assertThat(finalState.isRefreshing).isFalse()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.feedItems).isEqualTo(refreshedPage.content)
            assertThat(finalState.currentPage).isEqualTo(0)
            assertThat(finalState.totalElements).isEqualTo(50)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshHomeData_failure_setsErrorAndStopsRefreshing() = runTest {
        // Given: 빈 응답 본문 대신 처리되지 않은 JSON 응답 본문 사용
        val errorJson = """{"code":"INTERNAL_SERVER_ERROR","message":"Internal Server Error"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(500, errorResponse))

        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(httpException))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.refreshHomeData()

            // 1. isRefreshing = true 상태를 소비
            assertThat(awaitItem().isRefreshing).isTrue()

            // 2. 이후 3개의 상태 업데이트(isLoading=true, isRefreshing=false, 최종 에러 상태)가 발생합니다.
            //    비동기 실행 순서에 따라 이들을 순차적으로 소비합니다.
            //    (loadHomeData 시작, refreshHomeData 종료, loadHomeData 실패 종료)
            awaitItem()
            awaitItem()
            val finalState = awaitItem()

            assertThat(finalState.isRefreshing).isFalse()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
            assertThat(finalState.feedItems).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshHomeData_whenPaginating_forcesReload() = runTest {
        // Given: 페이지네이션 중복 호출 방지 로직과 무관하게 refresh는 강제로 호출되어야 함

        // When
        viewModel.refreshHomeData()
        advanceUntilIdle() // API 호출이 완료되기를 기다림

        // Then 1: Mockito 검증 (API 호출 횟수 확인)
        verify(programRepository, times(2)).getPrograms(page = 0, size = 20, category = "")

        // Then 2: 상태 검증 (loadDataCount 확인)
        assertThat(viewModel.homeUi.value.loadDataCount).isEqualTo(2)
        assertThat(viewModel.homeUi.value.isLoading).isFalse()
        assertThat(viewModel.homeUi.value.isRefreshing).isFalse()
    }

    @Test
    fun refreshHomeData_maintainsSelectedCategory() = runTest {
        val newCategory = Category(category = "education", value = "교육")
        val educationPage = dummyPage1.copy(content = emptyList(), totalElements = 0)

        // 카테고리 선택 후
        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.success(educationPage))
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        // When: 새로고침
        viewModel.refreshHomeData()
        advanceUntilIdle()

        // Then: education 카테고리로 2번 호출 (onCategorySelected 내부의 loadHomeData + refreshHomeData)
        verify(programRepository, times(2)).getPrograms(page = 0, size = 20, category = newCategory.category)

        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(newCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshHomeData_clearsErrorOnSuccess() = runTest {
        // 1. 초기 에러 상태 설정 (실패 Mock)
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))
        viewModel.refreshHomeData()
        advanceUntilIdle()
        //assertThat(viewModel.homeUi.value.generalError).isNotNull() // 에러 발생

        // 2. 성공 Mock 설정
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))

        // When: 새로고침 (성공)
        viewModel.refreshHomeData()
        advanceUntilIdle()

        // Then: 에러 상태가 클리어됨
        assertThat(viewModel.homeUi.value.generalError).isNull()
    }

    // ========== Pagination Tests (7) ==========

    @Test
    fun loadNextPage_success_appendsNewItems() = runTest {
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태

            viewModel.loadNextPage()

            assertThat(awaitItem().isPaginating).isTrue() // 1. isPaginating = true

            val finalState = awaitItem() // 2. 최종 상태

            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.currentPage).isEqualTo(1)
            assertThat(finalState.isLastPage).isTrue() // dummyPage2 에서 isLast = true 였음
            assertThat(finalState.feedItems.size).isEqualTo(dummyPage1.content.size + dummyPage2.content.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNextPage_failure_setsErrorAndStopsPaginating() = runTest {
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.loadNextPage()

            awaitItem() // isPaginating = true

            val finalState = awaitItem()

            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(finalState.feedItems).isEqualTo(dummyPage1.content) // 기존 데이터 유지
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNextPage_whenIsLastPage_doesNothing() = runTest {
        // 1. page 1 로드 (isLast = true)
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2)) // isLast = true
        viewModel.loadNextPage()
        advanceUntilIdle()
        assertThat(viewModel.homeUi.value.isLastPage).isTrue()
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(1)

        // When: 마지막 페이지에서 다시 다음 페이지 로드 시도
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: API가 호출되지 않아야 함 (page = 2 호출 X)
        verify(programRepository, never()).getPrograms(page = 2, size = 20, category = "")

        viewModel.homeUi.test {
            awaitItem() // StateFlow 가 구독 시 내보내는 현재 상태를 소비합니다. (추가된 라인)
            expectNoEvents() // 이후 추가적인 상태 업데이트가 없는지 확인합니다.
        }
    }

    @Test
    fun loadNextPage_whenAlreadyPaginating_doesNothing() = runTest {
        // Given
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            // When: 1. 첫 번째 호출 (정상 실행)
            viewModel.loadNextPage()

            val paginatingState = awaitItem()
            assertThat(paginatingState.isPaginating).isTrue()

            // When: 2. Paginating 중에 두 번째 호출 (무시되어야 함)
            viewModel.loadNextPage()

            // Then: API는 1번만 호출되어야 함 (page = 1)
            verify(programRepository, times(1)).getPrograms(page = 1, size = 20, category = "")

            val finalState = awaitItem()
            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.loadNextCount).isEqualTo(1) // 호출 횟수 1회 확인

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNextPage_clearsPreviousErrorOnSuccess() = runTest {
        // 1. 초기 에러 상태 설정 (page 1 실패 Mock)
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))
        viewModel.loadNextPage()
        advanceUntilIdle()
        //assertThat(viewModel.homeUi.value.generalError).isNotNull() // 에러 발생

        // 2. page 1 로드 성공 Mock 설정
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))

        // When: 다음 페이지 로드
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: 에러 상태가 클리어됨
        assertThat(viewModel.homeUi.value.generalError).isNull()
    }

    @Test
    fun loadNextPage_withCategoryFilter_maintainsCategory() = runTest {
        val newCategory = Category(category = "education", value = "교육")
        val educationPage = dummyPage1.copy(isLast = false)
        val nextEducationPage = dummyPage2.copy(isLast = true)

        // 카테고리 선택 후
        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.success(educationPage))
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(0)

        // When: 다음 페이지 로드
        `when`(programRepository.getPrograms(page = 1, size = 20, category = newCategory.category))
            .thenReturn(Result.success(nextEducationPage))
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: page 1, newCategory 로 API 호출 확인
        verify(programRepository).getPrograms(page = 1, size = 20, category = newCategory.category)
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(1)
    }

    @Test
    fun loadNextPage_updatesLoadNextCount() = runTest {
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))

        // When
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.homeUi.value.loadNextCount).isEqualTo(1)

        // When
        viewModel.loadNextPage() // isLastPage=true 이므로 호출 무시
        advanceUntilIdle()

        // Then
        assertThat(viewModel.homeUi.value.loadNextCount).isEqualTo(1)
    }

    // ========== Category Selection Tests (5) ==========

    @Test
    fun onCategorySelected_updatesCategoryAndReloadsData() = runTest {
        val newCategory = Category(category = "education", value = "교육")
        val educationPage = dummyPage1.copy(content = emptyList(), totalElements = 0)

        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.success(educationPage))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            // When
            viewModel.onCategorySelected(newCategory)

            // 1. 카테고리 업데이트 상태
            val state1 = awaitItem()
            assertThat(state1.selectedCategory).isEqualTo(newCategory)

            // 2. 로딩 시작 상태
            assertThat(awaitItem().isLoading).isTrue()

            // 3. 최종 상태
            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.feedItems).isEqualTo(educationPage.content)
            assertThat(finalState.totalElements).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onCategorySelected_callsLoadHomeDataWithNewCategory() = runTest {
        val newCategory = Category(category = "welfare", value = "복지")
        val welfarePage = dummyPage1.copy(totalElements = 50)

        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.success(welfarePage))

        // When
        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        // Then: 올바른 카테고리로 page 0 이 호출되었는지 확인
        verify(programRepository).getPrograms(page = 0, size = 20, category = newCategory.category)

        // `all` 카테고리는 init 시에만 호출됨 (초기 호출 1회 + 카테고리 변경 호출 1회)
        verify(programRepository, times(1)).getPrograms(page = 0, size = 20, category = "")
    }

    @Test
    fun onCategorySelected_loadFailure_setsError() = runTest {
        val newCategory = Category(category = "education", value = "교육")
        val errorJson = """{"code":"CATEGORY_LOAD_FAIL","message":"Category load failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(400, errorResponse))

        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.failure(httpException))

        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        assertThat(viewModel.homeUi.value.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
        assertThat(viewModel.homeUi.value.isLoading).isFalse()
    }

    @Test
    fun onCategorySelected_resetsPagingState() = runTest {
        // 1. 페이지네이션 상태를 1로 만듦
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))
        viewModel.loadNextPage()
        advanceUntilIdle()
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(1)
        assertThat(viewModel.homeUi.value.isLastPage).isTrue()

        // When: 카테고리 변경
        val newCategory = Category(category = "welfare", value = "복지")
        val welfarePage = dummyPage1.copy(isLast = false)
        `when`(programRepository.getPrograms(page = 0, size = 20, category = newCategory.category))
            .thenReturn(Result.success(welfarePage))

        viewModel.onCategorySelected(newCategory)
        advanceUntilIdle()

        // Then: 페이지 상태가 리셋됨
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(0)
        assertThat(viewModel.homeUi.value.isLastPage).isFalse() // 새로운 응답의 isLast를 따름
    }

    @Test
    fun onCategorySelected_selectsSameCategory_reloadsData() = runTest {
        // When: 현재 선택된 카테고리("")를 다시 선택
        val currentCategory = Category(category = "", value = "전체")

        // reload를 위해 Mock을 재설정 (init에서 이미 1회 호출됨)
        `when`(programRepository.getPrograms(page = 0, size = 20, category = currentCategory.category))
            .thenReturn(Result.success(dummyPage1.copy(totalElements = 100)))

        viewModel.onCategorySelected(currentCategory)
        advanceUntilIdle()

        // Then: API가 총 2번 호출됨 (init + reload)
        verify(programRepository, times(2)).getPrograms(page = 0, size = 20, category = "")
        assertThat(viewModel.homeUi.value.totalElements).isEqualTo(100)
    }

    // ========== LoadHomeData Private Function Tests (4) ==========

    @Test
    fun loadHomeData_setsLoadDataCount() = runTest {
        assertThat(viewModel.homeUi.value.loadDataCount).isEqualTo(1) // init 시 1

        // When
        viewModel.refreshHomeData() // 내부적으로 loadHomeData 호출
        advanceUntilIdle()

        // Then
        assertThat(viewModel.homeUi.value.loadDataCount).isEqualTo(2)
    }

    @Test
    fun loadHomeData_successfulLoad_resetsPagingState() = runTest {
        // Given: 페이지네이션 상태를 1로 만듦
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))
        viewModel.loadNextPage()
        advanceUntilIdle()
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(1)

        // When: loadHomeData() 직접 호출 (refreshHomeData 내부 로직과 유사)
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))
        viewModel.refreshHomeData()
        advanceUntilIdle()

        // Then: 페이지 상태가 0으로 리셋됨
        assertThat(viewModel.homeUi.value.currentPage).isEqualTo(0)
    }

    @Test
    fun loadHomeData_httpError_showsUnknownMessage() = runTest {
        // Given: ApiErrorParser에 정의되지 않은 HTTP 에러 코드/메시지 사용
        val errorJson = """{"code":"NOT_FOUND","message":"Resource not found"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(404, errorResponse))

        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(httpException))

        viewModel.refreshHomeData()
        advanceUntilIdle()

        // ApiErrorParser에 정의되지 않은 코드는 ApiError.Unknown()으로 처리됨
        assertThat(viewModel.homeUi.value.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
    }

    @Test
    fun loadHomeData_networkError_showsNetworkMessage() = runTest {
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.refreshHomeData()
        advanceUntilIdle()

        // IOException은 ApiError.NetworkError()로 처리됨
        assertThat(viewModel.homeUi.value.generalError).isEqualTo("네트워크 연결을 확인해주세요")
    }

    // ========== Bookmark Tests (주석 처리된 기능, 19개) ==========
    // 주석 처리된 함수에 대한 테스트는 현재 HomeViewModel.kt 에 존재하는 주석 처리된 코드를 기반으로 작성함
    // 테스트는 주석을 해제할 때를 대비하여 Mocking 및 검증 라인을 주석 처리하여 유지합니다.

/*    // getBookmarkPrograms 테스트 (3)
    @Test
    fun getBookmarkPrograms_success_updatesState() = runTest {
        // Given
        val bookmarkedList = listOf(dummyProgramResponse.copy(id = 101), dummyProgramResponse.copy(id = 102))
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(bookmarkedList))

        // When
        *//* viewModel.getBookmarkPrograms()
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).isEqualTo(listOf(101, 102))
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
        verify(programRepository).getBookmarkPrograms() *//*
    }

    @Test
    fun getBookmarkPrograms_failure_setsError() = runTest {
        // Given
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.failure(IOException("Bookmark load fail")))

        // When
        *//* viewModel.getBookmarkPrograms()
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        } *//*
    }

    @Test
    fun getBookmarkPrograms_resetsIsLoading() = runTest {
        // Given
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(emptyList()))

        // When
        *//* viewModel.getBookmarkPrograms()
        advanceUntilIdle()

        // Then
        viewModel.homeUi.test {
            awaitItem() // isLoading=true
            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        } *//*
    }

    // onFeedBookmarkClicked 테스트 (8)

    @Test
    fun onFeedBookmarkClicked_bookmark_success_updatesBookmarkList() = runTest {
        // Given: 프로그램 2는 북마크 되어있지 않음 (기존 목록에 없음)
        val targetId = 2
        // 북마크 API 성공
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))
        // getBookmarkPrograms Mock
        `when`(programRepository.getBookmarkPrograms())
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 클릭 전 목록
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1), dummyProgramResponse.copy(id = 2)))) // 클릭 후 리로드 목록

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        advanceUntilIdle()

        // Then
        verify(programRepository).bookmarkProgram(targetId)
        verify(programRepository, times(2)).getBookmarkPrograms() // 클릭 전 + 클릭 후 리로드
        // assertThat(viewModel.homeUi.value.bookmarkPrograms).contains(targetId) *//*
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_success_updatesBookmarkList() = runTest {
        // Given: 프로그램 1은 북마크 되어있음 (초기 목록에 1이 있다고 가정)
        val targetId = 1
        // 북마크 해제 API 성공
        `when`(programRepository.unBookmarkProgram(targetId)).thenReturn(Result.success(Unit))
        // getBookmarkPrograms Mock
        `when`(programRepository.getBookmarkPrograms())
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 클릭 전 목록
            .thenReturn(Result.success(emptyList())) // 클릭 후 리로드 목록

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        advanceUntilIdle()

        // Then
        verify(programRepository).unBookmarkProgram(targetId)
        verify(programRepository, times(2)).getBookmarkPrograms()
        // assertThat(viewModel.homeUi.value.bookmarkPrograms).doesNotContain(targetId) *//*
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_failure_setsError() = runTest {
        // Given: 프로그램 2는 북마크 되어있지 않음
        val targetId = 2
        // 북마크 API 실패
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 초기
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.failure(IOException("Bookmark fail")))

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        advanceUntilIdle()

        // Then
        // assertThat(viewModel.homeUi.value.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        verify(programRepository, never()).getBookmarkPrograms() // API 실패 시 리로드 방지 *//*
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_failure_setsError() = runTest {
        // Given: 프로그램 1은 북마크 되어있음
        val targetId = 1
        // 북마크 해제 API 실패
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 초기
        `when`(programRepository.unBookmarkProgram(targetId)).thenReturn(Result.failure(Exception("Unbookmark fail")))

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        advanceUntilIdle()

        // Then
        // assertThat(viewModel.homeUi.value.generalError).isEqualTo("알 수 없는 오류가 발생했습니다") *//*
    }

    @Test
    fun onFeedBookmarkClicked_setsAndResetsIsLoading() = runTest {
        // Given: 성공 시나리오
        val targetId = 2
        `when`(programRepository.getBookmarkPrograms())
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 1. 초기 상태
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1)))) // 2. 클릭 시작 시
            .thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1), dummyProgramResponse.copy(id = 2)))) // 4. getBookmarkPrograms 리로드 시
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)

        // Then:
        viewModel.homeUi.test {
            awaitItem() // 1. isLoading=true (onFeedBookmarkClicked 시작)
            awaitItem() // 2. isLoading=false (bookmarkProgram 성공)
            awaitItem() // 3. isLoading=true (getBookmarkPrograms 시작)
            awaitItem() // 4. isLoading=false (getBookmarkPrograms 성공)
        } *//*
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_alreadyLoading_doesNothing() = runTest {
        // Given: 이미 로딩 중인 상태를 Mocking
        val targetId = 2

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        viewModel.onFeedBookmarkClicked(targetId) // 두 번째 호출 (무시되어야 함)
        advanceUntilIdle()

        // Then: API는 1번만 호출되어야 함
        verify(programRepository, times(1)).bookmarkProgram(targetId) *//*
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_alreadyLoading_doesNothing() = runTest {
        // Given: 이미 로딩 중인 상태를 Mocking 및 북마크 되어있는 상태
        val targetId = 1
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1))))

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        viewModel.onFeedBookmarkClicked(targetId) // 두 번째 호출 (무시되어야 함)
        advanceUntilIdle()

        // Then: API는 1번만 호출되어야 함
        verify(programRepository, times(1)).unBookmarkProgram(targetId) *//*
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_http404Error_setsError() = runTest {
        // Given: 프로그램 2는 북마크 되어있지 않음
        val targetId = 2
        val errorJson = """{"code":"NOT_FOUND","message":"Resource not found"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(404, errorResponse))

        // 북마크 API 실패
        `when`(programRepository.getBookmarkPrograms()).thenReturn(Result.success(listOf(dummyProgramResponse.copy(id = 1))))
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.failure(httpException))

        // When
        *//* viewModel.onFeedBookmarkClicked(targetId)
        advanceUntilIdle()

        // Then
        // assertThat(viewModel.homeUi.value.generalError).isEqualTo("알 수 없는 오류가 발생했습니다") // 404 폴백 *//*
    }*/
}
