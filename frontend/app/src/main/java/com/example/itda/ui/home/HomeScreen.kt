package com.example.itda.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.data.model.Category
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedList
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.home.components.HomeHeader
import com.example.itda.ui.home.components.ProgramFilterRow
import com.example.itda.ui.navigation.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ui: HomeViewModel.HomeUiState, // UiState를 인자로 받음
    onFeedClick: (Int) -> Unit,
    onFeedBookmarkClick : (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onRefresh: () -> Unit,
    onLoadNext: () -> Unit,
    onRefreshProfile: () -> Unit,
    scrollToTopEventFlow: Flow<Unit>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onRefreshProfile()
    }

    val listState = rememberLazyListState()


    LaunchedEffect(listState) {
        // 마지막에서 N번째 아이템에 도달했을 때 로딩을 시작하도록 임계점 설정
        val threshold = 1

        // listState의 스크롤 변화를 지속적으로 관찰
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItemCount = listState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

                // 마지막 보이는 아이템의 인덱스가 전체 아이템 수 - 임계점보다 클 경우
                val shouldLoadMore = lastVisibleItemIndex >= (totalItemCount - threshold)

                // 로딩 함수 호출
                if (shouldLoadMore && totalItemCount > 0 && !ui.isPaginating) {
                    onLoadNext()
                }
            }
    }

    LaunchedEffect(Unit) {
        scrollToTopEventFlow.collect {

            // 딜레이시켜 UI 갱신 안정화 시간을 확보
            delay(200)

            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset


            if (index != 0 || offset != 0) {

                listState.animateScrollToItem(0)

            }
        }
    }


    val pullToRefreshState = rememberPullToRefreshState()


    BaseScreen(
        title = "home",
        topBarVisible = false,
    ) { paddingValues ->
        Column(
            modifier = modifier
        ) {
            Spacer(Modifier.height(20.dp).fillMaxWidth())
            HomeHeader(
                username = ui.username,
            )
            ProgramFilterRow(
                categories = ui.categories,
                selectedCategory = ui.selectedCategory,
                selectedCategoryCount =
                    if(ui.generalError.isNullOrBlank())
                        ui.totalElements
                    else
                        0,
                onCategorySelected = onCategorySelected
            )

            if(ui.isLoading) {
                LoadingScreen(
                    text = ""
                )
            }
            else {
                PullToRefreshBox(
                    isRefreshing = ui.isPullToRefreshing,
                    onRefresh = onRefresh,
                    state = pullToRefreshState,
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = ui.isPullToRefreshing,
                            containerColor = MaterialTheme.colorScheme.surface,
                            color = MaterialTheme.colorScheme.primary,
                            state = pullToRefreshState
                        )
                    }
                ) {
                    if(ui.isRefreshing.not() && ui.feedItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(8.dp),
                                state = listState,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item {
                                    Text(
                                        text = ui.generalError ?: "추천 정책이 없어요 :(",
                                        fontSize = 18.scaledSp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                    )
                                }
                            }
                        }
                    }
                    else {
                        FeedList(
                            items = ui.feedItems,
                            bookmarkPrograms = ui.bookmarkPrograms,
                            listState = listState,
                            onItemClick = { feed -> onFeedClick(feed.id) },
                            onItemBookmarkClicked = { id -> onFeedBookmarkClick(id) },
                            isPaginating = ui.isPaginating,
                        )
                    }
                }
            }
        }
    }
}
