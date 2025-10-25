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
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String?,

    @SerializedName("token_type")
    val tokenType: String?,

    @SerializedName("expires_in")
    val expiresIn: Int?
)

data class ProfileRequest(
    val name: String,
    val age: Int?,
    val gender: String?,
    val address: String?
)

data class ProfileResponse(
    val id: Int,
    val email: String,
    val name: String,
    val age: Int?,
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