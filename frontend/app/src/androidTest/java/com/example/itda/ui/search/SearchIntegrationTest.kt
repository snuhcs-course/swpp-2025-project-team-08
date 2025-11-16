package com.example.itda.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.model.Category
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.repository.FakeProgramRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchViewModel
    private lateinit var fakeProgramRepository: FakeProgramRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeProgramRepository = FakeProgramRepository()
        viewModel = SearchViewModel(fakeProgramRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun search_success_updatesStateWithResults() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

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
        }

        assertThat(fakeProgramRepository.searchByRankCalled).isTrue()
        assertThat(fakeProgramRepository.lastSearchByRankQuery).isEqualTo("건강")
    }

    @Test
    fun search_emptyQuery_doesNotSearch() = runTest {
        viewModel.onSearchQueryChange("")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.hasSearched).isFalse()
            assertThat(state.isSearching).isFalse()
        }

        assertThat(fakeProgramRepository.searchByRankCalled).isFalse()
    }

    @Test
    fun search_addsToRecentSearches() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("돌봄")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.recentSearches).containsExactly("돌봄", "건강").inOrder()
        }
    }

    @Test
    fun sortTypeChange_triggersNewSearch() = runTest {
        val rankResponse = PageResponse(
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

        val latestResponse = PageResponse(
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

        fakeProgramRepository.searchByRankResult = rankResponse
        fakeProgramRepository.searchByLatestResult = latestResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        assertThat(fakeProgramRepository.searchByRankCalled).isTrue()

        viewModel.onSortTypeChange(SearchViewModel.SortType.LATEST)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.sortType).isEqualTo(SearchViewModel.SortType.LATEST)
            assertThat(state.searchResults).hasSize(1)
            assertThat(state.searchResults[0].title).isEqualTo("최신 프로그램")
        }

        assertThat(fakeProgramRepository.searchByLatestCalled).isTrue()
    }

    @Test
    fun categoryChange_triggersNewSearch() = runTest {
        val allCategoryResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 1,
                    title = "전체 프로그램",
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

        val healthCategoryResponse = PageResponse(
            content = listOf(
                ProgramResponse(
                    id = 2,
                    title = "보건 프로그램",
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

        fakeProgramRepository.searchByRankResult = allCategoryResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        fakeProgramRepository.searchByRankResult = healthCategoryResponse

        val healthCategory = Category("health", "보건, 의료")
        viewModel.onCategorySelected(healthCategory)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedCategory).isEqualTo(healthCategory)
            assertThat(state.searchResults).hasSize(1)
            assertThat(state.searchResults[0].title).isEqualTo("보건 프로그램")
        }

        assertThat(fakeProgramRepository.lastSearchByRankCategory).isEqualTo("health")
    }

    @Test
    fun pagination_success_appendsResults() = runTest {
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

        fakeProgramRepository.searchByRankResult = firstPageResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        fakeProgramRepository.searchByRankResult = secondPageResponse

        viewModel.onLoadNext()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).hasSize(25)
            assertThat(state.searchResults[0].id).isEqualTo(1)
            assertThat(state.searchResults[20].id).isEqualTo(21)
            assertThat(state.currentPage).isEqualTo(1)
            assertThat(state.isPaginating).isFalse()
            assertThat(state.isLastPage).isTrue()
        }

        assertThat(fakeProgramRepository.lastSearchByRankPage).isEqualTo(1)
    }

    @Test
    fun pagination_lastPage_doesNotLoadMore() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        val initialCallCount = fakeProgramRepository.lastSearchByRankPage

        viewModel.onLoadNext()
        advanceUntilIdle()

        assertThat(fakeProgramRepository.lastSearchByRankPage).isEqualTo(initialCallCount)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLastPage).isTrue()
            assertThat(state.isPaginating).isFalse()
        }
    }

    @Test
    fun recentSearchClick_triggersSearch() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

        viewModel.onSearchQueryChange("건강")
        viewModel.onSearch()
        advanceUntilIdle()

        viewModel.onRecentSearchClick("건강")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("건강")
            assertThat(state.hasSearched).isTrue()
        }
    }

    @Test
    fun deleteRecentSearch_removesFromList() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

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
        }
    }

    @Test
    fun clearAllRecentSearches_clearsAllSearches() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

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
        }
    }

    @Test
    fun duplicateSearch_movesToTopOfRecentSearches() = runTest {
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

        fakeProgramRepository.searchByRankResult = mockResponse

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
        }
    }
}
