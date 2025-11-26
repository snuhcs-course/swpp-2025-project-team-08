package com.example.itda.ui.navigation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.common.theme.Primary60
import com.example.itda.ui.common.theme.Primary70
import com.example.itda.ui.common.theme.Primary80

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = EaseInOut),
        label = "splash_progress"
    )

    var textAlpha by remember { mutableFloatStateOf(0f) }
    val animatedTextAlpha by animateFloatAsState(
        targetValue = textAlpha,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
        label = "text_fade_in"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        kotlinx.coroutines.delay(500)
        textAlpha = 1f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SplashBubbles(progress = animatedProgress)

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer { alpha = animatedTextAlpha },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "숨은 복지와 당신을",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "잇다",
                fontSize = 65.sp,
                fontWeight = FontWeight.Bold,
                color = Primary40
            )
        }
    }
}

@Composable
private fun SplashBubbles(progress: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        val reverseProgress = 1.0f - progress

        val rotation1 = reverseProgress * 90f
        val alpha1 = progress.coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-80).dp)
                .graphicsLayer {
                    rotationZ = rotation1
                    translationX = reverseProgress * (-size.width) * 0.6f
                    translationY = reverseProgress * size.height * 0.6f
                    alpha = alpha1
                }
                .size(280.dp)
                .clip(CircleShape)
                .background(Primary60.copy(alpha = 0.4f))
        )

        val rotation2 = reverseProgress * (-90f)

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 50.dp)
                .graphicsLayer {
                    rotationZ = rotation2
                    translationX = reverseProgress * size.width * 0.6f
                    translationY = reverseProgress * (-size.height) * 0.4f
                    alpha = alpha1
                }
                .size(240.dp)
                .clip(CircleShape)
                .background(Primary70.copy(alpha = 0.6f))
        )

        val scale3 = progress.coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 160.dp, y = (-120).dp)
                .graphicsLayer {
                    scaleX = scale3
                    scaleY = scale3
                    alpha = alpha1
                }
                .size(150.dp)
                .clip(CircleShape)
                .background(Primary80.copy(alpha = 0.4f))
        )
    }
}
