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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.feed.components.FeedDetailCard
import com.example.itda.ui.feed.components.FeedHeaderSection
import com.example.itda.ui.feed.components.FeedInfoCard
import com.example.itda.ui.feed.components.FeedSummaryCard
import com.example.itda.ui.navigation.LoadingScreen


@SuppressLint("QueryPermissionsNeeded")
@Composable
fun FeedScreen(
    ui: FeedViewModel.FeedUiState,
    onBack: () -> Unit,
    onBookmarkClicked: () -> Unit,
    toggleLike: () -> Unit = {},
    toggleDislike: () -> Unit = {},
) {
//    val feedViewModel : FeedViewModel = hiltViewModel()

    val scrollState = rememberScrollState()
    var detailExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var url = ""
    if (ui.feed.applyUrl.isNullOrBlank()) {
        url = ui.feed.referenceUrl.orEmpty()
    } else {
        url = ui.feed.applyUrl
    }

    BaseScreen(
        title = " ",
        onBack = onBack,
        topBarVisible = true,
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
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // 상단 제목 / 태그
                FeedHeaderSection(
                    title = ui.feed.title,
                    endDate = ui.feed.applyEndAt ?: "",
                    tags = listOf(ui.feed.categoryValue),
                    isBookmarked = ui.isBookmarked,
                    onBookmarkClicked = onBookmarkClicked,
                    toggleLike = toggleLike,
                    isLiked = ui.isLiked,
                    toggleDisLike = toggleDislike,
                    isDisliked = ui.isDisliked,
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

                FeedSummaryCard(content = ui.feed.summary)

                Spacer(Modifier.height(12.dp))



                FeedDetailCard(
                    expanded = detailExpanded,
                    onToggle = { detailExpanded = !detailExpanded },
                    details = ui.feed.details
                )

                Spacer(Modifier.height(12.dp))

                BottomApplyButton(
                    url = url,
                    context = context
                )
                Spacer(Modifier.height(12.dp))
            }
        }

    }
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun BottomApplyButton(
    url : String = "",
    context: android.content.Context,
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Button(
                onClick = {
                    var finalUrl = url
                    val bokjiroUrl = "https://www.bokjiro.go.kr/"

                    if (finalUrl.isBlank()) {
                        finalUrl = bokjiroUrl
                    }
                    val intent = Intent ( Intent . ACTION_VIEW , finalUrl.toUri() )

                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                enabled = true
            ) {
                Text(
                    text = "신청하러가기",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 16.scaledSp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewFeedScreen() {
//    // 미리보기를 위한 더미 함수
//    FeedScreen(1, {})
//}