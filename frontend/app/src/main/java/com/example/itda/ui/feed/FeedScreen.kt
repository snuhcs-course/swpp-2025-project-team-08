package com.example.itda.ui.feed

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.theme.Neutral100
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.Neutral90
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.feed.components.FeedContentCard
import com.example.itda.ui.feed.components.FeedDetailCard
import com.example.itda.ui.feed.components.FeedHeaderSection
import com.example.itda.ui.feed.components.FeedInfoCard


@Composable
fun FeedScreen(
    ui: FeedViewModel.FeedUiState, // UiState를 인자로 받음
    onBack: () -> Unit
) {
//    val feedViewModel : FeedViewModel = hiltViewModel()

    val scrollState = rememberScrollState()
    var detailExpanded by remember { mutableStateOf(false) }

    val feed = try {
        ui.feed
    } catch (e: Exception) {
        Log.e("FeedScreen", "Feed load failed: $e")
        null
    }

    Log.d("FeedScreen", "Feed load success: ${feed?.content}")

    if (feed == null) {
        Text("피드 데이터를 불러오지 못했습니다.")
        return
    }

    BaseScreen(
        title = " ",
        onBack = onBack,
        topBarVisible = true,
        bottomBar = {
            Button(
                onClick = { /* feed.link (신청 페이지) 로 이동 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (feed.isEligible)
                        Primary40  // 활성화 시 메인 컬러
                    else
                        Neutral90,
                    disabledContainerColor = Neutral80
                ),
                enabled = feed.isEligible
            ) {
                Text(
                    text = "신청하러가기",
                    color = Neutral100,
                    fontSize = 16.sp
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // 상단 제목 / 태그
            FeedHeaderSection(
                title = feed.title,
                endDate = feed.end_date,
                tags = feed.categories,
                isEligible = feed.isEligible,
                isStarred = feed.isStarred
            )

            Spacer(Modifier.height(16.dp))

            // 지원혜택 카드
            FeedInfoCard(
                categories = feed.categories,
                startDate = feed.start_date,
                endDate = feed.end_date,
                department = feed.department
            )

            Spacer(Modifier.height(12.dp))

            // 내용 카드
            FeedContentCard()

            Spacer(Modifier.height(12.dp))

            // 상세내용 (접히는 영역)
            FeedDetailCard(
                expanded = detailExpanded,
                onToggle = { detailExpanded = !detailExpanded }
            )

            Spacer(Modifier.height(60.dp)) // 하단 버튼 여유 공간
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewFeedScreen() {
//    // 미리보기를 위한 더미 함수
//    FeedScreen(1, {})
//}