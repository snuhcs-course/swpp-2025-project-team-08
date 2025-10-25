package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

/**
 * 공통 입력 필드 컴포넌트
 */
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    isPassword: Boolean = false
) {
    val isError = errorMessage != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Neutral10,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = Neutral60)
            },
            visualTransformation = if (isPassword)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary60,
                unfocusedBorderColor = Neutral90,
                focusedTextColor = Neutral10,
                unfocusedTextColor = Neutral10
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            supportingText = if (isError) {
                {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null
        )
    }
}

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


/**
 * 나이 계산하는 헬퍼 함수
 */
fun calculateAge(birthDate: String): Int? {
    if (birthDate.length != 8) return null

    val birthYear = birthDate.substring(0, 4).toIntOrNull() ?: return null
    val currentYear = 2025

    return currentYear - birthYear
}

/**
 * 생년월일 유효성 검사
 */
fun isValidBirthDate(birthDate: String): Boolean {
    if (birthDate.length != 8) return false

    val year = birthDate.substring(0, 4).toIntOrNull() ?: return false
    val month = birthDate.substring(4, 6).toIntOrNull() ?: return false
    val day = birthDate.substring(6, 8).toIntOrNull() ?: return false

    // 기본 범위 체크
    if (year < 1900 || year > 2025) return false
    if (month < 1 || month > 12) return false
    if (day < 1 || day > 31) return false

    return true
}