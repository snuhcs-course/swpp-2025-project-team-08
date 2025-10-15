package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.itda.ui.common.theme.Neutral90
import com.example.itda.ui.common.theme.Primary40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    title : String,
    isBack : Boolean = false,
    onBackClicked: () -> Unit = {}, // 뒤로가기 버튼 클릭 콜백
    visible: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {} // 우측 아이콘 영역
) {
    if (visible) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1, // 제목이 길어질 경우 ... 으로 표시
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            navigationIcon = {
                // isBack 파라미터가 true일 경우에만 뒤로가기 아이콘 표시
                if (isBack) {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            },
            // 파라미터로 받은 actions Composable을 그대로 사용
            actions = actions,
            // 배경색 등 추가적인 디자인 설정
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primary,
//                titleContentColor = MaterialTheme.colorScheme.onPrimary
                containerColor = Neutral90,
                titleContentColor = Primary40
            )
        )
    }
}