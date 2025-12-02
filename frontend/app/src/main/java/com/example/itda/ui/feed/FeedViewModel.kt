package com.example.itda.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.dummyProgramDetailResponse
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.source.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val programRepository: ProgramRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {


    data class FeedUiState(
        val feed: ProgramDetailResponse = dummyProgramDetailResponse, // 피드 데이터[0], // 사용자 정보
        val isBookmarked : Boolean = false,
        val isLiked : Boolean = false,
        val isDisliked : Boolean = false,
        val isLoading: Boolean = false,
        val generalError : String? = null,
    )

    private val _hasBookmarkChanged = MutableStateFlow(false)
    val hasBookmarkChanged: StateFlow<Boolean> = _hasBookmarkChanged.asStateFlow()


    private val _feedUi = MutableStateFlow(FeedUiState())
    val feedUi: StateFlow<FeedViewModel.FeedUiState> = _feedUi.asStateFlow()

    fun clearGeneralError() {
        _feedUi.update { it.copy(generalError = null) }
    }

    fun getFeedItem(feedId: Int) {
        viewModelScope.launch {
            _feedUi.update {it.copy(isLoading = true)}

            val feedItem = programRepository.getProgramDetails(feedId)
            feedItem
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _feedUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false
                        )
                    }
                }
            feedItem
                .onSuccess { feedItem ->
                    _feedUi.update {
                        it.copy(
                            feed = feedItem,
                            isBookmarked = feedItem.isBookmarked,
                            isLiked = feedItem.likeStatus == "LIKED",
                            isDisliked = feedItem.likeStatus == "DISLIKED",
                            isLoading = false
                        )
                    }

                }
        }
    }

    fun onBookmarkClicked() {
        viewModelScope.launch {

            val isBookmarked = feedUi.value.isBookmarked
            val updateIsBookmarked = !isBookmarked

            val apiCall = if(isBookmarked)
                programRepository.unbookmarkProgram(programId = feedUi.value.feed.id)
            else
                programRepository.bookmarkProgram(programId = feedUi.value.feed.id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _feedUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isBookmarked = feedUi.value.isBookmarked,
                        )
                    }
                    _hasBookmarkChanged.update { false }
                }
                .onSuccess { response ->
                    _feedUi.update {
                        it.copy(
                            generalError = null,
                            isBookmarked = updateIsBookmarked
                        )
                    }
                    val info = Pair(feedUi.value.feed.id, updateIsBookmarked)
                    savedStateHandle["bookmark_change_info"] = info
                    _hasBookmarkChanged.update { true }
                }
        }
    }

    fun toggleLike() {

        viewModelScope.launch {
            val isLiked = feedUi.value.isLiked
            val apiCall = if(isLiked)
                programRepository.unlikeLikeProgram(programId = feedUi.value.feed.id)
            else
                programRepository.likeLikeProgram(programId = feedUi.value.feed.id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _feedUi.update {
                        it.copy(
                            generalError = apiError.message,
                        )
                    }
                }
                .onSuccess { likeResponse ->

                    _feedUi.update {
                        it.copy(
                            generalError = null,
                            isLiked = !isLiked,
                            isDisliked = false,
                        )
                    }
                }
        }
    }


    fun toggleDisLike() {
        viewModelScope.launch {
            val isDisliked = feedUi.value.isDisliked
            val apiCall = if(isDisliked)
                programRepository.unlikeDislikeProgram(programId = feedUi.value.feed.id)
            else
                programRepository.likeDislikeProgram(programId = feedUi.value.feed.id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _feedUi.update {
                        it.copy(
                            generalError = apiError.message,
                        )
                    }
                }
                .onSuccess { disklikeResponse ->
                    _feedUi.update {
                        it.copy(
                            generalError = null,
                            isDisliked = !isDisliked,
                            isLiked = false
                        )
                    }
                }
        }
    }
}