package com.example.itda.data.repository

import android.content.Context
import com.example.itda.data.source.local.PrefDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefDataSource = PrefDataSource(context)

    suspend fun login(email: String, password: String): Result<Boolean> {
        // TODO: 실제 API 연동 추가 - 아래는 임시 login 로직. 작성만 해두면 로그인됨.
        return if (email.isNotBlank() && password.isNotBlank()) {
            prefDataSource.saveLoginToken("dummy_token_123")
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    fun isLoggedIn(): Flow<Boolean> =
        prefDataSource.getLoginToken().map { it != null }

    suspend fun logout() {
        // TODO: 실제 API 연동 추가 - 아래는 임시 logout 로직. 작성만 해두면 로그인됨.
        prefDataSource.clearLoginToken()
    }

    fun savePersonalInfo() {

    }
}