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
    onMaritalStatusChange: (String?) -> Unit,
    onEducationLevelChange: (String?) -> Unit,
    onHouseholdSizeChange: (String) -> Unit,
    onHouseholdIncomeChange: (String) -> Unit,
    onEmploymentStatusChange: (String?) -> Unit,
    onSubmit: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<AddressResult?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 56.dp, bottom = 16.dp)
        ) {
            item {
                Text(
                    text = "당신을 알려주세요!",
                    fontSize = 32.scaledSp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
                    fontSize = 14.scaledSp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ========== 첫 번째 카드: 필수 입력 항목 ==========
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "성별",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
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
                            fontSize = 12.scaledSp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "주소",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAddressDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (ui.addressError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
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
                                        fontSize = 14.scaledSp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedAddress!!.address,
                                        fontSize = 14.scaledSp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(
                                        text = "주소를 검색해주세요",
                                        fontSize = 14.scaledSp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "우편번호 찾기",
                                fontSize = 14.scaledSp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (ui.addressError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.addressError,
                                fontSize = 12.scaledSp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💡딱 맞는 복지 프로그램을 찾아드릴 수 있어요!",
                        fontSize = 16.scaledSp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral10,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "아래 정보를 알려주시면\n" +
                                "회원님께 꼭 맞는 복지 프로그램을 추천해드려요.\n\n" +
                                "지금 입력하지 않아도 괜찮아요.\n" +
                                "나중에 언제든 추가하거나 바꿀 수 있어요!",
                        fontSize = 14.scaledSp,
                        color = Neutral40,
                        lineHeight = 20.scaledSp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
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
                        Text(
                            text = "혼인 상태",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral10,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SelectionButtonRow(
                            options = listOf(
                                "SINGLE" to "미혼",
                                "MARRIED" to "기혼",
                                "DIVORCED_OR_BEREAVED" to "이혼/사별"
                            ),
                            selectedValue = ui.maritalStatus,
                            onOptionSelected = onMaritalStatusChange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "최종 학력",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral10,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        EducationLevelDropdown(
                            selectedValue = ui.educationLevel,
                            onValueSelected = onEducationLevelChange
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        NumberInputField(
                            label = "가구원 수",
                            value = ui.householdSize,
                            onValueChange = onHouseholdSizeChange,
                            placeholder = "예: 4",
                            suffix = "명"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        NumberInputField(
                            label = "연간 가구 소득",
                            value = ui.householdIncome,
                            onValueChange = onHouseholdIncomeChange,
                            placeholder = "예: 5000",
                            suffix = "만원"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "고용 상태",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral10,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SelectionButtonRow(
                            options = listOf(
                                "EMPLOYED" to "재직자",
                                "UNEMPLOYED" to "미취업자",
                                "SELF_EMPLOYED" to "자영업자"
                            ),
                            selectedValue = ui.employmentStatus,
                            onOptionSelected = onEmploymentStatusChange
                        )

                        if (ui.generalError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ui.generalError,
                                fontSize = 12.scaledSp,
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
                                containerColor = if (isFormValid)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = isFormValid && !ui.isLoading
                        ) {
                            if (ui.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "제출하기",
                                    fontSize = 16.scaledSp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isFormValid)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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