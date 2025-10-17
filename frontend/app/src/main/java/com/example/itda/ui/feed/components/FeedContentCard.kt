package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FeedContentCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("📋 내용", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("💰 금액: 1인당 10만원")
            Text("✅ 사용처: 동네 작은 가게 (식당, 카페, 마트 등)")
            Text("❌ 대형마트, 백화점, 온라인쇼핑 불가")
            Text("📍 지역: 내가 사는 지역에서만 사용 가능")
            Text("💳 수령: 카드 / 지역사랑상품권 / 선불카드")
        }
    }
}