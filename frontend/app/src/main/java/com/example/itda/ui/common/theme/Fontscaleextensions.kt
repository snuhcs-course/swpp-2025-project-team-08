package com.example.itda.ui.common.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 폰트 크기에 스케일을 적용하는 확장 함수
 *
 * 사용 예시:
 * Text(
 *     text = "안녕하세요",
 *     fontSize = 16.scaledSp  // 설정에 따라 자동으로 스케일 적용
 * )
 */
val Int.scaledSp: TextUnit
    @Composable
    get() = (this * LocalFontScale.current).sp

val Float.scaledSp: TextUnit
    @Composable
    get() = (this * LocalFontScale.current).sp

val Double.scaledSp: TextUnit
    @Composable
    get() = (this * LocalFontScale.current).sp

/**
 * 폰트 크기가 이미 TextUnit인 경우
 *
 * 사용 예시:
 * Text(
 *     text = "안녕하세요",
 *     fontSize = 16.sp.scaled
 * )
 */
val TextUnit.scaled: TextUnit
    @Composable
    get() = this * LocalFontScale.current