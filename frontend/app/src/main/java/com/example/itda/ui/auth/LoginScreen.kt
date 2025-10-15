package com.example.itda.ui.auth

import android.R.attr.data
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Neutral0
import com.example.itda.ui.common.theme.Neutral10
import com.example.itda.ui.common.theme.Neutral100
import com.example.itda.ui.common.theme.Neutral40
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.Neutral90
import com.example.itda.ui.common.theme.Primary60

//@Preview(showBackground = true)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral100),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "잇다",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login 타이틀
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Neutral100
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                border = BorderStroke(1.dp, Neutral90)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    InputField(
                        label = "이메일",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "이메일을 입력해주세요."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = "비밀번호",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "비밀번호를 입력해주세요.",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 로그인 버튼
                    val isFormValid = email.isNotEmpty() && password.isNotEmpty()

                    Button(
                        onClick = {
                            /* TODO: 로그인 API 연결 */
                            Log.d("login", "LoginButtonClicked on LoginScreen: $data")
                            authViewModel.login(
                                email = email,
                                password = password
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid)
                                Primary60  // 활성화 시 메인 컬러
                            else
                                Neutral90,
                            disabledContainerColor = Neutral80
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isFormValid
                    ) {
                        Text(
                            "로그인",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isFormValid) Neutral100 else Neutral40
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 회원가입 링크
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "계정이 없으신가요? ",
                            fontSize = 14.sp,
                            color = Neutral40
                        )
                        Text(
                            text = "회원가입하기",
                            fontSize = 14.sp,
                            color = Neutral10,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onSignUpClick()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 비밀번호 찾기
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "비밀번호 찾기",
                            fontSize = 14.sp,
                            color = Neutral40,
                            modifier = Modifier
                                .clickable { }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}