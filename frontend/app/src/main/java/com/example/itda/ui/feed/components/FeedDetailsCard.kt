package com.example.itda.ui.feed.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.example.itda.ui.common.theme.*

@Composable
fun FeedDetailCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    details: String = ""
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),          // ← 바깥 padding 제거
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
            ) {
                Text(
                    "상세 내용",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "toggle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                MarkdownText(
                    modifier = Modifier.padding(10.dp),
                    markdown = details,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.scaledSp,
                        lineHeight = 20.scaledSp,
                        textAlign = TextAlign.Left,
                    ),
                )
            }
        }
    }
}
