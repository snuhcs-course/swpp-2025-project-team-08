package com.example.itda.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.Category
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.ProgramRepository
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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var programRepository: ProgramRepository

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        // NPE 방지를 위해 getAllUserBookmarks에 대한 기본 목킹 추가
        // SearchViewModel의 onSearch()가 initBookmarkList()를 호출하므로,
        // 이를 호출하는 모든 테스트가 안정적으로 동작하도록 합니다.
        runTest {
            Mockito.`when`(programRepository.getAllUserBookmarks())
                .thenReturn(Result.success(emptyList()))
        }
        viewModel = SearchViewModel(programRepository)
    }

    // 검색어 테스트

    @Test
    fun onSearchQueryChange_updatesSearchQuery() = runTest {
        viewModel.onSearchQueryChange("건강")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("건강")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearchQueryChange_emptyQuery_resetsHasSearched() = runTest {
        val mockResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 1,
                    title = "프로그램1",
                    preview = "미리보기1",
                    operatingEntity = "기관1",
                    operatingEntityType = "타입1",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            ),
            totalPages = 1,
            totalElements = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 1,
            empty = false
        )
        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEmpty()
            assertThat(state.hasSearched).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // 검색 테스트

    @Test
    fun onSearch_emptyQuery_doesNothing() = runTest {
        viewModel.onSearchQueryChange("")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.hasSearched).isFalse()
            assertThat(state.isSearching).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_success_updatesState() = runTest {
        val mockResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 1,
                    title = "건강검진 프로그램",
                    preview = "무료 건강검진",
                    operatingEntity = "보건복지부",
                    operatingEntityType = "central",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            ),
            totalPages = 1,
            totalElements = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 1,
            empty = false
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.hasSearched).isTrue()
            assertThat(state.isSearching).isFalse()
            assertThat(state.searchResults).hasSize(1)
            assertThat(state.searchResults[0].title).isEqualTo("건강검진 프로그램")
            assertThat(state.totalElements).isEqualTo(1)
            assertThat(state.recentSearches).containsExactly("건강")
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_addsToRecentSearches() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("돌봄")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).containsExactly("돌봄", "건강").inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_duplicateQuery_movesToTop() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("돌봄")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).containsExactly("건강", "돌봄").inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_moreThan10Searches_keepsOnly10() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        repeat(15) { index ->
            viewModel.onSearchQueryChange("검색$index")
            viewModel.onSearch()
            advanceUntilIdle()
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).hasSize(10)
            assertThat(state.recentSearches.first()).isEqualTo("검색14")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearch_failure_setsGeneralError() = runTest {
        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenThrow(RuntimeException("Network error"))

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isSearching).isFalse()
            assertThat(state.generalError).contains("검색 중 오류가 발생했습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    // 정렬 테스트

    @Test
    fun onSortTypeChange_sameType_doesNothing() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        Mockito.clearInvocations(programRepository)

        viewModel.onSortTypeChange(SearchViewModel.SortType.RANK)
        advanceUntilIdle()

        Mockito.verifyNoMoreInteractions(programRepository)
    }

    @Test
    fun onSortTypeChange_differentType_triggersNewSearch() = runTest {
        val mockRankResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 1,
                    title = "랭크 프로그램",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            ),
            totalPages = 1,
            totalElements = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 1,
            empty = false
        )

        val mockLatestResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 2,
                    title = "최신 프로그램",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            ),
            totalPages = 1,
            totalElements = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 1,
            empty = false
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockRankResponse)
        Mockito.`when`(programRepository.searchByLatest(any(), any(), any(), any()))
            .thenReturn(mockLatestResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSortTypeChange(SearchViewModel.SortType.LATEST)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.sortType).isEqualTo(SearchViewModel.SortType.LATEST)
            assertThat(state.searchResults).hasSize(1)
            assertThat(state.searchResults[0].title).isEqualTo("최신 프로그램")
            assertThat(state.currentPage).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSortTypeChange_noRecentSearches_doesNotSearch() = runTest {
        viewModel.onSortTypeChange(SearchViewModel.SortType.LATEST)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.sortType).isEqualTo(SearchViewModel.SortType.LATEST)
            assertThat(state.isSearching).isFalse()
            cancelAndIgnoreRemainingEvents()
        }

        Mockito.verifyNoInteractions(programRepository)
    }

    // 카테고리 테스트

    @Test
    fun onCategorySelected_triggersNewSearch() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        val healthCategory = Category("health", "보건, 의료")
        viewModel.onCategorySelected(healthCategory)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(healthCategory)
            assertThat(state.currentPage).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onCategorySelected_noRecentSearches_doesNotSearch() = runTest {
        val healthCategory = Category("health", "보건, 의료")
        viewModel.onCategorySelected(healthCategory)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(healthCategory)
            assertThat(state.isSearching).isFalse()
            cancelAndIgnoreRemainingEvents()
        }

        Mockito.verifyNoInteractions(programRepository)
    }

    // 페이지네이션

    @Test
    fun onLoadNext_success_appendsResults() = runTest {
        val firstPageResponse = PageResponse(
            content = List(20) { index ->
                ProgramResponse(
                    id = index + 1,
                    title = "프로그램${index + 1}",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            },
            totalPages = 2,
            totalElements = 25,
            size = 20,
            number = 0,
            first = true,
            last = false,
            numberOfElements = 20,
            empty = false
        )

        val secondPageResponse = PageResponse(
            content = List(5) { index ->
                ProgramResponse(
                    id = index + 21,
                    title = "프로그램${index + 21}",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            },
            totalPages = 2,
            totalElements = 25,
            size = 20,
            number = 1,
            first = false,
            last = true,
            numberOfElements = 5,
            empty = false
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(firstPageResponse)
            .thenReturn(secondPageResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onLoadNext()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).hasSize(25)
            assertThat(state.searchResults[0].id).isEqualTo(1)
            assertThat(state.searchResults[20].id).isEqualTo(21)
            assertThat(state.currentPage).isEqualTo(1)
            assertThat(state.isPaginating).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onLoadNext_alreadyPaginating_doesNothing() = runTest {
        val mockResponse = PageResponse(
            content = List(20) { index ->
                ProgramResponse(
                    id = index,
                    title = "프로그램$index",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            },
            totalPages = 2,
            totalElements = 40,
            size = 20,
            number = 0,
            first = true,
            last = false,
            numberOfElements = 20,
            empty = false
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onLoadNext()
        viewModel.onLoadNext()

        advanceUntilIdle()

        Mockito.verify(programRepository, Mockito.times(2))
            .searchByRank(any(), any(), any(), any())
    }

    @Test
    fun onLoadNext_isLastPage_doesNothing() = runTest {
        val mockResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 1,
                    title = "프로그램1",
                    preview = "미리보기",
                    operatingEntity = "기관",
                    operatingEntityType = "타입",
                    category = "health",
                    categoryValue = "보건, 의료"
                )
            ),
            totalPages = 1,
            totalElements = 1,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 1,
            empty = false
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        Mockito.clearInvocations(programRepository)

        viewModel.onLoadNext()
        advanceUntilIdle()

        Mockito.verifyNoMoreInteractions(programRepository)
    }

    // 최근 검색어 테스트

    @Test
    fun onRecentSearchClick_setsQueryAndSearches() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        Mockito.clearInvocations(programRepository)

        viewModel.onRecentSearchClick("건강")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("건강")
            assertThat(state.hasSearched).isTrue()
            cancelAndIgnoreRemainingEvents()
        }

        Mockito.verify(programRepository, Mockito.times(1))
            .searchByRank(any(), any(), any(), any())
    }

    @Test
    fun onDeleteRecentSearch_removesFromList() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("돌봄")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onDeleteRecentSearch("건강")

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).containsExactly("돌봄")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onClearAllRecentSearches_clearsAllSearches() = runTest {
        val mockResponse = PageResponse<ProgramResponse>(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        Mockito.`when`(programRepository.searchByRank(any(), any(), any(), any()))
            .thenReturn(mockResponse)

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("돌봄")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onClearAllRecentSearches()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun initBookmarkList_success_updatesBookmarkPrograms() = runTest {
        val mockBookmarks = listOf(
            ProgramResponse(1, "p1", "pv1", "op1", "ot1", "c1", "cv1"),
            ProgramResponse(5, "p5", "pv5", "op5", "ot5", "c5", "cv5")
        )

        // Mock the specific call, overriding the one in setup()
        Mockito.`when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.success(mockBookmarks))

        viewModel.initBookmarkList()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).containsExactly(1, 5)
            assertThat(state.isSearching).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun initBookmarkList_failure_setsGeneralError() = runTest {
        val networkException = RuntimeException("Network issue")

        // Mock the specific call, overriding the one in setup()
        Mockito.`when`(programRepository.getAllUserBookmarks())
            .thenReturn(Result.failure(networkException))

        viewModel.initBookmarkList()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.generalError).contains("알 수 없는 오류가 발생했습니다")
            assertThat(state.isSearching).isFalse()
            assertThat(state.bookmarkPrograms).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateBookmarkStatusInList_updates_correctly() = runTest {
        // 1. Add ID 1
        viewModel.updateBookmarkStatusInList(1, true)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).containsExactly(1)
            cancelAndIgnoreRemainingEvents()
        }

        // 2. Add ID 2
        viewModel.updateBookmarkStatusInList(2, true)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).containsExactly(1, 2)
            cancelAndIgnoreRemainingEvents()
        }

        // 3. Remove ID 1
        viewModel.updateBookmarkStatusInList(1, false)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).containsExactly(2)
            cancelAndIgnoreRemainingEvents()
        }

        // 4. Try to remove non-existent ID 3 (should remain [2])
        viewModel.updateBookmarkStatusInList(3, false)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).containsExactly(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_success() = runTest {
        val programId = 100

        // Mock API success for bookmark
        Mockito.`when`(programRepository.bookmarkProgram(programId)).thenReturn(Result.success(Unit))

        viewModel.onFeedBookmarkClicked(programId)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).contains(programId)
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }

        // Verify API was called
        Mockito.verify(programRepository).bookmarkProgram(programId)
        Mockito.verify(programRepository, Mockito.never()).unbookmarkProgram(programId)
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_success() = runTest {
        val programId = 101

        // Simulating the state having the bookmark initially
        viewModel.updateBookmarkStatusInList(programId, true)

        // Mock API success for unbookmark
        Mockito.`when`(programRepository.unbookmarkProgram(programId)).thenReturn(Result.success(Unit))

        viewModel.onFeedBookmarkClicked(programId)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.bookmarkPrograms).doesNotContain(programId)
            assertThat(state.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }

        // Verify API was called
        Mockito.verify(programRepository).unbookmarkProgram(programId)
        Mockito.verify(programRepository, Mockito.never()).bookmarkProgram(programId)
    }

    @Test
    fun onFeedBookmarkClicked_bookmark_failure_revertsState() = runTest {
        val programId = 102

        // Initial state: not bookmarked

        // Mock API failure for bookmark
        val exception = RuntimeException("Auth error")
        Mockito.`when`(programRepository.bookmarkProgram(programId)).thenReturn(Result.failure(exception))

        viewModel.onFeedBookmarkClicked(programId)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            // State should revert to not bookmarked (initial state)
            assertThat(state.bookmarkPrograms).doesNotContain(programId)
            // Generic RuntimeException은 Unknown Error로 처리됩니다.
            assertThat(state.generalError).contains("알 수 없는 오류가 발생했습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onFeedBookmarkClicked_unbookmark_failure_revertsState() = runTest {
        val programId = 103

        // Initial state: bookmarked
        viewModel.updateBookmarkStatusInList(programId, true)

        // Mock API failure for unbookmark
        val exception = RuntimeException("Auth error")
        Mockito.`when`(programRepository.unbookmarkProgram(programId)).thenReturn(Result.failure(exception))

        viewModel.onFeedBookmarkClicked(programId)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            // State should revert to bookmarked
            assertThat(state.bookmarkPrograms).contains(programId)
            // Generic RuntimeException은 Unknown Error로 처리됩니다.
            assertThat(state.generalError).contains("알 수 없는 오류가 발생했습니다")
            cancelAndIgnoreRemainingEvents()
        }
    }
}