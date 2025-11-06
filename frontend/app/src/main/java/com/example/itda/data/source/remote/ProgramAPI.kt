package com.example.itda.data.source.remote

import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//TODO - openapi.yaml 에는 이 타입의 repsonse 가 온다고 되어있는데
    // 아직 수정이 덜 된 것 같아 임의로 getPrograms 의 return type을 List<Program> 으로 둡니다.



interface ProgramAPI {
    /**
     * /programs (GET)
     * Get lists of programs specific for the user
     */
    @GET("programs")
    suspend fun getPrograms(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String
    ): ProgramPageResponse

    /**
     * /programs/{id} (GET)
     * Retrieve detailed information about a program by its ID
     */
    @GET("programs/{id}")
    suspend fun getProgramDetails(@Path("id") id: Int): ProgramDetailResponse



    @GET("programs/examples")
    suspend fun getExamples(): List<ProgramResponse>

    @GET("programs/examples/{id}")
    suspend fun getExampleDetails(@Path("id") id: Int): ProgramDetailResponse


    @GET("programs/search/rank")
    suspend fun searchProgramsByRank(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("category") category: String? = null
    ): PageResponse<ProgramResponse>

    @GET("programs/search/latest")
    suspend fun searchProgramsByLatest(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("category") category: String? = null
    ): PageResponse<ProgramResponse>
}

