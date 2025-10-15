package com.example.itda.ui.common.components

//import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.R
import com.example.itda.ui.common.theme.GreenPrimary
import com.example.itda.ui.common.theme.GreenSecondary
import com.example.itda.ui.common.theme.Neutral20
import com.example.itda.ui.common.theme.Neutral60
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.YellowPrimary



@Composable
fun FeedItem(
    id: Int,                // 프로그램 고유 ID
    title: String,          // 프로그램 제목
    category: String,       // 프로그램 카테고리
    department: String,     // 주관 부서
    link: String ? = null,           // 관련 링크 (URL)
    content: String,        // 프로그램 설명 (text)
    // start_date: String ? = null,     // 시작 날짜 (ISO 포맷 문자열로 전달)
    // end_date: String ? = null,       // 종료 날짜

    isStarred : Boolean,     // 즐겨찾기 여부
    logo : Int = R.drawable.gov_logo,    // 로고 ID?
    // TODO - coil library 를 활용해 url 을 받아와 붙여넣는 방법도 고민중
    // logoUrl : String ? = null
    isEligible: Boolean,    // 신청 대상자 여부
    onClick : () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단 섹션: 로고, 기관명, 카테고리, 즐겨찾기 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 기관 로고
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        //TODO - logo image ID? 어떤 타입으로 넣어둘건지?
                        // Image(painter = painterResource(id = logo), contentDescription = department)
                        Image(
                            painter = painterResource(
                                id = logo
                            ),
                            contentDescription = department + "_logo",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = department, // 정부 기관명
                            fontSize = 14.sp,
                            color = Neutral20
                        )
                        Text(
                            text = category, // 카테고리
                            fontSize = 12.sp,
                            color = Neutral60
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "즐겨찾기",
                    tint = if (isStarred) YellowPrimary else Neutral80,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 중앙 섹션: 쿠폰 제목 및 신청 대상 여부
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                if (isEligible) {
                    // 신청 대상자 버튼
                    Row(
                        modifier = Modifier
                            .height(30.dp)
                            .background(
                                color = GreenSecondary,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "check",
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "신청 대상자",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단 섹션: content
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// 미리보기
@Preview(showBackground = true)
@Composable
fun PreviewFeedItem() {
    MaterialTheme {
        Column {
            FeedItem(
                id = 1,
                title = "민생회복 소비쿠폰",
                category = "소비지원",
                department = "행정안전부",
                content = "25만원 받을 수 있음",
                isStarred = true, // 즐겨찾기 설정됨
                isEligible = true, // 신청 대상자 O
                logo = R.drawable.gov_logo,
                onClick = {  }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 신청 대상자가 아닌 경우
            FeedItem(
                id = 2,
                title = "청년 도약 계좌",
                category = "자산형성",
                department = "금융위원회",
                content = "최대 5천만원 목돈 마련 기회",
                isStarred = false, // 즐겨찾기 설정 안 됨
                isEligible = false, // 신청 대상자 X
                logo = R.drawable.hissf_logo,
                onClick = {  }
            )
        }
    }
}