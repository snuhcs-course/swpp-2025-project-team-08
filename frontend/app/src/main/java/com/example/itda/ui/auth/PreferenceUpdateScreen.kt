package com.example.itda.ui.auth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.source.remote.PreferenceRequest
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedCard
import com.example.itda.ui.common.theme.Primary50
import kotlinx.coroutines.launch

// TODO: AuthViewModel에 임시 Program List를 추가했다고 가정
// val dummyProgramsForPreference = listOf(ProgramResponse(...), ...)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceUpdateScreen(
    ui : AuthViewModel.PreferenceUIState,
    onSubmit: () -> Unit,
) {
    // AuthViewModel의 Preference 상태를 구독
    val scope = rememberCoroutineScope()

    // 임시 정책 목록 (실제로는 VM에서 API 호출을 통해 가져와야 함)
    // 여기서는 간단한 테스트용 더미 데이터를 가정합니다.
    val dummyProgramList = remember {
        listOf(
            ProgramResponse(id = 101, title = "청년 희망 저축", preview = "저축 장려", operatingEntity = "central", category = "CASH", categoryValue = "빈곤 완화"),
            ProgramResponse(id = 102, title = "어르신 돌봄 서비스", preview = "정서적/신체적 돌봄 지원", operatingEntity = "중앙 복지부", category = "CARE", categoryValue = "돌봄, 요양"),
            ProgramResponse(id = 103, title = "주거 안정 자금 대출", preview = "저금리 주택 대출 지원", operatingEntity = "경기도", category = "HOUSING", categoryValue = "주거 지원"),
            ProgramResponse(id = 104, title = "디지털 일자리 교육", preview = "취업 역량 강화 프로그램", operatingEntity = "central", category = "EMPLOYMENT", categoryValue = "고용, 일자리"),
            ProgramResponse(id = 105, title = "치매 예방 캠페인", preview = "정기 치매 검진 지원", operatingEntity = "부산시", category = "DEMENTIA", categoryValue = "치매 관련"),
        )
    }

    // 현재 페이지 상태 관리 (총 5개)
    val pageCount = dummyProgramList.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // 현재 선택된 선호도 목록 (MutableStateFlow의 preferenceRequestList 대신 화면용 State 사용)
    var currentPreferences by remember {
        mutableStateOf(dummyProgramList.map { PreferenceRequest(id = it.id, value = 0) })
    }

    // 모든 항목이 1 이상 (선택됨)인지 확인
    val isSubmitEnabled = currentPreferences.all { it.value > 0 }

    BaseScreen(
        title = "선호도 설정",
        topBarVisible = true,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            // 헤더: 현재 몇 번째 정책인지 표시
            Text(
                text = "선호도 설정 (${pagerState.currentPage + 1} / $pageCount)",
                fontWeight = FontWeight.Bold,
                color = Primary50,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- 1. Horizontal Pager (정책 카드 뷰어) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // 남은 공간 차지
            ) { page ->
                val program = dummyProgramList[page]
                val currentScore = currentPreferences.find { it.id == program.id }?.value ?: 0

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
                        isStarred = false,
                        logo = if (program.operatingEntity == "central") R.drawable.gov_logo else R.drawable.hissf_logo,
                        isEligible = false,
                        onClick = {} // 클릭 비활성화
                    )

                    Spacer(Modifier.height(32.dp))

                    // --- 2. 선호도 슬라이더 섹션 ---
                    PreferenceSlider(
                        currentScore = currentScore,
                        onScoreChange = { newScore ->
                            // 선호도 상태 업데이트
                            currentPreferences = currentPreferences.map {
                                if (it.id == program.id) it.copy(value = newScore) else it
                            }
                        }
                    )
                }
            }

            // --- 3. 내비게이션 및 제출 버튼 섹션 ---
            PagerNavigation(
                pagerState = pagerState,
                pageCount = pageCount,
                isSubmitEnabled = isSubmitEnabled,
                onSubmit = onSubmit,
                onNext = {
                    scope.launch {
                        if (pagerState.currentPage < pageCount - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onPrevious = {
                    scope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                }
            )
        }
    }
}

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
            steps = 3, // 1, 2, 3, 4, 5
            valueRange = 1f..5f,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 (매우 싫음)", style = MaterialTheme.typography.bodySmall)
            Text("5 (매우 좋음)", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerNavigation(
    pagerState: PagerState,
    pageCount: Int,
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val isLastPage = pagerState.currentPage == pageCount - 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이전 버튼
        if (pagerState.currentPage > 0) {
            Button(
                onClick = onPrevious,
                enabled = pagerState.currentPage > 0, // 0보다 클 때만 활성화
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "이전")
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp)) // 버튼 공간 확보를 위해
        }

        // 최종 제출 버튼 또는 다음 버튼
        Button(
            onClick = onSubmit,
            enabled = isSubmitEnabled, // 5개 모두 선택했을 때만 활성화
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary50
            )
        ) {
            Text("선호도 제출 및 완료", fontWeight = FontWeight.Bold)
        }

        // 다음 버튼 (마지막 페이지가 아닐 때만 별도 표시)
        if (!isLastPage) {
            Button(
                onClick = onNext,
                enabled = pagerState.currentPage < pageCount - 1,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "다음")
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp)) // 버튼 공간 확보를 위해
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewPreferenceUpdateScreen() {
    val vm: AuthViewModel = hiltViewModel()
    PreferenceUpdateScreen(vm.preferenceUi.value, {})
}*/
