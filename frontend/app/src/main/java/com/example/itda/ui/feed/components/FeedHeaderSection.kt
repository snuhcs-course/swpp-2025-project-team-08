package com.example.itda.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.components.BookmarkButton
import com.example.itda.ui.common.components.StatusTag
import com.example.itda.ui.common.components.StatusType
import com.example.itda.ui.common.util.getDDayLabel

enum class isLiked {
    NeitherClicked,
    LikeClicked,
    DisLikeClicked
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedHeaderSection(
    title: String,
    endDate: String,
    tags: List<String>,
    isBookmarked: Boolean,
    onBookmarkClicked : () -> Unit,
    toggleLike: () -> Unit = {},
    isLiked: Boolean = false,
    toggleDisLike: () -> Unit = {},
    isDisliked: Boolean = false,

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
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlowRow(
                modifier = Modifier.weight(7f),
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
            }
            if(!isExample) {
                LikeButtonRow(
                    toggleLike = toggleLike,
                    isLiked = isLiked,
                    toggleDisLike = toggleDisLike,
                    isDisliked = isDisliked,
                )
            }
        }
    }
}

@Composable
fun LikeButtonRow(
    toggleLike: () -> Unit = {},
    isLiked: Boolean = true,
    toggleDisLike: () -> Unit = {},
    isDisliked: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좋아요 버튼
        IconButton(onClick = toggleLike) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        // 싫어요 버튼
        IconButton(onClick = toggleDisLike) {
            Icon(
                imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                contentDescription = if (isDisliked) "싫어요 취소" else "싫어요",
                tint = if (isDisliked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedHeaderSection() {
    FeedHeaderSection(
        title = "title",
        endDate = "",
        tags = listOf(""),
        isBookmarked = false,
        onBookmarkClicked = {}
    )
}