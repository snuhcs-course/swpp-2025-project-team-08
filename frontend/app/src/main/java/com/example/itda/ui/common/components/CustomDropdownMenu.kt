package com.example.itda.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CustomDropdownMenu(
    expanded : Boolean,
    onDropdownDismissRequest : () -> Unit,
    onDismissClicked : () -> Unit,
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDropdownDismissRequest,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clip(RoundedCornerShape(8.dp))
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "관심없음",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
           },
            onClick = {
                onDropdownDismissRequest()
                onDismissClicked() // '관심없음' 액션 요청
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = "공유하기",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )  },
            onClick = {
                onDropdownDismissRequest()
                // TODO - 공유하기
            }
        )
        // TODO - 다른 메뉴 아이템 추가
    }
}