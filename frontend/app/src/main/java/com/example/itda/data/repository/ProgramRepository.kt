package com.example.itda.data.repository

import android.content.Context
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.FeedItem
import com.example.itda.data.model.Program
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProgramRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getProgram(programId: Int): Program {
        // TODO - Program API 호출 구현
        //  현재는 dummy에서 받아오도록 구현
        return DummyData.dummyPrograms[programId]
    }

    // TODO - program 의 type이 정립되면 getProgram으로 대체
    fun getFeed(feedId: Int): FeedItem {
        return DummyData.dummyFeedItems.find { it.id == feedId } ?: DummyData.dummyFeedItems[0]
    }

    // TODO - User에 맞는 program list를 전부 불러오는 함수. 현재는 dummtFeedList 불러오기
    fun getFeedList(): List<FeedItem> {
        return DummyData.dummyFeedItems
    }
}