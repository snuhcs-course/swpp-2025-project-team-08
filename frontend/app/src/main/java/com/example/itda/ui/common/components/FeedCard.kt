package com.example.itda.ui.common.components

//import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.R
import com.example.itda.data.model.Category
import com.example.itda.data.model.DummyData
import com.example.itda.ui.common.theme.Neutral20
import com.example.itda.ui.common.theme.Neutral60


@Composable
fun FeedCard(
    id: Int,                // 프로그램 고유 ID
    title: String,          // 프로그램 제목
    categories: List<Category>,       // 프로그램 카테고리
    department: String,     // 주관 부서
    link: String? = null,           // 관련 링크 (URL)
    content: String,        // 프로그램 설명 (text)
    // start_date: String ? = null,     // 시작 날짜 (ISO 포맷 문자열로 전달)
    // end_date: String ? = null,       // 종료 날짜

    isStarred: Boolean,     // 즐겨찾기 여부
    logo: Int = R.drawable.gov_logo,    // 로고 ID?
    // TODO - coil library 를 활용해 url 을 받아와 붙여넣는 방법도 고민중
    // logoUrl : String ? = null
    isEligible: Boolean,    // 신청 대상자 여부
    onClick: () -> Unit,
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
                    CircleImage(
                        imgId = logo,
                        contentDescription = department + "_logo"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = department, // 정부 기관명
                            fontSize = 14.sp,
                            color = Neutral20
                        )
                        Row { //TODO - category 이렇게 담지말고 status tag 등으로  담는 방식
                            for (category in categories) {
                                Text(
                                    text = category.name, // 카테고리
                                    fontSize = 12.sp,
                                    color = Neutral60
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
                StarButton(isStarred)
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
                if (isEligible) StatusTag("신청 대상자", StatusType.POSITIVE) // 신청 대상자 버튼
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
            FeedCard(
                id = 1,
                title = "민생회복 소비쿠폰",
                categories = DummyData.dummyCategories,
                department = "행정안전부",
                content = "25만원 받을 수 있음",
                isStarred = true, // 즐겨찾기 설정됨
                isEligible = true, // 신청 대상자 O
                logo = R.drawable.gov_logo,
                onClick = { }
            )
        }
    }
}