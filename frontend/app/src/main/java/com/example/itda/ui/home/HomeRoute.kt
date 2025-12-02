package com.example.itda.ui.home


import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(
    onFeedClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vm: HomeViewModel = hiltViewModel()
) {
    val ui by vm.homeUi.collectAsState() // ViewModel의 UI 상태를 구독
    val scrollToTopEventFlow = remember(vm) { vm.scrollToTopEvent }
    val context = LocalContext.current
    LaunchedEffect(ui.generalError) {
        ui.generalError?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }


    HomeScreen (
        ui = ui, // HomeScreen에 UI 상태를 통째로 전달
        onCategorySelected = vm::onCategorySelected,
        onFeedClick = onFeedClick,
        onFeedBookmarkClick = vm::onFeedBookmarkClicked,
        onRefresh = vm::refreshHomeData,
        onLoadNext = vm::loadNextPage,
        onRefreshProfile = vm::loadMyProfile,
        scrollToTopEventFlow = scrollToTopEventFlow,
        modifier = modifier
    )
}
