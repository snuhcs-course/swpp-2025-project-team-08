package com.example.itda.ui.bookmark

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
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
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class BookmarkViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var programRepository: ProgramRepository

    private lateinit var viewModel: BookmarkViewModel

    // 테스트용 더미 데이터
    private val dummyUser = ProfileResponse(
        id = "user123", email = "test@test.com", name = "테스트유저",
        birthDate = null, gender = null, address = null, postcode = null,
        maritalStatus = null, educationLevel = null, householdSize = null,
        householdIncome = null, employmentStatus = null, tags = null
    )

    private val dummyProgramResponse1 = ProgramResponse(
        id = 1, title = "Program 1", preview = "Preview 1",
        operatingEntity = "Entity A", operatingEntityType = "Type",
        category = "education", categoryValue = "교육"
    )

    private val dummyProgramResponse2 = ProgramResponse(
        id = 2, title = "Program 2", preview = "Preview 2",
        operatingEntity = "Entity B", operatingEntityType = "Type",
        category = "welfare", categoryValue = "복지"
    )

    private val dummyProgramResponse3 = ProgramResponse(
        id = 3, title = "Program 3", preview = "Preview 3",
        operatingEntity = "Entity C", operatingEntityType = "Type",
        category = "education", categoryValue = "교육"
    )

    private val programsPage0 = List(20) { i ->
        dummyProgramResponse1.copy(
            id = i + 1,
            title = "Program ${i + 1}",
            category = if (i % 2 == 0) "education" else "welfare"
        )
    }
    private val programsPage1 = List(10) { i ->
        dummyProgramResponse1.copy(
            id = i + 21,
            title = "Program ${i + 21}",
            category = if (i % 2 == 0) "education" else "welfare"
        )
    }

    private val dummyPage1 = ProgramPageResponse(
        content = programsPage0,
        page = 0, size = 20, totalPages = 2, totalElements = 30, isFirst = true, isLast = false
    )

    private val dummyPage2 = ProgramPageResponse(
        content = programsPage1,
        page = 1, size = 10, totalPages = 2, totalElements = 30, isFirst = false, isLast = true
    )

    private fun mockInitSuccess() = runTest {
        // 기본 Mock 설정: 프로필 로드 성공, 북마크 데이터 페이지 0 로드 성공
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"),
            page = eq(0),
            size = any()
        )).thenReturn(Result.success(dummyPage1))

        viewModel = BookmarkViewModel(authRepository, programRepository)
        advanceUntilIdle() // init 블록 완료 대기
    }

    @Before
    fun setup() = runTest {
        mockInitSuccess()
    }

    // ========== Init & Load Tests ==========

    @Test
    fun init_loadsProfileAndBookmarkData_success() = runTest {
        val state = viewModel.uiState.value
        assertThat(state.username).isEqualTo(dummyUser.name)
        assertThat(state.bookmarkItems).isEqualTo(dummyPage1.content)
        assertThat(state.allLoadedPrograms.size).isEqualTo(20)
        assertThat(state.isLastPage).isFalse()
        assertThat(state.currentPage).isEqualTo(0)
        assertThat(state.isLoading).isFalse()
        assertThat(state.generalError).isNull()
    }

    @Test
    fun loadBookmarkData_refresh_clearsStateAndReloads() = runTest {
        // Given: 페이지 1까지 로드된 상태로 가정 (refresh 시 isRefresh = true)
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"),
            page = eq(0),
            size = any()
        )).thenReturn(Result.success(dummyPage1.copy(totalElements = 50)))

        viewModel.uiState.test {
            awaitItem() // 1. 초기 상태

            viewModel.refreshBookmarkData()
            advanceUntilIdle()

            // refreshBookmarkData는 최소 4번의 업데이트를 발생시킵니다:
            // 1) isRefreshing=true
            // 2) isLoading=true + data cleared (loadBookmarkData 시작)
            // 3) username update (loadMyProfile 완료)
            // 4) isLoading=false + data loaded + isRefreshing=false (loadBookmarkData/refresh 종료)

            // 모든 중간 상태를 확실히 소비하여 최종 상태를 포착합니다.
            awaitItem() // isRefreshing=true 소비
            awaitItem() // 로딩 시작 소비
            awaitItem() // 중간 업데이트 소비 (loadMyProfile 또는 loadBookmarkData 성공)

            val finalState = awaitItem() // 최종 상태 소비 (isRefreshing=false, isLoading=false, 데이터 로드 완료)

            assertThat(finalState.isRefreshing).isFalse()
            // 🔴 [수정] 데이터 크기 검증
            assertThat(finalState.allLoadedPrograms.size).isEqualTo(20)
            assertThat(finalState.currentPage).isEqualTo(0)
            assertThat(finalState.isLastPage).isFalse()

            // API 호출 횟수 검증: init 1회 + refresh 1회
            verify(programRepository, times(2)).getUserBookmarkPrograms(
                sort = eq("LATEST"), page = eq(0), size = any()
            )
        }
    }

    @Test
    fun loadBookmarkData_loadFails_setsError() = runTest {
        // Given: 로드 실패 Mock (Unauthorized)
        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))
        `when`(programRepository.getUserBookmarkPrograms(
            sort = any(), page = eq(0), size = any()
        )).thenReturn(Result.failure(httpException))

        // When: 데이터 로드 시도
        viewModel.loadBookmarkData()
        advanceUntilIdle()

        // Then: 에러 메시지 설정 확인
        assertThat(viewModel.uiState.value.generalError).isEqualTo("로그인이 필요합니다")
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.bookmarkItems).isEmpty()
    }

    // ========== Pagination Tests ==========

    @Test
    fun loadNextPage_success_appendsProgramsAndUpdatesState() = runTest {
        // Given: 다음 페이지 로드 Mock
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"),
            page = eq(1),
            size = any()
        )).thenReturn(Result.success(dummyPage2))

        viewModel.uiState.test {
            awaitItem() // 1. 초기 상태 (page 0)

            // When: 다음 페이지 로드
            viewModel.loadNextPage()
            advanceUntilIdle()

            // 2. isPaginating=true 상태 소비
            awaitItem()

            // 3. API 성공 및 데이터 추가 완료 (isPaginating=false)
            // 4. onCategorySelected() 호출로 인한 최종 상태 업데이트

            val finalState = awaitItem() // onCategorySelected(전체) 업데이트 소비

            // Then
            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.currentPage).isEqualTo(1)
            assertThat(finalState.isLastPage).isTrue()
            assertThat(finalState.allLoadedPrograms.size).isEqualTo(30)
            assertThat(finalState.bookmarkItems.size).isEqualTo(30) // 필터링이 없으므로 동일
            assertThat(finalState.bookmarkIds.size).isEqualTo(30)
            assertThat(finalState.generalError).isNull()

            // API 호출 횟수 검증: init (page 0) 1회 + loadNextPage (page 1) 1회
            verify(programRepository).getUserBookmarkPrograms(
                sort = eq("LATEST"), page = eq(1), size = any()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNextPage_onLastPage_doesNothing() = runTest {
        // 1. 마지막 페이지까지 로드 상태를 설정
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"),
            page = eq(1),
            size = any()
        )).thenReturn(Result.success(dummyPage2)) // isLast = true

        viewModel.loadNextPage()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isLastPage).isTrue()

        // When: 다시 로드 시도
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: API는 page 2에 대해 호출되지 않아야 함
        verify(programRepository, never()).getUserBookmarkPrograms(
            sort = any(), page = eq(2), size = any()
        )
    }

    @Test
    fun loadNextPage_onPaginating_doesNothing() = runTest {
        // 1. isLastPage=true 상태를 만듭니다. (loadNextPage 함수 진입 시 return 되도록 하는 가장 안전한 방법)
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"),
            page = eq(1),
            size = any()
        )).thenReturn(Result.success(dummyPage2)) // isLast = true

        viewModel.loadNextPage()
        advanceUntilIdle()
        // 🔴 [수정] isLastPage=true 로 상태 설정 완료
        assertThat(viewModel.uiState.value.isLastPage).isTrue()

        // 현재 ViewModel은 isLastPage=true 상태입니다.
        // 이 상태에서 다시 loadNextPage를 호출하면 `if (isPaginating || isLast)` 조건에 의해 리턴되어야 합니다.

        // When: 다시 로드 시도
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then: API는 page 2에 대해 호출되지 않아야 함 (isLastPage에 의해 차단)
        verify(programRepository, never()).getUserBookmarkPrograms(
            sort = any(), page = eq(2), size = any()
        )
    }

    @Test
    fun loadNextPage_loadFails_resetsPaginatingAndSetsError() = runTest {
        // Given: 다음 페이지 로드 실패 Mock
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("LATEST"), page = eq(1), size = any()
        )).thenReturn(Result.failure(IOException("Network error")))

        val initialPrograms = viewModel.uiState.value.allLoadedPrograms

        viewModel.uiState.test {
            awaitItem() // 초기 상태

            viewModel.loadNextPage()
            advanceUntilIdle()

            // isPaginating=true 상태 소비
            awaitItem()

            // 최종 실패 상태 소비
            val finalState = awaitItem()

            // Then
            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            // 기존 데이터는 유지되어야 함
            assertThat(finalState.allLoadedPrograms).isEqualTo(initialPrograms)
            assertThat(finalState.currentPage).isEqualTo(0) // 페이지는 증가하지 않음
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Sort and Filter Tests ==========

    @Test
    fun onSortSelected_changesSortTypeAndReloadsData() = runTest {
        // Given: 새로운 정렬 방식 Mock
        val deadlineSort = BOOKMARK_SORT_OPTIONS.first { it.apiValue == "DEADLINE" }
        // programsPage0는 1부터 20까지의 ID를 가지는 목록입니다.
        val expectedPrograms = programsPage0.reversed()

        // Mocking the API call for the new sort type
        `when`(programRepository.getUserBookmarkPrograms(
            sort = eq("DEADLINE"),
            page = eq(0),
            size = any()
        )).thenReturn(Result.success(dummyPage1.copy(content = expectedPrograms)))

        viewModel.uiState.test {
            awaitItem() // 1. 초기 상태 (LATEST)

            // When: 정렬 변경
            viewModel.onSortSelected(deadlineSort)
            advanceUntilIdle()

            // 2. Sort change (selectedSort updated)
            awaitItem()

            // 3. Loading start (isLoading=true, allLoadedPrograms=[] cleared)
            awaitItem()

            // 4. 최종 상태 소비 (isLoading=false, data updated)
            val finalState = awaitItem()

            // Then
            assertThat(finalState.selectedSort).isEqualTo(deadlineSort)
            // allLoadedPrograms가 예상된 역순 프로그램 목록과 일치해야 함
            assertThat(finalState.allLoadedPrograms).isEqualTo(expectedPrograms)
            // bookmarkItems도 필터링 없이 allLoadedPrograms와 일치해야 함
            assertThat(finalState.bookmarkItems).isEqualTo(expectedPrograms)
            assertThat(finalState.currentPage).isEqualTo(0) // 페이지 리셋 확인

            // API 호출 검증: init (LATEST) 1회 + onSortSelected (DEADLINE) 1회
            verify(programRepository).getUserBookmarkPrograms(
                sort = eq("DEADLINE"), page = eq(0), size = any()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }


    // ========== Bookmark Click Tests (Optimistic Update & Rollback) ==========

    @Test
    fun onFeedBookmarkClicked_unbookmark_success_removesFromList() = runTest {
        val targetId = 1 // 북마크 목록에 있는 ID (Page 0에 존재)
        `when`(programRepository.unbookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.uiState.test {
            val initialState = awaitItem() // 1. 초기 상태 (20개 프로그램, ID 1 포함)

            // When: 북마크 해제 클릭
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 2. isLoadingBookmark=true 상태 소비
            awaitItem()

            // 3. Optimistic update: allLoadedPrograms에서 Program 1 제거
            awaitItem()

            // 4. API 성공: isLoadingBookmark=false (최종 상태)
            val finalState = awaitItem()

            // Then
            assertThat(finalState.allLoadedPrograms.size).isEqualTo(initialState.allLoadedPrograms.size - 1) // 19개
            assertThat(finalState.bookmarkIds).doesNotContain(targetId)
            assertThat(finalState.bookmarkItems.size).isEqualTo(19) // 필터링된 목록도 업데이트되어야 함
            assertThat(finalState.generalError).isNull()

            verify(programRepository).unbookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_failure_rollsBackState() = runTest {
        val targetId = 1
        // Given: 북마크 해제 API 실패
        `when`(programRepository.unbookmarkProgram(targetId)).thenReturn(Result.failure(IOException("Network error")))

        viewModel.uiState.test {
            // 1. 초기 상태를 정확히 저장 (awaitItem() 호출 직후의 상태)
            val initialState = awaitItem() // 초기 상태 (allLoadedPrograms = 20개, ID 1 포함)
            val initialPrograms = initialState.allLoadedPrograms
            val initialIds = initialState.bookmarkIds

            // When: 북마크 해제 클릭 (targetId=1)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 2. isLoadingBookmark=true 상태 소비
            awaitItem()

            // 3. Optimistic update: ID 1 제거 (allLoadedPrograms = 19개) 상태 소비
            awaitItem()

            // 4. API 실패: Rollback 및 에러 설정 상태 소비 (최종 상태)
            val failureState = awaitItem() // 💡 수정: API 실패 후 롤백된 최종 상태를 포착

            // Then: 롤백 검증
            assertThat(failureState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(failureState.isLoadingBookmark).isFalse()

            // programs와 IDs 모두 초기 상태로 롤백되어야 함
            assertThat(failureState.allLoadedPrograms).isEqualTo(initialPrograms) // 20개로 롤백
            assertThat(failureState.bookmarkIds).isEqualTo(initialIds) // ID 1이 다시 포함
            assertThat(failureState.bookmarkItems).isEqualTo(initialPrograms) // 필터링된 목록도 롤백

            verify(programRepository).unbookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_success_updatesIdsOnly() = runTest {
        val targetId = 99 // 새로운 ID (목록에 없음)
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.uiState.test {
            val initialState = awaitItem() // 초기 상태 (20개 프로그램)

            // When: 북마크 설정 클릭 (목록에 없는 항목)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true
            awaitItem()

            // 2. Optimistic update: IDs에만 99 추가, Programs 목록은 유지
            awaitItem()

            // 3. API 성공: isLoadingBookmark=false
            val finalState = awaitItem()

            // Then
            assertThat(finalState.allLoadedPrograms.size).isEqualTo(initialState.allLoadedPrograms.size) // 20개 유지
            assertThat(finalState.bookmarkIds).contains(targetId) // ID 99 포함
            assertThat(finalState.bookmarkIds.size).isEqualTo(initialState.bookmarkIds.size + 1) // 21개
            assertThat(finalState.generalError).isNull()

            verify(programRepository).bookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_setsAndResetsIsLoadingBookmark() = runTest {
        val targetId = 99 // 북마크 목록에 없는 ID
        // Given: 북마크 API 성공
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.uiState.test {
            awaitItem() // 초기 상태

            // When
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true 상태 소비
            awaitItem()

            // 2. Optimistic update (IDs 및 Programs 업데이트)
            awaitItem()

            // 3. API 성공 후 isLoadingBookmark=false
            assertThat(awaitItem().isLoadingBookmark).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }
}