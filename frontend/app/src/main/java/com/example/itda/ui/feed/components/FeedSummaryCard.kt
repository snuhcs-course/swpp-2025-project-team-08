package com.example.itda.ui.feed.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.common.theme.Primary95

@Composable
fun FeedSummaryCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    summary : String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary95)
            .border(1.dp, Primary40, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .animateContentSize()
    ) {
        Column(Modifier
            .background(Primary95)
            .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
            ) {
                Text("📖 AI 요약", fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "toggle"
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(summary)
            }
        }
    }
}