package com.example.itda.data.repository

import com.example.itda.data.model.AuthRequest
import com.example.itda.data.model.AuthResponse
import com.example.itda.data.model.PreferenceRequest
import com.example.itda.data.model.ProfileRequest
import com.example.itda.data.model.ProfileUpdateRequest
import com.example.itda.data.model.RefreshTokenRequest
import com.example.itda.data.model.User
import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.AuthAPI
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var pref: PrefDataSource

    @Mock
    private lateinit var api: AuthAPI

    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        repository = AuthRepositoryImpl(pref, api)
    }

    // ========== Login Tests ==========

    @Test
    fun login_success_savesTokens() = runTest {
        val email = "test@test.com"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            tokenType = "Bearer",
            expiresIn = 3600
        )

        Mockito.`when`(api.login(AuthRequest(email, password))).thenReturn(authResponse)

        val result = repository.login(email, password)

        assertThat(result.isSuccess).isTrue()
        verify(pref).saveTokens(
            access = "access-token",
            refresh = "refresh-token",
            type = "Bearer",
            expires = 3600
        )
    }

    @Test
    fun login_failure_returnsFailure() = runTest {
        val email = "test@test.com"
        val password = "wrong"
        val errorResponse = Response.error<AuthResponse>(
            401,
            "Unauthorized".toResponseBody()
        )

        Mockito.`when`(api.login(AuthRequest(email, password)))
            .thenThrow(HttpException(errorResponse))

        val result = repository.login(email, password)

        assertThat(result.isFailure).isTrue()
        verify(pref, never()).saveTokens(any(), any(), any(), any())
    }

    // ========== Signup Tests ==========

    @Test
    fun signup_success_savesTokens() = runTest {
        val email = "new@test.com"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            tokenType = "Bearer",
            expiresIn = 3600
        )

        Mockito.`when`(api.signup(AuthRequest(email, password))).thenReturn(authResponse)

        val result = repository.signup(email, password)

        assertThat(result.isSuccess).isTrue()
        verify(pref).saveTokens(
            access = "access-token",
            refresh = "refresh-token",
            type = "Bearer",
            expires = 3600
        )
    }

    @Test
    fun signup_failure_returnsFailure() = runTest {
        val email = "duplicate@test.com"
        val password = "password123"
        val errorResponse = Response.error<AuthResponse>(
            409,
            "Conflict".toResponseBody()
        )

        Mockito.`when`(api.signup(AuthRequest(email, password)))
            .thenThrow(HttpException(errorResponse))

        val result = repository.signup(email, password)

        assertThat(result.isFailure).isTrue()
        verify(pref, never()).saveTokens(any(), any(), any(), any())
    }

    // ========== Logout Tests ==========

    @Test
    fun logout_success_clearsPreferences() = runTest {
        Mockito.`when`(api.logout()).then { }

        val result = repository.logout()

        assertThat(result.isSuccess).isTrue()
        verify(api).logout()
        verify(pref).clear()
    }

    @Test
    fun logout_apiFailure_stillClearsPreferences() = runTest {
        Mockito.`when`(api.logout()).thenThrow(RuntimeException("Network error"))

        val result = repository.logout()

        assertThat(result.isSuccess).isTrue()
        verify(pref).clear()
    }

    // ========== Refresh Token Tests ==========

    @Test
    fun getRefreshToken_returnsToken() = runTest {
        Mockito.`when`(pref.getRefreshToken()).thenReturn("refresh-token")

        val result = repository.getRefreshToken()

        assertThat(result).isEqualTo("refresh-token")
    }

    @Test
    fun getRefreshToken_returnsNull_whenNoToken() = runTest {
        Mockito.`when`(pref.getRefreshToken()).thenReturn(null)

        val result = repository.getRefreshToken()

        assertThat(result).isNull()
    }

    @Test
    fun refreshToken_success_savesNewTokens() = runTest {
        val refreshToken = "old-refresh-token"
        val authResponse = AuthResponse(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token",
            tokenType = "Bearer",
            expiresIn = 3600
        )

        Mockito.`when`(pref.getRefreshToken()).thenReturn(refreshToken)
        Mockito.`when`(api.refreshToken(RefreshTokenRequest(refreshToken)))
            .thenReturn(authResponse)

        val result = repository.refreshToken()

        assertThat(result.isSuccess).isTrue()
        verify(pref).saveTokens(
            access = "new-access-token",
            refresh = "new-refresh-token",
            type = "Bearer",
            expires = 3600
        )
    }

    @Test
    fun refreshToken_noToken_returnsFailure() = runTest {
        Mockito.`when`(pref.getRefreshToken()).thenReturn(null)

        val result = repository.refreshToken()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("No refresh token available")
        verify(api, never()).refreshToken(any())
    }

    @Test
    fun refreshToken_apiFailure_returnsFailure() = runTest {
        val refreshToken = "invalid-token"
        val errorResponse = Response.error<AuthResponse>(
            401,
            "Unauthorized".toResponseBody()
        )

        Mockito.`when`(pref.getRefreshToken()).thenReturn(refreshToken)
        Mockito.`when`(api.refreshToken(RefreshTokenRequest(refreshToken)))
            .thenThrow(HttpException(errorResponse))

        val result = repository.refreshToken()

        assertThat(result.isFailure).isTrue()
        verify(pref, never()).saveTokens(any(), any(), any(), any())
    }

    // ========== Get Profile Tests ==========

    @Test
    fun getProfile_success_returnsUser() = runTest {
        val user = User(
            id = "user-123",
            email = "test@test.com",
            name = "Test User",
            birthDate = "1990-01-01",
            gender = "MALE",
            address = "Seoul",
            postcode = "12345",
            maritalStatus = "SINGLE",
            educationLevel = "BACHELORS",
            householdSize = 1,
            householdIncome = 3000,
            employmentStatus = "EMPLOYED",
            tags = listOf("tag1", "tag2")
        )

        Mockito.`when`(api.getProfile()).thenReturn(user)

        val result = repository.getProfile()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(user)
    }

    @Test
    fun getProfile_failure_returnsFailure() = runTest {
        val errorResponse = Response.error<User>(
            401,
            "Unauthorized".toResponseBody()
        )

        Mockito.`when`(api.getProfile()).thenThrow(HttpException(errorResponse))

        val result = repository.getProfile()

        assertThat(result.isFailure).isTrue()
    }

    // ========== Update Profile Tests ==========

    @Test
    fun updateProfile_success() = runTest {
        val updateRequest = ProfileUpdateRequest.Builder()
            .name("Updated Name")
            .birthDate("19900101")
            .gender("MALE")
            .address("Seoul")
            .postcode("12345")
            .maritalStatus("SINGLE")
            .educationLevel("BACHELORS")
            .householdSize(1)
            .householdIncome(3000)
            .employmentStatus("EMPLOYED")
            .tags(listOf("tag1"))
            .build()
            .getOrThrow()

        val expectedApiRequest = ProfileRequest(
            name = "Updated Name",
            birthDate = "1990-01-01",
            gender = "MALE",
            address = "Seoul",
            postcode = "12345",
            maritalStatus = "SINGLE",
            educationLevel = "BACHELORS",
            householdSize = 1,
            householdIncome = 3000,
            employmentStatus = "EMPLOYED",
            tags = listOf("tag1")
        )

        Mockito.`when`(api.updateProfile(expectedApiRequest)).then { }

        val result = repository.updateProfile(updateRequest)

        assertThat(result.isSuccess).isTrue()
        verify(api).updateProfile(expectedApiRequest)
    }

    @Test
    fun updateProfile_failure_returnsFailure() = runTest {
        val updateRequest = ProfileUpdateRequest.Builder()
            .name("Updated Name")
            .birthDate("19900101")
            .gender("MALE")
            .address("Seoul")
            .postcode("12345")
            .maritalStatus("SINGLE")
            .educationLevel("BACHELORS")
            .householdSize(1)
            .householdIncome(3000)
            .employmentStatus("EMPLOYED")
            .tags(listOf("tag1"))
            .build()
            .getOrThrow()

        Mockito.`when`(api.updateProfile(any()))
            .thenThrow(RuntimeException("Network error"))

        val result = repository.updateProfile(updateRequest)

        assertThat(result.isFailure).isTrue()
    }

    // ========== Update Preference Tests ==========

    @Test
    fun updatePreference_success() = runTest {
        val preferenceList = emptyList<PreferenceRequest>()

        Mockito.`when`(api.updatePreferences(preferenceList)).then { }

        val result = repository.updatePreference(preferenceList)

        assertThat(result.isSuccess).isTrue()
        verify(api).updatePreferences(preferenceList)
    }

    @Test
    fun updatePreference_failure_returnsFailure() = runTest {
        val preferenceList = emptyList<PreferenceRequest>()

        Mockito.`when`(api.updatePreferences(preferenceList))
            .thenThrow(RuntimeException("Network error"))

        val result = repository.updatePreference(preferenceList)

        assertThat(result.isFailure).isTrue()
    }

    // ========== Email Persistence Tests ==========

    @Test
    fun saveEmail_callsPrefDataSource() = runTest {
        val email = "test@test.com"

        repository.saveEmail(email)

        verify(pref).saveEmail(email)
    }

    @Test
    fun getSavedEmail_returnsEmail() = runTest {
        Mockito.`when`(pref.getSavedEmail()).thenReturn("saved@test.com")

        val result = repository.getSavedEmail()

        assertThat(result).isEqualTo("saved@test.com")
    }

    @Test
    fun getSavedEmail_returnsNull_whenNoEmail() = runTest {
        Mockito.`when`(pref.getSavedEmail()).thenReturn(null)

        val result = repository.getSavedEmail()

        assertThat(result).isNull()
    }

    @Test
    fun clearSavedEmail_callsPrefDataSource() = runTest {
        repository.clearSavedEmail()

        verify(pref).clearSavedEmail()
    }
}