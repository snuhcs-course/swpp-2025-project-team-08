package com.example.itda.user.controller

import com.example.itda.user.persistence.UserEntity
import java.time.format.DateTimeFormatter

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val birthDate: String?,
    val gender: String?,
    val address: String?,
    val maritalStatus: String?,
    val educationLevel: String?,
    val householdSize: Int?,
    val householdIncome: Int?,
    val employmentStatus: String?,
) {
    companion object {
        fun fromEntity(entity: UserEntity): User {
            return User(
                id = entity.id!!,
                email = entity.email,
                name = entity.name,
                birthDate = entity.birthDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
                address = entity.address,
                householdSize = entity.householdSize,
                householdIncome = entity.householdIncome,
                gender = entity.gender?.value,
                maritalStatus = entity.maritalStatus?.value,
                educationLevel = entity.educationLevel?.value,
                employmentStatus = entity.employmentStatus?.value,
            )
        }
    }
}
