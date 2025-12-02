package com.example.itda.ui.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.repository.FakeProgramRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * FeedViewModel Integration Test
 * Fake Repository를 사용하여 ViewModel이 상태(UiState)를 올바르게 업데이트하는지 검증
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FeedViewModel
    private lateinit var fakeProgramRepository: FakeProgramRepository

    // ========== 더미 데이터 정의 ==========
    private val TEST_FEED_ID = 101
    private val dummyDetail = ProgramDetailResponse(
        id = TEST_FEED_ID, uuid = "uuid101", category = "cash", categoryValue = "빈곤 완화",
        title = "통합 테스트 정책", details = "상세 내용", summary = "AI 요약", preview = "미리보기",
        applicationMethod = "온라인", applyUrl = "http://apply.com", referenceUrl = null,
        eligibilityMinAge = 65, eligibilityMaxAge = 100, eligibilityMinHousehold = 1,
        eligibilityMaxHousehold = 4, eligibilityMinIncome = 0, eligibilityMaxIncome = 5000000,
        eligibilityRegion = "전국", eligibilityGender = "무관", eligibilityMaritalStatus = "무관",
        eligibilityEducation = "무관", eligibilityEmployment = "무관", applyStartAt = "2025-01-01T00:00:00Z",
        applyEndAt = "2025-12-31T23:59:59Z", createdAt = null, operatingEntity = "보건복지부", operatingEntityType = "정부",
        likeStatus = null, isBookmarked = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeProgramRepository = FakeProgramRepository()
        viewModel = FeedViewModel(fakeProgramRepository, SavedStateHandle())
    }

    @After
    fun tearDown() {
        // Dispatchers.resetMain() // TestRule이 자동으로 처리할 수 있음
    }

    // ========== 1. 피드 로딩 (getFeedItem) 테스트 ==========

    @Test
    fun getFeedItem_success_updatesUiState() = runTest {
        // Given
        fakeProgramRepository.getProgramDetailsResult = Result.success(dummyDetail)

        viewModel.feedUi.test {
            // 1. 초기 더미 상태 소비 (ViewModel 생성 시)
            awaitItem()

            // When
            viewModel.getFeedItem(TEST_FEED_ID)
            advanceUntilIdle() // 모든 로딩 및 성공 상태 변화가 완료됨

            // 2. 로딩 시작 상태 소비
            // (Flow는 isLoading=true 상태와 최종 성공 상태를 내보냅니다.
            //  advanceUntilIdle() 덕분에 이들은 즉시 제공 가능합니다.)
            assertThat(awaitItem().isLoading).isTrue()

            // 3. 최종 성공 상태 소비
            val successState = awaitItem()

            // Then: Repository 호출 및 최종 상태 검증
            assertThat(fakeProgramRepository.getProgramDetailsCalled).isTrue()
            assertThat(fakeProgramRepository.lastGetProgramDetailsId).isEqualTo(TEST_FEED_ID)
            assertThat(successState.feed.id).isEqualTo(TEST_FEED_ID)
            assertThat(successState.feed.title).isEqualTo("통합 테스트 정책")
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.generalError).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFeedItem_networkFailure_setsError() = runTest {
        // Given
        val errorMessage = "네트워크 연결 오류"
        fakeProgramRepository.getProgramDetailsResult = Result.failure(IOException(errorMessage))

        viewModel.feedUi.test {
            // 1. 초기 더미 상태 소비 (ViewModel 생성 시)
            awaitItem()

            // When
            viewModel.getFeedItem(TEST_FEED_ID)
            advanceUntilIdle() // 모든 로딩 및 실패 상태 변화가 완료됨

            // 2. 로딩 시작 상태 소비
            assertThat(awaitItem().isLoading).isTrue()

            // 3. 최종 실패 상태 소비
            val state = awaitItem()

            // Then
            assertThat(fakeProgramRepository.getProgramDetailsCalled).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요") // ApiErrorParser 에 의해 파싱된 메시지
            // 데이터는 변경되지 않아야 함 (초기값 유지)
            assertThat(state.feed.id).isNotEqualTo(TEST_FEED_ID)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFeedItem_multipleCalls_updatesLatestFeed() = runTest {
        // Given: 첫 번째 호출은 ID 101, 두 번째 호출은 ID 102
        val firstDetail = dummyDetail.copy(id = 101, title = "첫 번째 정책")
        val secondDetail = dummyDetail.copy(id = 102, title = "두 번째 정책")

        // 'test' 블록 내에서 모든 호출을 처리하여 상태 변화를 추적합니다.
        viewModel.feedUi.test {
            awaitItem() // 0. 초기 상태 소비 (DummyData)

            // --- 1차 호출 (ID 101) ---
            fakeProgramRepository.getProgramDetailsResult = Result.success(firstDetail)
            viewModel.getFeedItem(101)

            awaitItem() // 1. 로딩 시작 (isLoading=true)
            val state101 = awaitItem() // 2. 101 성공 상태
            assertThat(state101.feed.id).isEqualTo(101)
            assertThat(fakeProgramRepository.lastGetProgramDetailsId).isEqualTo(101)

            fakeProgramRepository.reset() // Repositories should support resetting call count

            // --- 2차 호출 (ID 102) ---
            fakeProgramRepository.getProgramDetailsResult = Result.success(secondDetail)
            viewModel.getFeedItem(102)

            awaitItem() // 3. 로딩 시작 (isLoading=true)
            val state102 = awaitItem() // 4. 102 최종 성공 상태

            // Then: 마지막 호출 결과를 따름
            assertThat(fakeProgramRepository.lastGetProgramDetailsId).isEqualTo(102)
            assertThat(state102.feed.id).isEqualTo(102)
            assertThat(state102.feed.title).isEqualTo("두 번째 정책")
            assertThat(state102.isLoading).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

}