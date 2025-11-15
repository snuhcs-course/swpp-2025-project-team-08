package com.example.itda.ui.navigation

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.itda.R
import com.example.itda.ui.common.theme.Neutral30
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.Neutral90
import com.example.itda.ui.common.theme.scaledSp

// ⚠️ 실제 프로젝트의 R.raw. ID로 대체해야 합니다.
private val onboardingVideos = listOf(
    R.raw.onboarding1_1_home_feed,
    R.raw.onboarding1_2_category,
    R.raw.onboarding1_3_exclusion,
    R.raw.onboarding2_search,
    R.raw.onboarding3_bookmark,
    R.raw.onboarding4_profile_update,
    R.raw.onboarding5_setting
)

private val onboardingVideoDescription = listOf(
    "📄 홈 화면에서 맞춤 정책을 확인하세요.",
    "🗂️ 카테고리 별로 확인할 수 있습니다.",
    "❌ 관심없는 정책은 왼쪽으로 밀어 제외하세요.",
    "🔍 검색 화면에서 원하는 정책을 키워드로 검색해보세요.",
    "🔖 북마크 화면에서 북마크한 정책들을 모아서 확인해보세요.",
    "👤 내 정보 화면에서 내가 입력했던 정보들을 수정할 수 있습니다.",
    "⚙️ 내 정보 화면에서 설정에 들어가 다크모드 / 글자크기 조정이 가능합니다."
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onSubmit: () -> Unit // 온보딩 완료 시 호출할 함수
) {
    val coroutineScope = rememberCoroutineScope()
    val pageCount = onboardingVideos.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val currentPage = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral90)
    ) {
        // 1. HorizontalPager: 스와이프 가능한 영역
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f) // 남은 공간을 모두 차지
        ) { pageIndex ->
            val videoResId = onboardingVideos[pageIndex]
            val videoDescription = onboardingVideoDescription[pageIndex]

            VideoPage(
                videoResId = videoResId,
                videoDescription = videoDescription,
                isCurrentPage = pageIndex == currentPage,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, end = 12.dp, start = 12.dp)
            )
        }

        // 2. 페이지 인디케이터 및 네비게이션 버튼
        OnBoardingBottomNavigation(
            pagerState = pagerState,
            pageCount = pageCount,
            onSubmit = onSubmit,
        )
    }
}

/**
 * 페이지별 비디오 재생을 담당하는 컴포저블.
 * isCurrentPage에 따라 재생/일시정지 상태를 관리합니다.
 */
@Composable
private fun VideoPage(
    videoResId: Int,
    videoDescription : String,
    isCurrentPage: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ExoPlayer 인스턴스 생성 및 기억 (페이지별로 고유한 플레이어)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = "android.resource://${context.packageName}/${videoResId}"
            val mediaItem = MediaItem.fromUri(videoUri)

            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ONE // 반복 재생
            playWhenReady = isCurrentPage // 현재 페이지일 때만 재생
            prepare()
        }
    }

    // ⭐️ isCurrentPage 상태가 변경될 때마다 재생/일시정지 상태를 업데이트합니다.
    DisposableEffect(isCurrentPage) {
        if (isCurrentPage) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
            // 페이지를 벗어날 때 처음으로 되감기 (선택 사항)
            exoPlayer.seekTo(0)
        }
        onDispose {}
    }

    // 컴포저블이 화면에서 제거될 때(Disposable) 플레이어를 해제
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        AndroidView(
            modifier = Modifier
                .weight(8f)
                .padding(top = 24.dp),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false // 컨트롤러 숨기기
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        Text(
            text = videoDescription,
            fontSize = 16.scaledSp,
            color = Neutral30,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnBoardingBottomNavigation(
    pagerState: PagerState,
    pageCount: Int,
    onSubmit: () -> Unit,
    // onPrev: () -> Unit, // ⚠️ 제거됨
    // onNext: () -> Unit // ⚠️ 제거됨
) {
    val currentPage = pagerState.currentPage
    val isLastPage = currentPage == pageCount - 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center, // 중앙 정렬
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLastPage) {
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "시작하기",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                repeat(pageCount) { index ->

                    val isSelected = index == currentPage

                    val dotSize by animateDpAsState(
                        targetValue = if (isSelected) 12.dp else 8.dp,
                        label = "Dot Size Animation"
                    )

                    val color = if (index == currentPage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Neutral80
                    }

                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnBoardingScreen() {
    OnBoardingScreen({})
}
