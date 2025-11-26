package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.util.formatIsoDateToYmd

@Composable
fun FeedInfoCard(
    categories: List<String>,
    startDate: String,
    endDate: String,
    department: String
) {
    val ymdStartDate = formatIsoDateToYmd(startDate)
    val ymdEndDate = formatIsoDateToYmd(endDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp) // 전체 Column에 padding 적용
        ) {
            // 1. 지원 혜택 (categories)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // 항목별 수직 패딩
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "지원혜택",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.End) {
                    categories.forEachIndexed { index, category ->
                        Text(
                            category,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End // 텍스트 우측 정렬
                        )
                        // 마지막 카테고리가 아니면 쉼표와 공백 추가
                        if (index < categories.size - 1) {
                            Text(", ", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // 2. 신청 기간 (startDate/endDate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "신청기간",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f)) // ⬅️ 왼쪽-오른쪽 내용 분리
                if(startDate == "" && endDate == "") {
                    Text(
                        "상시 신청 가능",
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                }
                else {
                    Text(
                        "$ymdStartDate ~ $ymdEndDate",
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                }
            }

            // 3. 정책 기관 (department)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "정책기관",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f)) // ⬅️ 왼쪽-오른쪽 내용 분리
                Text(
                    department,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End, // ⬅️ 텍스트가 여러 줄이 되어도 우측 정렬 유지
                    modifier = Modifier.align(Alignment.CenterVertically) // CenterVertically로 재확인
                )
            }
        }
    }
}