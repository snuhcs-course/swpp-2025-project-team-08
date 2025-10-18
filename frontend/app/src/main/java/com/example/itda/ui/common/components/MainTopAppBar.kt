package com.example.itda.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.Neutral10
import com.example.itda.ui.common.theme.Primary40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    title: String,
    isBack: Boolean = false,
    onBackClicked: () -> Unit = {}, // 뒤로가기 버튼 클릭 콜백
    visible: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {} // 우측 아이콘 영역
) {
    if (visible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // 원하는 높이
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBack) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Neutral10
                    )
                }
            }
            Text(
                text = title,
                color = Primary40,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            actions()
        }
    }
}
