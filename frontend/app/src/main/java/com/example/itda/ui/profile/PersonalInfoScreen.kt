package com.example.itda.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.common.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PersonalInfoViewModel = hiltViewModel()
    val ui by viewModel.personalInfoUi.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "개인정보 수정",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Neutral99)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Neutral90,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    PersonalInfoFieldSimple(
                        label = "이름",
                        value = ui.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = "이름을 입력해주세요",
                        errorMessage = ui.nameError
                    )

                    PersonalInfoFieldSimple(
                        label = "생년월일",
                        value = ui.birthDate,
                        onValueChange = { viewModel.onBirthDateChange(it) },
                        placeholder = "YYYY-MM-DD",
                        errorMessage = ui.birthDateError
                    )

                    // 성별 드롭다운
                    PersonalInfoDropdown(
                        label = "성별",
                        value = ui.gender,
                        options = listOf("남성", "여성"),
                        onValueChange = { viewModel.onGenderChange(it) },
                        errorMessage = ui.genderError
                    )

                    PersonalInfoFieldSimple(
                        label = "주소지",
                        value = ui.address,
                        onValueChange = { viewModel.onAddressChange(it) },
                        placeholder = "주소를 입력해주세요",
                        errorMessage = ui.addressError
                    )

                    // 결혼 여부
                    PersonalInfoDropdown(
                        label = "결혼 여부",
                        value = ui.maritalStatus,
                        options = listOf("미혼", "기혼", "이혼/사별"),
                        onValueChange = { viewModel.onMaritalStatusChange(it) }
                    )

                    // 학력
                    PersonalInfoDropdown(
                        label = "학력",
                        value = ui.education,
                        options = listOf("고졸", "재학생", "휴학생", "졸업예정", "전문대졸", "대졸", "석사", "박사"),
                        onValueChange = { viewModel.onEducationChange(it) }
                    )

                    PersonalInfoFieldSimple(
                        label = "가구원 수",
                        value = ui.householdSize,
                        onValueChange = { viewModel.onHouseholdSizeChange(it) },
                        placeholder = "숫자만 입력"
                    )

                    PersonalInfoFieldSimple(
                        label = "가구원 소득 (만원)",
                        value = ui.householdIncome,
                        onValueChange = { viewModel.onHouseholdIncomeChange(it) },
                        placeholder = "숫자만 입력"
                    )

                    // 취업 상태
                    PersonalInfoDropdown(
                        label = "취업 상태",
                        value = ui.employmentStatus,
                        options = listOf("재직자", "미취업자", "자영업자"),
                        onValueChange = { viewModel.onEmploymentStatusChange(it) },
                        isLast = true
                    )

                    if (ui.generalError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ui.generalError ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val success = viewModel.submitPersonalInfo()
                                if (success) {
                                    onBack()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Neutral20,
                            disabledContainerColor = Neutral80
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !ui.isLoading
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "완료",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PersonalInfoFieldSimple(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = if (enabled) Neutral10 else Neutral50,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder,
                    color = Neutral70,
                    fontSize = 14.sp
                )
            },
            enabled = enabled,
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Neutral20,
                unfocusedBorderColor = Neutral90,
                disabledBorderColor = Neutral95,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Neutral99,
                focusedTextColor = Neutral10,
                unfocusedTextColor = Neutral10,
                disabledTextColor = Neutral50
            ),
            shape = RoundedCornerShape(8.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoDropdown(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    isLast: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Neutral10,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                placeholder = {
                    Text(
                        "선택해주세요",
                        color = Neutral70,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Neutral20,
                    unfocusedBorderColor = Neutral90,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}