package com.example.itda.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.Category
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.model.dummyCategories
import com.example.itda.data.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val totalElements: Int = 0,
        val currentPage: Int = 0,
        val sortType: SortType = SortType.RANK,
        val categories: List<Category> = dummyCategories,
        val selectedCategory: Category = Category("","전체"),
        val feedItems: List<ProgramResponse> = emptyList(),
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true,
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

        _uiState.value = _uiState.value.copy(
            sortType = sortType,
            isSearching = true,
            currentPage = 0,
            searchResults = emptyList()
        )

        val query = _uiState.value.recentSearches.firstOrNull() ?: return
        performSearch(query, 0, sortType, _uiState.value.selectedCategory.category)
    }

    fun onCategorySelected(category: Category) {

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            isSearching = true,
            currentPage = 0,
            searchResults = emptyList()
        )

        val query = _uiState.value.recentSearches.firstOrNull() ?: return
        performSearch(query, 0, _uiState.value.sortType, _uiState.value.selectedCategory.category)
    }

    fun onLoadNext() {
        val currentState = _uiState.value

        if (currentState.isLoadingMore || !currentState.hasMorePages) return

        _uiState.value = currentState.copy(isLoadingMore = true)

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

    private fun handleSearchResults(response: PageResponse<ProgramResponse>, isLoadMore: Boolean) {
        _uiState.value = _uiState.value.let { currentState ->
            val currentList = if (isLoadMore) currentState.searchResults else emptyList()
            val newList = response.content

            val combinedList = (currentList + newList).distinctBy { it.id }

            currentState.copy(
                searchResults = combinedList,
                totalElements = response.totalElements,
                currentPage = response.number,
                hasMorePages = !response.last,
                isSearching = false,
                isLoadingMore = false,
                generalError = null
            )
        }
    }

    private fun performSearch(
        query: String,
        page: Int,
        sortType: SortType,
        category: String?,
        isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val response = when (sortType) {
                    SortType.RANK -> programRepository.searchByRank(
                        query = query,
                        page = page,
                        size = 10,
                        category = category
                    )
                    SortType.LATEST -> programRepository.searchByLatest(
                        query = query,
                        page = page,
                        size = 10,
                        category = category
                    )
                }

                handleSearchResults(response, isLoadMore)



            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    isLoadingMore = false,
                    generalError = "검색 중 오류가 발생했습니다: ${e.message}"
                )
            }
        }
    }
}
