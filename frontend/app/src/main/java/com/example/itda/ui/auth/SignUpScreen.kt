package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.auth.InputField

@Preview(showBackground = true)
@Composable
fun SignUpScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }

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
                text = "Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral0,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                border = BorderStroke(1.dp,Neutral90)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = "비밀번호 확인",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "비밀번호를 다시 입력해주세요.",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 약관 동의 (껍데기)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreeTerms,
                            onCheckedChange = { agreeTerms = it }
                        )
                        Column {
                            Text(
                                text = "개인정보 취급 동의",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "자세한 설명",
                                fontSize = 10.sp,
                                color = Neutral40
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val isFormValid = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && agreeTerms && password == confirmPassword

                    Button(
                        onClick = { /* TODO: 회원가입 API 연결 */ },
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
                            "회원가입",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isFormValid) Neutral100 else Neutral40
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "이미 계정이 있으신가요? ",
                            fontSize = 14.sp,
                            color = Neutral40
                        )
                        Text(
                            text = "로그인하기",
                            fontSize = 14.sp,
                            color = Neutral10,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    }

}