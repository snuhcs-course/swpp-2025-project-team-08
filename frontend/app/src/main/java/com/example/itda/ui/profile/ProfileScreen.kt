package com.example.itda.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.ScreenContract
import com.example.itda.ui.common.theme.Neutral20
import com.example.itda.ui.common.theme.Neutral90

object ProfileContract : ScreenContract {
    override val route = "profile"
    override val title = "Profile"
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel
) {
    BaseScreen("Profile") { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text("프로필 화면", style = MaterialTheme.typography.headlineMedium)


            Button(
                onClick = {
                    /* TODO: 로그아웃 API 연결 */
                    authViewModel.logout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Neutral90,
                    )
                ) {
                Text(
                    "로그아웃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Neutral20
                )
            }
        }
    }
}