package com.example.itda.data.source.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore

import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")


class PrefDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 가벼운 자잘히 필요할 키 값들을 저장해둘 용도.
    // 토큰, 마지막 로그인 시간등
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_TYPE = stringPreferencesKey("token_type")
        val EXPIRES_IN = intPreferencesKey("expires_in")
    }

    // 토큰 Flow
    val accessTokenFlow: Flow<String?> = context.dataStore.data.map {
        it[Keys.ACCESS_TOKEN]
    }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map {
        it[Keys.REFRESH_TOKEN]
    }

    // 토큰 저장
    suspend fun saveTokens(
        access: String,
        refresh: String?,
        type: String?,
        expires: Int?
    ) {
        try {
            context.dataStore.edit { prefs ->
                prefs[Keys.ACCESS_TOKEN] = access.trim()
                if (refresh != null) {
                    prefs[Keys.REFRESH_TOKEN] = refresh.trim()
                } else {
                    prefs.remove(Keys.REFRESH_TOKEN)
                }
                if (type != null) {
                    prefs[Keys.TOKEN_TYPE] = type
                } else {
                    prefs.remove(Keys.TOKEN_TYPE)
                }
                if (expires != null) {
                    prefs[Keys.EXPIRES_IN] = expires
                } else {
                    prefs.remove(Keys.EXPIRES_IN)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // 로그인 여부 확인
    fun isLoggedIn(): Flow<Boolean> = accessTokenFlow.map {
        !it.isNullOrBlank()
    }

    // 토큰 가져오기
    suspend fun getAccessToken(): String? {
        val token = accessTokenFlow.firstOrNull()
        return token
    }

    suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()

    // 전체 삭제 (로그아웃)
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}