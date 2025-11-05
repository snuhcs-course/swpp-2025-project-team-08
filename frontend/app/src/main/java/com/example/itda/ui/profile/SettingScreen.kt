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
import androidx.compose.material3.MaterialTheme
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
import com.example.itda.ui.profile.component.SettingDestination
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.theme.Neutral30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    ui: SettingsViewModel.SettingsUiState,
    onBack: () -> Unit,
    onNavigateToDestination: (SettingDestination) -> Unit,
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
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
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
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
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
                SettingMenuItemSimple("공지사항") {
                    onNavigateToDestination(SettingDestination.Notice)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("자주 묻는 질문") {
                    onNavigateToDestination(SettingDestination.FAQ)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("고객 문의") {
                    onNavigateToDestination(SettingDestination.CustomerSupport)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 이용 정보
            SettingSectionTitleSimple("이용 정보")
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                SettingMenuItemSimple("이용 약관") {
                    onNavigateToDestination(SettingDestination.Terms)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("개인정보 처리방침") {
                    onNavigateToDestination(SettingDestination.Privacy)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("개인정보 수집/이용 동의 (맞춤정책)") {
                    onNavigateToDestination(SettingDestination.PersonalInfo)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("민감정보 수집/이용 동의 (맞춤정책)") {
                    onNavigateToDestination(SettingDestination.SensitiveInfo)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("위치기반서비스 이용약관 동의") {
                    onNavigateToDestination(SettingDestination.Location)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                SettingMenuItemSimple("마케팅 이용동의") {
                    onNavigateToDestination(SettingDestination.Marketing)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
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
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
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
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Normal
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun SettingMenuItemSimple(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}