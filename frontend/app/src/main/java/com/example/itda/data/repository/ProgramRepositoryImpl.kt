package com.example.itda.data.repository

import coil.network.HttpException
import com.example.itda.data.model.PageResponse
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

    override suspend fun searchByRank(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse> {
        return api.searchProgramsByRank(
            query = query,
            category = category,
            page = page,
            size = size
        )
    }

    override suspend fun searchByLatest(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse> {
        return api.searchProgramsByLatest(
            query = query,
            category = category,
            page = page,
            size = size
        )
    }

    override suspend fun getUserBookmarkPrograms(sort: String, page: Int, size: Int): Result<ProgramPageResponse> = runCatching {
        api.getUserBookmarkPrograms(sort, page, size)
    }

    override suspend fun bookmarkProgram(programId: Int): Result<Unit> = runCatching {
        val response = api.bookmarkProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }

    override suspend fun unbookmarkProgram(programId: Int): Result<Unit> = runCatching  {
        val response = api.unbookmarkProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }


    override suspend fun likeLikeProgram(programId: Int): Result<Unit> = runCatching  {
        val response = api.likeLikeProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }

    override suspend fun unlikeLikeProgram(programId: Int): Result<Unit> = runCatching  {
        val response = api.unlikeLikeProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }
    override suspend fun likeDislikeProgram(programId: Int): Result<Unit> = runCatching  {
        val response = api.likeDislikeProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }

    override suspend fun unlikeDislikeProgram(programId: Int): Result<Unit> = runCatching  {
        val response = api.unlikeDislikeProgram(programId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw HttpException(response.raw())
        }
    }

    override suspend fun getAllUserBookmarks(): Result<List<ProgramResponse>> {
        return runCatching {
            val allPrograms = mutableListOf<ProgramResponse>()
            var currentPage = 0
            val pageSize = 20

            while (true) {
                val pageResult = getUserBookmarkPrograms(sort = "LATEST", page = currentPage, size = pageSize)

                // API 호출 결과에서 ProgramPageResponse를 가져옴 (실패 시 예외 발생)
                val response = pageResult.getOrThrow()

                // ProgramPageResponse가 content: List<Program>와 last: Boolean 필드를 가진다고 가정
                allPrograms.addAll(response.content)

                // 마지막 페이지이면 루프 종료
                if (response.isLast) {
                    break
                }

                // 다음 페이지로 이동
                currentPage++
            }

            allPrograms
        }
    }

}