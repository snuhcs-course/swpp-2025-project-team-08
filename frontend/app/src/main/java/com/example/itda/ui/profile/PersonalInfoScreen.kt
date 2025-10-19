package com.example.itda.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    navController: NavController
) {
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
                title = { Text("개인정보 수정", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color(0xFF6B4C7A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFAF8FC)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFFAF8FC))
                .padding(20.dp)
        ) {
            // 안내 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0E6F6)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF6B4C7A),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "정확한 정보를 입력하시면\n더 나은 정책을 추천받을 수 있어요",
                        fontSize = 14.sp,
                        color = Color(0xFF6B4C7A),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 입력 필드들을 카드로 감싸기
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    PersonalInfoFieldStyled(
                        label = "이름",
                        value = name,
                        onValueChange = { viewModel.updateName(it) },
                        placeholder = "이름을 입력하세요",
                        icon = Icons.Default.Person
                    )

                    PersonalInfoFieldStyled(
                        label = "나이",
                        value = age,
                        onValueChange = { viewModel.updateAge(it) },
                        placeholder = "나이를 입력하세요",
                        icon = Icons.Default.DateRange
                    )

                    PersonalInfoFieldStyled(
                        label = "성별",
                        value = gender,
                        onValueChange = { viewModel.updateGender(it) },
                        placeholder = "성별을 입력하세요",
                        icon = Icons.Default.Face
                    )

                    PersonalInfoFieldStyled(
                        label = "주소지",
                        value = address,
                        onValueChange = { viewModel.updateAddress(it) },
                        placeholder = "주소를 입력하세요",
                        icon = Icons.Default.Place
                    )

                    PersonalInfoFieldStyled(
                        label = "결혼 여부",
                        value = maritalStatus,
                        onValueChange = { viewModel.updateMaritalStatus(it) },
                        placeholder = "결혼 여부를 입력하세요",
                        icon = Icons.Default.FavoriteBorder
                    )

                    PersonalInfoFieldStyled(
                        label = "학력",
                        value = education,
                        onValueChange = { viewModel.updateEducation(it) },
                        placeholder = "학력을 입력하세요",
                        icon = Icons.Default.School
                    )

                    PersonalInfoFieldStyled(
                        label = "가구원 수",
                        value = householdSize,
                        onValueChange = { viewModel.updateHouseholdSize(it) },
                        placeholder = "가구원 수를 입력하세요",
                        icon = Icons.Default.Home
                    )

                    PersonalInfoFieldStyled(
                        label = "가구원 소득",
                        value = householdIncome,
                        onValueChange = { viewModel.updateHouseholdIncome(it) },
                        placeholder = "가구원 소득을 입력하세요",
                        icon = Icons.Default.AccountBalance
                    )

                    PersonalInfoFieldStyled(
                        label = "제외 알고리즘",
                        value = excludedKeywords,
                        onValueChange = { viewModel.updateExcludedKeywords(it) },
                        placeholder = "제외할 키워드를 입력하세요",
                        icon = Icons.Default.Close,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 완료 버튼
            Button(
                onClick = {
                    viewModel.savePersonalInfo()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C7BA8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "완료",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PersonalInfoFieldStyled(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF9C7BA8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D2D2D)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder,
                    color = Color(0xFFBDBDBD),
                    fontSize = 15.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9C7BA8),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFAF8FC),
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF2D2D2D),
                unfocusedTextColor = Color(0xFF2D2D2D)
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (!isLast) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PersonalInfoScreenPreview() {
    PersonalInfoScreen(
        viewModel = PersonalInfoViewModel(),
        navController = rememberNavController()
    )
}