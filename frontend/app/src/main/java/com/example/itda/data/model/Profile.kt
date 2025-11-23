package com.example.itda.data.model

data class ProfileRequest(
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
    val tags: List<String>?
)

typealias PreferenceRequestList = List<PreferenceRequest>

data class PreferenceRequest(
    val id : Int,
    val score : Int
)