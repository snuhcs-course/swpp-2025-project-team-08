package com.example.itda.user.persistence

import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

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
    @Column(name = "birth_date", nullable = true)
    var birthDate: LocalDate? = null,
    @Column(nullable = true)
    var gender: Gender? = null,
    @Column(nullable = true)
    var address: String? = null,
    @Column(name = "marital_status", nullable = true)
    var maritalStatus: MaritalStatus? = null,
    @Column(name = "education_level", nullable = true)
    var educationLevel: EducationLevel? = null,
    @Column(name = "household_size", nullable = true)
    var householdSize: Int? = null,
    @Column(name = "household_income", nullable = true)
    var householdIncome: Int? = null,
    @Column(name = "employment_status", nullable = true)
    var employmentStatus: EmploymentStatus? = null,
)
