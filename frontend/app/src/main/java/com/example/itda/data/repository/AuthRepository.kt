package com.example.itda.data.repository

import com.example.itda.data.source.remote.ProfileResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isLoggedInFlow: Flow<Boolean>

    fun isLoggedIn(): Flow<Boolean>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun signup(email: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun getProfile(): Result<ProfileResponse>

    suspend fun updateProfile(
        name: String,
        birthDate: String? = null,
        gender: String? = null,
        address: String? = null,
        maritalStatus: String? = null,
        educationLevel: String? = null,
        householdSize: Int? = null,
        householdIncome: Int? = null,
        employmentStatus: String? = null
    ): Result<Unit>
}