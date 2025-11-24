package com.example.itda.data.model

data class AuthRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String?,
    val tokenType: String?,
    val expiresIn: Int?
)