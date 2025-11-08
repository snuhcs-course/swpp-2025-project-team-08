package com.example.itda.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchRoute(
    onFeedClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onFeedClick = onFeedClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearch = viewModel::onSearch,
        onLoadNext = viewModel::onLoadNext,
        onRecentSearchClick = viewModel::onRecentSearchClick,
        onDeleteRecentSearch = viewModel::onDeleteRecentSearch,
        onClearAllRecentSearches = viewModel::onClearAllRecentSearches,
        onSortTypeChange = viewModel::onSortTypeChange,
        onCategorySelected = viewModel::onCategorySelected,
        modifier = modifier
    )
}
