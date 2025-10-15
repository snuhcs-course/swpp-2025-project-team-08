package com.example.itda.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Neutral0
import com.example.itda.ui.common.theme.Neutral60

@Composable
fun HomeHeader(
    username: String,
    programCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${username}님의 맞춤 정책",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0
            )
            Text(
                text = "${programCount}개",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "위로 드래그하여 더 많은 정보를 로드해보세요!",
            fontSize = 12.sp,
            color = Neutral60
        )
    }
}