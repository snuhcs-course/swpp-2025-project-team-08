package com.example.itda.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.common.utils.scaledSp

/**
 * 숫자 입력 필드 (가구원 수, 소득 등)
 */
@Composable
fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String, // "명", "만원" 등
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.scaledSp(),
            fontWeight = FontWeight.Medium,
            color = Neutral10,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Neutral95
            ),
            border = BorderStroke(1.dp, Neutral80)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        // 숫자만 입력 가능
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            onValueChange(newValue)
                        }
                    },
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 14.scaledSp(),
                            color = Neutral60
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Neutral95,
                        unfocusedContainerColor = Neutral95,
                        disabledContainerColor = Neutral95,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.scaledSp()
                    ),
                    singleLine = true
                )

                Text(
                    text = suffix,
                    fontSize = 14.scaledSp(),
                    color = Neutral40,
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp)
                )
            }
        }
    }
}