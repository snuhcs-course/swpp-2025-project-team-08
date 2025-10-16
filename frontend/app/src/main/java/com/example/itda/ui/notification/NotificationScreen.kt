package com.example.itda.ui.notification

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.ScreenContract

object NotificationContract : ScreenContract {
    override val route = "notification"
    override val title = "notification"
}

@Composable
fun NotificationScreen() {
    BaseScreen("notification") {
        Text("알림 화면")
    }
}