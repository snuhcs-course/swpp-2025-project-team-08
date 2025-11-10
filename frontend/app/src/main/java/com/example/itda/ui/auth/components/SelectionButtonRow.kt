package com.example.itda.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.common.utils.scaledSp

/**
 * 선택 버튼 행 (혼인상태, 고용상태 등)
 */
@Composable
fun SelectionButtonRow(
    options: List<Pair<String, String>>, // (value, label) 쌍
    selectedValue: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = selectedValue == value

            OutlinedButton(
                onClick = { onOptionSelected(value) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) Primary60 else Neutral100,
                    contentColor = if (isSelected) Neutral100 else Neutral40
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) Primary60 else Neutral80
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 14.scaledSp(),
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}