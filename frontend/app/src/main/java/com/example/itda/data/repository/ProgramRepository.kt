package com.example.itda.data.repository

import com.example.itda.data.model.DummyData
import com.example.itda.data.model.Program
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.source.remote.ProgramAPI
import com.example.itda.data.source.remote.RetrofitInstance
import javax.inject.Inject

class ProgramRepository @Inject constructor(
) {
    private val api: ProgramAPI = RetrofitInstance.programAPI

    suspend fun getPrograms(page: Int = 0, size: Int = 20, category : String = ""): Result<ProgramPageResponse> = runCatching {
        api.getPrograms(page, size, category)
    }

    suspend fun getProgramDetails(programId: Int): Result<ProgramDetailResponse> = runCatching {
        api.getProgramDetails(programId)
    }

    fun getProgram(programId: Int): Program {
        // TODO - Program API 호출 구현
        //  현재는 dummy에서 받아오도록 구현
        return DummyData.dummyPrograms[programId]
    }

    // TODO - program 의 type이 정립되면 getProgram으로 대체
    fun getFeed(feedId: Int): Program {
        return DummyData.dummyFeedItems.find { it.id == feedId } ?: DummyData.dummyFeedItems[0]
    }

    // TODO - 임시 함수. User에 맞는 program list를 전부 불러오는 함수. 현재는 dummyFeedList 불러오기
    fun getFeedList(): List<Program> {
        return DummyData.dummyFeedItems
    }
}