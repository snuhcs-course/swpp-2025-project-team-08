package com.example.itda.program.persistence

import com.example.itda.program.config.AppConstants
import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import com.example.itda.program.persistence.enums.OperatingEntityType
import com.example.itda.program.persistence.enums.ProgramCategory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Array
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "program")
class ProgramEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    @Column(name = "uuid", nullable = false, unique = true, length = 255)
    var uuid: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 10, nullable = false)
    var category: ProgramCategory,
    @Column(name = "title", nullable = false, length = 255)
    var title: String,
    @Column(name = "details", nullable = false, columnDefinition = "TEXT")
    var details: String,
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    var summary: String,
    @Column(name = "preview", nullable = false, columnDefinition = "TEXT")
    var preview: String,
    @Column(name = "application_method", columnDefinition = "TEXT")
    var applicationMethod: String? = null,
    @Column(name = "apply_url", columnDefinition = "TEXT")
    var applyUrl: String? = null,
    @Column(name = "reference_url", columnDefinition = "TEXT")
    var referenceUrl: String? = null,
    @Column(name = "eligibility_min_age")
    var eligibilityMinAge: Int? = null,
    @Column(name = "eligibility_max_age")
    var eligibilityMaxAge: Int? = null,
    @Column(name = "eligibility_region", columnDefinition = "TEXT")
    var eligibilityRegion: String? = null,
    @Column(name = "eligibility_min_household")
    var eligibilityMinHousehold: Int? = null,
    @Column(name = "eligibility_max_household")
    var eligibilityMaxHousehold: Int? = null,
    @Column(name = "eligibility_min_income")
    var eligibilityMinIncome: Int? = null,
    @Column(name = "eligibility_max_income")
    var eligibilityMaxIncome: Int? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_gender", length = 10)
    var eligibilityGender: Gender? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_marital_status", length = 20)
    var eligibilityMaritalStatus: MaritalStatus? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_education", length = 30)
    var eligibilityEducation: EducationLevel? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_employment", length = 20)
    var eligibilityEmployment: EmploymentStatus? = null,
    @Column(name = "apply_start_at")
    var applyStartAt: OffsetDateTime? = null,
    @Column(name = "apply_end_at")
    var applyEndAt: OffsetDateTime? = null,
    @Column(
        name = "created_at",
        insertable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP",
    )
    var createdAt: OffsetDateTime? = null,
    @Column(name = "operating_entity", nullable = false)
    var operatingEntity: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "operating_entity_type", nullable = false)
    var operatingEntityType: OperatingEntityType,
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var embedding: FloatArray,
    @OneToMany(mappedBy = "program")
    var bookmarks: MutableSet<BookmarkEntity> = mutableSetOf(),
)
