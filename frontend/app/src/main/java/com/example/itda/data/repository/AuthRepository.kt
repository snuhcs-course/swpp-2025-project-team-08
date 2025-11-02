package com.example.itda.data.repository

import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.AuthRequest
import com.example.itda.data.source.remote.ProfileRequest
import com.example.itda.data.source.remote.ProfileResponse
import com.example.itda.data.source.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val pref: PrefDataSource
) {
    private val api = RetrofitInstance.authAPI

    val isLoggedInFlow: Flow<Boolean> = pref.accessTokenFlow.map {
        !it.isNullOrBlank()
    }

    fun isLoggedIn(): Flow<Boolean> = pref.isLoggedIn()

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val res = api.login(AuthRequest(email, password))
        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    suspend fun signup(email: String, password: String): Result<Unit> {
        return try {
            val res = api.signup(AuthRequest(email, password))
            pref.saveTokens(
                access = res.accessToken,
                refresh = res.refreshToken,
                type = res.tokenType,
                expires = res.expiresIn
            )
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        runCatching { api.logout() }
        pref.clear()
    }

    suspend fun getProfile(): Result<ProfileResponse> = runCatching {
        api.getProfile()
    }

    suspend fun updateProfile(
        name: String,
        birthDate: String? = null,
        gender: String? = null,
        address: String? = null,
        maritalStatus: String? = null,      // 추가
        educationLevel: String? = null,     // 추가
        householdSize: Int? = null,         // 추가
        householdIncome: Int? = null,       // 추가
        employmentStatus: String? = null    // 추가
    ): Result<Unit> = runCatching {
        val request = ProfileRequest(
            name = name,
            birthDate = birthDate,
            gender = gender,
            address = address,
            maritalStatus = maritalStatus,
            educationLevel = educationLevel,
            householdSize = householdSize,
            householdIncome = householdIncome,
            employmentStatus = employmentStatus
        )
        api.updateProfile(request)
    }
}