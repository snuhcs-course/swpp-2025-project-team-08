// TextVariants.kt
@file:Suppress("FunctionName")

package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// 형태를 신경 안 쓰고 format만 잡아놓은 상태.
// 개발하면서 추가/변경하며 쓰시면 됩니다.

data class TextVariant(
    val outline: Boolean = false,
    val outlineWidth: Float = 2f,
    val outlineColor: Color = Color.Black,
    val fontSize: TextUnit = 16.sp,
    val color: Color = Color.Unspecified,
    val weight: FontWeight = FontWeight.Normal,
)

object AppTextVariants {
    val Title = TextVariant(
        outline = false,
        fontSize = 20.sp,
        weight = FontWeight.SemiBold,
        color = Color.Unspecified
    )
    val Subtitle = TextVariant(
        outline = false,
        fontSize = 18.sp,
        weight = FontWeight.Medium,
        color = Color.Unspecified
    )
    val Body = TextVariant(
        outline = false,
        fontSize = 16.sp,
        weight = FontWeight.Normal,
        color = Color.Unspecified
    )
    val Caption = TextVariant(
        outline = false,
        fontSize = 12.sp,
        weight = FontWeight.Normal,
        color = Color.Unspecified
    )

    val OutlinedTitle = TextVariant(
        outline = true,
        outlineWidth = 3f,
        outlineColor = Color.Black,
        fontSize = 22.sp,
        weight = FontWeight.ExtraBold,
        color = Color.White
    )
}

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    variant: TextVariant = AppTextVariants.Body,
    color: Color? = null,
    fontSize: TextUnit? = null,
    weight: FontWeight? = null,
    outline: Boolean? = null,
    outlineColor: Color? = null,
    outlineWidth: Float? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
    baseStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    val resolved = variant.copy(
        color = color ?: variant.color,
        fontSize = fontSize ?: variant.fontSize,
        weight = weight ?: variant.weight,
        outline = outline ?: variant.outline,
        outlineColor = outlineColor ?: variant.outlineColor,
        outlineWidth = outlineWidth ?: variant.outlineWidth
    )

    val fillColor = if (resolved.color == Color.Unspecified)
        MaterialTheme.colorScheme.onSurface
    else
        resolved.color

    val commonStyle = baseStyle.merge(
        TextStyle(
            fontSize = resolved.fontSize,
            fontWeight = resolved.weight,
            color = fillColor // 채움 색
        )
    )

    if (!resolved.outline) {
        Text(
            text = text,
            modifier = modifier,
            style = commonStyle,
            maxLines = maxLines,
            textAlign = textAlign,
            overflow = overflow
        )
    } else {
        Box(modifier = modifier) {
            Text(
                text = text,
                style = commonStyle.copy(
                    color = resolved.outlineColor,
                    drawStyle = Stroke(width = resolved.outlineWidth)
                ),
                maxLines = maxLines,
                textAlign = textAlign,
                overflow = overflow
            )
            Text(
                text = text,
                style = commonStyle,
                maxLines = maxLines,
                textAlign = textAlign,
                overflow = overflow
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    // 미리보기를 위한 더미 함수
    AppText(variant = AppTextVariants.OutlinedTitle, text = "Test", fontSize = 5.sp)
}
