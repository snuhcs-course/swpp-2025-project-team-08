package com.example.itda.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.itda.ui.common.components.BaseScreen
import com.example.itda.ui.common.components.FeedList
import com.example.itda.ui.common.components.ScreenContract
import com.example.itda.ui.home.components.HomeHeader
import com.example.itda.ui.home.components.ProgramFilterRow

object HomeContract : ScreenContract {
    override val route = "home"
    override val title = "home"
}

@Composable
fun HomeScreen(
    ui: HomeViewModel.HomeUiState, // UiState를 인자로 받음
    onFeedClick: (Int) -> Unit,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
//    val homeViewModel : HomeViewModel = hiltViewModel()

    // TODO
    //  이런식으로 homeviewmodel 에서 program 을 가져오고
    //  이걸 가공해서 feedList 에 FeedItem Type 으로 넣어줘야할듯.
    //  Feedlist 인자 타입도 FeedItem으로 바꾸고.
    //  근데 Program에 logoURl, user와의 star, eligible 관계 모두 들어있거나 불러올 수 있으면 노상관

    val filteredFeedPrograms = if (ui.selectedCategoryName == "전체" || ui.selectedCategoryName.isEmpty()) {
        ui.feedItems
    } else {
        ui.feedItems.filter { item ->
            item.categories.any { category ->
                category.name == ui.selectedCategoryName
            } }
    }


    BaseScreen(
        title = "home",
        topBarVisible = false,
    ) { paddingValues ->
        Column(modifier = modifier) {
            HomeHeader(
                username = ui.user.name ?: "사용자",
                programCount = filteredFeedPrograms.size
            )
            ProgramFilterRow(
                categories = ui.categories.map { category -> category.name },
                selectedCategory = ui.selectedCategoryName,
                onCategorySelected = onCategorySelected
            )
            FeedList(
                items = filteredFeedPrograms, // TODO - FeedItem Type으로 되어있는 것들 Program Type 개선되면 수정필요
                filterCategory = ui.selectedCategoryName,
                onItemClick = { feed -> onFeedClick(feed.id) }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewHomeScreen() {
//    // 미리보기를 위한 더미 함수
//    HomeScreen({})
//}