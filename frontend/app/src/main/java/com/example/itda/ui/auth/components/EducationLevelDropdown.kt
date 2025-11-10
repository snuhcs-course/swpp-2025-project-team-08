package com.example.itda.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.common.utils.scaledSp

/**
 * 학력 선택 드롭다운
 */
@Composable
fun EducationLevelDropdown(
    selectedValue: String?,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // (API 값, 표시 텍스트) 쌍
    val educationOptions = listOf(
        "ELEMENTARY_SCHOOL_STUDENT" to "초등학생",
        "MIDDLE_SCHOOL_STUDENT" to "중학생",
        "HIGH_SCHOOL_STUDENT" to "고등학생",
        "COLLEGE_STUDENT" to "대학생",
        "ELEMENTARY_SCHOOL" to "초졸",
        "MIDDLE_SCHOOL" to "중졸",
        "HIGH_SCHOOL" to "고졸",
        "ASSOCIATE" to "전문대졸",
        "BACHELOR" to "대졸"
    )

    val selectedLabel = educationOptions.find { it.first == selectedValue }?.second ?: "선택해주세요"

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Neutral95
            ),
            border = BorderStroke(1.dp, Neutral80)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLabel,
                    fontSize = 14.scaledSp(),
                    color = if (selectedValue != null) Neutral10 else Neutral60,
                    fontWeight = if (selectedValue != null) FontWeight.Normal else FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "펼치기",
                    tint = Neutral40
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            educationOptions.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontSize = 14.scaledSp(),
                            color = if (selectedValue == value) Primary60 else Neutral10
                        )
                    },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}