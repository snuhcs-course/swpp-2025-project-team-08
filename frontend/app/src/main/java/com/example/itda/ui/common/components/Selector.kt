// Selector.kt
@file:Suppress("FunctionName")

package com.example.itda.ui.common.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// on/off를 보여주는 토글 셀렉터
// 자세한 모양은 추후에 변경

@Composable
fun ToggleSelector(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val trackWidth: Dp = 44.dp
    val trackHeight: Dp = 26.dp
    val thumbSize: Dp = 16.dp
    val borderWidth: Dp = 2.dp
    val shape = RoundedCornerShape(percent = 50)

    val borderColor = Color(0xFF9CA3AF)
    val trackColorOn = Color(0xFFBFC5CC)
    val trackColorOff = Color(0xFFE6E9EE)
    val thumbColorEnabled = Color(0xFF6B7280)
    val thumbColorDisabled = Color(0xFF9AA1AA)

    // 내부 패딩(썸이 트랙에 맞닿지 않도록)
    val innerPadding = ((trackHeight - thumbSize) / 2f)

    // 썸 이동 거리 계산: (트랙 내부폭) - (좌우패딩*2) - 썸크기
    val innerWidth = trackWidth - borderWidth * 2
    val travel = innerWidth - (innerPadding * 2) - thumbSize

    val targetX = if (checked) travel else 0.dp
    val offsetX = animateDpAsState(targetValue = targetX, label = "thumbOffset")

    val interaction = remember { MutableInteractionSource() }
    val alpha = if (enabled) 1f else 0.5f

    // ---- 트랙 ----
    Box(
        modifier = modifier
            .size(trackWidth, trackHeight)
            .border(BorderStroke(borderWidth, borderColor), shape)
            .background(color = (if (checked) trackColorOn else trackColorOff), shape = shape)
            .alpha(alpha)
            .semantics {
                stateDescription = if (checked) "켜짐" else "꺼짐"
            }
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interaction,
                indication = ripple(bounded = true, radius = trackHeight) // 트랙 기반 리플
            ) { onCheckedChange(!checked) }
    ) {
        // ---- 썸 ----
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = innerPadding) // 좌우 여백
                .offset(x = offsetX.value)          // 애니메이션 이동
                .size(thumbSize)
                .background(
                    color = if (enabled) thumbColorEnabled else thumbColorDisabled,
                    shape = CircleShape
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    var checked by remember { mutableStateOf(true) }   // ✅ 상태 보유
    ToggleSelector(
        checked = checked,
        onCheckedChange = { checked = it }
    )
}
