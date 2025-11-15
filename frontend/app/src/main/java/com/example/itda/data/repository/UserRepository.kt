package com.example.itda.data.repository

interface UserRepository {
    suspend fun getMe(): com.example.itda.data.model.User
    suspend fun updateProfile(
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
    ): Result<Unit>
    suspend fun clearUser()
    suspend fun getUser(userId: Int): com.example.itda.data.model.User
    suspend fun getUserList(): List<com.example.itda.data.model.User>
}