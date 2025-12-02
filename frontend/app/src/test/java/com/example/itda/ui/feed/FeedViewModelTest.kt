package com.example.itda.ui.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
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
import org.mockito.kotlin.verify
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

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: FeedViewModel

    // 공통으로 쓰는 더미 ProgramDetailResponse
    private fun createProgramDetail(
        id: Int = 1,
        isBookmarked: Boolean = false,
        likeStatus: String? = null
    ): ProgramDetailResponse {
        return ProgramDetailResponse(
            id = id,
            uuid = "uuid-$id",
            category = "health",
            categoryValue = "보건",
            title = "프로그램 $id",
            details = "상세 설명",
            summary = "요약",
            preview = "미리보기",
            applicationMethod = "온라인",
            applyUrl = "https://example.com/apply/$id",
            referenceUrl = "https://example.com/$id",
            eligibilityMinAge = 10,
            eligibilityMaxAge = 60,
            eligibilityMinHousehold = null,
            eligibilityMaxHousehold = null,
            eligibilityMinIncome = null,
            eligibilityMaxIncome = null,
            eligibilityRegion = null,
            eligibilityGender = null,
            eligibilityMaritalStatus = null,
            eligibilityEducation = null,
            eligibilityEmployment = null,
            applyStartAt = "2025-01-01",
            applyEndAt = "2025-01-31",
            createdAt = "2024-12-01",
            operatingEntity = "보건복지부",
            operatingEntityType = "central",
            likeStatus = likeStatus,
            isBookmarked = isBookmarked
        )
    }

    @Before
    fun setUp() {
        savedStateHandle = SavedStateHandle()
        viewModel = FeedViewModel(programRepository, savedStateHandle)
    }

    // ========== getFeedItem ==========

    @Test
    fun getFeedItem_success_updatesState() = runTest {
        val programId = 1
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = true,
            likeStatus = "LIKED"
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.generalError).isNull()
        assertThat(state.feed).isEqualTo(detail)
        assertThat(state.isBookmarked).isTrue()
        assertThat(state.isLiked).isTrue()
        assertThat(state.isDisliked).isFalse()
    }

    @Test
    fun getFeedItem_failure_setsErrorAndStopsLoading() = runTest {
        val programId = 1
        val errorJson = """{"code":"UNAUTHORIZED","message":"Authenticate failed"}"""
        val errorResponse = errorJson.toResponseBody()
        val httpException = HttpException(Response.error<Any>(401, errorResponse))

        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.failure(httpException))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.isLoading).isFalse()
        // ApiErrorParser.Unauthorized() -> "로그인이 필요합니다"
        assertThat(state.generalError).isEqualTo("로그인이 필요합니다")
    }

    @Test
    fun clearGeneralError_clearsError() = runTest {
        val programId = 1
        // 먼저 네트워크 오류를 발생시켜 generalError 를 채운다
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.generalError).isNotNull()

        viewModel.clearGeneralError()

        assertThat(viewModel.feedUi.value.generalError).isNull()
    }

    // ========== onBookmarkClicked ==========

    @Test
    fun onBookmarkClicked_bookmark_success_updatesStateAndBookmarkChanged() = runTest {
        val programId = 10
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.bookmarkProgram(programId))
            .thenReturn(Result.success(Unit))

        // 초기 피드 로드 (북마크 안 된 상태)
        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isBookmarked).isFalse()

        viewModel.onBookmarkClicked()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isBookmarked).isTrue()
        assertThat(viewModel.hasBookmarkChanged.value).isTrue()

        val info = savedStateHandle.get<Pair<Int, Boolean>>("bookmark_change_info")
        assertThat(info).isEqualTo(Pair(programId, true))

        verify(programRepository).bookmarkProgram(programId)
    }

    @Test
    fun onBookmarkClicked_unbookmark_success_updatesStateAndBookmarkChanged() = runTest {
        val programId = 20
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = true,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.unbookmarkProgram(programId))
            .thenReturn(Result.success(Unit))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isBookmarked).isTrue()

        viewModel.onBookmarkClicked()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isBookmarked).isFalse()
        assertThat(viewModel.hasBookmarkChanged.value).isTrue()

        val info = savedStateHandle.get<Pair<Int, Boolean>>("bookmark_change_info")
        assertThat(info).isEqualTo(Pair(programId, false))

        verify(programRepository).unbookmarkProgram(programId)
    }

    @Test
    fun onBookmarkClicked_bookmark_failure_setsErrorAndDoesNotChangeBookmark() = runTest {
        val programId = 30
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.bookmarkProgram(programId))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isBookmarked).isFalse()

        viewModel.onBookmarkClicked()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.isBookmarked).isFalse()
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(viewModel.hasBookmarkChanged.value).isFalse()
        assertThat(savedStateHandle.get<Pair<Int, Boolean>>("bookmark_change_info")).isNull()

        verify(programRepository).bookmarkProgram(programId)
    }

    @Test
    fun onBookmarkClicked_unbookmark_failure_setsErrorAndDoesNotChangeBookmark() = runTest {
        val programId = 40
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = true,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.unbookmarkProgram(programId))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isBookmarked).isTrue()

        viewModel.onBookmarkClicked()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.isBookmarked).isTrue()
        assertThat(state.generalError).isEqualTo("네트워크 연결을 확인해주세요")
        assertThat(viewModel.hasBookmarkChanged.value).isFalse()
        assertThat(savedStateHandle.get<Pair<Int, Boolean>>("bookmark_change_info")).isNull()

        verify(programRepository).unbookmarkProgram(programId)
    }

    // ========== toggleLike ==========

    @Test
    fun toggleLike_like_success_setsLikedAndClearsDislike() = runTest {
        val programId = 50
        // 처음에는 DISLIKED 상태로 로드 -> toggleLike 시 likeLikeProgram 호출되고 DISLIKE 해제
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = "DISLIKED"
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.likeLikeProgram(programId))
            .thenReturn(Result.success(Unit))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        val initial = viewModel.feedUi.value
        assertThat(initial.isLiked).isFalse()
        assertThat(initial.isDisliked).isTrue()

        viewModel.toggleLike()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isLiked).isTrue()
        assertThat(state.isDisliked).isFalse()

        verify(programRepository).likeLikeProgram(programId)
    }

    @Test
    fun toggleLike_unlike_success_turnsOffLike() = runTest {
        val programId = 60
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = "LIKED"
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.unlikeLikeProgram(programId))
            .thenReturn(Result.success(Unit))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isLiked).isTrue()

        viewModel.toggleLike()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isLiked).isFalse()
        assertThat(state.isDisliked).isFalse()

        verify(programRepository).unlikeLikeProgram(programId)
    }

    @Test
    fun toggleLike_failure_setsErrorAndKeepsState() = runTest {
        val programId = 70
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.likeLikeProgram(programId))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        val before = viewModel.feedUi.value

        viewModel.toggleLike()
        advanceUntilIdle()

        val after = viewModel.feedUi.value
        assertThat(after.isLiked).isEqualTo(before.isLiked)
        assertThat(after.isDisliked).isEqualTo(before.isDisliked)
        assertThat(after.generalError).isEqualTo("네트워크 연결을 확인해주세요")

        verify(programRepository).likeLikeProgram(programId)
    }

    // ========== toggleDisLike ==========

    @Test
    fun toggleDisLike_dislike_success_setsDislikedAndClearsLike() = runTest {
        val programId = 80
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = "LIKED"
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.likeDislikeProgram(programId))
            .thenReturn(Result.success(Unit))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        val initial = viewModel.feedUi.value
        assertThat(initial.isLiked).isTrue()
        assertThat(initial.isDisliked).isFalse()

        viewModel.toggleDisLike()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isDisliked).isTrue()
        assertThat(state.isLiked).isFalse()

        verify(programRepository).likeDislikeProgram(programId)
    }

    @Test
    fun toggleDisLike_undislike_success_turnsOffDislike() = runTest {
        val programId = 90
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = "DISLIKED"
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.unlikeDislikeProgram(programId))
            .thenReturn(Result.success(Unit))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        assertThat(viewModel.feedUi.value.isDisliked).isTrue()

        viewModel.toggleDisLike()
        advanceUntilIdle()

        val state = viewModel.feedUi.value
        assertThat(state.generalError).isNull()
        assertThat(state.isDisliked).isFalse()
        assertThat(state.isLiked).isFalse()

        verify(programRepository).unlikeDislikeProgram(programId)
    }

    @Test
    fun toggleDisLike_failure_setsErrorAndKeepsState() = runTest {
        val programId = 100
        val detail = createProgramDetail(
            id = programId,
            isBookmarked = false,
            likeStatus = null
        )
        `when`(programRepository.getProgramDetails(programId))
            .thenReturn(Result.success(detail))
        `when`(programRepository.likeDislikeProgram(programId))
            .thenReturn(Result.failure(IOException("Network error")))

        viewModel.getFeedItem(programId)
        advanceUntilIdle()
        val before = viewModel.feedUi.value

        viewModel.toggleDisLike()
        advanceUntilIdle()

        val after = viewModel.feedUi.value
        assertThat(after.isDisliked).isEqualTo(before.isDisliked)
        assertThat(after.isLiked).isEqualTo(before.isLiked)
        assertThat(after.generalError).isEqualTo("네트워크 연결을 확인해주세요")

        verify(programRepository).likeDislikeProgram(programId)
    }

}
