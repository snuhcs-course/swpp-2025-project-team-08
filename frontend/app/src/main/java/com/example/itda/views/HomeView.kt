package com.example.itda.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// 사진과 같은 Home 화면을 구성하는 Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    // ViewModel에서 데이터를 새로고침할 수 있는 액션을 받습니다.
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("잇다 - 맞춤 정책") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. 맞춤 정책 추천 섹션
            Text(
                "나에게 딱 맞는 정책 5가지",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    "청년 도약 계좌, 주거 안정 지원금 등",
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 검색 및 알림 섹션
            Button(onClick = onRefresh) {
                Text("정책 새로고침")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("새로운 알림 3건이 도착했습니다.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeView() {
    // 미리보기를 위한 더미 함수
    HomeView(onRefresh = {})
}