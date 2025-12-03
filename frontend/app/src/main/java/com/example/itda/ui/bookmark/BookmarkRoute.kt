package com.example.itda.ui.bookmark

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    LaunchedEffect(ui.generalError) {
        ui.generalError?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
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