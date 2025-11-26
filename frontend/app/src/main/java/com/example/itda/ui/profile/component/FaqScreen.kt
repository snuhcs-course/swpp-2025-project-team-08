package com.example.itda.ui.profile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.scaledSp

data class FAQItem(
    val question: String,
    val answer: String,
    val category: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faqItems = remember {
        listOf(
            FAQItem(
                question = "잇다는 어떤 서비스인가요?",
                answer = "잇다는 정부와 지자체에서 제공하는 다양한 정책 정보를 한 곳에서 확인하고, 나에게 맞는 정책을 추천받을 수 있는 서비스입니다. 복잡한 정책 정보를 쉽게 찾고 신청할 수 있도록 도와드립니다.",
                category = "서비스 소개"
            ),
            FAQItem(
                question = "회원가입은 어떻게 하나요?",
                answer = "앱을 실행한 후 '시작하기' 버튼을 누르고, 이메일 주소와 비밀번호를 입력하시면 됩니다. 이메일 인증을 완료하신 후 기본 정보를 입력하시면 회원가입이 완료됩니다.",
                category = "계정"
            ),
            FAQItem(
                question = "비밀번호를 잊어버렸어요.",
                answer = "로그인 화면에서 '비밀번호 찾기'를 선택하신 후, 가입하신 이메일 주소를 입력하시면 비밀번호 재설정 링크를 보내드립니다. 이메일을 확인하시고 새로운 비밀번호를 설정해주세요.",
                category = "계정"
            ),
            FAQItem(
                question = "정책 추천은 어떻게 받나요?",
                answer = "프로필 설정에서 나이, 거주지역, 가구정보, 소득수준 등을 입력하시면 자동으로 맞춤형 정책을 추천해드립니다. 더 자세한 정보를 입력할수록 더 정확한 추천을 받으실 수 있습니다.",
                category = "정책 추천"
            ),
            FAQItem(
                question = "내 정보는 안전한가요?",
                answer = "잇다는 개인정보 보호법에 따라 모든 개인정보를 암호화하여 안전하게 보관하고 있습니다. 회원님의 정보는 정책 추천 목적으로만 사용되며, 동의 없이 제3자에게 제공되지 않습니다.",
                category = "개인정보"
            ),
            FAQItem(
                question = "정책 신청은 어떻게 하나요?",
                answer = "관심있는 정책을 선택하시면 상세 정보와 함께 '신청하기' 버튼이 표시됩니다. 버튼을 누르시면 해당 정책의 공식 신청 페이지로 이동하며, 그곳에서 직접 신청하실 수 있습니다.",
                category = "정책 신청"
            ),
            FAQItem(
                question = "관심 정책을 저장할 수 있나요?",
                answer = "네, 가능합니다. 각 정책 카드의 북마크 아이콘을 누르시면 '내 정책'에 저장되어 나중에 다시 확인하실 수 있습니다. 북마크한 정책은 마이페이지에서 모아서 보실 수 있습니다.",
                category = "기능"
            ),
            FAQItem(
                question = "정책 정보는 얼마나 자주 업데이트되나요?",
                answer = "정부 및 지자체의 공식 정책 발표를 실시간으로 모니터링하여 매일 업데이트하고 있습니다. 새로운 정책이 등록되거나 기존 정책이 변경되면 즉시 반영됩니다.",
                category = "정책 정보"
            ),
            FAQItem(
                question = "지역 설정은 어떻게 변경하나요?",
                answer = "마이페이지 > 프로필 수정에서 거주지역을 변경하실 수 있습니다. 지역을 변경하시면 해당 지역의 정책 정보가 자동으로 업데이트됩니다.",
                category = "설정"
            ),
            FAQItem(
                question = "알림 설정을 변경하고 싶어요.",
                answer = "설정 > 알림 설정에서 새로운 정책 알림, 신청 마감 알림 등을 원하는 대로 설정하실 수 있습니다. 알림을 받고 싶지 않으시면 모두 끄실 수도 있습니다.",
                category = "설정"
            ),
            FAQItem(
                question = "회원 탈퇴는 어떻게 하나요?",
                answer = "설정 > 계정 관리 > 회원 탈퇴에서 진행하실 수 있습니다. 탈퇴 시 모든 개인정보와 저장된 데이터가 삭제되며, 삭제된 정보는 복구할 수 없으니 신중히 결정해주세요.",
                category = "계정"
            ),
            FAQItem(
                question = "서비스 이용 중 문제가 발생했어요.",
                answer = "고객센터(설정 > 고객 문의)를 통해 문의해주시면 빠르게 도움을 드리겠습니다. 문제 상황을 자세히 설명해주시면 더 신속하게 해결해드릴 수 있습니다.",
                category = "기타"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "자주 묻는 질문",
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            faqItems.forEachIndexed { index, item ->
                ExpandableFAQItem(
                    faqItem = item,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < faqItems.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExpandableFAQItem(
    faqItem: FAQItem,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Q",
                    fontSize = 18.scaledSp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = faqItem.question,
                    fontSize = 15.scaledSp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "접기" else "펼치기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "A",
                        fontSize = 18.scaledSp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = faqItem.answer,
                        fontSize = 14.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}