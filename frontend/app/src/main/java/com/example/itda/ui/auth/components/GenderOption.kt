package com.example.itda.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

/**
 * 성별 선택 컴포넌트
 */

@Composable
fun GenderOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Primary60 else Neutral100,
            contentColor = if (selected) Neutral100 else Neutral40
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Primary60 else Neutral80
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


/**
 * 8자리 생년월일 입력 필드
 */
@Composable
fun BirthDateInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val isError = errorMessage != null

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Neutral10,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // 숫자만 입력 가능, 최대 8자리
                if (newValue.all { it.isDigit() } && newValue.length <= 8) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("(예: 20001205)", color = Neutral60, fontSize = 14.sp)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number  // 숫자 키패드
            ),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary60,
                unfocusedBorderColor = Neutral90,
                focusedTextColor = Neutral10,
                unfocusedTextColor = Neutral10
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            supportingText = {
                if (isError) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}