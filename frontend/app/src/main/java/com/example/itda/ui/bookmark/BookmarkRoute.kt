package com.example.itda.ui.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookmarkRoute(
    onFeedClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vm: BookmarkViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()

    BookmarkScreen (
        ui = ui,
        onCategorySelected = vm::onCategorySelected,
        onFeedClick = onFeedClick,
        onFeedBookmarkClick = vm::onFeedBookmarkClicked,
        onRefresh = vm::refreshBookmarkData,
        // 💡 수정: loadNextPage 함수를 전달합니다.
        onLoadNext = vm::loadNextPage,
        onRefreshProfile = vm::loadMyProfile,
        onSortSelected = vm::onSortSelected,
        modifier = modifier
    )
}