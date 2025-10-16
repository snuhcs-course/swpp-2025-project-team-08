package com.example.itda.ui.feed

import androidx.lifecycle.ViewModel
import com.example.itda.data.model.FeedItem
import com.example.itda.data.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val programRepository: ProgramRepository
) : ViewModel() {


//    private val _feedItem = MutableStateFlow(DummyData.dummyFeedItems[0]) // viewmodel 내에서 관리하는 Writable StateFlow
//    val feedItem: StateFlow<FeedItem> = _feedItem.asStateFlow() // 외부 접근 가능한 Read-Only StateFlow



    fun getFeedItem (feedId : Int): FeedItem {
        // TODO - Repository 연결해서 Feed 가져오기
        // feedItem = {FeedRepository.getFeedItem or programRepository.getProgram}
        return programRepository.getFeed(feedId)

    }


    fun toggleStar(feedId : Int) {
        // TODO - Repository 연결해서 toggle Star

    }

    fun applicationProgram(feedID : Int) {
        // TOOD - Repository 연결해서 program 바로 신청
    }
}