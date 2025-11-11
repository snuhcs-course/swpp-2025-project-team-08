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

class ProgramRepositoryImpl @Inject constructor(
) : ProgramRepository {
    private val api: ProgramAPI = RetrofitInstance.programAPI

    override suspend fun getPrograms(page: Int, size: Int, category: String): Result<ProgramPageResponse> = runCatching {
        api.getPrograms(page, size, category)
    }

    override suspend fun getProgramDetails(programId: Int): Result<ProgramDetailResponse> = runCatching {
        api.getProgramDetails(programId)
    }

    override suspend fun getExamples(): Result<List<ProgramResponse>> = runCatching {
        api.getExamples()
    }

    override suspend fun getExampleDetails(exampleId: Int): Result<ProgramDetailResponse> = runCatching {
        api.getExampleDetails(exampleId)
    }

    // TODO - 임시 함수. User에 맞는 program list를 전부 불러오는 함수. 현재는 dummyFeedList 불러오기
    override fun getFeedList(): List<Program> {
        return DummyData.dummyFeedItems
    }

    override suspend fun searchByRank(
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

    override suspend fun searchByLatest(
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