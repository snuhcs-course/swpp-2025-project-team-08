package com.example.itda.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.search.components.RecentSearchChip
import com.example.itda.ui.search.components.SearchInputField

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BaseScreen(
        title = "search",
        topBarVisible = false,
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 검색 입력창
            SearchInputField(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearch = { viewModel.onSearch() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 최근 검색어 섹션
            if (uiState.recentSearches.isNotEmpty()) {
                // 최근 검색어 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "최근 검색어",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Neutral20
                    )

                    // 전체 삭제 버튼
                    Text(
                        text = "전체 삭제",
                        fontSize = 14.sp,
                        color = Neutral50,
                        modifier = Modifier.clickable {
                            viewModel.onClearAllRecentSearches()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 가로 스크롤 칩 리스트
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            // TODO: 🔥 인기 검색어 섹션 (추후 추가 예정)
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // 인기 검색어 헤더
//            Text(
//                text = "🔥 인기 검색어",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium,
//                color = Neutral20
//            )
        }
    }
}