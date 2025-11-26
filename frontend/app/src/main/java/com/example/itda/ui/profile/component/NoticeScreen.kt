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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
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

data class NoticeItem(
    val title: String,
    val content: String,
    val date: String,
    val isImportant: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notices = remember {
        listOf(
            NoticeItem(
                title = "[중요] 2025년 새해 정책 업데이트 안내",
                content = """안녕하세요, 잇다입니다.

2025년 새해를 맞이하여 다양한 신규 정책들이 업데이트되었습니다.

주요 업데이트 내용:
• 청년 정책 50여 개 신규 추가
• 지역별 복지 정책 확대
• AI 기반 추천 알고리즘 개선
• 정책 신청 바로가기 기능 강화

앱을 최신 버전으로 업데이트하시면 더 나은 서비스를 이용하실 수 있습니다.

감사합니다.""",
                date = "2025.01.02",
                isImportant = true
            ),
            NoticeItem(
                title = "설 연휴 고객센터 운영 안내",
                content = """설 연휴 기간 동안 고객센터 운영 일정을 안내드립니다.

• 휴무 기간: 2025년 1월 28일 ~ 2월 1일
• 정상 운영: 2025년 2월 2일부터

휴무 기간 중에는 이메일 문의만 가능하며, 순차적으로 답변 드리겠습니다.

즐거운 명절 보내시기 바랍니다.""",
                date = "2025.01.20",
                isImportant = false
            ),
            NoticeItem(
                title = "개인정보 처리방침 개정 안내",
                content = """개인정보 처리방침이 다음과 같이 개정되었음을 알려드립니다.

• 시행일: 2025년 1월 15일
• 주요 변경사항:
  - 개인정보 보유기간 명시
  - 제3자 제공 내역 상세화
  - 이용자 권리 보호 조항 강화

자세한 내용은 설정 > 개인정보 처리방침에서 확인하실 수 있습니다.""",
                date = "2025.01.10",
                isImportant = false
            ),
            NoticeItem(
                title = "앱 버전 2.0 업데이트 완료",
                content = """잇다 앱 버전 2.0 업데이트가 완료되었습니다.

새로운 기능:
• 정책 비교 기능 추가
• 마이페이지 UI/UX 개선
• 알림 설정 세분화
• 정책 카테고리 재분류
• 검색 기능 고도화

업데이트 후 더욱 편리하게 이용하실 수 있습니다.
앱스토어 또는 플레이스토어에서 업데이트해주세요.""",
                date = "2024.12.28",
                isImportant = false
            ),
            NoticeItem(
                title = "[안내] 서비스 점검 완료",
                content = """12월 20일 새벽에 진행되었던 서비스 점검이 완료되었습니다.

점검 내용:
• 서버 안정화 작업
• 데이터베이스 최적화
• 보안 업데이트

점검 중 불편을 드려 죄송합니다.
앞으로 더 나은 서비스로 보답하겠습니다.""",
                date = "2024.12.20",
                isImportant = false
            ),
            NoticeItem(
                title = "지역별 맞춤 정책 추천 기능 출시",
                content = """사용자의 거주 지역을 기반으로 한 맞춤형 정책 추천 기능이 새롭게 추가되었습니다.

주요 기능:
• 시/도 및 시/군/구별 정책 필터링
• 지역 특화 정책 우선 표시
• 지자체 정책 알림 설정

프로필에서 거주지를 설정하시면 더 정확한 정책을 추천받으실 수 있습니다.""",
                date = "2024.12.15",
                isImportant = false
            ),
            NoticeItem(
                title = "회원 가입 이벤트 당첨자 발표",
                content = """2024년 12월 신규 회원 가입 이벤트 당첨자를 발표합니다.

당첨자 확인:
• 1등 (스타벅스 5만원): 3명
• 2등 (편의점 상품권 2만원): 10명
• 3등 (커피 쿠폰): 50명

당첨자분들께는 개별 연락을 드렸으며, 2주 내로 경품이 발송됩니다.

참여해주신 모든 분들께 감사드립니다.""",
                date = "2024.12.05",
                isImportant = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "공지사항",
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
            notices.forEachIndexed { index, notice ->
                ExpandableNoticeItem(
                    noticeItem = notice,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < notices.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExpandableNoticeItem(
    noticeItem: NoticeItem,
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                if (noticeItem.isImportant) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "중요 공지",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = noticeItem.title,
                        fontSize = 15.scaledSp,
                        fontWeight = if (noticeItem.isImportant) FontWeight.Bold else FontWeight.Medium,
                        color = if (noticeItem.isImportant)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = noticeItem.date,
                        fontSize = 12.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "접기" else "펼치기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
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

                Text(
                    text = noticeItem.content,
                    fontSize = 14.scaledSp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}