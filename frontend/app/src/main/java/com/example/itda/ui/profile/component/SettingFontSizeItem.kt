package com.example.itda.ui.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.profile.component.FontSizeOption
import com.example.itda.ui.profile.SettingsViewModel

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
