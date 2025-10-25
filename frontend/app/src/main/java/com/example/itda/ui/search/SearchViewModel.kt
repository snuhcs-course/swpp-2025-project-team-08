package com.example.itda.ui.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    // 나중에 SearchRepository 추가
) : ViewModel() {

    // UI 상태
    data class SearchUiState(
        val searchQuery: String = "",                           // 검색창에 입력된 텍스트
        val recentSearches: List<String> = emptyList(),         // 최근 검색어 리스트
        val isSearching: Boolean = false                        // 검색 중인지 여부
    )
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // 검색어 입력 시 호출
    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
    }

    // 검색 실행 (엔터 또는 검색 버튼 클릭 시)
    fun onSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return

        // 최근 검색어 추가
        val updatedSearches = listOf(query) +
                _uiState.value.recentSearches.filter { it != query }

        _uiState.value = _uiState.value.copy(
            recentSearches = updatedSearches.take(10),
            searchQuery = "" // 검색 후 입력창 초기화
        )

        // TODO: searchRepository.search(query)
    }

    // 최근 검색어 클릭 시
    fun onRecentSearchClick(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
        onSearch()
    }

    // 최근 검색어 삭제
    fun onDeleteRecentSearch(query: String) {
        val updatedSearches = _uiState.value.recentSearches.filter { it != query }
        _uiState.value = _uiState.value.copy(
            recentSearches = updatedSearches
        )
    }

    // 모든 최근 검색어 삭제
    fun onClearAllRecentSearches() {
        _uiState.value = _uiState.value.copy(
            recentSearches = emptyList()
        )
    }
}