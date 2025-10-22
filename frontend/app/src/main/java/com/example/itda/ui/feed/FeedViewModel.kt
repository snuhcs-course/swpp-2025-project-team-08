package com.example.itda.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.FeedItem
import com.example.itda.data.repository.ProgramRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    data class FeedUiState(
        val feed: FeedItem = DummyData.dummyFeedItems[0], // 피드 데이터[0], // 사용자 정보
        val isLoading: Boolean = false
    )


    private val _feedUi = MutableStateFlow(FeedUiState())
    val feedUi: StateFlow<FeedViewModel.FeedUiState> = _feedUi.asStateFlow()


    fun getFeedItem(feedId: Int) {
        viewModelScope.launch {
            _feedUi.update {it.copy(isLoading = true)}
            val feedItem = programRepository.getFeed(feedId) // TODO - getFeed 말고 getProgram?
            _feedUi.update {
                it.copy(
                    feed = feedItem,
                    isLoading = false
                )
            }
        }



    }


    fun toggleBookmark(feedId: Int) {
        // TODO - Repository 연결해서 toggle bookmark

    }

    fun applicationProgram(feedID: Int) {
        // TOOD - Repository 연결해서 program 바로 신청
    }
}