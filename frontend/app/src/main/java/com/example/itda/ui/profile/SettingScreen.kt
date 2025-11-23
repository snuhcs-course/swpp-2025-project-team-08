package com.example.itda.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
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
    onFontSizeChange: (SettingsViewModel.FontSize) -> Unit,
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
                        fontSize = 18.scaledSp,
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
                SettingFontSizeItem(
                    currentFontSize = ui.fontSize,
                    onFontSizeChange = onFontSizeChange
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
            fontSize = 20.scaledSp,
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
            fontSize = 15.scaledSp,
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
fun SettingFontSizeItem(
    currentFontSize: SettingsViewModel.FontSize,
    onFontSizeChange: (SettingsViewModel.FontSize) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(
            text = "글자 크기",
            fontSize = 15.scaledSp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsViewModel.FontSize.entries.forEach { fontSize ->
                FontSizeOption(
                    fontSize = fontSize,
                    isSelected = currentFontSize == fontSize,
                    onClick = { onFontSizeChange(fontSize) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FontSizeOption(
    fontSize: SettingsViewModel.FontSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = fontSize.displayName,
            fontSize = (14 * fontSize.scale).scaledSp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "선택됨",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
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
            fontSize = 15.scaledSp,
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