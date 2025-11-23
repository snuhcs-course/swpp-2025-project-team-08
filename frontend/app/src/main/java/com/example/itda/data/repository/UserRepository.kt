package com.example.itda.data.repository

import com.example.itda.data.model.ProfileUpdateRequest

interface UserRepository {
    suspend fun getMe(): com.example.itda.data.model.User
    suspend fun updateProfile(request: ProfileUpdateRequest): Result<Unit>
    suspend fun clearUser()
    suspend fun getUser(userId: Int): com.example.itda.data.model.User
    suspend fun getUserList(): List<com.example.itda.data.model.User>
}