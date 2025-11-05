package com.example.itda.ui.profile.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Primary95

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScreen (
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Primary95,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "준비 중입니다",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "빠른 시일 내에 서비스 예정입니다",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// 👇 Preview 추가
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultScreenPreview() {
    DefaultScreen(
        title = "공지사항",
        onBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultScreenPreview_FAQ() {
    DefaultScreen(
        title = "자주 묻는 질문",
        onBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultScreenPreview_CustomerSupport() {
    DefaultScreen(
        title = "고객 문의",
        onBack = {}
    )
}