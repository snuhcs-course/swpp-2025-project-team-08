package com.example.itda.user.controller

import com.example.itda.user.persistence.UserEntity

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val age: Int,
    val gender: String,
    val address: String,
    val maritalStatus: String,
    val educationLevel: String,
    val householdSize: Int,
    val householdIncome: Int,
) {
    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User(
                id = entity.id!!,
                email = entity.email,
                name = entity.name,
                age = entity.age,
                gender = entity.gender,
                address = entity.address,
                maritalStatus = entity.maritalStatus,
                educationLevel = entity.educationLevel,
                householdSize = entity.householdSize,
                householdIncome = entity.householdIncome,
            )
        }
    }
}
