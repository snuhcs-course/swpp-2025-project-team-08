package com.example.itda.ui.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itda.R
import com.example.itda.data.model.ProgramResponse
import com.example.itda.ui.navigation.LoadingScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedList(
    items: List<ProgramResponse>,
    bookmarkPrograms : List<Int>,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (ProgramResponse) -> Unit,
    onItemBookmarkClicked : (Int) -> Unit,
    isPaginating : Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        state = listState
    ) {
        items(items, key = {it.id}) { item ->
            Row(
                modifier = Modifier.animateItem()
            ) {
                FeedCard(
                    id = item.id,
                    title = item.title,
                    categories = listOf(item.categoryValue),
                    department = item.operatingEntity,
                    content = item.preview,
                    isBookmarked = item.id in bookmarkPrograms,
                    reason = item.reason,
                    logo =
                        if (item.operatingEntityType == "central")
                            R.drawable.gov_logo
                        else
                            R.drawable.local,
                    onClick = { onItemClick(item) },
                    onBookmarkClicked = { onItemBookmarkClicked(item.id) },

                    )
            }
        }
        if(isPaginating) {
            item {
                LoadingScreen(
                    text = ""
                )
            }
        }
    }

}
