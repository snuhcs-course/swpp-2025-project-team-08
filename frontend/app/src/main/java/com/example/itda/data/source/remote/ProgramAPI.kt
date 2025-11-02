package com.example.itda.data.source.remote

import com.example.itda.data.model.Program
import retrofit2.http.GET
import retrofit2.http.Path

data class ProgramSummary(
    val id: Int,
    val title: String
)   //TODO - openapi.yaml 에는 이 타입의 repsonse 가 온다고 되어있는데
    // 아직 수정이 덜 된 것 같아 임의로 getPrograms 의 return type을 List<Program> 으로 둡니다.


interface ProgramAPI {
    /**
     * /programs (GET)
     * Get lists of programs specific for the user
     */
    @GET("programs")
    suspend fun getPrograms(): List<Program>

    /**
     * /programs/{id} (GET)
     * Retrieve detailed information about a program by its ID
     */
    @GET("programs/{id}")
    suspend fun getProgramDetails(@Path("id") id: Int): Program
}