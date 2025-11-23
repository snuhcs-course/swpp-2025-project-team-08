package com.example.itda.data.repository

import com.example.itda.data.model.PreferenceRequestList
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {

    var loginResult: Result<Unit> = Result.success(Unit)
    var signupResult: Result<Unit> = Result.success(Unit)
    var logoutResult: Result<Unit> = Result.success(Unit)
    var getProfileResult: Result<User> = Result.success(
        User(
            id = "1",
            email = "test@example.com",
            name = "테스트",
            birthDate = "2000-01-01",
            gender = "FEMALE",
            address = "서울시",
            postcode = "12345",
            maritalStatus = "",
            educationLevel = "",
            householdSize = 0,
            householdIncome = 0,
            employmentStatus = "",
            tags = listOf("태그1", "태그2")
        )
    )
    var updateProfileResult: Result<Unit> = Result.success(Unit)
    var updatePreferenceResult: Result<Unit> = Result.success(Unit)

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedInFlow: Flow<Boolean> = _isLoggedIn

    private var savedEmail: String? = null

    var loginCalled = false
    var signupCalled = false
    var logoutCalled = false
    var getProfileCalled = false
    var updateProfileCalled = false
    var updatePreferenceCalled = false
    var saveEmailCalled = false
    var getSavedEmailCalled = false
    var clearSavedEmailCalled = false

    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null
    var lastSignupEmail: String? = null
    var lastSignupPassword: String? = null
    var lastUpdateProfileRequest: ProfileUpdateRequest? = null
    var lastPreferenceScores: PreferenceRequestList? = null

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

    override suspend fun getProfile(): Result<User> {
        getProfileCalled = true
        return getProfileResult
    }

    override suspend fun updateProfile(request: ProfileUpdateRequest): Result<Unit> {
        updateProfileCalled = true
        lastUpdateProfileRequest = request
        return updateProfileResult
    }

    override suspend fun updatePreference(
        satisfactionScores: PreferenceRequestList
    ): Result<Unit> {
        updatePreferenceCalled = true
        lastPreferenceScores = satisfactionScores
        return updatePreferenceResult
    }

    override suspend fun saveEmail(email: String) {
        saveEmailCalled = true
        savedEmail = email
    }

    override suspend fun getSavedEmail(): String? {
        getSavedEmailCalled = true
        return savedEmail
    }

    override suspend fun clearSavedEmail() {
        clearSavedEmailCalled = true
        savedEmail = null
    }

    fun reset() {
        loginResult = Result.success(Unit)
        signupResult = Result.success(Unit)
        logoutResult = Result.success(Unit)
        updateProfileResult = Result.success(Unit)
        updatePreferenceResult = Result.success(Unit)
        _isLoggedIn.value = false
        savedEmail = null

        loginCalled = false
        signupCalled = false
        logoutCalled = false
        getProfileCalled = false
        updateProfileCalled = false
        updatePreferenceCalled = false
        saveEmailCalled = false
        getSavedEmailCalled = false
        clearSavedEmailCalled = false

        lastLoginEmail = null
        lastLoginPassword = null
        lastSignupEmail = null
        lastSignupPassword = null
        lastUpdateProfileRequest = null
        lastPreferenceScores = null
    }
}