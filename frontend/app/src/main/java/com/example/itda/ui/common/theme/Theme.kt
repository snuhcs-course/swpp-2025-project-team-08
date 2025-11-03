package com.example.itda.ui.common.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 👇 다크모드 ColorScheme (앱 색상 기반)
private val DarkColorScheme = darkColorScheme(
    primary = Primary60,
    onPrimary = Neutral10,
    primaryContainer = Primary30,
    onPrimaryContainer = Primary90,

    secondary = Neutral60,
    onSecondary = Neutral10,

    tertiary = YellowPrimary,
    onTertiary = Neutral10,

    background = Neutral10,
    onBackground = Neutral95,

    surface = Neutral20,
    onSurface = Neutral95,

    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral90,

    error = RedPrimary,
    onError = Neutral100
)

// 👇 라이트모드 ColorScheme (앱 색상 기반)
private val LightColorScheme = lightColorScheme(
    primary = Primary50,
    onPrimary = Neutral100,
    primaryContainer = Primary95,
    onPrimaryContainer = Primary10,

    secondary = Neutral50,
    onSecondary = Neutral100,

    tertiary = YellowPrimary,
    onTertiary = Neutral10,

    background = Neutral100,
    onBackground = Neutral10,

    surface = Neutral99,
    onSurface = Neutral10,

    surfaceVariant = Neutral95,
    onSurfaceVariant = Neutral30,

    error = RedPrimary,
    onError = Neutral100
)

@Composable
fun ItdaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // 👈 false로 변경 (앱 고유 색상 사용)
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