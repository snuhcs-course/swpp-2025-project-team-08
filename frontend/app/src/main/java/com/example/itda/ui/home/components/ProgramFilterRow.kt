package com.example.itda.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.data.model.Category
import com.example.itda.ui.common.theme.scaledSp

@Composable
fun ProgramFilterRow(
    categories: List<Category>,
    selectedCategory: Category,
    selectedCategoryCount : Int,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.category == selectedCategory.category

            // 애니메이션 값을 제어할 수 있는 Animatable을 생성
            val animatedCount = remember { Animatable(0f) }

            // isSelected 나 selectedCategoryCount 값이 변경될 때마다 이펙트 실행
            LaunchedEffect(key1 = isSelected, key2 = selectedCategoryCount) {
                if (isSelected) {
                    val durationPerItem = 20
                    animatedCount.snapTo(0f)

                    val dynamicDuration = (selectedCategoryCount * durationPerItem)
                        .coerceIn(150, 500)

                    animatedCount.snapTo(0f)

                    animatedCount.animateTo(
                        targetValue = selectedCategoryCount.toFloat(),
                        // 계산된 dynamicDuration을 tween에 적용
                        animationSpec = tween(durationMillis = dynamicDuration)
                    )
                } else {
                    // 선택이 해제되면 0으로 리셋
                    animatedCount.snapTo(0f)
                }
            }

            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    text = if (isSelected)
                        "${category.value} ${animatedCount.value.toInt()}"
                    else
                        category.value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.scaledSp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
