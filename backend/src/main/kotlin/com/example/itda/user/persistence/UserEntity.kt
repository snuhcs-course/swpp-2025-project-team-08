package com.example.itda.user.persistence

import com.example.itda.program.config.AppConstants
import com.example.itda.program.persistence.BookmarkEntity
import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Array
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var gender: Gender? = null,
    @Column(nullable = true)
    var address: String? = null,
    @Column(nullable = true)
    var postcode: String? = null,
    @Column(name = "household_size", nullable = true)
    var householdSize: Int? = null,
    @Column(name = "household_income", nullable = true)
    var householdIncome: Int? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", nullable = true)
    var educationLevel: EducationLevel? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = true)
    var maritalStatus: MaritalStatus? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = true)
    var employmentStatus: EmploymentStatus? = null,
    @Column(name = "embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var embedding: FloatArray? = null,
    @Column(name = "user_embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var userEmbedding: FloatArray? = null,
    @Column(name = "liked_embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var likesEmbedding: FloatArray? = null,
    @Column(name = "bookmarked_embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var bookmarksEmbedding: FloatArray? = null,
    @Column(name = "seeless_embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = AppConstants.EMBEDDING_DIMENSION)
    var seeLessEmbedding: FloatArray? = null,
    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var tags: MutableList<TagEntity> = mutableListOf(),
    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var bookmarks: MutableList<BookmarkEntity> = mutableListOf(),
)
