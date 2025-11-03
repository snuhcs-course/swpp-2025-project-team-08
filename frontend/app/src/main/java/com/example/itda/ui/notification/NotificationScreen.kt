package com.example.itda.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier
) {
    // TODO: 나중에 ViewModel에서 알림 리스트 가져오기
    val notifications = emptyList<String>() // 임시: 빈 리스트

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "알림",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        modifier = modifier
    ) { padding ->
        if (notifications.isEmpty()) {
            // Empty State: 알림이 없을 때
            EmptyNotificationState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            // 알림 리스트: 스크롤 가능
            NotificationList(
                notifications = notifications,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun EmptyNotificationState(
    modifier: Modifier = Modifier
) {
    // Box를 사용하되, verticalArrangement로 위치 조정
    Column(
        modifier = modifier
            .background(Neutral99)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center  // 중앙 정렬
    ) {
        // 상단 여백 추가 (화면을 살짝 위로)
        Spacer(modifier = Modifier.weight(0.3f))  // 30%

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Neutral90,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp, horizontal = 32.dp)  // 더 넓게
            ) {
                // 알림 아이콘 (배경 원형)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Primary95,
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "알림 없음",
                        modifier = Modifier.size(48.dp),
                        tint = Primary50
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 메인 메시지
                Text(
                    text = "확인하지 않은 알림이 없습니다",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral20,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 서브 메시지
                Text(
                    text = "새로운 알림이 도착하면\n여기에 표시됩니다",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Neutral60,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // 하단 여백
        Spacer(modifier = Modifier.weight(0.7f))  // 70%
    }
}

@Composable
private fun NotificationList(
    notifications: List<String>,
    modifier: Modifier = Modifier
) {
    // 나중에 알림이 생기면 여기에 LazyColumn으로 리스트 표시
    LazyColumn(
        modifier = modifier
            .background(Neutral99)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(notifications.size) { index ->
            // TODO: 알림 아이템 디자인
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = notifications[index],
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun NotificationScreenPreview() {
//    ItdaTheme {
//        NotificationScreen()
//    }
//}