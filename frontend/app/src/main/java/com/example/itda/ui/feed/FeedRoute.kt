package com.example.itda.ui.feed


import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FeedRoute(
    feedId: Int,
    onBack: () -> Unit,
    vm: FeedViewModel = hiltViewModel()
) {
    val ui by vm.feedUi.collectAsState() // ViewModel의 UI 상태를 구독

    LaunchedEffect(key1 = feedId) {
        vm.getFeedItem(feedId)
    }
    val context = LocalContext.current
    LaunchedEffect(ui.generalError) {
        ui.generalError?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    FeedScreen(
        ui = ui, // FeedScreen에 UI 상태를 통째로 전달
        onBack = onBack,
        onBookmarkClicked = vm::onBookmarkClicked,
        toggleLike = vm::toggleLike,
        toggleDislike = vm::toggleDisLike,
    )
}
