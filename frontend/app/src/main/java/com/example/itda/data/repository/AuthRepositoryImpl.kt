package com.example.itda.data.repository

import com.example.itda.data.model.AuthRequest
import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProfileRequest
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.RefreshTokenRequest
import com.example.itda.data.model.User
import com.example.itda.data.source.local.PrefDataSource
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

    override suspend fun signup(email: String, password: String): Result<Unit> = runCatching {
        val res = api.signup(AuthRequest(email, password))

        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        // 서버 로그아웃 실패해도 로컬 데이터는 삭제
        runCatching { api.logout() }
        pref.clear()
    }

    override suspend fun getRefreshToken(): String? {
        return pref.getRefreshToken()
    }

    override suspend fun refreshToken(): Result<Unit> = runCatching {
        val refreshToken = pref.getRefreshToken()
            ?: throw Exception("No refresh token available")

        val res = api.refreshToken(
            RefreshTokenRequest(refreshToken)
        )

        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    override suspend fun getProfile(): Result<User> = runCatching {
        api.getProfile()
    }

    override suspend fun updateProfile(request: ProfileUpdateRequest): Result<Unit> = runCatching {
        val apiRequest = ProfileRequest(
            name = request.name,
            birthDate = request.birthDate,
            gender = request.gender,
            address = request.address,
            postcode = request.postcode,
            maritalStatus = request.maritalStatus,
            educationLevel = request.educationLevel,
            householdSize = request.householdSize,
            householdIncome = request.householdIncome,
            employmentStatus = request.employmentStatus,
            tags = request.tags
        )
        api.updateProfile(apiRequest)
    }


    override suspend fun updatePreference(
        satisfactionScores: PreferenceRequestList
    ): Result<Unit> = runCatching {
        api.updatePreferences(satisfactionScores)
    }

    override suspend fun saveEmail(email: String) {
        pref.saveEmail(email)
    }

    override suspend fun getSavedEmail(): String? {
        return pref.getSavedEmail()
    }

    override suspend fun clearSavedEmail() {
        pref.clearSavedEmail()
    }
}