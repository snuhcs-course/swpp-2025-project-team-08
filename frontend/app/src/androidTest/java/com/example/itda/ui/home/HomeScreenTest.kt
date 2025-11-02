package com.example.itda.ui.home

// Compose UI Test Matcher 임포트
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.itda.data.model.Category
import com.example.itda.data.model.Program
import com.example.itda.ui.home.HomeViewModel.HomeUiState
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeScreenNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ... (더미 데이터 정의 및 initialState 정의는 이전과 동일)

    private val mockCategories = listOf(
        Category(0, "전체"),
        Category(1, "대회"),
        Category(2, "소비지원")
    )
    private val clickTargetId = 101
    private val mockFeedItems = listOf(
        Program(id = clickTargetId, title = "클릭 대상 해커톤", categories = listOf(mockCategories[1]), department = "소프트웨어", link = "", content = "클릭 대상 내용", start_date = "", end_date = "", isStarred = false, logo = 0, isEligible = true),
        Program(id = 102, title = "다른 프로그램", categories = listOf(mockCategories[2]), department = "금융", link = "", content = "다른 프로그램 내용", start_date = "", end_date = "", isStarred = false, logo = 0, isEligible = true)
    )

    private val initialState = HomeUiState(
        userId = "user_123", username = "홍길동", categories = mockCategories,
        feedItems = mockFeedItems, selectedCategoryName = "전체"
    )

    // 이 테스트는 두 테스트 함수가 사용하는 공통 설정 코드를 setContent로 한 번만 실행합니다.
    // 하지만 각 테스트는 독립적이어야 하므로, 각 테스트 함수 내에 setContent를 둡니다.
    // 만약 테스트 러너가 문제를 일으킨다면, 테스트를 분리하거나 하나의 테스트에서 두 동작을 검증해야 합니다.

    // ========================================
    // Home -> Feed 내비게이션 통합 테스트
    // ========================================

    @Test
    fun clickingFeedItem_callsOnFeedClickWithCorrectId() {
        var capturedFeedId: Int? = null

        composeTestRule.setContent { // ✅ setContent는 한 번 호출
            HomeScreen(
                ui = initialState,
                onCategorySelected = {},
                onRefresh = {},
                onFeedClick = { id -> capturedFeedId = id }
            )
        }

        // WHEN: 목록에서 "클릭 대상 해커톤" 텍스트를 가진 항목을 찾아 클릭합니다.
        composeTestRule.onNodeWithText("클릭 대상 해커톤").performClick()

        // THEN: 예상한 ID를 전달했는지 검증합니다.
        assertThat(capturedFeedId, `is`(clickTargetId))
    }

    // ========================================
    // 카테고리 필터링 통합 테스트 (UI/로직) - 중복 노드 및 setContent 문제 해결
    // ========================================

    @Test
    fun selectingCategory_callsOnCategorySelectedWithCorrectName() {
        // GIVEN: HomeScreen 실행
        var selectedCategory: String? = null
        val targetCategoryName = "소비지원"

        composeTestRule.setContent { // ✅ setContent는 한 번 호출
            HomeScreen(
                ui = initialState,
                onFeedClick = {},
                onRefresh = {},
                onCategorySelected = { name -> selectedCategory = name }
            )
        }

        // WHEN: "소비지원" 카테고리 버튼을 클릭합니다.-
        composeTestRule.onAllNodesWithText(targetCategoryName, ignoreCase = false)
            .filter(hasClickAction()) // 클릭 가능한 요소만 남기고
            .onFirst() // 그 중에서 첫 번째 요소(카테고리 버튼)를 선택합니다.
            .performClick()

        // THEN: onCategorySelected 람다가 호출되었으며, 예상한 카테고리 이름을 전달했는지 검증합니다.
        assertThat(selectedCategory, `is`(targetCategoryName))
    }
}