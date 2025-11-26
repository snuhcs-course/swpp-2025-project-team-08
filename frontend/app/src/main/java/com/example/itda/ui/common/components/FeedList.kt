package com.example.itda.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.ProgramResponse
import com.example.itda.ui.navigation.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FeedList(
    // 표시할 FeedItem 데이터의 리스트를 인자로 받습니다.
    items: List<ProgramResponse>,
    bookmarkPrograms : List<Int>,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (ProgramResponse) -> Unit,
    dismissable : Boolean = true,
    onItemDismissed : (ProgramResponse) -> Unit = {},
    onItemDislike : (Int) -> Unit = {},
    onItemBookmarkClicked : (Int) -> Unit,
    isPaginating : Boolean = false
) {
// TODO - item id 를 보고 user 와의 관계에 대한 정보 가공 in program repository?
    //  ex : isStared, isEligible..
    //  homeViewmodel.getFeedInfo() -> programRepository.starred
    val scope = rememberCoroutineScope()
    var isInitialLoadComplete by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        state = listState
    ) {
        items(items, key = {it.id}) { item ->
            var itemVisible by remember { mutableStateOf(true) }
            val dismissButtonWidth = 120.dp // "관심없음" 버튼 너비
            val dismissButtonWidthPx = with(LocalDensity.current) { dismissButtonWidth.toPx() }
            val offsetX = remember { Animatable(0f) }

            if(dismissable) {
                AnimatedVisibility(
                    visible = itemVisible, // 스와이프 안됐을 때만 보임
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 300),
                        shrinkTowards = Alignment.Top
                    ) + fadeOut(animationSpec = tween(durationMillis = 300)),
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent) // 뒷배경 색상
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        itemVisible = false
                                        delay(300L)
                                        onItemDismissed(item)
                                        onItemDislike(item.id) // TODO - dislike 하면 자동으로 아예 배제된다면 이대로
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "관심없음",
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "관심없음",
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(offsetX.value.roundToInt(), 0) } // 스와이프 위치 적용
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        // 스와이프 중 offset 업데이트
                                        scope.launch {
                                            // 왼쪽으로만 스와이프, 최대는 버튼 너비만큼
                                            val newOffset =
                                                (offsetX.value + delta).coerceIn(
                                                    -dismissButtonWidthPx,
                                                    0f
                                                )
                                            offsetX.snapTo(newOffset)
                                        }
                                    },
                                    onDragStopped = {
                                        // [요구사항 1, 2] 스와이프 멈췄을 때 고정
                                        scope.launch {
                                            if (offsetX.value < -dismissButtonWidthPx / 2) {
                                                // 절반 이상 밀었으면: 버튼 너비만큼 밀어서 고정
                                                offsetX.animateTo(-dismissButtonWidthPx, tween(100))
                                            } else {
                                                // 절반 미만 밀었으면: 제자리로 복귀
                                                offsetX.animateTo(0f, tween(100))
                                            }
                                        }
                                    }
                                )
                                .background(MaterialTheme.colorScheme.background) // FeedCard 배경색
                        ) {
                            // 10. 실제 컨텐츠 (기존 FeedCard)
                            FeedCard(
                                id = item.id,
                                title = item.title,
                                categories = listOf(item.categoryValue),
                                department = item.operatingEntity,
                                content = item.preview,
                                isBookmarked = item.id in bookmarkPrograms,
                                reason = item.reason,
                                logo =
                                    if (item.operatingEntityType == "central")
                                        R.drawable.gov_logo
                                    else
                                        R.drawable.local,
                                onClick = {
                                    // 스와이프된 상태에서는 클릭 시 원위치
                                    if (offsetX.value != 0f) {
                                        scope.launch { offsetX.animateTo(0f) }
                                    } else {
                                        onItemClick(item)
                                    }
                                },
                                onBookmarkClicked = { onItemBookmarkClicked(item.id) },
                                onDismissRequest = {
                                    scope.launch {
                                        itemVisible = false
                                        delay(300L)
                                        onItemDismissed(item)
                                        // dismissState.dismiss(SwipeToDismissBoxValue.EndToStart)
                                    }
                                }
                            )
                        }
                    }

                }
            }
            else {
                FeedCard(
                    id = item.id,
                    title = item.title,
                    categories = listOf(item.categoryValue),
                    department = item.operatingEntity,
                    content = item.preview,
                    isBookmarked = item.id in bookmarkPrograms,
                    reason = item.reason,
                    logo =
                        if (item.operatingEntityType == "central")
                            R.drawable.gov_logo
                        else
                            R.drawable.local,
                    onClick = { onItemClick(item) },
                    onBookmarkClicked = { onItemBookmarkClicked(item.id) },
                    onDismissRequest = {}
                )
            }

        }
        if(isPaginating) {
            item {
                LoadingScreen(
                    text = ""
                )
            }
        }


        /*
        itemsIndexed(items, key = { index, item -> "${item.id}_$index" }) { index, item ->
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = item.id) {
                // index * 50L 만큼 지연 후 상태 변경 (차르르륵 효과)
                val delayTime =
                    if (!isInitialLoadComplete && index < 20)
                        index * 5L
                    else
                        0L
                delay(delayTime)
                isVisible = true

                if (index >= 20 && !isInitialLoadComplete) {
                    isInitialLoadComplete = true
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    // 1. 애니메이션 지속 시간 설정
                    animationSpec = tween(
                        durationMillis = 300, // 애니메이션 지속 시간 (0.3초)
                        // delayMillis = index * 50 // 아이템 인덱스에 따라 지연 시간 추가 (차르르륵 효과)
                    )
                ) + slideInVertically(
                    // 수직 방향으로 살짝 내려오면서 나타나는 효과
                    animationSpec = tween(
                        durationMillis = 300,
                        // delayMillis = index * 50
                    ),
                    // 시작 위치 (y= -100px에서 시작하여 0으로 이동)
                    initialOffsetY = { fullHeight -> fullHeight / 2 }
                )
            ) {
                FeedCard(
                    id = item.id,
                    title = item.title,
                    categories = listOf(item.categoryValue),
                    department = item.operatingEntity,
                    content = item.preview,

                    isStarred = false, // TODO - 변수로 지정 필요
                    logo =
                        if(item. operatingEntityType == "central")
                            R.drawable.gov_logo
                        else
                            R.drawable.local,
                    onClick = { onItemClick(item) }
                )
            }

//            Spacer(modifier = Modifier.height(6.dp))
        }
    */
    }

}

// 11. 스와이프 시 뒷면에 표시될 "관심없음" UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(targetValue: SwipeToDismissBoxValue) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd // 오른쪽에 정렬
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "관심없음",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.errorContainer
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "관심없음",
                tint = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}
