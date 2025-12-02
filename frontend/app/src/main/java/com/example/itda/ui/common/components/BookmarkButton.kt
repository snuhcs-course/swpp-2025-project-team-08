package com.example.itda.ui.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf // 초기 로드 상태를 추적하기 위해 추가
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.YellowPrimary

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick : () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val haptics = LocalHapticFeedback.current

    val isInitialized = remember { mutableStateOf(false) }

    val fasterSpring = remember { spring<Float>(dampingRatio = 0.5f, stiffness = 1500f) }

    LaunchedEffect(isBookmarked) {
        // 1. 초기 로드 시점 확인:
        if (!isInitialized.value) {
            isInitialized.value = true
            return@LaunchedEffect
        }

        // 2. 상태 변경 시에만 애니메이션 실행:
        if (isBookmarked) {
            // true로 바뀔 때: 커졌다가 줄어드는 애니메이션 실행
            scale.animateTo(targetValue = 1.3f, animationSpec = fasterSpring)
            scale.animateTo(targetValue = 1f, animationSpec = fasterSpring)
        } else {
            // false로 바뀔 때: 애니메이션 중단 및 원래 크기로 복구
            scale.animateTo(1f)
        }
    }

    Icon(
        imageVector = if(isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
        contentDescription = "즐겨찾기",
        tint = if (isBookmarked) YellowPrimary else MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            )
            .clickable(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                indication = null,
                interactionSource = null
            )
    )
}