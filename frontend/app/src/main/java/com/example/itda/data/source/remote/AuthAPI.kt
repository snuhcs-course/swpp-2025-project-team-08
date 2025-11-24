package com.example.itda.data.source.remote

import com.example.itda.data.model.AuthRequest
import com.example.itda.data.model.AuthResponse
import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProfileRequest
import com.example.itda.data.model.RefreshTokenRequest
import com.example.itda.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthAPI {
    @POST("auth/signup")
    suspend fun signup(@Body body: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse

    @GET("my-profile")
    suspend fun getProfile(): User

    @PUT("my-profile")
    suspend fun updateProfile(@Body request: ProfileRequest): Unit

    @PUT("my-profile/preferences")
    suspend fun updatePreferences(@Body request: PreferenceRequestList): Unit
}