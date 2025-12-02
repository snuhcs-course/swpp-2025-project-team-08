package com.example.itda.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.Category
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.model.dummyCategories
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.source.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val programRepository: ProgramRepository
) : ViewModel() {

    enum class SortType {
        RANK,
        LATEST
    }

    data class SearchUiState(
        val searchQuery: String = "",
        val recentSearches: List<String> = emptyList(),
        val recommendedKeywords: List<String> = listOf(
            "건강", "돌봄", "치매", "일자리", "복지관", "주거"
        ),
        val isSearching: Boolean = false,
        val hasSearched: Boolean = false,
        val searchResults: List<ProgramResponse> = emptyList(),
        val bookmarkPrograms : List<Int> = emptyList<Int>(),
        val totalElements: Int = 0,
        val currentPage: Int = 0,
        val sortType: SortType = SortType.RANK,
        val categories: List<Category> = dummyCategories,
        val selectedCategory: Category = Category("","전체"),
        val totalPages: Int = 0,
        val isPaginating: Boolean = false,
        val isLastPage: Boolean = false,
        val generalError: String? = null
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            hasSearched = if (query.trim().isEmpty()) false else _uiState.value.hasSearched
        )
    }

    fun onSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return

        val updatedSearches = listOf(query) +
                _uiState.value.recentSearches.filter { it != query }

        _uiState.value = _uiState.value.copy(
            recentSearches = updatedSearches.take(10),
            isSearching = true,
            hasSearched = true,
            currentPage = 0,
            searchResults = emptyList(),
            generalError = null
        )

        performSearch(query, 0, _uiState.value.sortType, _uiState.value.selectedCategory.category)
    }

    fun onSortTypeChange(sortType: SortType) {
        if (_uiState.value.sortType == sortType) return

        val query = _uiState.value.recentSearches.firstOrNull()

        if (query == null) {
            _uiState.value = _uiState.value.copy(
                sortType = sortType
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            sortType = sortType,
            isSearching = true,
            currentPage = 0,
            searchResults = emptyList()
        )

        performSearch(query, 0, sortType, _uiState.value.selectedCategory.category)
    }

    fun onCategorySelected(category: Category) {
        val query = _uiState.value.recentSearches.firstOrNull()

        if (query == null) {
            _uiState.value = _uiState.value.copy(
                selectedCategory = category
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            isSearching = true,
            currentPage = 0,
            searchResults = emptyList()
        )

        performSearch(query, 0, _uiState.value.sortType, _uiState.value.selectedCategory.category)
    }

    fun onLoadNext() {
        val currentState = _uiState.value
        val isLast = currentState.isLastPage

        if (currentState.isPaginating || isLast) return

        _uiState.value = currentState.copy(isPaginating = true)

        val query = currentState.recentSearches.firstOrNull() ?: return
        val nextPage = currentState.currentPage + 1

        performSearch(
            query = query,
            page = nextPage,
            sortType = currentState.sortType,
            category = currentState.selectedCategory.category,
            isLoadMore = true
        )
    }

    fun onRecentSearchClick(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
        onSearch()
    }

    fun onDeleteRecentSearch(query: String) {
        val updatedSearches = _uiState.value.recentSearches.filter { it != query }
        _uiState.value = _uiState.value.copy(
            recentSearches = updatedSearches
        )
    }

    fun onClearAllRecentSearches() {
        _uiState.value = _uiState.value.copy(
            recentSearches = emptyList()
        )
    }

    private fun handleSearchResults(response: PageResponse<ProgramResponse>, isLoadMore: Boolean, requestedPage: Int) {
        _uiState.value = _uiState.value.let { currentState ->
            val currentList = if (isLoadMore) currentState.searchResults else emptyList()
            val newList = response.content

            val combinedList = (currentList + newList).distinctBy { it.id }

            val isLast = response.content.size < 20 || requestedPage >= response.totalPages - 1

            currentState.copy(
                searchResults = combinedList,
                totalElements = response.totalElements,
                totalPages = response.totalPages,
                currentPage = requestedPage,
                isLastPage = isLast,
                isSearching = false,
                isPaginating = false,
                generalError = null
            )
        }
    }

    private fun performSearch(
        query: String,
        page: Int,
        sortType: SortType,
        category: String,
        isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val response = when (sortType) {
                    SortType.RANK -> programRepository.searchByRank(
                        query = query,
                        category = category,
                        page = page,
                        size = 20
                    )
                    SortType.LATEST -> programRepository.searchByLatest(
                        query = query,
                        category = category,
                        page = page,
                        size = 20
                    )
                }

                handleSearchResults(response, isLoadMore, page)
                initBookmarkList()


            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    isPaginating = false,
                    generalError = "검색 중 오류가 발생했습니다: ${e.message}"
                )
            }
        }
    }



    fun updateBookmarkStatusInList(feedId: Int, isBookmarked: Boolean) {
        _uiState.update { currentState ->
            val currentBookmarks = currentState.bookmarkPrograms
            val newBookmarks = if (isBookmarked) {
                (currentBookmarks + feedId).distinct() // 북마크 설정 및 중복 방지
            } else {
                currentBookmarks - feedId // 북마크 해제
            }
            currentState.copy(
                bookmarkPrograms = newBookmarks, // UI 상태의 북마크 목록을 업데이트
            )
        }
    }

    fun initBookmarkList() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isSearching = true,
                bookmarkPrograms = emptyList()
            ) }

            val allBookmarkPrograms = programRepository.getAllUserBookmarks()
            allBookmarkPrograms
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            isSearching = false,
                        )
                    }
                }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            // generalError = null,
                            bookmarkPrograms = response.map { it.id },
                            isSearching = false,
                        )
                    }
                }
        }
    }

    fun onFeedBookmarkClicked(id: Int) {
        viewModelScope.launch {

            // UI 에서 즉시 북마크 상태를 토글합니다.
            val isBookmarked = id in uiState.value.bookmarkPrograms
            val updatedBookmarkPrograms = if (isBookmarked) {
                uiState.value.bookmarkPrograms - id // 북마크 해제 (리스트에서 제거)
            } else {
                uiState.value.bookmarkPrograms + id // 북마크 설정 (리스트에 추가)
            }


            // API 호출
            val apiCall = if(isBookmarked)
                programRepository.unbookmarkProgram(id)
            else
                programRepository.bookmarkProgram(id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    // API 실패 시, UI 상태를 원래대로 되돌립니다.
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            bookmarkPrograms = uiState.value.bookmarkPrograms,
                        )
                    }

                }
                .onSuccess { response ->
                    // API 성공
                    _uiState.update {
                        it.copy(
                            generalError = null,
                            bookmarkPrograms = updatedBookmarkPrograms
                        )
                    }

                }
        }
    }
}
