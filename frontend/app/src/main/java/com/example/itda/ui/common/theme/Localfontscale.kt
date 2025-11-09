package com.example.itda.ui.common.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * 앱 전체의 폰트 크기 스케일을 관리하는 CompositionLocal
 * 기본값은 1.0f (보통 크기)
 */
val LocalFontScale = compositionLocalOf { 1.0f }