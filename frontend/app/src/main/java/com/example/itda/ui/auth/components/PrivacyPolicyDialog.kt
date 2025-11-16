package com.example.itda.ui.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.itda.ui.common.theme.*

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    onAgree: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "개인정보 수집 및 이용 동의",
                    fontSize = 20.scaledSp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = buildPrivacyPolicyText(),
                        fontSize = 14.scaledSp,
                        lineHeight = 24.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "취소",
                            fontSize = 16.scaledSp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            onAgree()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "동의하기",
                            fontSize = 16.scaledSp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun buildPrivacyPolicyText(): String {
    return """
        '잇다'(이하 '회사')는 개인정보 보호법에 따라 이용자의 개인정보를 보호하고 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 다음과 같이 개인정보 처리방침을 수립·공개합니다.
        
        
        1. 수집하는 개인정보 항목
        
        회사는 회원가입 및 서비스 제공을 위해 다음과 같은 개인정보를 수집합니다.
        
        • 필수 항목: 이메일, 비밀번호, 이름, 생년월일, 성별, 주소, 우편번호
        
        
        2. 개인정보의 수집 및 이용 목적
        
        회사는 수집한 개인정보를 다음의 목적을 위해 활용합니다.
        
        • 회원 가입 및 관리
        • 노인 복지 프로그램 검색 및 맞춤 추천 서비스 제공
        • 서비스 이용에 따른 본인 확인 및 인증
        • 서비스 개선 및 통계 분석
        • 공지사항 전달 및 고객 문의 응대
        
        
        3. 개인정보의 보유 및 이용 기간
        
        회사는 이용자의 개인정보를 다음과 같이 보유 및 이용합니다.
        
        • 회원 탈퇴 시까지 보유 및 이용
        • 관계 법령에 따라 보존할 필요가 있는 경우 해당 법령에서 정한 기간 동안 보관
        
        
        4. 개인정보의 제3자 제공
        
        회사는 원칙적으로 이용자의 개인정보를 외부에 제공하지 않습니다. 다만, 다음의 경우에는 예외로 합니다.
        
        • 이용자가 사전에 동의한 경우
        • 법령의 규정에 의거하거나, 수사 목적으로 법령에 정해진 절차와 방법에 따라 수사기관의 요구가 있는 경우
        
        
        5. 이용자의 권리와 의무
        
        이용자는 언제든지 자신의 개인정보를 조회하거나 수정할 수 있으며, 회원 탈퇴를 통해 개인정보의 삭제를 요청할 수 있습니다.
        
        
        6. 동의 거부 권리 및 불이익
        
        이용자는 개인정보 수집 및 이용에 대한 동의를 거부할 권리가 있습니다. 다만, 필수 항목에 대한 동의를 거부하실 경우 회원가입 및 서비스 이용이 제한될 수 있습니다.
        
        
        위 내용을 확인하였으며, 개인정보 수집 및 이용에 동의하십니까?
    """.trimIndent()
}