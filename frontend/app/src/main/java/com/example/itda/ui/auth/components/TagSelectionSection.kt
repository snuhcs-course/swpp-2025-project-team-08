package com.example.itda.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.*

/**
 * 태그 선택 섹션 컴포넌트
 */
@Composable
fun TagSelectionSection(
    selectedTags: List<String>,
    tagInput: String,
    onTagInputChange: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val exampleTags = listOf(
        "독거노인",
        "저소득층",
        "장애인",
        "기초생활수급자",
        "국가유공자",
        "당뇨"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📌회원님에 대해 더 알고 싶어요!",
                fontSize = 20.scaledSp,
                fontWeight = FontWeight.SemiBold,
                color = Neutral10,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "회원님의 상황을 자유롭게 입력해주세요.\n" +
                        "더 정확한 맞춤 복지 정보를 받아보실 수 있어요!\n" +
                        "예시를 참고하시면 더 쉽게 작성하실 수 있어요☺️",
                fontSize = 13.scaledSp,
                color = Neutral40,
                lineHeight = 18.scaledSp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Text(
            text = "직접 입력",
            fontSize = 13.scaledSp,
            fontWeight = FontWeight.Medium,
            color = Neutral20,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = tagInput,
                onValueChange = onTagInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "태그를 입력하세요",
                        fontSize = 14.scaledSp,
                        color = Neutral60
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary60,
                    unfocusedBorderColor = Neutral80,
                    focusedContainerColor = Neutral100,
                    unfocusedContainerColor = Neutral100
                )
            )

            IconButton(
                onClick = { onAddTag(tagInput) },
                modifier = Modifier.size(48.dp),
                enabled = tagInput.trim().isNotEmpty(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (tagInput.trim().isNotEmpty()) Primary60 else Neutral90,
                    contentColor = Neutral100,
                    disabledContainerColor = Neutral90,
                    disabledContentColor = Neutral60
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "태그 추가",
                    tint = Neutral100
                )
            }
        }

        Text(
            text = "예시 태그",
            fontSize = 13.scaledSp,
            fontWeight = FontWeight.Medium,
            color = Neutral20,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exampleTags.forEach { tag ->
                val isSelected = tag in selectedTags

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onRemoveTag(tag)
                        } else {
                            onAddTag(tag)
                        }
                    },
                    label = {
                        Text(
                            text = tag,
                            fontSize = 14.scaledSp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Neutral100,
                        labelColor = Neutral30,
                        selectedContainerColor = Primary60,
                        selectedLabelColor = Neutral100
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) Primary60 else Neutral80
                    )
                )
            }
        }

        if (selectedTags.isNotEmpty()) {
            Text(
                text = "추가한 태그",
                fontSize = 13.scaledSp,
                fontWeight = FontWeight.Medium,
                color = Neutral20,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { onRemoveTag(tag) },
                        label = {
                            Text(
                                text = "#$tag",
                                fontSize = 14.scaledSp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "태그 제거",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Neutral100,
                            labelColor = Primary60,
                            trailingIconContentColor = Primary60
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Primary60
                        )
                    )
                }
            }
        }
    }
}