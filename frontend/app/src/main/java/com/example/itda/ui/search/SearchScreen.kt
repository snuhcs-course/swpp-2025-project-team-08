package com.example.itda.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.ScreenContract

object SearchContract : ScreenContract {
    override val route = "search"
    override val title = "search"
}

@Composable
fun SearchScreen() {
    BaseScreen(SearchContract) {
        Text("검색 화면")
    }
}