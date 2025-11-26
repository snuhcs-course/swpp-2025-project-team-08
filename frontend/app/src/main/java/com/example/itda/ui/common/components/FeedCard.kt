package com.example.itda.ui.common.components

//import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.dummyCategories
import com.example.itda.ui.common.theme.scaledSp

@Composable
fun FeedCard(
    id: Int,                // 프로그램 고유 ID
    title: String,          // 프로그램 제목
    categories: List<String>,       // 프로그램 카테고리
    department: String,     // 주관 부서
    content: String,        // 프로그램 설명 (text)
    // start_date: String ? = null,     // 시작 날짜 (ISO 포맷 문자열로 전달)
    // end_date: String ? = null,       // 종료 날짜

    isBookmarked : Boolean = false,     // 즐겨찾기 여부
    reason : String? = null, // 추천 이유
    logo: Int = R.drawable.gov_logo,    // 로고 ID?
    // logoUrl : String ? = null
    onClick: () -> Unit,
    onBookmarkClicked : () -> Unit,

    onDismissRequest: () -> Unit, // 관심없음 처리
    isExample : Boolean = false,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick,

    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            if(reason != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = reason,
                        // 폰트 스타일: bodySmall, 얇은 두께
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 상단 섹션: 로고, 기관명, 카테고리, 즐겨찾기 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .widthIn(120.dp, 240.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    // 기관 로고
                    CircleImage(
                        imgId = logo,
                        contentDescription = department + "_logo"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = department, // 정부 기관명
                            fontSize = 14.scaledSp,
                            lineHeight = 16.scaledSp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row {
                            for (category in categories) {
                                Text(
                                    text = category, // 카테고리
                                    fontSize = 12.scaledSp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
                if(!isExample) {
                    Row (
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookmarkButton (
                            isBookmarked = isBookmarked,
                            onClick = {
                                onBookmarkClicked()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 중앙 섹션: 제목 및 신청 대상 여부
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 20.scaledSp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                //if (isEligible) StatusTag("신청 대상자", StatusType.POSITIVE) // 신청 대상자 버튼
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단 섹션: content
            Text(
                text = content,
                fontSize = 14.scaledSp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                categories = dummyCategories.map { it -> it.value },
                department = "행정안전부",
                content = "25만원 받을 수 있음",
                isBookmarked = true, // 즐겨찾기 설정됨
                logo = R.drawable.gov_logo,
                onClick = { },
                onBookmarkClicked = {},
                onDismissRequest = {}
            )
        }
    }
}