package com.example.itda.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    ui: SettingsViewModel.SettingsUiState,
    onBack: () -> Unit,
    toggleDarkMode: () -> Unit,
    toggleAlarm: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Setting",
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
                .background(Color.White)
                .padding(20.dp)
        ) {
            // 계정 설정
            SettingSectionTitleSimple("계정 설정")
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                SettingToggleItemSimple(
                    title = "다크 모드",
                    checked = ui.darkMode,
                    onCheckedChange = { toggleDarkMode() }
                )
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingToggleItemSimple(
                    title = "알림 설정",
                    checked = ui.alarmEnabled,
                    onCheckedChange = { toggleAlarm() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 이용 안내
            SettingSectionTitleSimple("이용 안내")
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                SettingMenuItemSimple("공지사항") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("자주 묻는 질문") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("고객 문의") { }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 이용 정보
            SettingSectionTitleSimple("이용 정보")
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                SettingMenuItemSimple("이용 약관") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("개인정보 처리방침") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("개인정보 수집/이용 동의 (맞춤정책)") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("민감정보 수집/이용 동의 (맞춤정책)") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("위치기반서비스 이용약관 동의") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("마케팅 이용동의") { }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                SettingMenuItemSimple("로그아웃") { onLogout() }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingSectionTitleSimple(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Primary80,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1B1F),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SettingToggleItemSimple(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary95)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(0xFF1C1B1F),
            fontWeight = FontWeight.Normal
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF9E9E9E),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Composable
fun SettingMenuItemSimple(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary95)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(0xFF1C1B1F),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(20.dp)
        )
    }
}