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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.components.StarButton
import com.example.itda.ui.common.components.StatusTag
import com.example.itda.ui.common.components.StatusType
import com.example.itda.ui.common.util.getDDayLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedHeaderSection(
    title : String,
    endDate : String,
    tags : List<String>, // TODO - 지금은 category 이름을 String 으로 하나만 받아오지만 여러개 카테고리로 바뀌면 List 를 잘 활용할 수 있을 것
    isEligible : Boolean,
    isStarred : Boolean
) {
    val dayDiff = getDDayLabel(endDate)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            StarButton(isStarred = isStarred)
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when {
                dayDiff > 0 -> StatusTag(
                    "마감 D-${dayDiff}",
                    if(dayDiff > 30) StatusType.NEUTRAL else StatusType.NEGATIVE
                )
                dayDiff < 0 -> StatusTag("마감 완료", StatusType.NEUTRAL)
                else -> StatusTag("오늘 마감", StatusType.NEGATIVE)
            }
            Spacer(Modifier.width(6.dp))
            tags.map {
                tag -> StatusTag(tag, StatusType.NEUTRAL)
            }
            Spacer(Modifier.width(6.dp))
            if(isEligible)
                StatusTag("신청 대상자", StatusType.POSITIVE)
        }
    }
}