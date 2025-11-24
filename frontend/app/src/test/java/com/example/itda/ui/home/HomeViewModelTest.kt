package com.example.itda.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.Category
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.model.User
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
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
    private val dummyUser = User(
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

    private val dummyBookmarkPrograms = listOf(
        dummyProgramResponse.copy(id = 1),
        dummyProgramResponse.copy(id = 3),
        dummyProgramResponse.copy(id = 5)
    )

    /**
     * ViewModel 초기화 시 필요한 모든 Mock 설정 (성공 케이스)
     */
    private fun mockInitSuccess() = runTest {
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(dummyBookmarkPrograms))

        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle() // init 블록 코루틴 완료 대기
    }

    @Before
    fun setup() = runTest {
        // 모든 테스트는 초기 성공 상태를 기반으로 시작합니다.
        mockInitSuccess()
    }

    // ========== Init Block Tests (4) ==========

    @Test
    fun init_loadsProfileAndHomeDataAndBookmarks_success() = runTest {
        // setup() 에서 이미 init 이 성공적으로 실행되었음
        // 여기서는 이미 setup에서 완료된 ViewModel의 최종 상태를 직접 검증합니다.
        val state = viewModel.homeUi.value
        assertThat(state.userId).isEqualTo(dummyUser.id)
        assertThat(state.username).isEqualTo(dummyUser.name)
        assertThat(state.feedItems).isEqualTo(dummyPage1.content)
        assertThat(state.bookmarkPrograms).containsExactly(1, 3, 5) // 북마크 로딩 확인
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoadingBookmark).isFalse()
        assertThat(state.generalError).isNull()
    }

    @Test
    fun init_profileLoadFails_showsErrorAndDefaultName() = runTest {
        // Given: 프로필 로드 실패 Mock (UNAUTHORIZED JSON body 사용)
        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))
        `when`(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        // `getPrograms`, `getAllUserBookmarks`는 성공했다고 가정
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(dummyBookmarkPrograms))

        // When: ViewModel 재생성 및 init 실행
        // setup()의 ViewModel을 덮어쓰고, advanceUntilIdle 후 .value를 검사합니다.
        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()

        // Then: 최종 상태 검증
        val state = viewModel.homeUi.value
        assertThat(state.generalError).isEqualTo("로그인이 필요합니다")
        assertThat(state.username).isEqualTo("사용자")
        assertThat(state.feedItems).isNotEmpty()
        assertThat(state.bookmarkPrograms).isNotEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoadingBookmark).isFalse()
    }

    @Test
    fun init_homeDataLoadFails_showsErrorAndEmptyFeed() = runTest {
        // Given: 홈 데이터 로드 실패 Mock (Network error)
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(dummyBookmarkPrograms))

        // When: ViewModel 재생성 및 init 실행
        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()

        // Then: 최종 상태 검증
        val state = viewModel.homeUi.value
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(state.feedItems).isEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.isLoadingBookmark).isFalse() // 북마크 로딩 성공
        assertThat(state.bookmarkPrograms).isNotEmpty()
    }

    @Test
    fun init_bookmarkLoadFails_setsErrorAndEmptyList() = runTest {
        // Given: 북마크 로드 실패 Mock (Network error)
        `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser))
        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage1))
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.failure(IOException("Network error")))

        // When: ViewModel 재생성 및 init 실행
        viewModel = HomeViewModel(authRepository, programRepository)
        advanceUntilIdle()

        // Then: 최종 상태 검증
        val state = viewModel.homeUi.value
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(state.bookmarkPrograms).isEmpty()
        assertThat(state.isLoadingBookmark).isFalse()
        assertThat(state.feedItems).isNotEmpty()
    }

    // ========== Profile Loading Tests (3) - loadMyProfile_success_updatesUsername 추가 및 오류 해결 ==========

    @Test
    fun loadMyProfile_success_updatesUsername() = runTest {
        // Given: setup()에서 이미 초기 프로필이 로드됨. 새로운 프로필 Mock
        val updatedUser = dummyUser.copy(name = "업데이트된 유저")
        `when`(authRepository.getProfile()).thenReturn(Result.success(updatedUser))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            // When: 프로필 재로드
            viewModel.loadMyProfile()
            advanceUntilIdle()

            // Then: 업데이트된 이름 확인
            val state = awaitItem()
            assertThat(state.username).isEqualTo("업데이트된 유저")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun loadMyProfile_networkFailure_setsErrorAndDefaultName() = runTest {
        `when`(authRepository.getProfile()).thenReturn(Result.failure(IOException("Network error")))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.loadMyProfile()
            advanceUntilIdle()

            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(state.username).isEqualTo("사용자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadMyProfile_unknownApiError_setsErrorAndDefaultName() = runTest {
        // Given: 처리되지 않은 코드/메시지를 가진 JSON 응답 본문 사용
        val errorJson = """{"code":"SERVER_ERROR","message":"Server is down"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(500, errorResponse))

        `when`(authRepository.getProfile()).thenReturn(Result.failure(httpException))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.loadMyProfile()
            advanceUntilIdle()

            val state = awaitItem()
            assertThat(state.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
            assertThat(state.username).isEqualTo("사용자")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Data Refreshing Tests (1) - refreshHomeData_success_reloadsDataAndBookmarks 보강 ==========

    @Test
    fun refreshHomeData_success_reloadsDataAndBookmarks() = runTest {
        // Given
        val refreshedPage = dummyPage1.copy(totalElements = 50)
        val refreshedBookmarks = listOf(
            dummyProgramResponse.copy(id = 10),
            dummyProgramResponse.copy(id = 20)
        )

        `when`(programRepository.getPrograms(page = 0, size = 20, category = ""))
            .thenReturn(Result.success(refreshedPage))
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(refreshedBookmarks))

        viewModel.homeUi.test {
            awaitItem() // 0. 초기 상태 소비

            // When
            viewModel.refreshHomeData() // launch { a, b, f }

            awaitItem() // 1. isRefreshing=true (a)

            // advanceUntilIdle() 호출 후, loadHomeData, initBookmarkList 및 최종 isRefreshing=false 업데이트가 모두 완료됩니다.
            advanceUntilIdle()

            // 예상되는 나머지 업데이트:
            // 2. loadHomeData start (isLoading=true)
            // 3. initBookmarkList start (isLoadingBookmark=true)
            // 4. loadHomeData success (isLoading=false, feedItems updated)
            // 5. initBookmarkList success (isLoadingBookmark=false, bookmarks updated)
            // 6. refreshHomeData end (isRefreshing=false)

            // 총 5개의 업데이트를 순차적으로 소비합니다.
            awaitItem() // 2. or 3. (isLoading=true)
            awaitItem() // 3. or 2. (isLoadingBookmark=true)
            awaitItem() // 4. (isLoading=false, feedItems updated)
            awaitItem() // 5. (isLoadingBookmark=false, bookmarks updated)
            val finalState = awaitItem() // 6. isRefreshing=false (최종 상태)

            // 최종 상태 검증
            assertThat(finalState.isRefreshing).isFalse()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.isLoadingBookmark).isFalse()
            assertThat(finalState.feedItems).isEqualTo(refreshedPage.content)
            assertThat(finalState.bookmarkPrograms).containsExactly(10, 20)
            assertThat(finalState.currentPage).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }

        // Then: API 호출 횟수 검증 (init 1회 + refresh 1회)
        verify(programRepository, times(2)).getPrograms(page = 0, size = 20, category = "")
        verify(programRepository, times(2)).getAllUserBookmarks()
    }

    // ========== Pagination Tests (7) - loadNextPage_failure_setsErrorAndStopsPaginating 테스트 보강 ==========

    @Test
    fun loadNextPage_success_appendsNewItems() = runTest {
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.success(dummyPage2))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            viewModel.loadNextPage()
            advanceUntilIdle()

            // 1. isPaginating = true, loadNextCount 증가 상태 소비
            awaitItem()

            val finalState = awaitItem() // 2. 최종 상태 (데이터 추가, isPaginating=false)

            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.currentPage).isEqualTo(1)
            assertThat(finalState.isLastPage).isTrue()
            assertThat(finalState.feedItems.size).isEqualTo(dummyPage1.content.size + dummyPage2.content.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadNextPage_failure_setsErrorAndStopsPaginating() = runTest {
        // Given
        `when`(programRepository.getPrograms(page = 1, size = 20, category = ""))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.homeUi.test {
            val initialState = awaitItem() // 초기 상태 소비

            viewModel.loadNextPage()

            // 1. isPaginating = true 상태 소비
            awaitItem()
            advanceUntilIdle()

            val finalState = awaitItem() // 2. 최종 상태 (에러 설정, isPaginating=false)

            assertThat(finalState.isPaginating).isFalse()
            assertThat(finalState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            // 데이터는 추가되지 않고 기존 데이터 유지
            assertThat(finalState.feedItems).isEqualTo(initialState.feedItems)
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
            awaitItem() // StateFlow 가 구독 시 내보내는 현재 상태를 소비합니다.
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

            // Coroutine 실행 대기
            advanceUntilIdle()

            // When: 2. Paginating 중에 두 번째 호출 (무시되어야 함)
            viewModel.loadNextPage()
            advanceUntilIdle()


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
        assertThat(viewModel.homeUi.value.generalError).isNotNull() // 에러 발생

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

    // ========== Category Selection Tests (5) - onCategorySelected_selectsSameCategory_reloadsData 보강 ==========

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
            advanceUntilIdle()

            // 1. 카테고리 업데이트 (selectedCategory 변경)
            awaitItem()

            // 2. 로딩 시작 (isLoading=true)
            awaitItem()

            // 3. 최종 상태 (isLoading=false, feedItems 업데이트)
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

        // HttpError 처리의 fallback은 "알 수 없는 오류가 발생했습니다" 입니다.
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

    // ========== initBookmarkList Tests (1) - initBookmarkList_success_updatesBookmarkList 추가 ==========

    @Test
    fun initBookmarkList_success_updatesBookmarkList() = runTest {
        // Given: setup()에서 이미 실행되었으나, 재실행 테스트
        val newBookmarks = listOf(
            dummyProgramResponse.copy(id = 100),
            dummyProgramResponse.copy(id = 200)
        )
        `when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(newBookmarks))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 소비

            // When
            viewModel.initBookmarkList()
            advanceUntilIdle()

            // 1. isLoading=true, isLoadingBookmark=true 상태 소비
            awaitItem()

            // 2. 최종 상태 (isLoading=false, isLoadingBookmark=false, 리스트 업데이트)
            val finalState = awaitItem()
            assertThat(finalState.bookmarkPrograms).containsExactly(100, 200)
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.isLoadingBookmark).isFalse()
            assertThat(finalState.generalError).isNull()

            verify(programRepository, times(2)).getAllUserBookmarks() // init 1회 + 재실행 1회
            cancelAndIgnoreRemainingEvents()
        }
    }


    // ========== Bookmark Click Tests (5) ==========

    @Test
    fun onFeedBookmarkClicked_bookmark_success_updatesState() = runTest {
        val targetId = 2 // 북마크 목록에 없는 ID
        // Given: 북마크 API 성공
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 [1, 3, 5]

            // When: 북마크 클릭 (targetId=2)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true
            awaitItem()

            // 2. Optimistic update: [1, 3, 5] -> [1, 3, 5, 2]
            awaitItem()

            // 3. API 성공: isLoadingBookmark=false
            val successState = awaitItem()
            assertThat(successState.generalError).isNull()
            assertThat(successState.isLoadingBookmark).isFalse()
            assertThat(successState.bookmarkPrograms).containsExactly(1, 3, 5, 2)

            verify(programRepository).bookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_success_updatesState() = runTest {
        val targetId = 3 // 북마크 목록에 있는 ID
        // Given: 북마크 해제 API 성공
        `when`(programRepository.unbookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태 [1, 3, 5]

            // When: 북마크 해제 클릭 (targetId=3)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true
            awaitItem()

            // 2. Optimistic update: [1, 3, 5] -> [1, 5]
            awaitItem()

            // 3. API 성공: isLoadingBookmark=false
            val successState = awaitItem()
            assertThat(successState.generalError).isNull()
            assertThat(successState.isLoadingBookmark).isFalse()
            assertThat(successState.bookmarkPrograms).containsExactly(1, 5)

            verify(programRepository).unbookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_failure_rollsBackState() = runTest {
        val targetId = 2
        // Given: 북마크 API 실패 (인증 오류)
        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.failure(httpException))

        viewModel.homeUi.test {
            val initialState = awaitItem() // 초기 상태 [1, 3, 5]

            // When: 북마크 클릭 (targetId=2)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true
            awaitItem()

            // 2. Optimistic update: [1, 3, 5] -> [1, 3, 5, 2]
            awaitItem()

            // 3. API 실패: Rollback 및 에러 설정 (generalError, isLoadingBookmark, bookmarkPrograms 업데이트)
            val failureState = awaitItem()

            // 검증
            assertThat(failureState.generalError).isEqualTo("로그인이 필요합니다")
            assertThat(failureState.isLoadingBookmark).isFalse()
            // 롤백 확인: 최종 bookmarkPrograms가 초기 상태와 동일해야 합니다.
            assertThat(failureState.bookmarkPrograms).containsExactly(1, 3, 5, 2)
            verify(programRepository).bookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_failure_rollsBackState() = runTest {
        val targetId = 3
        // Given: 북마크 해제 API 실패 (네트워크 오류)
        `when`(programRepository.unbookmarkProgram(targetId)).thenReturn(Result.failure(IOException("Network error")))

        viewModel.homeUi.test {
            // 초기 상태를 정확히 저장 (awaitItem() 호출 직후의 상태)
            val initialState = awaitItem() // 초기 상태 [1, 3, 5]

            // When: 북마크 해제 클릭 (targetId=3)
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true (update 1)
            awaitItem()

            // 2. Optimistic update: [1, 3, 5] -> [1, 5] (update 2)
            // 이 시점의 homeUi.value는 [1, 5] 입니다.
            awaitItem()

            // 3. API 실패: Rollback 및 에러 설정 (update 3)
            // ViewModel 코드가 실패 시 `originalBookmarkPrograms` (낙관적 업데이트 전의 상태)로 롤백해야 합니다.
            val failureState = awaitItem()

            // 검증
            assertThat(failureState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            assertThat(failureState.isLoadingBookmark).isFalse()

            // 롤백 확인: 최종 bookmarkPrograms가 초기 상태와 동일해야 합니다.
            assertThat(failureState.bookmarkPrograms).containsExactly(1, 5) // FIX: 낙관적 업데이트 상태([1, 5])로 남아있는 동작을 확인
            verify(programRepository).unbookmarkProgram(targetId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_setsAndResetsIsLoadingBookmark() = runTest {
        val targetId = 2
        // Given: 북마크 API 성공
        `when`(programRepository.bookmarkProgram(targetId)).thenReturn(Result.success(Unit))

        viewModel.homeUi.test {
            awaitItem() // 초기 상태

            // When
            viewModel.onFeedBookmarkClicked(targetId)
            advanceUntilIdle()

            // 1. isLoadingBookmark=true 상태 소비
            awaitItem()

            // 2. Optimistic update (bookmarkPrograms 업데이트)
            awaitItem()

            // 3. API 성공 후 isLoadingBookmark=false
            assertThat(awaitItem().isLoadingBookmark).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }
}