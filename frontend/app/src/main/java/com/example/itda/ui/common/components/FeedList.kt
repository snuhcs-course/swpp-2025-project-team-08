package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.FeedItem

@Composable
fun FeedList(
    // 표시할 FeedItem 데이터의 리스트를 인자로 받습니다.
    items: List<FeedItem>,
    filterCategory: String,
    onItemClick: (FeedItem) -> Unit
) {


    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            // TODO - item id 를 보고 user 와의 관계에 대한 정보 가공 in program repository?
            //  ex : isStared, isEligible..
            //  homeViewmodel.getFeedInfo() -> programRepository.starred
            FeedCard(
                id = item.id,
                title = item.title,
                category = item.category,
                department = item.department,
                content = item.content,

                isStarred = true, // TODO - 변수로 지정 필요
                isEligible = true, // TODO - 변수로 지정 필요
                logo = R.drawable.gov_logo, // TODO - 변수로 지정 필요
                onClick = { onItemClick(item) }
            )
//            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}