package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.auth.components.InputField
import com.example.itda.ui.common.theme.*

@Composable
fun SignUpScreen(
    ui: AuthViewModel.SignUpUiState,
    onLoginClick: () -> Unit,
    onSignUpEmailChange: (String) -> Unit,
    onSignUpPasswordChange: (String) -> Unit,
    onSignUpConfirmChange: (String) -> Unit,
    onAgreeTermsChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "잇다",
                fontSize = 60.scaledSp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Login 타이틀
            Text(
                text = "Sign Up",
                fontSize = 24.scaledSp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
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
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    InputField(
                        label = "이메일",
                        value = ui.email,
                        onValueChange = onSignUpEmailChange,
                        placeholder = "이메일을 입력해주세요.",
                        errorMessage = ui.emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = "비밀번호",
                        value = ui.password,
                        onValueChange = onSignUpPasswordChange,
                        placeholder = "비밀번호를 입력해주세요.",
                        isPassword = true,
                        errorMessage = ui.passwordError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = "비밀번호 확인",
                        value = ui.confirmPassword,
                        onValueChange = onSignUpConfirmChange,
                        placeholder = "비밀번호를 다시 입력해주세요.",
                        isPassword = true,
                        errorMessage = ui.confirmPasswordError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 약관 동의 (껏데기)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = ui.agreeTerms,
                            onCheckedChange = onAgreeTermsChange,
                        )
                        Column {
                            Text(
                                text = "개인정보 취급 동의",
                                fontSize = 12.scaledSp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "자세한 설명",
                                fontSize = 10.scaledSp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (ui.generalError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ui.generalError,
                            fontSize = 12.scaledSp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val isFormValid =
                        ui.email.isNotEmpty() && ui.password.isNotEmpty() && ui.confirmPassword.isNotEmpty() && ui.agreeTerms

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isFormValid
                    ) {
                        Text(
                            "회원가입",
                            fontSize = 16.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = if (isFormValid)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "이미 계정이 있으신가요? ",
                            fontSize = 14.scaledSp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "로그인하기",
                            fontSize = 14.scaledSp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onLoginClick()
                            }
                        )
                    }
                }
            }
        }
    }

}