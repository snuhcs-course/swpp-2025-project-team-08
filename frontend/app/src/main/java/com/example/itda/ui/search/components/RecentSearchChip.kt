package com.example.itda.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

/**
 * 최근 검색어 칩
 */
@Composable
fun RecentSearchChip(
    searchQuery: String,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp),
        color = Neutral90,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 검색어 텍스트
            Text(
                text = searchQuery,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Neutral20
            )

            // 삭제 아이콘
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "삭제",
                tint = Neutral50,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onDeleteClick)
            )
        }
    }
}