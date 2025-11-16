package com.example.itda.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.ui.common.theme.YellowPrimary

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick : () -> Unit
) {
    Icon(
        imageVector = if(isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
        contentDescription = "즐겨찾기",
        tint = if (isBookmarked) YellowPrimary else MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .size(24.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = null
            )
    )
}