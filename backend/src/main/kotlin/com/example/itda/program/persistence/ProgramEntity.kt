package com.example.itda.program.persistence

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
import java.time.OffsetDateTime

@Entity
@Table(name = "program")
class ProgramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "uuid", nullable = false, unique = true, length = 255)
    lateinit var uuid: String

    @Column(name = "title", nullable = false, length = 255)
    lateinit var title: String

    @Column(name = "details", columnDefinition = "TEXT")
    var details: String? = null

    @Column(name = "application_method", columnDefinition = "TEXT")
    var applicationMethod: String? = null

    @Column(name = "reference_url", columnDefinition = "TEXT")
    var referenceUrl: String? = null

    @Column(name = "eligibility_gender", length = 10)
    var eligibilityGender: Gender? = null

    @Column(name = "eligibility_min_age")
    var eligibilityMinAge: Int? = null

    @Column(name = "eligibility_max_age")
    var eligibilityMaxAge: Int? = null

    @Column(name = "eligibility_region", length = 100)
    var eligibilityRegion: String? = null

    @Column(name = "eligibility_marital_status", length = 10)
    var eligibilityMaritalStatus: MaritalStatus? = null

    @Column(name = "eligibility_education", length = 50)
    var eligibilityEducation: EducationLevel? = null

    @Column(name = "eligibility_min_household")
    var eligibilityMinHousehold: Int? = null

    @Column(name = "eligibility_max_household")
    var eligibilityMaxHousehold: Int? = null

    @Column(name = "eligibility_min_income")
    var eligibilityMinIncome: Long? = null

    @Column(name = "eligibility_max_income")
    var eligibilityMaxIncome: Long? = null

    @Column(name = "eligibility_employment", length = 20)
    var eligibilityEmployment: EmploymentStatus? = null

    @Column(name = "apply_start_at")
    var applyStartAt: OffsetDateTime? = null

    @Column(name = "apply_end_at")
    var applyEndAt: OffsetDateTime? = null

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: OffsetDateTime? = null

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: OffsetDateTime? = null
}
