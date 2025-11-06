package com.example.itda.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.Primary20
import com.example.itda.ui.common.theme.Primary40

/**
 * 로딩 상태를 표시하는 화면 컴포저블
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    text: String = "잇다"
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Primary40,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = Primary20,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingScreen() {
    MaterialTheme {
        LoadingScreen()
    }
}