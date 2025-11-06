package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.auth.components.*
import com.example.itda.ui.common.theme.*

@Composable
fun PersonalInfoScreen(
    ui: AuthViewModel.PersonalInfoUiState,
    onNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPostCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<AddressResult?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral100)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 56.dp, bottom = 16.dp) // 상하 여백 추가

        ) {
            item {
                Text(
                    text = "당신을 알려주세요!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral0
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "잇다에서는 사용자분들의 맞춤 정보를 \n" +
                            "입력받아 여러분이 찾고 계실 정책,\n" +
                            "지원 사업등을 추천해드리고 있습니다!",
                    fontSize = 14.sp,
                    color = Neutral40,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
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
                        // 기존 폼 내용들 (성함, 생년월일, 성별, 주소)
                        InputField(
                            label = "성함",
                            value = ui.name,
                            onValueChange = onNameChange,
                            placeholder = "성함을 입력해주세요",
                            errorMessage = ui.nameError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BirthDateInput(
                            label = "생년월일",
                            value = ui.birthDate,
                            onValueChange = onBirthDateChange,
                            errorMessage = ui.birthDateError
                        )

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
                                selected = ui.gender == "MALE",
                                onClick = { onGenderChange("MALE") },
                                modifier = Modifier.weight(1f)
                            )
                            GenderOption(
                                text = "여성",
                                selected = ui.gender == "FEMALE",
                                onClick = { onGenderChange("FEMALE") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (ui.genderError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.genderError,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "주소",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral10,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAddressDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Neutral95
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (ui.addressError != null) MaterialTheme.colorScheme.error else Neutral80
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                if (selectedAddress != null) {
                                    Text(
                                        text = "[${selectedAddress!!.zonecode}]",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary60
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedAddress!!.address,
                                        fontSize = 14.sp,
                                        color = Neutral10
                                    )
                                } else {
                                    Text(
                                        text = "주소를 검색해주세요",
                                        fontSize = 14.sp,
                                        color = Neutral60,
                                        modifier = Modifier.alpha(0.6f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showAddressDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Primary60
                            ),
                            border = BorderStroke(1.dp, Primary60),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "우편번호 찾기",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (ui.addressError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.addressError,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        if (ui.generalError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ui.generalError,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val isFormValid = ui.name.isNotEmpty() &&
                                ui.birthDate.isNotEmpty() &&
                                ui.gender.isNotEmpty() &&
                                selectedAddress != null

                        Button(
                            onClick = {
                                selectedAddress?.let {
                                    onAddressChange(it.address)
                                    onPostCodeChange(it.zonecode)
                                }
                                onSubmit()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) Primary60 else Neutral90,
                                disabledContainerColor = Neutral80
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = isFormValid && !ui.isLoading
                        ) {
                            if (ui.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Neutral100,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "제출하기",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isFormValid) Neutral100 else Neutral40
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp)) // 하단 여백 추가
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddressDialog) {
        KakaoAddressSearchDialog(
            onDismiss = { showAddressDialog = false },
            onAddressSelected = { result ->
                selectedAddress = result
                showAddressDialog = false
            }
        )
    }
}