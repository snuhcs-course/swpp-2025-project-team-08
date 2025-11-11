package com.example.itda.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.search.SearchViewModel

@Composable
fun SearchFilterRow(
    sortType: SearchViewModel.SortType,
    onSortTypeChange: (SearchViewModel.SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "정렬",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Neutral40
        )

        SortChip(
            text = "정확도순",
            isSelected = sortType == SearchViewModel.SortType.RANK,
            onClick = { onSortTypeChange(SearchViewModel.SortType.RANK) }
        )

        SortChip(
            text = "최신순",
            isSelected = sortType == SearchViewModel.SortType.LATEST,
            onClick = { onSortTypeChange(SearchViewModel.SortType.LATEST) }
        )
    }
}


@Composable
private fun SortChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Primary60 else Neutral95)
            .border(
                width = 1.dp,
                color = if (isSelected) Primary60 else Neutral80,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.scaledSp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Neutral100 else Neutral40
        )
    }
}