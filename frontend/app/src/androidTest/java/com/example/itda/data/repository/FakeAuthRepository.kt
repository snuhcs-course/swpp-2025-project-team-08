package com.example.itda.data.repository

import com.example.itda.data.source.remote.ProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {

    var loginResult: Result<Unit> = Result.success(Unit)
    var signupResult: Result<Unit> = Result.success(Unit)
    var logoutResult: Result<Unit> = Result.success(Unit)
    var getProfileResult: Result<ProfileResponse> = Result.success(
        ProfileResponse(
            id = "1",
            email = "test@example.com",
            name = "테스트",
            birthDate = "2000-01-01",
            gender = "여성",
            address = "서울시",
            maritalStatus = "",
            educationLevel = "",
            householdSize = 0,
            householdIncome = 0,
            employmentStatus = ""
        )
    )
    var updateProfileResult: Result<Unit> = Result.success(Unit)

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedInFlow: Flow<Boolean> = _isLoggedIn

    var loginCalled = false
    var signupCalled = false
    var logoutCalled = false
    var getProfileCalled = false
    var updateProfileCalled = false

    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null
    var lastSignupEmail: String? = null
    var lastSignupPassword: String? = null
    var lastUpdateProfileName: String? = null
    var lastUpdateProfileBirthDate: String? = null

    override fun isLoggedIn(): Flow<Boolean> = _isLoggedIn

    override suspend fun login(email: String, password: String): Result<Unit> {
        loginCalled = true
        lastLoginEmail = email
        lastLoginPassword = password

        if (loginResult.isSuccess) {
            _isLoggedIn.value = true
        }

        return loginResult
    }

    override suspend fun signup(email: String, password: String): Result<Unit> {
        signupCalled = true
        lastSignupEmail = email
        lastSignupPassword = password

        if (signupResult.isSuccess) {
            _isLoggedIn.value = true
        }

        return signupResult
    }

    override suspend fun logout(): Result<Unit> {
        logoutCalled = true

        if (logoutResult.isSuccess) {
            _isLoggedIn.value = false
        }

        return logoutResult
    }

    override suspend fun getProfile(): Result<ProfileResponse> {
        getProfileCalled = true
        return getProfileResult
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
    ): Result<Unit> {
        updateProfileCalled = true
        lastUpdateProfileName = name
        lastUpdateProfileBirthDate = birthDate
        return updateProfileResult
    }

    fun reset() {
        loginResult = Result.success(Unit)
        signupResult = Result.success(Unit)
        logoutResult = Result.success(Unit)
        updateProfileResult = Result.success(Unit)
        _isLoggedIn.value = false

        loginCalled = false
        signupCalled = false
        logoutCalled = false
        getProfileCalled = false
        updateProfileCalled = false

        lastLoginEmail = null
        lastLoginPassword = null
        lastSignupEmail = null
        lastSignupPassword = null
        lastUpdateProfileName = null
        lastUpdateProfileBirthDate = null
    }
}