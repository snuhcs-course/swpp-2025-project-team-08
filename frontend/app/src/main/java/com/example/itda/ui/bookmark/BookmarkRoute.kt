package com.example.itda.ui.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(Unit) {
        vm.loadBookmarkData()
    }

    BookmarkScreen (
        ui = ui,
        onFeedClick = onFeedClick,
        onFeedBookmarkClick = vm::onFeedBookmarkClicked,
        onRefresh = vm::refreshBookmarkData,
        onLoadNext = vm::loadNextPage,
        onRefreshProfile = vm::loadMyProfile,
        onSortSelected = vm::onSortSelected,
        modifier = modifier
    )
}