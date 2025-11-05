package com.example.itda.ui.common.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    // Primary colors - 다크모드에서는 어두운 청록
    primary = Primary40,                    // 버튼 배경
    onPrimary = Neutral100,                 // 버튼 텍스트
    primaryContainer = Primary30,           // 카드 배경 (어두운 청록)
    onPrimaryContainer = Neutral95,         // 카드 내 텍스트 (밝게)

    // Secondary colors - 섹션 제목 등에 사용
    secondary = Primary50,
    onSecondary = Neutral100,
    secondaryContainer = Primary40,         // 섹션 제목 배경 (중간 청록)
    onSecondaryContainer = Neutral95,       // 섹션 제목 텍스트 (밝게)

    // Tertiary colors - 아이콘 등에 사용
    tertiary = Primary60,                   // 밝은 청록 (아이콘용)
    onTertiary = Neutral10,
    tertiaryContainer = Primary30,
    onTertiaryContainer = Primary90,

    // Background & Surface
    background = Neutral10,                 // 메인 배경 (거의 검정)
    onBackground = Neutral95,               // 배경 위 텍스트 (밝게)
    surface = Neutral20,                    // TopAppBar 등
    onSurface = Neutral95,                  // 일반 텍스트 (밝게)
    surfaceVariant = Primary20,             // 아이콘 배경 (어두운 청록)
    onSurfaceVariant = Neutral80,           // 보조 텍스트 (밝게)

    // Borders & Outlines
    outline = Neutral40,                    // 테두리 (어둡게)
    outlineVariant = Neutral30,

    // 추가 Surface 레벨
    surfaceTint = Primary40,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,

    // Error states
    error = RedSecondary,
    onError = Neutral10,
    errorContainer = RedPrimary,
    onErrorContainer = Neutral95,
)

private val LightColorScheme = lightColorScheme(
    // Primary colors - 라이트모드에서는 밝은 청록
    primary = Primary50,                    // 버튼 배경
    onPrimary = Neutral100,                 // 버튼 텍스트
    primaryContainer = Primary95,           // 카드 배경 (밝은 청록)
    onPrimaryContainer = Neutral10,         // 카드 내 텍스트 (어둡게)

    // Secondary colors - 섹션 제목 등에 사용
    secondary = Primary40,
    onSecondary = Neutral100,
    secondaryContainer = Primary80,         // 섹션 제목 배경 (밝은 청록)
    onSecondaryContainer = Neutral30,       // 섹션 제목 텍스트 (어둡게)

    // Tertiary colors - 아이콘 등에 사용
    tertiary = Primary40,                   // 진한 청록 (아이콘용)
    onTertiary = Neutral100,
    tertiaryContainer = Primary95,
    onTertiaryContainer = Primary20,

    // Background & Surface
    background = Neutral100,                // 메인 배경 (흰색)
    onBackground = Neutral10,               // 배경 위 텍스트 (어둡게)
    surface = Neutral100,                   // TopAppBar 등
    onSurface = Neutral10,                  // 일반 텍스트 (어둡게)
    surfaceVariant = Primary99,             // 아이콘 배경 (거의 흰 청록)
    onSurfaceVariant = Neutral50,           // 보조 텍스트 (회색)

    // Borders & Outlines
    outline = Neutral90,                    // 테두리 (밝게)
    outlineVariant = Neutral80,

    // 추가 Surface 레벨
    surfaceTint = Primary50,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,

    // Error states
    error = RedPrimary,
    onError = Neutral100,
    errorContainer = RedSecondary,
    onErrorContainer = Neutral10,
)

@Composable
fun ItdaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}