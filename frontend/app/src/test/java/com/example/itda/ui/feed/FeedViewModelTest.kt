package com.example.itda.ui.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FeedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var programRepository: ProgramRepository

    private lateinit var viewModel: FeedViewModel

    // 테스트용 더미 데이터
    private val dummyDetail = ProgramDetailResponse(
        id = 101, uuid = "uuid101", category = "cash", categoryValue = "빈곤 완화",
        title = "테스트 지원 정책", details = "상세 내용", summary = "요약", preview = "미리보기",
        applicationMethod = "온라인", applyUrl = "http://apply.com", referenceUrl = null,
        eligibilityMinAge = 65, eligibilityMaxAge = 100, eligibilityMinHousehold = 1,
        eligibilityMaxHousehold = 4, eligibilityMinIncome = 0, eligibilityMaxIncome = 5000000,
        eligibilityRegion = "전국", eligibilityGender = "무관", eligibilityMaritalStatus = "무관",
        eligibilityEducation = "무관", eligibilityEmployment = "무관", applyStartAt = null,
        applyEndAt = null, createdAt = null, operatingEntity = "보건복지부", operatingEntityType = "정부"
    )

    @Before
    fun setup() {
        // SavedStateHandle은 필요 없으므로 Mock 또는 빈 객체 전달
        viewModel = FeedViewModel(programRepository, SavedStateHandle())
    }

    // ========== Init State Test ==========

    @Test
    fun init_state_isCorrectlyInitialized() = runTest {
        viewModel.feedUi.test {
            val initialState = awaitItem()
            // 초기 상태는 DummyData의 더미 응답으로 설정됨 (FeedViewModel.kt)
            assertThat(initialState.feed).isEqualTo(DummyData.dummyProgramDetailResponse)
            assertThat(initialState.isLoading).isFalse()
            assertThat(initialState.generalError).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== getFeedItem Tests (Success) ==========

    @Test
    fun getFeedItem_success_updatesFeedAndSetsLoadingFalse() = runTest {
        // Given: Repository Mock 설정 - 성공
        `when`(programRepository.getProgramDetails(any())).thenReturn(Result.success(dummyDetail))

        viewModel.feedUi.test {
            awaitItem() // 초기 상태 소비 (DummyData)

            // When: 피드 아이템 로드 시작
            viewModel.getFeedItem(feedId = 101)

            // 1. 로딩 시작 상태
            assertThat(awaitItem().isLoading).isTrue()

            // 2. 최종 성공 상태
            val successState = awaitItem()
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.feed.id).isEqualTo(dummyDetail.id)
            assertThat(successState.feed.title).isEqualTo(dummyDetail.title)
            assertThat(successState.generalError).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== getFeedItem Tests (Failure) ==========

    @Test
    fun getFeedItem_networkFailure_setsErrorAndLoadingFalse() = runTest {
        // Given: Repository Mock 설정 - 네트워크 오류
        val ioException = IOException("Network error")
        `when`(programRepository.getProgramDetails(any())).thenReturn(Result.failure(ioException))

        viewModel.feedUi.test {
            awaitItem() // 초기 상태 소비

            // When
            viewModel.getFeedItem(feedId = 101)

            // 1. 로딩 시작 상태
            assertThat(awaitItem().isLoading).isTrue()

            // 2. 최종 실패 상태
            val failureState = awaitItem()
            assertThat(failureState.isLoading).isFalse()
            // ApiErrorParser의 IOException 처리 결과
            assertThat(failureState.generalError).isEqualTo("네트워크 연결을 확인해주세요")
            // Feed 데이터는 변경되지 않아야 함 (초기값 유지)
            assertThat(failureState.feed).isEqualTo(DummyData.dummyProgramDetailResponse)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFeedItem_http404Failure_setsErrorAndLoadingFalse() = runTest {
        // Given: Repository Mock 설정 - 404 Not Found (ApiErrorParser에 정의되지 않은 에러)
        val errorJson = """{"code":"NOT_FOUND","message":"Program not found"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(404, errorResponse))
        `when`(programRepository.getProgramDetails(any())).thenReturn(Result.failure(httpException))

        viewModel.feedUi.test {
            awaitItem() // 초기 상태 소비

            // When
            viewModel.getFeedItem(feedId = 101)
            advanceUntilIdle()

            // 1. 로딩 시작 상태
            assertThat(awaitItem().isLoading).isTrue()

            // 2. 최종 실패 상태
            val failureState = awaitItem()
            assertThat(failureState.isLoading).isFalse()
            // ApiErrorParser의 Unknown Error 처리 결과
            assertThat(failureState.generalError).isEqualTo("알 수 없는 오류가 발생했습니다")
            assertThat(failureState.feed).isEqualTo(DummyData.dummyProgramDetailResponse)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Feature Toggle Tests (Placeholder) ==========

    @Test
    fun toggleBookmark_noImplementation_doesNotThrow() = runTest {
        // 현재는 TODO 주석만 있으므로, 실행 시 예외가 발생하지 않는지만 확인
        viewModel.toggleBookmark(feedId = 101)
        // advanceUntilIdle() // 비동기 작업이 없으므로 필요 없음
    }

    @Test
    fun applicationProgram_noImplementation_doesNotThrow() = runTest {
        // 현재는 TODO 주석만 있으므로, 실행 시 예외가 발생하지 않는지만 확인
        viewModel.applicationProgram(feedID = 101)
        // advanceUntilIdle() // 비동기 작업이 없으므로 필요 없음
    }
}