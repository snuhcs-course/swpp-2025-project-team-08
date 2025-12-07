package com.example.itda.ui.feed.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
) {
    val fasterSpring = remember { spring<Float>(dampingRatio = 0.5f, stiffness = 1500f) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좋아요 버튼
        LikeDislikeButton(
            isClicked = isLiked,
            onClick = toggleLike,
            iconFilled = Icons.Filled.ThumbUp,
            iconOutlined = Icons.Outlined.ThumbUp,
            tintColor = MaterialTheme.colorScheme.primary,
            contentDescriptionOn = "좋아요 취소",
            contentDescriptionOff = "좋아요",
            springSpec = fasterSpring
        )

        // 싫어요 버튼
        LikeDislikeButton(
            isClicked = isDisliked,
            onClick = toggleDisLike,
            iconFilled = Icons.Filled.ThumbDown,
            iconOutlined = Icons.Outlined.ThumbDown,
            tintColor = MaterialTheme.colorScheme.error,
            contentDescriptionOn = "싫어요 취소",
            contentDescriptionOff = "싫어요",
            springSpec = fasterSpring
        )
    }
}

@Composable
fun LikeDislikeButton(
    isClicked: Boolean,
    onClick: () -> Unit,
    iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    iconOutlined: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: androidx.compose.ui.graphics.Color,
    contentDescriptionOn: String,
    contentDescriptionOff: String,
    springSpec: androidx.compose.animation.core.SpringSpec<Float>
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isClicked) {
        if (isClicked) {
            scale.animateTo(targetValue = 1.3f, animationSpec = springSpec)
            scale.animateTo(targetValue = 1f, animationSpec = springSpec)
        } else {
            scale.animateTo(1f) // 취소 시 원래 크기로 복구
        }
    }

    Icon(
        imageVector = if (isClicked) iconFilled else iconOutlined,
        contentDescription = if (isClicked) contentDescriptionOn else contentDescriptionOff,
        tint = if (isClicked) tintColor else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .size(32.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            )
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(4.dp)
    )
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