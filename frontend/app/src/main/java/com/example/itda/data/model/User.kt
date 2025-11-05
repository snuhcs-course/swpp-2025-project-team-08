package com.example.itda.data.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val birthDate: String?,
    val gender: String?,
    val address: String?,
    val postcode: String?,
    val maritalStatus: String?,
    val educationLevel: String?,
    val householdSize: Int?,
    val householdIncome: Int?,
    val employmentStatus: String?,
)
