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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.ui.auth.components.PreferenceSelector
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedCard
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
    val scope = rememberCoroutineScope()


    val pageCount = ui.examplePrograms.size // 7
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                text = "선호도 설정 (${pagerState.currentPage + 1} / $pageCount)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- 1. Horizontal Pager (정책 카드 뷰어) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // 남은 공간 차지
            ) { page ->
                val program = ui.examplePrograms[page]
                val currentScore = currentPreferences.find { it.id == program.id }?.score ?: 0

                // 정책 카드와 슬라이더를 담는 컬럼
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 중앙 정책 카드 (FeedCard)
                    FeedCard(
                        id = program.id,
                        title = program.title,
                        categories = listOf(program.categoryValue),
                        department = program.operatingEntity,
                        content = program.preview,
                        isBookmarked = false,
                        logo = if (program.operatingEntityType == "central") R.drawable.gov_logo else R.drawable.local,
                        isEligible = false,
                        onClick = { onFeedExampleClick(program.id) },
                        onBookmarkClicked = {},
                        isExample = true,
                        onDismissRequest = {}
                    )


                    // --- 2. 선호도 선택 섹션 ---
                    PreferenceSelector(
                        currentScore = currentScore,
                        onScoreChange = { newScore ->
                            // 선호도 상태 업데이트
                            onPreferenceScoreChange(program.id, newScore)
                        }
                    )
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

            // --- 3. 내비게이션 및 제출 버튼 섹션 ---
            PagerNavigation(
                pagerState = pagerState,
                pageCount = pageCount,
                pageScores = pageScores,
                isSubmitEnabled = isSubmitEnabled,
                onSubmit = onSubmit,
            )
        }
        ui.exampleProgramDetail?.let { program ->
            AlertDialog(
                onDismissRequest = onDismissExampleDetail,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                text = {
                    Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Column() {

                            // 상단 제목 / 태그
                            FeedHeaderSection(
                                title = ui.exampleProgramDetail.title,
                                endDate = ui.exampleProgramDetail.applyEndAt ?: "",
                                tags = listOf(ui.exampleProgramDetail.categoryValue),
                                isEligible = false,
                                isBookmarked = false,
                                onBookmarkClicked = {}
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSlider(
    currentScore: Int,
    onScoreChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "선호도: ${if (currentScore == 0) "선택해주세요" else "$currentScore 점"}",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = currentScore.toFloat(),
            onValueChange = { onScoreChange(it.toInt()) },
            steps = 0, // 1, 2, 3, 4, 5
            valueRange = 1f..5f,
            modifier = Modifier.fillMaxWidth(0.8f),



            )

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 (매우 싫음)", style = MaterialTheme.typography.bodySmall)
            Text("(매우 좋음) 5", style = MaterialTheme.typography.bodySmall)
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
                    val isSelected = index == currentPage
                    val hasScore = (pageScores.getOrNull(index) ?: 0) > 0

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