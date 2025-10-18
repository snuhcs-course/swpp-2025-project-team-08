package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*


@Composable
fun PersonalInfoScreen(
    ui: AuthViewModel.PersonalInfoUiState,
    onNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral100),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "당신을 알려주세요!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "잇다에서는 사용자분들의 맞춤 정보를 \n" +
                        "입력받아 여러분이 찾고 계실 정책,\n" +
                        "지원 사업등을 추천해드리고 있습니다!",
                fontSize = 14.sp,
                color = Neutral40,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Neutral100
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                border = BorderStroke(1.dp, Neutral90)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // 성함
                    InputField(
                        label = "성함",
                        value = ui.name,
                        onValueChange = onNameChange,
                        placeholder = "성함을 입력해주세요"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 생년월일
                    BirthDatePicker(
                        label = "생년월일",
                        selectedDate = ui.birthDate,
                        onDateChange = onBirthDateChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 성별 (Radio Button)
                    Text(
                        text = "성별",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Neutral10,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GenderOption(
                            text = "남성",
                            selected = ui.gender == "Male",
                            onClick = { onGenderChange("Male") },
                            modifier = Modifier.weight(1f)
                        )
                        GenderOption(
                            text = "여성",
                            selected = ui.gender == "Female",
                            onClick = { onGenderChange("Female") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 주소
                    InputField(
                        label = "주소",
                        value = ui.address,
                        onValueChange = onAddressChange,
                        placeholder = "주소를 입력해주세요"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 제출하기 버튼
                    val isFormValid = ui.name.isNotEmpty() &&
                            ui.birthDate.isNotEmpty() &&
                            ui.gender.isNotEmpty() &&
                            ui.address.isNotEmpty()

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) Primary60 else Neutral90,
                            disabledContainerColor = Neutral80
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isFormValid
                    ) {
                        Text(
                            "제출하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isFormValid) Neutral100 else Neutral40
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                }
            }
        }
    }
}

