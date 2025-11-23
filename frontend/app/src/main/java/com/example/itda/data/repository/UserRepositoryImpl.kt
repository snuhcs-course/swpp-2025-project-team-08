package com.example.itda.data.repository

import android.util.Log
import com.example.itda.data.model.ProfileRequest
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.User
import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.RetrofitInstance
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val pref: PrefDataSource
) : UserRepository {
    private val api = RetrofitInstance.authAPI
    private val gson = Gson()

    private companion object {
        const val KEY_USER_CACHE = "user_cache"  // 캐시용
        const val TAG = "UserRepository"
    }

    // 서버에서 사용자 정보 가져오기
    override suspend fun getMe(): User {
        return runCatching {
            Log.d(TAG, "getMe() - Fetching from server")
            api.getProfile()
        }.onSuccess { user ->
            // 로컬 캐시에 저장 (선택적)
            cacheUser(user)
            Log.d(TAG, "getMe() - Loaded from server: $user")
        }.onFailure { e ->
            Log.e(TAG, "getMe() - Server error, trying cache", e)
        }.getOrElse {
            // 서버 실패 시 캐시에서 가져오기
            getCachedUser() ?: User(
                id = "",
                email = "",
                name = null,
                birthDate = null,
                gender = null,
                address = null,
                postcode = null,
                maritalStatus = null,
                educationLevel = null,
                householdSize = null,
                householdIncome = null,
                employmentStatus = null,
                tags = null
            )
        }
    }

    // 서버로 프로필 업데이트
    override suspend fun updateProfile(request: ProfileUpdateRequest): Result<Unit> = runCatching {
        Log.d(TAG, "updateProfile() - Sending to server")
        Log.d(TAG, "  name: ${request.name}")
        Log.d(TAG, "  birthDate: ${request.birthDate}")
        Log.d(TAG, "  gender: ${request.gender}")
        Log.d(TAG, "  address: ${request.address}")
        Log.d(TAG, "  maritalStatus: ${request.maritalStatus}")
        Log.d(TAG, "  educationLevel: ${request.educationLevel}")
        Log.d(TAG, "  householdSize: ${request.householdSize}")
        Log.d(TAG, "  householdIncome: ${request.householdIncome}")
        Log.d(TAG, "  employmentStatus: ${request.employmentStatus}")

        // 서버로 전송
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

        Log.d(TAG, "updateProfile() - Server update successful")

        // 서버 업데이트 성공 후 최신 정보 다시 가져와서 캐시
        val updatedUser = getMe()
        cacheUser(updatedUser)
    }

    // 로컬 캐시에 저장 (오프라인 대비)
    private suspend fun cacheUser(user: User) {
        runCatching {
            val userJson = gson.toJson(user)
            pref.saveUserCache(userJson)  // PrefDataSource에 추가 필요
            Log.d(TAG, "cacheUser() - Cached: $userJson")
        }.onFailure { e ->
            Log.e(TAG, "cacheUser() - Failed", e)
        }
    }

    // 캐시에서 가져오기
    private suspend fun getCachedUser(): User? {
        return runCatching {
            val userJson = pref.getUserCache()  // PrefDataSource에 추가 필요
            if (userJson != null) {
                gson.fromJson(userJson, User::class.java)
            } else {
                null
            }
        }.onFailure { e ->
            Log.e(TAG, "getCachedUser() - Failed", e)
        }.getOrNull()
    }

    // 로그아웃 시 캐시 제거
    override suspend fun clearUser() {
        Log.d(TAG, "clearUser() - Clearing cache")
        pref.clearUserCache()  // PrefDataSource에 추가 필요
    }

    // 하위 호환성 유지 (필요시)
    override suspend fun getUser(userId: Int): User {
        return getMe()
    }

    override suspend fun getUserList(): List<User> {
        val user = getMe()
        return if (user.id.isNotEmpty()) listOf(user) else emptyList()
    }

}