package com.example.itda.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Neutral0
import com.example.itda.ui.common.theme.Neutral100
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.Primary60

@Composable
fun ProgramFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            // Surface를 사용하여 둥근 배경과 클릭 효과 구현
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Primary60 else Neutral80 // GreenPrimary 또는 Gray
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Neutral100 else Neutral0,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}