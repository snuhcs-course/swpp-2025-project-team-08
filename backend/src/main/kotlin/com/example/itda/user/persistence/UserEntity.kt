package com.example.itda.user.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "\"user\"")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = true)
    var name: String? = null,
    @Column(nullable = true)
    var age: Int? = null,
    @Column(nullable = true)
    var gender: String? = null,
    @Column(nullable = true)
    var address: String? = null,
    @Column(name = "marital_status", nullable = true)
    var maritalStatus: String? = null,
    @Column(name = "education_level", nullable = true)
    var educationLevel: String? = null,
    @Column(name = "household_size", nullable = true)
    var householdSize: Int? = null,
    @Column(name = "household_income", nullable = true)
    var householdIncome: Int? = null,
)
