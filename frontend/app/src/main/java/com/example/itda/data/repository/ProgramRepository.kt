package com.example.itda.data.repository

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

}