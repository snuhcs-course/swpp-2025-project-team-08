package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    isPassword: Boolean = false
) {
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary60,
                unfocusedBorderColor = Neutral90,
                focusedTextColor = Neutral10,
                unfocusedTextColor = Neutral10
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
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
 * 생년월일 드롭다운 컴포넌트
 */
@Composable
fun BirthDatePicker(
    label: String,
    selectedDate: String,
    onDateChange: (String) -> Unit
) {
    // selectedDate를 년/월/일로 분리
    var year by remember(selectedDate) {
        mutableStateOf(
            if (selectedDate.length >= 4) selectedDate.substring(0, 4) else ""
        )
    }
    var month by remember(selectedDate) {
        mutableStateOf(
            if (selectedDate.length >= 6) selectedDate.substring(4, 6) else ""
        )
    }
    var day by remember(selectedDate) {
        mutableStateOf(
            if (selectedDate.length == 8) selectedDate.substring(6, 8) else ""
        )
    }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Neutral10,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 년도 입력 (4자리)
            OutlinedTextField(
                value = year,
                onValueChange = { newValue ->
                    if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                        year = newValue
                        updateFullDate(year, month, day, onDateChange)
                    }
                },
                modifier = Modifier.weight(1.5f),
                placeholder = {
                    Text(
                        "YYYY",
                        fontSize = 14.sp,
                        color = Neutral60,
                        textAlign = TextAlign.Center
                    )
                },
                suffix = { Text("년", fontSize = 14.sp, color = Neutral40) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary60,
                    unfocusedBorderColor = Neutral90,
                    focusedTextColor = Neutral10,
                    unfocusedTextColor = Neutral10
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )

            // 월 입력 (2자리)
            OutlinedTextField(
                value = month,
                onValueChange = { newValue ->
                    // 1~12 범위 체크
                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                        val monthNum = newValue.toIntOrNull()
                        if (monthNum == null || monthNum <= 12) {
                            month = newValue
                            updateFullDate(year, month, day, onDateChange)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "MM",
                        fontSize = 14.sp,
                        color = Neutral60,
                        textAlign = TextAlign.Center
                    )
                },
                suffix = { Text("월", fontSize = 14.sp, color = Neutral40) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary60,
                    unfocusedBorderColor = Neutral90,
                    focusedTextColor = Neutral10,
                    unfocusedTextColor = Neutral10
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )

            // 일 입력 (2자리)
            OutlinedTextField(
                value = day,
                onValueChange = { newValue ->
                    // 1~31 범위 체크
                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                        val dayNum = newValue.toIntOrNull()
                        if (dayNum == null || dayNum <= 31) {
                            day = newValue
                            updateFullDate(year, month, day, onDateChange)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "DD",
                        fontSize = 14.sp,
                        color = Neutral60,
                        textAlign = TextAlign.Center
                    )
                },
                suffix = { Text("일", fontSize = 14.sp, color = Neutral40) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary60,
                    unfocusedBorderColor = Neutral90,
                    focusedTextColor = Neutral10,
                    unfocusedTextColor = Neutral10
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

/**
 * 년/월/일을 YYYYMMDD 형식으로 합쳐서 콜백 호출
 */
private fun updateFullDate(
    year: String,
    month: String,
    day: String,
    onDateChange: (String) -> Unit
) {
    // 모든 값이 입력되었을 때만 업데이트
    if (year.length == 4 && month.isNotEmpty() && day.isNotEmpty()) {
        // 월과 일을 2자리로 패딩 (9 → 09)
        val paddedMonth = month.padStart(2, '0')
        val paddedDay = day.padStart(2, '0')

        val fullDate = "$year$paddedMonth$paddedDay"
        onDateChange(fullDate)
    }
}

/**
 * 나이 계산하는 헬퍼 함수
 */
fun calculateAge(birthDate: String): Int? {
    if (birthDate.length != 8) return null

    val birthYear = birthDate.substring(0, 4).toIntOrNull() ?: return null
    val currentYear = 2025 // 또는 Calendar.getInstance().get(Calendar.YEAR)

    return currentYear - birthYear
}