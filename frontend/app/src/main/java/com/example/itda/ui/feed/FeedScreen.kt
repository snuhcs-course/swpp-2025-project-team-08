package com.example.itda.ui.feed

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.theme.Neutral100
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.feed.components.FeedContentCard
import com.example.itda.ui.feed.components.FeedHeaderSection
import com.example.itda.ui.feed.components.FeedInfoCard
import com.example.itda.ui.feed.components.FeedSummaryCard
import com.example.itda.ui.navigation.LoadingScreen


@SuppressLint("QueryPermissionsNeeded")
@Composable
fun FeedScreen(
    ui: FeedViewModel.FeedUiState, // UiState를 인자로 받음
    onBack: () -> Unit
) {
//    val feedViewModel : FeedViewModel = hiltViewModel()

    val scrollState = rememberScrollState()
    var detailExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var url = ""
    if(ui.feed.applyUrl.toBoolean()) {
        url = ui.feed.applyUrl.toString()
    }
    else {
        url = ui.feed.referenceUrl.toString()
    }

    BaseScreen(
        title = " ",
        onBack = onBack,
        topBarVisible = true,
        bottomBar = {
            if(!ui.isLoading && url.toBoolean()) {
                Button(
                    onClick = {  /* feed.link (신청 페이지) 로 이동 */
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        // 안전을 위해 resolveActivity를 사용하여 처리 가능한 앱이 있는지 확인 후 실행하는 것이 좋습니다.
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary40,
                        disabledContainerColor = Neutral80
                    ),
                    enabled = true
                ) {
                    Text(
                        text = "신청하러가기",
                        color = Neutral100,
                        fontSize = 16.sp
                    )
                }
            }

        }
    ) { paddingValues ->

        if(ui.isLoading) {
            LoadingScreen()
        }
        else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Neutral100)
            ) {

                // 상단 제목 / 태그
                FeedHeaderSection(
                    title = ui.feed.title,
                    endDate = ui.feed.applyEndAt ?: "",
                    tags = listOf(ui.feed.categoryValue),
                    isEligible = true,
                    isStarred = false // ui.feed.isStarred
                )

                Spacer(Modifier.height(16.dp))

                // 지원혜택 카드
                FeedInfoCard(
                    categories = listOf(ui.feed.categoryValue),
                    startDate = ui.feed.applyStartAt ?: "",
                    endDate = ui.feed.applyEndAt ?: "",
                    department =
                        if(ui.feed.operatingEntity == "central")
                            "중앙정부"
                        else
                            ui.feed.operatingEntity,
                )
                Spacer(Modifier.height(12.dp))

                FeedSummaryCard(
                    expanded = detailExpanded,
                    onToggle = { detailExpanded = !detailExpanded },
                    summary = ui.feed.summary
                )

                Spacer(Modifier.height(12.dp))

                FeedContentCard(content = ui.feed.details)

                Spacer(Modifier.height(60.dp)) // 하단 버튼 여유 공간
            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewFeedScreen() {
//    // 미리보기를 위한 더미 함수
//    FeedScreen(1, {})
//}