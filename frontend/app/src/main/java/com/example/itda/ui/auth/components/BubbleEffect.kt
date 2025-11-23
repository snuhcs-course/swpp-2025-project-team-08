package com.example.itda.ui.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.Primary50

@Composable
fun BubbleEffect(
    pagerState: PagerState,
    paddingValues : PaddingValues,
) {

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        with(density) {


        // 현재 페이지의 오프셋 (-1.0 ~ 0.0)
        val offsetFraction = pagerState.currentPageOffsetFraction

        // 페이지 0에서 벗어날 때 (offsetFraction이 0에서 -1로 갈 때) 애니메이션 활성화
        // animationProgress: 0.0 (Page 0) -> 1.0 (Page 1)
        val animationProgress =
            if (pagerState.currentPage == 0) offsetFraction * -1.0f else 1.0f

        val scale3 = 1.0f - (animationProgress * 1.8f)
        val alpha3 = (1.0f - (animationProgress * 2.4f)).coerceIn(0f, 1f)
        // --- 배경 원 요소 1: 상단 오른쪽 큰 원 (회전하며 화면 밖으로 이동) ---
        val rotation1 = animationProgress * 90f // 0도 -> 90도


        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-80).dp) // 기준 위치 (미세 조정)
                .graphicsLayer {
                    // 회전 및 화면 크기에 비례한 이동 적용
                    rotationZ = rotation1
                    // 화면 너비/높이의 60% 만큼 이동하여 화면 밖으로 사라지도록 설정
                    translationX = animationProgress * (-this.size.width) * 0.6f
                    translationY = animationProgress * (this.size.height) * 0.6f
                    // 원이 사라질 때 투명도도 같이 줄여 자연스럽게 만듦
                    alpha = alpha3
                }
                .size(280.dp)
                .clip(CircleShape)
                .background(Primary50.copy(alpha = 0.2f))
        )

        // --- 배경 원 요소 2: 왼쪽 중간 원 (반대 방향 회전하며 화면 밖으로 이동) ---
        val rotation2 = animationProgress * (-90).dp.toPx() // 0도 -> -90도

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 180.dp) // 기준 위치
                .graphicsLayer {
                    rotationZ = rotation2
                    // 왼쪽 아래로 사라지도록 설정
                    translationX = animationProgress * (this.size.width) * 0.6f
                    translationY = animationProgress * (-this.size.height) * 0.4f
                    alpha = alpha3
                }
                .size(240.dp)
                .clip(CircleShape)
                .background(Primary50.copy(alpha = 0.4f))
        )

        // --- 배경 원 요소 3: 우측 작은 원 (크기만 줄어들도록) ---
        // 이 원은 회전 대신 크기만 줄어들어 부드럽게 사라지도록 합니다. // 1.0 -> 0.5 로 크기가 줄어듦

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 160.dp, y = (-100).dp) // 기준 위치
                .graphicsLayer {
                    // 크기만 줄어들도록 설정
                    scaleX = scale3
                    scaleY = scale3
                    alpha = alpha3 // 투명도 감소
                }
                .size(150.dp)
                .clip(CircleShape)
                .background(Primary50.copy(alpha = 0.2f))
        )
        }
    }

}