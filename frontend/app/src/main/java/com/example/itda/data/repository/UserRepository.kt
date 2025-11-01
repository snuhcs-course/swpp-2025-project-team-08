package com.example.itda.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.itda.data.model.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private companion object {
        const val KEY_USER = "current_user"
        const val TAG = "UserRepository"
    }

    suspend fun getMe(): User {
        val userJson = prefs.getString(KEY_USER, null)
        Log.d(TAG, "getMe() - userJson: $userJson")

        return if (userJson != null) {
            val user = gson.fromJson(userJson, User::class.java)
            Log.d(TAG, "getMe() - Loaded user: $user")
            user
        } else {
            Log.d(TAG, "getMe() - No saved user, returning empty")
            User(
                id = "",
                email = "",
                name = null,
                birthDate = null,
                gender = null,
                address = null,
                maritalStatus = null,
                educationLevel = null,
                householdSize = null,
                householdIncome = null,
                employmentStatus = null
            )
        }
    }

    suspend fun updateProfile(
        name: String?,
        birthDate: String?,
        gender: String?,
        address: String?,
        maritalStatus: String?,
        educationLevel: String?,
        householdSize: Int?,
        householdIncome: Int?,
        employmentStatus: String?
    ): Result<Unit> = runCatching {
        Log.d(TAG, "updateProfile() called with:")
        Log.d(TAG, "  name: $name")
        Log.d(TAG, "  birthDate: $birthDate")
        Log.d(TAG, "  gender: $gender")
        Log.d(TAG, "  address: $address")
        Log.d(TAG, "  maritalStatus: $maritalStatus")
        Log.d(TAG, "  educationLevel: $educationLevel")
        Log.d(TAG, "  householdSize: $householdSize")
        Log.d(TAG, "  householdIncome: $householdIncome")
        Log.d(TAG, "  employmentStatus: $employmentStatus")

        val existing = getMe()
        Log.d(TAG, "  existing user: $existing")

        val updatedUser = existing.copy(
            name = name ?: existing.name,
            birthDate = birthDate ?: existing.birthDate,
            gender = gender ?: existing.gender,
            address = address ?: existing.address,
            maritalStatus = maritalStatus ?: existing.maritalStatus,
            educationLevel = educationLevel ?: existing.educationLevel,
            householdSize = householdSize ?: existing.householdSize,
            householdIncome = householdIncome ?: existing.householdIncome,
            employmentStatus = employmentStatus ?: existing.employmentStatus
        )

        Log.d(TAG, "  updatedUser: $updatedUser")

        val jsonToSave = gson.toJson(updatedUser)
        Log.d(TAG, "  jsonToSave: $jsonToSave")

        val success = prefs.edit()
            .putString(KEY_USER, jsonToSave)
            .commit()  // apply() 대신 commit() 사용하고 결과 확인

        Log.d(TAG, "  save success: $success")

        if (!success) {
            throw Exception("Failed to save to SharedPreferences")
        }

        // 저장 확인
        val savedJson = prefs.getString(KEY_USER, null)
        Log.d(TAG, "  verification - savedJson: $savedJson")

        Unit
    }

    suspend fun getUser(userId: Int): User {
        return getMe()
    }

    suspend fun getUserList(): List<User> {
        val user = getMe()
        return if (user.id.isNotEmpty()) listOf(user) else emptyList()
    }

    suspend fun clearUser() {
        Log.d(TAG, "clearUser() called")
        prefs.edit().remove(KEY_USER).apply()
    }
}