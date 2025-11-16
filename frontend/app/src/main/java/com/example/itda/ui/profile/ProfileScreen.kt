package com.example.itda.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.example.itda.ui.common.theme.*
import androidx.compose.runtime.LaunchedEffect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    ui : ProfileViewModel.ProfileUiState,
    onSettingClick : () -> Unit,
    onPersonalInfoClick : () -> Unit,
    onRefresh: () -> Unit,
    modifier : Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "내 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.scaledSp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "설정",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (ui.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 맞춤 정보 헤더 카드
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "개인에게 알맞은\n정책을 추천해드립니다",
                            fontSize = 16.scaledSp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.scaledSp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = onPersonalInfoClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 20.dp,
                                vertical = 12.dp
                            )
                        ) {
                            Text("수정", fontSize = 16.scaledSp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 정보 카드
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        val user = ui.user

                        ProfileInfoItem("이름", user?.name ?: "")
                        ProfileInfoItem("생년월일", user?.birthDate ?: "")
                        ProfileInfoItem("성별", user?.gender ?: "")
                        ProfileInfoItem("주소", user?.address ?: "")
                        ProfileInfoItem("우편번호", user?.postcode ?: "")
                        ProfileInfoItem("결혼 여부", user?.maritalStatus ?: "")
                        ProfileInfoItem("학력", user?.educationLevel ?: "")
                        ProfileInfoItem("가구원 수", user?.householdSize?.toString() ?: "")
                        ProfileInfoItem("가구원 소득", user?.householdIncome?.let { "${it}만원" } ?: "")
                        ProfileInfoItem("취업 상태", user?.employmentStatus ?: "")
                        ProfileInfoItem(
                            "관심 태그",
                            user?.tags?.joinToString(", ") { "#$it" } ?: "",
                            isLast = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    highlight: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 15.scaledSp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (highlight) {
            Box(
                modifier = Modifier
                    .background(
                        color = YellowSecondary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = value.ifEmpty { "-" },
                    fontSize = 15.scaledSp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        } else {
            Text(
                text = value.ifEmpty { "-" },
                fontSize = 15.scaledSp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        if (!isLast) {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProfileScreenPreview() {
//    MaterialTheme {
//        ProfileScreen(
//            ui = ProfileViewModel.ProfileUiState(
//                user = com.example.itda.data.source.remote.ProfileResponse(
//                    id = "1",
//                    email = "test@example.com",
//                    name = "백일",
//                    birthDate = "1949-01-01",
//                    gender = "남성",
//                    address = "서울 노원구 동일로216길 92",
//                    postcode = "01754",
//                    maritalStatus = "미혼",
//                    educationLevel = "대졸",
//                    householdSize = 4,
//                    householdIncome = 500,
//                    employmentStatus = "재직자",
//                    tags = listOf("태그1", "태그2", "태그3")
//                ),
//                isLoading = false,
//                generalError = null
//            ),
//            onSettingClick = {},
//            onPersonalInfoClick = {},
//            onRefresh = {}
//        )
//    }
//}
