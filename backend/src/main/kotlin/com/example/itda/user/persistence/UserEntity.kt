package com.example.itda.user.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var password: String,
    var name: String,
    var age: Int,
    var gender: String,
    var address: String,
    @Column(name = "marital_status")
    var maritalStatus: String,
    @Column(name = "education_level")
    var educationLevel: String,
    @Column(name = "household_size")
    var householdSize: Int,
    @Column(name = "household_income")
    var householdIncome: Int,
)
