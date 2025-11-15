package com.example.itda.data.repository

import android.util.Log
import com.example.itda.data.model.User
import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.ProfileRequest
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
        return try {
            Log.d(TAG, "getMe() - Fetching from server")
            val response = api.getProfile()

            // User 객체로 변환
            val user = User(
                id = response.id,
                email = response.email,
                name = response.name,
                birthDate = response.birthDate,
                gender = response.gender,
                address = response.address,
                postcode = response.postcode,
                maritalStatus = response.maritalStatus,
                educationLevel = response.educationLevel,
                householdSize = response.householdSize,
                householdIncome = response.householdIncome,
                employmentStatus = response.employmentStatus
            )

            // 로컬 캐시에 저장 (선택적)
            cacheUser(user)

            Log.d(TAG, "getMe() - Loaded from server: $user")
            user

        } catch (e: Exception) {
            Log.e(TAG, "getMe() - Server error, trying cache", e)
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
                employmentStatus = null
            )
        }
    }

    // 서버로 프로필 업데이트
    override suspend fun updateProfile(
        name: String?,
        birthDate: String?,
        gender: String?,
        address: String?,
        postcode: String?,
        maritalStatus: String?,
        educationLevel: String?,
        householdSize: Int?,
        householdIncome: Int?,
        employmentStatus: String?
    ): Result<Unit> = runCatching {
        Log.d(TAG, "updateProfile() - Sending to server")
        Log.d(TAG, "  name: $name")
        Log.d(TAG, "  birthDate: $birthDate")
        Log.d(TAG, "  gender: $gender")
        Log.d(TAG, "  address: $address")
        Log.d(TAG, "  maritalStatus: $maritalStatus")
        Log.d(TAG, "  educationLevel: $educationLevel")
        Log.d(TAG, "  householdSize: $householdSize")
        Log.d(TAG, "  householdIncome: $householdIncome")
        Log.d(TAG, "  employmentStatus: $employmentStatus")

        // 서버로 전송
        val request = ProfileRequest(
            name = name,
            birthDate = birthDate,
            gender = gender,
            address = address,
            postcode = postcode,
            maritalStatus = maritalStatus,
            educationLevel = educationLevel,
            householdSize = householdSize,
            householdIncome = householdIncome,
            employmentStatus = employmentStatus,
            tags = null
        )

        api.updateProfile(request)

        Log.d(TAG, "updateProfile() - Server update successful")

        // 서버 업데이트 성공 후 최신 정보 다시 가져와서 캐시
        val updatedUser = getMe()
        cacheUser(updatedUser)
    }

    // 로컬 캐시에 저장 (오프라인 대비)
    private suspend fun cacheUser(user: User) {
        try {
            val userJson = gson.toJson(user)
            pref.saveUserCache(userJson)  // PrefDataSource에 추가 필요
            Log.d(TAG, "cacheUser() - Cached: $userJson")
        } catch (e: Exception) {
            Log.e(TAG, "cacheUser() - Failed", e)
        }
    }

    // 캐시에서 가져오기
    private suspend fun getCachedUser(): User? {
        return try {
            val userJson = pref.getUserCache()  // PrefDataSource에 추가 필요
            if (userJson != null) {
                gson.fromJson(userJson, User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCachedUser() - Failed", e)
            null
        }
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