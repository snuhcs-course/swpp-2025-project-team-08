package com.example.itda.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.ProgramResponse
import kotlinx.coroutines.delay

@Composable
fun FeedList(
    // 표시할 FeedItem 데이터의 리스트를 인자로 받습니다.
    items: List<ProgramResponse>,
    filterCategory: String,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (ProgramResponse) -> Unit
) {
// TODO - item id 를 보고 user 와의 관계에 대한 정보 가공 in program repository?
    //  ex : isStared, isEligible..
    //  homeViewmodel.getFeedInfo() -> programRepository.starred

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        state = listState
    ) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = item.id) {
                // index * 50L 만큼 지연 후 상태 변경 (차르르륵 효과)
                delay(index * 5L)
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    // 1. 애니메이션 지속 시간 설정
                    animationSpec = tween(
                        durationMillis = 300, // 애니메이션 지속 시간 (0.3초)
                        delayMillis = index * 50 // 아이템 인덱스에 따라 지연 시간 추가 (차르르륵 효과)
                    )
                ) + slideInVertically(
                    // 수직 방향으로 살짝 내려오면서 나타나는 효과
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50
                    ),
                    // 시작 위치 (y= -100px에서 시작하여 0으로 이동)
                    initialOffsetY = { fullHeight -> fullHeight / 2 }
                )
            ) {
                FeedCard(
                    id = item.id,
                    title = item.title,
                    categories = listOf(filterCategory),
                    department =
                        if(item.operatingEntity == "central")
                            "중앙정부"
                        else
                            item.operatingEntity,
                    content = item.preview,

                    isStarred = false, // TODO - 변수로 지정 필요
                    logo =
                        if(item.operatingEntity == "central")
                            R.drawable.gov_logo
                        else
                            R.drawable.local, // TODO - operatingEntity에 지자체 들어오면 그에 맞춘 로고를 넣어줘야할텐디...
                    onClick = { onItemClick(item) }
                )
            }

//            Spacer(modifier = Modifier.height(6.dp))
        }
    }

}

