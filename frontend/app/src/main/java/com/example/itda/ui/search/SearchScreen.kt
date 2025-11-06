package com.example.itda.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedList
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.navigation.LoadingScreen
import com.example.itda.ui.search.components.*
import com.example.itda.data.model.dummyCategories
import com.example.itda.ui.home.components.ProgramFilterRow

@Composable
fun SearchScreen(
    onFeedClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        val threshold = 4

        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItemCount = listState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

                val shouldLoadMore = lastVisibleItemIndex >= (totalItemCount - threshold)

                if (shouldLoadMore && totalItemCount > 0 && uiState.hasSearched) {
                    viewModel.onLoadNext()
                }
            }
    }

    BaseScreen(
        title = "search",
        topBarVisible = false,
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SearchInputField(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearch = { viewModel.onSearch() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.hasSearched) {
                if (uiState.recentSearches.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "최근 검색어",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral20
                        )

                        Text(
                            text = "전체 삭제",
                            fontSize = 14.sp,
                            color = Neutral50,
                            modifier = Modifier.clickable {
                                viewModel.onClearAllRecentSearches()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = uiState.recentSearches,
                            key = { it }
                        ) { searchQuery ->
                            RecentSearchChip(
                                searchQuery = searchQuery,
                                onItemClick = {
                                    viewModel.onRecentSearchClick(searchQuery)
                                },
                                onDeleteClick = {
                                    viewModel.onDeleteRecentSearch(searchQuery)
                                }
                            )
                        }
                    }
                }
            }
            else {
                if (uiState.isSearching) {
                    LoadingScreen(text = "검색 중...")
                }
                else if (uiState.searchResults.isNotEmpty()) {
                    Column {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            SearchResultHeader(
                                modifier = Modifier.weight(1f),
                                searchQuery = uiState.recentSearches.firstOrNull() ?: "",
                                totalResults = uiState.totalElements
                            )

                            SearchFilterRow(
                                sortType = uiState.sortType,
                                onSortTypeChange = { viewModel.onSortTypeChange(it) }
                            )
                        }

                        ProgramFilterRow(
                            categories = dummyCategories.map { category -> category.value },
                            selectedCategory = uiState.selectedCategory.value,
                            onCategorySelected = { viewModel.onCategorySelected(uiState.selectedCategory) }
                        )

                        FeedList(
                            items = uiState.searchResults,
                            listState = listState,
                            filterCategory = uiState.selectedCategory.value,
                            onItemClick = { feed -> onFeedClick(feed.id) }
                        )

                        if (uiState.isLoadingMore) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Primary40,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "검색 결과가 없습니다",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Neutral40
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "다른 검색어로 다시 시도해보세요",
                                fontSize = 14.sp,
                                color = Neutral50
                            )
                        }
                    }
                }

                uiState.generalError?.let { errorText ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorText,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

