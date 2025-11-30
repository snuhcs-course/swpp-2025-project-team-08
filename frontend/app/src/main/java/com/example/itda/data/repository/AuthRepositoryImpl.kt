package com.example.itda.data.repository

import android.util.Log
import com.example.itda.data.model.AuthRequest
import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProfileRequest
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.RefreshTokenRequest
import com.example.itda.data.model.User
import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.AuthAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val pref: PrefDataSource,
    private val api: AuthAPI
) : AuthRepository {

    private var isLoggingOut = false
    private val logoutMutex = Mutex()

    override val isLoggedInFlow: Flow<Boolean> = pref.accessTokenFlow.map {
        !it.isNullOrBlank()
    }

    override fun isLoggedIn(): Flow<Boolean> = pref.isLoggedIn()

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        logoutMutex.withLock {
            // 로그인 하면 토큰 발급 가능
            isLoggingOut = false
        }

        val res = api.login(AuthRequest(email, password))

        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    override suspend fun signup(email: String, password: String): Result<Unit> = runCatching {
        logoutMutex.withLock {
            // 회원가입 하면 토큰 발급 가능
            isLoggingOut = false
        }

        val res = api.signup(AuthRequest(email, password))

        pref.saveTokens(
            access = res.accessToken,
            refresh = res.refreshToken,
            type = res.tokenType,
            expires = res.expiresIn
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        logoutMutex.withLock {
            isLoggingOut = true

            // 서버 로그아웃 실패해도 로컬 데이터는 삭제
            runCatching { api.logout() }
            pref.clear()
        }
    }

    override suspend fun getRefreshToken(): String? {
        // 로그아웃 중이면 null 반환
        if (isLoggingOut) {
            return null
        }
        return pref.getRefreshToken()
    }

    override suspend fun refreshToken(): Result<Unit> = runCatching {
        // 로그아웃 중이면 토큰 갱신하지 않음
        if (isLoggingOut) {
            throw Exception("Logout in progress")
        }

        val refreshToken = pref.getRefreshToken()
            ?: throw Exception("No refresh token available")

        val res = api.refreshToken(
            RefreshTokenRequest(refreshToken)
        )

        // 토큰 저장 전에 다시 한번 로그아웃 상태 확인
        if (isLoggingOut) {
            throw Exception("Logout in progress")
        }

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