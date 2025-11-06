package com.example.itda.ui.search.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Neutral20
import com.example.itda.ui.common.theme.Primary60

@Composable
fun SearchResultHeader(
    searchQuery: String,
    totalResults: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "'$searchQuery'",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Primary60
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "검색 결과 ${totalResults}개",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Neutral20
        )
    }
}