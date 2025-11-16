package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.components.BookmarkButton
import com.example.itda.ui.common.components.StatusTag
import com.example.itda.ui.common.components.StatusType
import com.example.itda.ui.common.util.getDDayLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedHeaderSection(
    title: String,
    endDate: String,
    tags: List<String>, // TODO - 지금은 category 이름을 String 으로 하나만 받아오지만 여러개 카테고리로 바뀌면 List 를 잘 활용할 수 있을 것
    isEligible: Boolean,
    isBookmarked: Boolean,
    onBookmarkClicked : () -> Unit,
    isExample : Boolean = false,
) {
    val dayDiff =
        try {
            getDDayLabel(endDate)
        } catch(e: Exception) {
            null
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .widthIn(120.dp, 240.dp),
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if(!isExample) {
                BookmarkButton(
                    isBookmarked = isBookmarked,
                    onClick = onBookmarkClicked
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            if(dayDiff != null) {
                when {
                    dayDiff > 0 -> StatusTag(
                        "마감 D-${dayDiff}",
                        if (dayDiff > 30) StatusType.PRIMARY else StatusType.NEGATIVE
                    )

                    dayDiff < 0 -> StatusTag("마감 완료", StatusType.NEUTRAL)
                    else -> StatusTag("오늘 마감", StatusType.NEGATIVE)
                }
            }
            tags.map { tag ->
                StatusTag(tag, StatusType.PRIMARY)
            }
            if (isEligible) // TODO - 지금은 전부 true. 일단 false 로 바꿔두겠습니다;..
                StatusTag("신청 대상자", StatusType.POSITIVE)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedHeaderSection() {
    // 미리보기를 위한 더미 함수
    FeedHeaderSection(
        title = "title",
        endDate = "",
        tags = listOf(""),
        isEligible= false,
        isBookmarked = false,
        onBookmarkClicked = {}
    )
}