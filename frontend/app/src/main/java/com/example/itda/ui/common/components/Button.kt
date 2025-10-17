@file:Suppress("FunctionName")

package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.*

enum class ButtonKind { Filled, Link, Icon }

/**
 * 단일 진입점 버튼
 * - Filled : 배경색+텍스트, 기본 버튼
 * - Link   : 밑줄 텍스트, w-auto/h-auto
 * - Icon   : 아이콘만
 */
@Composable
fun AppButton(
    variants: ButtonKind,
    onClick: () -> Unit,
    color: Color,
    text: String? = null,
    icon: (@Composable (Modifier) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // Icon 변형 전용 파라미터
    iconSize: Dp = 20.dp,
    containerSize: Dp = 36.dp,
) {
    when (variants) {
        ButtonKind.Filled -> {
            require(text != null) { "Filled 버튼은 text가 필요합니다." }
            val onColor = if (color.luminance() > 0.5f) Color.Black else Color.White
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = RoundedCornerShape(20.dp), // 고정 shape 추후 변경 필요
                modifier = modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = onColor,
                    disabledContainerColor = color.copy(alpha = 0.38f),
                    disabledContentColor = onColor.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        ButtonKind.Link -> {
            require(text != null) { "Link 버튼은 text가 필요합니다." }
            TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier, // w-auto/h-auto
                colors = ButtonDefaults.textButtonColors(
                    contentColor = color,
                    disabledContentColor = color.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        ButtonKind.Icon -> {
            require(icon != null) { "Icon 버튼은 icon 슬롯이 필요합니다." }

            IconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier.size(containerSize),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = color,
                    disabledContentColor = color.copy(alpha = 0.38f)
                )
            ) {
                icon(Modifier.size(iconSize))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    // 미리보기를 위한 더미 함수
    AppButton(ButtonKind.Filled, onClick={}, Primary10, text = "Test")
}

