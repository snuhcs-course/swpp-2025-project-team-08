package com.example.itda.ui.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.ui.auth.components.BubbleEffect
import com.example.itda.ui.auth.components.PreferenceSelector
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedCard
import com.example.itda.ui.common.theme.Primary50
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.feed.components.FeedDetailCard
import com.example.itda.ui.feed.components.FeedHeaderSection
import com.example.itda.ui.feed.components.FeedInfoCard
import com.example.itda.ui.feed.components.FeedSummaryCard

// TODO: AuthViewModel에 임시 Program List를 추가했다고 가정
// val dummyProgramsForPreference = listOf(ProgramResponse(...), ...)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceUpdateScreen(
    ui : AuthViewModel.PreferenceUIState,
    onPreferenceScoreChange : (Int, Int) -> Unit,
    onFeedExampleClick : (Int) -> Unit,
    onDismissExampleDetail : () -> Unit,
    onSubmit: () -> Unit,
) {
//    val scope = rememberCoroutineScope()


    val exampleProgramCount = ui.examplePrograms.size // 7
    val pageCount = exampleProgramCount + 1 // 7 + 설명 페이지
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // 현재 선택된 선호도 목록 (MutableStateFlow의 preferenceRequestList 대신 화면용 State 사용)
    val currentPreferences = ui.preferenceRequestList

    // 모든 항목이 1 이상 (선택됨)인지 확인
    val isSubmitEnabled = ui.examplePrograms.isNotEmpty() && currentPreferences.all { it.score > 0 }

    var detailExpanded by remember { mutableStateOf(false) }

    val pageScores = ui.examplePrograms.map { program ->
        currentPreferences.find { it.id == program.id }?.score ?: 0
    }

    BaseScreen(
        title = "선호도 설정",
        topBarVisible = false,
    ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                BubbleEffect(
                    pagerState = pagerState,
                    paddingValues = paddingValues
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (pagerState.currentPage > 0) {
                            Text(
                                text = "이런 정책은 어떠신가요?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.scaledSp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 64.dp, bottom = 16.dp)
                            )
                        }
                        else {
                            Spacer(Modifier.fillMaxSize())
                        }
                    }



                    // Horizontal Pager (정책 카드 뷰어)
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f) // 남은 공간 차지
                    ) { page ->
                        if (page == 0) {
                            // --- 안내 페이지 (Page 0) ---
                            PreferenceIntroPage()
                        } else {
                            val programIndex = page - 1 // page가 1일 때, programIndex는 0이 됨
                            val program = ui.examplePrograms[programIndex]
                            val currentScore = currentPreferences.find { it.id == program.id }?.score ?: 0

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(720.dp)
                                    .padding(vertical = 64.dp, horizontal = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                FeedCard(
                                    id = program.id,
                                    title = program.title,
                                    categories = listOf(program.categoryValue),
                                    department = program.operatingEntity,
                                    content = program.preview,
                                    isBookmarked = false,
                                    logo = if (program.operatingEntityType == "central") R.drawable.gov_logo else R.drawable.local,
                                    onClick = { onFeedExampleClick(program.id) },
                                    onBookmarkClicked = {},
                                    isExample = true,
                                )


                                PreferenceSelector(
                                    currentScore = currentScore,
                                    onScoreChange = { newScore ->
                                        onPreferenceScoreChange(program.id, newScore)
                                    }
                                )
                            }
                        }
                    }

                    if (ui.generalError != null) {
                        Text(
                            text = ui.generalError,
                            color = MaterialTheme.colorScheme.error, // 빨간 글씨
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // 내비게이션 및 제출 버튼 섹션
                        PagerNavigation(
                            pagerState = pagerState,
                            pageCount = pageCount,
                            pageScores = pageScores,
                            isSubmitEnabled = isSubmitEnabled,
                            onSubmit = onSubmit,
                        )
                    }
                }
            }
        }

        ui.exampleProgramDetail?.let { program ->
            AlertDialog(
                onDismissRequest = onDismissExampleDetail,
                containerColor = MaterialTheme.colorScheme.background,
                text = {
                    Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Column() {

                            // 상단 제목 / 태그
                            FeedHeaderSection(
                                title = ui.exampleProgramDetail.title,
                                endDate = ui.exampleProgramDetail.applyEndAt ?: "",
                                tags = listOf(ui.exampleProgramDetail.categoryValue),
                                isBookmarked = false,
                                onBookmarkClicked = {},
                                isExample = true
                            )

                            Spacer(Modifier.height(16.dp))

                            // 지원혜택 카드
                            FeedInfoCard(
                                categories = listOf(ui.exampleProgramDetail.categoryValue),
                                startDate = ui.exampleProgramDetail.applyStartAt ?: "",
                                endDate = ui.exampleProgramDetail.applyEndAt ?: "",
                                department =
                                    if(ui.exampleProgramDetail.operatingEntity == "central")
                                        "중앙정부"
                                    else
                                        ui.exampleProgramDetail.operatingEntity,
                            )
                            Spacer(Modifier.height(12.dp))

                            FeedSummaryCard(content = ui.exampleProgramDetail.summary)

                            Spacer(Modifier.height(12.dp))



                            FeedDetailCard(
                                expanded = detailExpanded,
                                onToggle = { detailExpanded = !detailExpanded },
                                details = ui.exampleProgramDetail.details
                            )

                            Spacer(Modifier.height(60.dp)) // 하단 버튼 여유 공간
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismissExampleDetail) { // 닫기 버튼
                        Text("닫기")
                    }
                },
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
            )
        }

}

@Composable
fun PreferenceIntroPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "여러분을 더 알려주세요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Primary50,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "어떤 정책이 마음에 드시나요?",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "다음 정책들이 얼마나 필요한지 알려주세요.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "이를 바탕으로 당신에게 딱 맞는 정책들을 추천해 드립니다.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text = "👉 화면을 오른쪽으로 밀어서 넘기기",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerNavigation(
    pagerState: PagerState,
    pageCount: Int,
    pageScores: List<Int>,
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit,
) {
    val currentPage = pagerState.currentPage
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center, // 중앙 정렬
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(isSubmitEnabled) {
                Button(
                    onClick = onSubmit,
                    enabled = isSubmitEnabled, // 모든 항목이 1점 이상일 때만 활성화
                    shape = RoundedCornerShape(8.dp),
                    // weight를 제거하고 고정된 너비를 주거나, 필요에 따라 조정 가능
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "선호도 제출",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else {
                repeat(pageCount) { index ->
                    val isSelected = index == currentPage  // page 1 -> index 0 선택
                    val hasScore =
                        if(index == 0)
                            true
                        else
                            (pageScores.getOrNull(index - 1) ?: 0) > 0


                    val dotSize by animateDpAsState(
                        targetValue = if (isSelected) 12.dp else 8.dp,
                        label = "Dot Size Animation"
                    )

                    val dotColor = if (hasScore || isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }

                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}



/*@Preview(showBackground = true)
@Composable
fun PreviewPreferenceUpdateScreen() {
    data class PreferenceUIState(
        val preferenceRequestList : PreferenceRequestList = emptyList<PreferenceRequest>(),
        val isLoading: Boolean = false,
        val generalError: String? = null
    )
    val preferenceUi: PreferenceRequestList
    PreferenceUpdateScreen(preferenceUi, {})
}*/