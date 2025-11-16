package com.example.itda.ui.auth.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun PreferenceSelector(
    currentScore: Int,
    onScoreChange: (Int) -> Unit
) {
    // 1점부터 5점까지 아이콘과 설명 매핑
    val preferences = listOf(
        1 to (Icons.Default.SentimentVeryDissatisfied to "매우 싫음"),
        2 to (Icons.Default.SentimentDissatisfied to "싫음"),
        3 to (Icons.Default.SentimentNeutral to "보통"),
        4 to (Icons.Default.SentimentSatisfied to "좋음"),
        5 to (Icons.Default.SentimentVerySatisfied to "매우 좋음")
    )

    val prefColors = mapOf(
        1 to Color(0xFFFF8A80), // 파스텔 레드 (매우 싫음)
        2 to Color(0xFFF79C9C), // 파스텔 오렌지 (싫음)
        3 to Color(0xFF80D8FF), // 파스텔 블루 (보통/중립)
        4 to Color(0xFF83C470), // 파스텔 그린 (좋음)
        5 to Color(0xFF5CA84F)  // 진한 그린 (매우 좋음)
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            preferences.forEach { (score, iconInfo) ->
                val (icon, description) = iconInfo
                val isSelected = (currentScore == score)
                val iconSize = ((1 + (abs(score - 3).toDouble() / 10.0)) * 36).dp
                val color = prefColors[score] ?: MaterialTheme.colorScheme.primary

                // 애니메이션 색상 (선택 여부에 따라 배경색 변경)
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) color else MaterialTheme.colorScheme.surface, // 선택 시 색상 채우기
                    label = "backgroundColor"
                )
                // 애니메이션 테두리 색상 (선택 여부에 따라 테두리 색상 변경)
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    label = "borderColor"
                )
                // 애니메이션 텍스트 색상 (선택 여부에 따라 텍스트 색상 변경)
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    label = "textColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = backgroundColor, // 애니메이션 배경색
                        border = BorderStroke(1.5.dp, borderColor), // 애니메이션 테두리 색상
                        modifier = Modifier
                            .size(iconSize) // 동그란 버튼 크기
                            .clickable(
                                onClick = { onScoreChange(score) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // 물결 효과 제거
                            )
                    ) {}
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = textColor // 설명 텍스트 색상
                    )
                }
            }
        }
    }
}