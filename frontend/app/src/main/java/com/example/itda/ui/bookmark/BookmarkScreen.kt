package com.example.itda.ui.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.data.model.Category
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedList
import com.example.itda.ui.common.theme.scaledSp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    ui: BookmarkViewModel.BookmarkUiState,
    onFeedClick: (Int) -> Unit,
    onFeedBookmarkClick : (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onRefresh: () -> Unit,
    onLoadNext: () -> Unit,
    onRefreshProfile: () -> Unit,
    onSortSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onRefreshProfile()
    }

    val listState = rememberLazyListState()

    // 💡 수정: 스크롤 감지 및 다음 페이지 로드 로직 재구현
    LaunchedEffect(listState) {
        val threshold = 5 // 마지막 5개 아이템이 보일 때 로드 시작
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItemCount = listState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0

                val shouldLoadMore = lastVisibleItemIndex >= (totalItemCount - threshold)

                // 로드 조건: 더 로드해야 하고, 전체 아이템이 0보다 커야 하며, 현재 로딩 중이 아니어야 함
                if (shouldLoadMore && totalItemCount > 0 && !ui.isPaginating) {
                    // 마지막 페이지가 아닐 때만 로드
                    if (!ui.isLastPage) {
                        onLoadNext()
                    }
                }
            }
    }


    LaunchedEffect(ui.bookmarkItems) {
        // 💡 카테고리 필터링이 적용되더라도, 새 목록이 들어오면 맨 위로 스크롤
        if (ui.bookmarkItems.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()


    BaseScreen(
        title = "북마크",
        topBarVisible = false,
    ) { paddingValues ->
        Column(
            modifier = modifier) {
            Spacer(Modifier.height(20.dp).fillMaxWidth())
            BookmarkHeader(
                username = ui.username,
            )
            BookmarkSortRow(
                sortOptions = ui.sortOptions,
                selectedSort = ui.selectedSort,
                onSortSelected = onSortSelected
            )
            if(ui.isLoading && ui.bookmarkItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            else {
                PullToRefreshBox(
                    isRefreshing = ui.isRefreshing,
                    onRefresh = onRefresh,
                    state = pullToRefreshState,
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = ui.isRefreshing,
                            containerColor = MaterialTheme.colorScheme.surface,
                            color = MaterialTheme.colorScheme.primary,
                            state = pullToRefreshState
                        )
                    }
                ) {
                    if(ui.bookmarkItems.isEmpty()) {
                        EmptyBookmarkState()
                    }
                    else {
                        FeedList(
                            items = ui.bookmarkItems,
                            bookmarkPrograms = ui.bookmarkIds,
                            listState = listState,
                            onItemClick = { feed -> onFeedClick(feed.id) },
                            onItemBookmarkClicked = { id -> onFeedBookmarkClick(id) },
                            // 💡 수정: isPaginating 상태를 전달하여 로딩 표시기를 띄웁니다.
                            isPaginating = ui.isPaginating,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkSortRow(
    sortOptions: List<SortOption>,
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 💡 수정: items(List<T>) 대신 items(count: Int)를 사용
        items(
            count = sortOptions.size
        ) { index ->
            // 💡 index를 사용하여 리스트 항목에 접근합니다.
            val sortOption = sortOptions[index]

            val isSelected = sortOption.apiValue == selectedSort.apiValue
            Text(
                text = sortOption.display,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSortSelected(sortOption) }
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun BookmarkHeader(
    username: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.scaledSp
                    )
                    ) {
                        append(username)
                    }
                    append("님의 북마크한 정책들")
                },
                fontSize = 20.scaledSp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun EmptyBookmarkState() {
    // ... (기존 EmptyBookmarkState 컴포저블) ...
    // Note: You must ensure this component is fully implemented in your actual file.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "북마크 없음",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "북마크된 정책이 없습니다",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "원하는 정책을 북마크하시면\\n여기에 표시됩니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.7f))
    }
}