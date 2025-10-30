package com.example.itda.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PersonalInfoViewModel = hiltViewModel()

    val name by viewModel.name.collectAsState()
    val age by viewModel.age.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val address by viewModel.address.collectAsState()
    val maritalStatus by viewModel.maritalStatus.collectAsState()
    val education by viewModel.education.collectAsState()
    val householdSize by viewModel.householdSize.collectAsState()
    val householdIncome by viewModel.householdIncome.collectAsState()
    val excludedKeywords by viewModel.excludedKeywords.collectAsState()

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
                .background(Color(0xFFF5F5F5))
                .padding(20.dp)  // 화면 가장자리 여백
        ) {
            Spacer(modifier = Modifier.height(16.dp))  // 상단 여백

            // 테두리 있는 박스로 모든 필드 감싸기
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)  // Card 내부 여백
                ) {
                    PersonalInfoFieldSimple(
                        label = "이름",
                        value = name,
                        onValueChange = { viewModel.updateName(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "나이",
                        value = age,
                        onValueChange = { viewModel.updateAge(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "성별",
                        value = gender,
                        onValueChange = { viewModel.updateGender(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "주소지",
                        value = address,
                        onValueChange = { viewModel.updateAddress(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "결혼 여부",
                        value = maritalStatus,
                        onValueChange = { viewModel.updateMaritalStatus(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "학력",
                        value = education,
                        onValueChange = { viewModel.updateEducation(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "가구원 수",
                        value = householdSize,
                        onValueChange = { viewModel.updateHouseholdSize(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "가구원 소득",
                        value = householdIncome,
                        onValueChange = { viewModel.updateHouseholdIncome(it) },
                        placeholder = "Value"
                    )

                    PersonalInfoFieldSimple(
                        label = "제외 알고리즘",
                        value = excludedKeywords,
                        onValueChange = { viewModel.updateExcludedKeywords(it) },
                        placeholder = "Value",
                        isLast = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))  // 완료 버튼 위 여백

                    // 완료 버튼
                    Button(
                        onClick = {
                            viewModel.savePersonalInfo()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D2D2D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "완료",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))  // 하단 여백
        }
    }
}

@Composable
fun PersonalInfoFieldSimple(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 24.dp)  // 필드 간 간격
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1C1B1F),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder,
                    color = Color(0xFFBDBDBD),
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2D2D2D),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF1C1B1F),
                unfocusedTextColor = Color(0xFF1C1B1F)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}