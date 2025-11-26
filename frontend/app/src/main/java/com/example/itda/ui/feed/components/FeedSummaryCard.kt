package com.example.itda.ui.feed.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.navigation.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun FeedSummaryCard(
    content : String
) {
// 1. 무한 애니메이션을 위한 트랜지션 객체 생성
    val infiniteTransition = rememberInfiniteTransition(label = "GradientAnimation")

    // 2. 0f에서 2000f까지 6초 동안 선형적으로 움직이는 Float 값 생성
    val gradientOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f, // 그라데이션이 크게 이동하도록 설정
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing), // 6초 동안 부드럽게 이동
            repeatMode = RepeatMode.Reverse // 0f -> 2000f -> 0f 로 왕복
        ),
        label = "GradientOffsetAnimation"
    )

    // 3. 애니메이션 값을 이용하여 움직이는 그라데이션 Brush 정의
    val animatedGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4285F4), // Google Blue
            Color(0xFF34A853), // Google Green
            Color(0xFFFBC02D), // Google Yellow
            Color(0xFFEA4335), // Google Red
            Color(0xFF4285F4), // 다시 Blue
        ),
        // gradientOffset 값에 따라 시작점과 끝점을 계속 이동시켜 일렁이는 효과 생성
        start = Offset(gradientOffset.value, gradientOffset.value),
        end = Offset(gradientOffset.value + 2000f, gradientOffset.value + 2000f)
    )

    var isLoading by remember { mutableStateOf(true) }
    var isSweepFinished by remember { mutableStateOf(false) }
    val textGradientAnimatable = remember { Animatable(-1000f) }
    // 컴포저블이 처음 로드될 때 로딩을 시뮬레이션하고, 일정 시간 후 해제합니다.
    LaunchedEffect(Unit) {
        delay(300) // 0.3초 로딩 시뮬레이션
        isLoading = false
        // 로딩이 끝난 후, 텍스트 스윕 애니메이션 실행
        textGradientAnimatable.animateTo(
            targetValue = 1500f, // 최종 도착 지점
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing) // 단일 실행 스펙
        )

        // 애니메이션 완료 후 상태 전환
        isSweepFinished = true
    }

    // 텍스트 스윕 Brush 정의
    val sweepingBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onSurface,
            Color(0xFF4285F4), // Sweep 하이라이트
            Color(0xFF34A853),
            MaterialTheme.colorScheme.onSurface,
        ),
        // Animatable의 현재 값으로 Offset을 조정
        start = Offset(textGradientAnimatable.value - 200f, textGradientAnimatable.value -200f),
        end = Offset(textGradientAnimatable.value + 200f, textGradientAnimatable.value + 200f)
    )

    val currentStyle = if (isSweepFinished) {

        SpanStyle(MaterialTheme.colorScheme.onSurface)
    } else {
        SpanStyle(brush = sweepingBrush, fontWeight = FontWeight.Normal) // Sweep 진행 중
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = animatedGradient,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    ) {
        Column(
            Modifier
                .padding(12.dp)
        ) {
            Text(
                text = "AI 요약",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(10.dp))
            if(isLoading) {
                LoadingScreen(
                    modifier = Modifier.height(120.dp),
                    text = ""
                )
            }
            else {
                AnimatedVisibility(
                    visible = !isLoading,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1500))
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = buildAnnotatedString {
                            withStyle(style = currentStyle) {
                                append(content) // 👈 인자로 받은 content를 여기에 넣습니다.
                            } },
                        style = TextStyle(
                            fontSize = 16.scaledSp,
                            lineHeight = 20.scaledSp,
                            textAlign = TextAlign.Left,
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedContentCard() {
    // 미리보기를 위한 더미 함수
    FeedSummaryCard(
        """
        ## 프로그램 개요
        국가 암검진 사업을 통해 암을 조기 발견, 치료를 유도함으로써 암의 치료율을 높이고 암으로 인한 사망을 줄입니다.\\n\\n**지원 대상**\\n암 종류별 대상자 기준(표준 검진 연령 및 성별) 및 검진주기는 다음과 같습니다. (위암) 40세 이상의 남성과 여성 / 2년 (간암) 40세 이상의 해당연도 전 2년간 간암발생고위험군 해당자 / 6개월 (대장암) 50세 이상의 남성과 여성 / 1년 (유방암) 40세 이상의 여성 / 2년 (자궁경부암) 20세 이상의 여성 / 2년 (폐암) 54세~74세 중 30갑년 이상의 흡연력을 가진 흡연자 중 기준 충족하는 경우 / 2년 암검진비용 중 수검자 본인부담금 지원 대상자는 다음과 같습니다. 「의료급여법」에 따른 의료급여수급권자 건강보험가입자 및 피부양자로서 당해연도 검진대상자 중 보험료 부과기준으로 직장가입자는 월 127,500원 이하, 지역가입자는 월57,000원 이하인 자(2024년 11월 기준)\\n\\n**신청 방법**\\n당해연도 암검진대상자로 선정된 경우 지정 암검진기관에서 암검진 수검이 가능합니다. 검진기관 안내 : 건강검진은 주소지와 관계없이 지정된 검진기관 전국 어디서나 받을 수 있습니다. * 검진기관 사정에 따라 예약이 조기에 마감될 수 있으니 사전 확인 및 예약 후 검진을 받으시기 바랍니다. 검진기관 찾기 : 국민건강보험공단 홈페이지 (www.nhis.or.kr)또는 The건강보험 모바일앱에서 가능합니다. * \\\"국민건강보험 > 건강iN >검진기관/병원찾기\\\" 메뉴를 통해 찾아볼 수 있습니다.\\n\\n**지원 내용**\\n위암, 간암, 대장암, 자궁경부암, 유방암, 폐암의 6종에 대한 검진을 실시합니다. 수검자 자부담 10%에 대한 비용을 지원합니다. - 건강보험가입자 상위 50% 대장암, 자궁경부암 대상자 - 의료급여수급권자 - 건강보험가입자 하위 50%"
            
        """.trimIndent()
    )
}
