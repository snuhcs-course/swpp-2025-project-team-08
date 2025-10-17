package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.Neutral80
import com.example.itda.ui.common.theme.YellowPrimary

@Composable
fun StarButton(
    isStarred : Boolean
) {
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "즐겨찾기",
        tint = if (isStarred) YellowPrimary else Neutral80,
        modifier = Modifier.size(24.dp)
    )
}