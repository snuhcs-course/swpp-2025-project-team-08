// Inputs.kt
@file:Suppress("FunctionName")

package com.example.itda.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.composed
import androidx.compose.ui.tooling.preview.Preview

sealed class InputVariant {
    // 상단에 라벨, 기본 인풋
    data class Labeled(
        val label: String,
        val placeholder: String = ""
    ) : InputVariant()

    // 검색창 용
    data class Search(
        val placeholder: String = "",
        val showBack: Boolean = true, // 뒤로가기 버튼
        val showClear: Boolean = true // input text 초기화 버튼
    ) : InputVariant()
}

@Composable
fun AppInput(
    variant: InputVariant,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    bgColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    placeholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(20.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
) {
    when (variant) {
        is InputVariant.Labeled -> LabeledInput(
            label = variant.label,
            placeholder = variant.placeholder,
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            bgColor = bgColor,
            textColor = textColor,
            placeholderColor = placeholderColor,
            borderColor = borderColor,
            focusedBorderColor = focusedBorderColor,
            shape = shape,
            textStyle = textStyle
        )

        is InputVariant.Search -> SearchInput(
            placeholder = variant.placeholder,
            showBack = variant.showBack,
            showClear = variant.showClear,
            value = value,
            onValueChange = onValueChange,
            modifier = modifier, // 검색창은 레이아웃에서 너비 제어
            visualTransformation = visualTransformation,
            bgColor = bgColor,
            textColor = textColor,
            placeholderColor = placeholderColor,
            borderColor = borderColor,
            focusedBorderColor = focusedBorderColor,
            shape = RoundedCornerShape(24.dp), // 추후 변경 필요
            textStyle = textStyle
        )
    }
}

/* ---------- Implementations ---------- */

@Composable
private fun LabeledInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    visualTransformation: VisualTransformation,
    bgColor: Color,
    textColor: Color,
    placeholderColor: Color,
    borderColor: Color,
    focusedBorderColor: Color,
    shape: Shape,
    textStyle: TextStyle,
) {
    var focused by remember { mutableStateOf(false) }
    val border = if (focused) focusedBorderColor else borderColor

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = visualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(bgColor, shape)
                .border(1.dp, border, shape)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = placeholderColor,
                            style = textStyle
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun SearchInput(
    placeholder: String,
    showBack: Boolean,
    showClear: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    visualTransformation: VisualTransformation,
    bgColor: Color,
    textColor: Color,
    placeholderColor: Color,
    borderColor: Color,
    focusedBorderColor: Color,
    shape: Shape,
    textStyle: TextStyle,
) {
    var focused by remember { mutableStateOf(false) }
    val border = if (focused) focusedBorderColor else borderColor

    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(bgColor, shape)
            .border(1.dp, border, shape)
            .padding(horizontal = 8.dp)
            .onFocusChanged { focused = it.isFocused },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .padding(horizontal = 4.dp)
            )
            Spacer(Modifier.width(4.dp))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(color = textColor, textAlign = TextAlign.Start),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = visualTransformation,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            decorationBox = { inner ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = placeholderColor,
                            style = textStyle
                        )
                    }
                    inner()
                }
            }
        )

        if (showClear) {
            Spacer(Modifier.width(4.dp))
            if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(horizontal = 4.dp)
                        .then(
                            Modifier
                                .padding(4.dp)
                                .tapOrClick { onValueChange("") } // 작은 유틸, 아래 정의
                        )
                )
            }
        }
    }
}

private fun Modifier.tapOrClick(onClick: () -> Unit) = composed {
    val interaction = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interaction,
        indication = null // ripple 효과 없애기
    ) { onClick() }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    var text by remember { mutableStateOf("Test") }

    AppInput(
        variant = InputVariant.Labeled(
            label = "제목",
            placeholder = "입력하세요"
        ),
        value = text,
        onValueChange = { text = it }
    )
    AppInput(
            variant = InputVariant.Search(
                placeholder = "입력하세요"
            ),
    value = text,
    onValueChange = { text = it }
    )
}
