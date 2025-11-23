package com.example.itda.data.repository

import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isLoggedInFlow: Flow<Boolean>

    fun isLoggedIn(): Flow<Boolean>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun signup(email: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun getProfile(): Result<User>

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<Unit>

    suspend fun updatePreference(
        satisfactionScores: PreferenceRequestList
    ): Result<Unit>

    suspend fun saveEmail(email: String)

    suspend fun getSavedEmail(): String?

    suspend fun clearSavedEmail()
}