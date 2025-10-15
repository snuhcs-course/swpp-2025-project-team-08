package com.example.itda.data.source.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PrefDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 가벼운 자잘히 필요할 키 값들을 저장해둘 용도.
    // 토큰, 마지막 로그인 시간등

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    suspend fun saveLoginToken(token: String) {
        prefs.edit { putString("token", token) }
    }

    fun getLoginToken(): Flow<String?> = flow {
        emit(prefs.getString("token", null))
    }

    suspend fun clearLoginToken() {
        prefs.edit { remove("token") }
    }
}