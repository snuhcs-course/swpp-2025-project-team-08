package com.example.itda.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import com.example.itda.ui.common.theme.ItdaTheme
import com.example.itda.ui.profile.SettingsViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 👇 SettingsViewModel에서 다크모드 설정 가져오기
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settingsUi.collectAsState()

            // 👇 ItdaTheme 사용 (다크모드 설정 반영)
            ItdaTheme(darkTheme = settings.darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}