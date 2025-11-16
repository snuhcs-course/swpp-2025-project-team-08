package com.example.itda.data.repository

import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.Program
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse

interface ProgramRepository {

    suspend fun getPrograms(page: Int = 0, size: Int = 20, category: String = ""): Result<ProgramPageResponse>

    suspend fun getProgramDetails(programId: Int): Result<ProgramDetailResponse>

    suspend fun getExamples(): Result<List<ProgramResponse>>

    suspend fun getExampleDetails(exampleId: Int): Result<ProgramDetailResponse>

    fun getFeedList(): List<Program>

    suspend fun searchByRank(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse>

    suspend fun searchByLatest(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse>

    suspend fun getUserBookmarkPrograms(sort: String, page: Int = 0, size: Int = 20): Result<ProgramPageResponse>
    suspend fun getAllUserBookmarks(): Result<List<ProgramResponse>>

    suspend fun bookmarkProgram(programId: Int): Result<Unit>

    suspend fun unbookmarkProgram(programId: Int): Result<Unit>

}