package com.example.itda.data.repository

import com.example.itda.data.model.DummyData
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.Program
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse
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

    suspend fun getExamples(): Result<List<ProgramResponse>> = runCatching {
        api.getExamples()
    }

    suspend fun getExampleDetails(exampleId : Int): Result<ProgramDetailResponse> = runCatching {
        api.getExampleDetails(exampleId )
    }

    // TODO - 임시 함수. User에 맞는 program list를 전부 불러오는 함수. 현재는 dummyFeedList 불러오기
    fun getFeedList(): List<Program> {
        return DummyData.dummyFeedItems
    }

    suspend fun searchByRank(
        query: String,
        page: Int,
        size: Int,
        category: String?
    ): PageResponse<ProgramResponse> {
        return api.searchProgramsByRank(
            query = query,
            page = page,
            size = size,
            category = category
        )
    }

    suspend fun searchByLatest(
        query: String,
        page: Int,
        size: Int,
        category: String?
    ): PageResponse<ProgramResponse> {
        return api.searchProgramsByLatest(
            query = query,
            page = page,
            size = size,
            category = category
        )
    }
}