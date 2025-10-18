package com.example.itda.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.User
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
    onFeedClick: (Int) -> Unit
) {
//    val homeViewModel : HomeViewModel = hiltViewModel()

    // TODO
    //  이런식으로 homeviewmodel 에서 program 을 가져오고
    //  이걸 가공해서 feedList 에 FeedItem Type 으로 넣어줘야할듯.
    //  Feedlist 인자 타입도 FeedItem으로 바꾸고.
    //  근데 Program에 logoURl, user와의 star, eligible 관계 모두 들어있거나 불러올 수 있으면 노상관
    val feedPrograms = DummyData.dummyPrograms
    val dummyFeedItems = DummyData.dummyFeedItems

    val user: User = DummyData.dummyUser[0]

    val categories = remember { DummyData.dummyCategories }
    var selectedCategory by remember { mutableStateOf(categories[0].name) }

    val filteredFeedPrograms = if (selectedCategory == "전체" || selectedCategory.isEmpty()) {
        dummyFeedItems
    } else {
        dummyFeedItems.filter { it.category == selectedCategory }
    }

    BaseScreen(
        title = "home",
        topBarVisible = false, // TODO - toppappbar 사라지지 않는 문제 해결하기
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            HomeHeader(
                username = user.name,
                programCount = filteredFeedPrograms.size
            )
            ProgramFilterRow(
                categories = categories.map { category -> category.name },
                selectedCategory = selectedCategory,
                onCategorySelected = { newCategory ->
                    selectedCategory = newCategory
                }
            )
            FeedList(
                items = filteredFeedPrograms, // TODO - FeedItem Type으로 되어있는 것들 Program Type 개선되면 수정필요
                filterCategory = selectedCategory,
                onItemClick = { feed -> onFeedClick(feed.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    // 미리보기를 위한 더미 함수
    HomeScreen({})
}