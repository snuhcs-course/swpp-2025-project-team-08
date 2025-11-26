package com.example.itda.data.source.remote

import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


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
        @Query("category") category: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<ProgramResponse>

    @GET("programs/search/latest")
    suspend fun searchProgramsByLatest(
        @Query("query") query: String,
        @Query("category") category: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<ProgramResponse>

    /**
     * /users/me/bookmarks (GET)
     * Get lists of bookmarked programs for the user
     */
    @GET("users/me/bookmarks")
    suspend fun getUserBookmarkPrograms(
        @Query("sort") sort: String, // LATEST (default), DEADLINE
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ProgramPageResponse


    /**
     * /programs/{id}/bookmark (POST)
     * bookmark the program
     */
    @POST("programs/{id}/bookmark")
    suspend fun bookmarkProgram(@Path("id") id: Int): retrofit2.Response<Unit>

    /**
     * /programs/{id}/unbookmark (POST)
     * bookmark the program
     */
    @POST("programs/{id}/unbookmark")
    suspend fun unbookmarkProgram(@Path("id") id: Int): retrofit2.Response<Unit>


    /**
     * /programs/{id}/like?type=true (POST)
     * bookmark the program
     */
    @POST("programs/{id}/like?type=true")
    suspend fun likeLikeProgram(@Path("id") id: Int): retrofit2.Response<Unit>

    /**
     * /programs/{id}/unlike?type=true (POST)
     * bookmark the program
     */
    @POST("programs/{id}/unlike?type=true")
    suspend fun unlikeLikeProgram(@Path("id") id: Int): retrofit2.Response<Unit>

    /**
     * /programs/{id}/like?type=false (POST)
     * bookmark the program
     */
    @POST("programs/{id}/like?type=false")
    suspend fun likeDislikeProgram(@Path("id") id: Int): retrofit2.Response<Unit>

    /**
     * /programs/{id}/unlike?type=false (POST)
     * bookmark the program
     */
    @POST("programs/{id}/unlike?type=false")
    suspend fun unlikeDislikeProgram(@Path("id") id: Int): retrofit2.Response<Unit>
}

