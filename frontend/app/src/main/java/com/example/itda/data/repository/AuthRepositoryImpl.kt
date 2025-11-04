package com.example.itda.data.repository

import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.AuthRequest
import com.example.itda.data.source.remote.ProfileRequest
import com.example.itda.data.source.remote.ProfileResponse
import com.example.itda.data.source.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val pref: PrefDataSource
) : AuthRepository {
    private val api = RetrofitInstance.authAPI

    override val isLoggedInFlow: Flow<Boolean> = pref.accessTokenFlow.map {
        !it.isNullOrBlank()
    }

    override fun isLoggedIn(): Flow<Boolean> = pref.isLoggedIn()

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val res = api.login(AuthRequest(email, password))

        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    override suspend fun signup(email: String, password: String): Result<Unit> {
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

    override suspend fun logout(): Result<Unit> = runCatching {
        runCatching { api.logout() }
        pref.clear()
    }

    override suspend fun getProfile(): Result<ProfileResponse> = runCatching {
        api.getProfile()
    }

    override suspend fun updateProfile(
        name: String,
        birthDate: String?,
        gender: String?,
        address: String?,
        maritalStatus: String?,
        educationLevel: String?,
        householdSize: Int?,
        householdIncome: Int?,
        employmentStatus: String?
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