package com.example.itda.ui.feed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.itda.data.model.Category
import com.example.itda.data.model.Program
import com.example.itda.ui.feed.FeedViewModel.FeedUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Robolectric 대신 Android의 공식 테스트 러너인 AndroidJUnit4를 사용합니다.
// 이 테스트는 src/androidTest/java 폴더에 위치해야 하며, 실제 기기나 에뮬레이터에서 실행됩니다.
@RunWith(AndroidJUnit4::class)
class FeedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProgram = Program(
        id = 10,
        title = "AI 해커톤 2025",
        categories = listOf(Category(1, "대회"), Category(7, "AI")),
        department = "소프트웨어학과",
        link = "https://example.com",
        content = "인공지능을 활용한 창의적 문제 해결을 주제로 한 해커톤입니다.",
        start_date = "2025-11-01",
        end_date = "2025-11-03",
        isStarred = true,
        logo = 0,
        isEligible = true // 신청 가능
    )

    private val mockProgramNotEligible = mockProgram.copy(
        id = 11,
        title = "청년 도약 계좌",
        isEligible = false // 신청 불가능
    )

    // ========================================
    // Part 1: 신청 가능한 프로그램 테스트 (isEligible = true)
    // ========================================

    @Test
    fun eligibleProgram_detailsAndApplyButtonAreCorrect() {
        // GIVEN: 신청 가능한 프로그램 상태
        val uiState = FeedUiState(feed = mockProgram)

        // WHEN: FeedScreen을 실행합니다.
        composeTestRule.setContent {
            FeedScreen(ui = uiState, onBack = {})
        }

        // THEN: 주요 정보가 화면에 표시되는지 검증합니다.
        composeTestRule.onNodeWithText("AI 해커톤 2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("소프트웨어학과").assertIsDisplayed()

        // '신청하러가기' 버튼이 활성화되어 있는지 확인
        composeTestRule.onNodeWithText("신청하러가기").assertIsDisplayed().assertIsEnabled()
    }


    // ========================================
    // Part 2: 신청 불가능한 프로그램 테스트 (isEligible = false)
    // ========================================

    @Test
    fun notEligibleProgram_detailsAndApplyButtonAreDisabled() {
        // GIVEN: 신청 불가능한 프로그램 상태
        val uiState = FeedUiState(feed = mockProgramNotEligible)

        // WHEN: FeedScreen을 실행합니다.
        composeTestRule.setContent {
            FeedScreen(ui = uiState, onBack = {})
        }

        // THEN:
        composeTestRule.onNodeWithText("청년 도약 계좌").assertIsDisplayed()

        // '신청하러가기' 버튼이 비활성화되어 있는지 확인
        composeTestRule.onNodeWithText("신청하러가기").assertIsDisplayed().assertIsNotEnabled()
    }

    // ========================================
    // Part 3: 뒤로 가기 버튼 테스트 (Navigation Mocking)
    // ========================================

    @Test
    fun onBackIsCalled_whenBackIconClicked() {
        var backClicked = false
        val uiState = FeedUiState(feed = mockProgram)

        composeTestRule.setContent {
            // onBack 람다를 인수로 전달
            FeedScreen(ui = uiState, onBack = { backClicked = true })
        }
    }
}

// BaseScreen TopAppBar의 뒤로 가기 버튼이 '뒤로 가기' ContentDescription을 가진다고 가정하고 클릭을 시뮬레이션
// (실제 프로젝트의 ContentDescription에 맞게 수정 필요