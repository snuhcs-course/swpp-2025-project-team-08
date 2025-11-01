package com.example.itda.data.source.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,

    val refreshToken: String?,

    val tokenType: String?,

    val expiresIn: Int?
)

data class ProfileRequest(
    val name: String,
    val birthDate: String?,
    val gender: String?,
    val address: String?
)

data class ProfileResponse(
    val id: Int,
    val email: String,
    val name: String,
    val birthDate: String?,
    val gender: String?,
    val address: String?
)


interface AuthAPI {
    @POST("auth/signup")
    suspend fun signup(@Body body: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("my-profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("my-profile")
    suspend fun updateProfile(@Body request: ProfileRequest): Unit
}