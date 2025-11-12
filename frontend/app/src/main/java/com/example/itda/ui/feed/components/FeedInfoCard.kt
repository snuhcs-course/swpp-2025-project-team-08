package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
//        val formattedStartDate = DateFormatter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("지원혜택", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("신청기간", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("정책기관", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(
                Modifier.padding(12.dp),
                horizontalAlignment = Alignment.End

            ) {
                Row {
                    categories.forEach { category ->
                        Text(category, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                if(startDate == "" && endDate == "") {
                    Text("상시 신청 가능", color = MaterialTheme.colorScheme.onSurface)
                }
                else {
                    Text("$ymdStartDate ~ $ymdEndDate", color = MaterialTheme.colorScheme.onSurface)
                }
                Text(department, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
