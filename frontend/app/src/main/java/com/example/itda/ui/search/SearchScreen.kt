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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.data.model.Category
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedList
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.home.components.ProgramFilterRow
import com.example.itda.ui.navigation.LoadingScreen
import com.example.itda.ui.search.components.*

@Composable
fun SearchScreen(
    uiState: SearchViewModel.SearchUiState,
    onFeedClick: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLoadNext: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onSortTypeChange: (SearchViewModel.SortType) -> Unit,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.searchResults.firstOrNull()?.id, uiState.sortType) {
        if (uiState.searchResults.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(listState) {
        val threshold = 4

        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItemCount = listState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

                val shouldLoadMore = lastVisibleItemIndex >= (totalItemCount - threshold)

                if (shouldLoadMore && totalItemCount > 0 && uiState.hasSearched) {
                    onLoadNext()
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
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
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
                                onClearAllRecentSearches()
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
                                    onRecentSearchClick(searchQuery)
                                },
                                onDeleteClick = {
                                    onDeleteRecentSearch(searchQuery)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 이런 키워드로 검색해보세요 !",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Neutral20,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = uiState.recommendedKeywords,
                            key = { it }
                        ) { keyword ->
                            RecommendedSearchChip(
                                keyword = keyword,
                                onClick = {
                                    onRecentSearchClick(keyword) // 같은 함수 재활용!
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
                                onSortTypeChange = onSortTypeChange
                            )
                        }

                        ProgramFilterRow(
                            categories = uiState.categories,
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = onCategorySelected
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
