package com.example.itda.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.ScreenContract

object ProfileContract : ScreenContract {
    override val route = "profile"
    override val title = "Profile"
}

@Composable
fun ProfileScreen() {
    BaseScreen(ProfileContract) {
        Text("프로필 화면")
    }
}