package com.example.itda.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.GreenPrimary
import com.example.itda.ui.common.theme.GreenSecondary
import com.example.itda.ui.common.theme.Neutral40
import com.example.itda.ui.common.theme.Neutral90
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.common.theme.Primary90
import com.example.itda.ui.common.theme.RedPrimary
import com.example.itda.ui.common.theme.scaledSp

enum class StatusType {
    PRIMARY, // 파랑
    NEUTRAL,   // 회색
    POSITIVE,  // 초록
    NEGATIVE   // 빨강
}

@Composable
fun StatusTag(
    text: String,
    status: StatusType
) {
    val primaryColor = when (status) {
        StatusType.PRIMARY -> Primary40
        StatusType.POSITIVE -> GreenPrimary
        StatusType.NEUTRAL -> Neutral40
        StatusType.NEGATIVE -> RedPrimary
    }
    val secondaryColor = when (status) {
        StatusType.PRIMARY -> Primary90
        StatusType.POSITIVE -> GreenSecondary
        StatusType.NEUTRAL -> Neutral90
        StatusType.NEGATIVE -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .height(30.dp)
            .background(
                color = secondaryColor,
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, primaryColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == StatusType.POSITIVE) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "check",
                tint = primaryColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            fontSize = 12.scaledSp,
            fontWeight = FontWeight.SemiBold,
            color = primaryColor
        )
    }
}