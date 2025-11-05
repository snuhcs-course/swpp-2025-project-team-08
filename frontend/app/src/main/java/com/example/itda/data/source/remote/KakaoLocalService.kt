package com.example.itda.data.source.remote

import com.example.itda.data.model.KakaoAddressResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * 카카오 로컬 API 서비스
 */
interface KakaoLocalService {
    @GET("v2/local/search/address.json")
    suspend fun searchAddress(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 30
    ): KakaoAddressResponse
}