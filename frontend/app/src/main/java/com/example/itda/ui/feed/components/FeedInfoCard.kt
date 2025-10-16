package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FeedInfoCard(
    category : String,
    startDate : String,
    endDate : String,
    department : String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(Modifier.padding(12.dp)) {
                Text("지원혜택", fontWeight = FontWeight.Bold)
                Text("신청기간", fontWeight = FontWeight.Bold)
                Text("정책기관", fontWeight = FontWeight.Bold)
            }

            Column(Modifier.padding(12.dp)) {
                Text(category)
                Text("$startDate ~ $endDate")//"25.09.15(월) ~ 25.11.30(일)")
                Text(department)
            }
        }
    }
}