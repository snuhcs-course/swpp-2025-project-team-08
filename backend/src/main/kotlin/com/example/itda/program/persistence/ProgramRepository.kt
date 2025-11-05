package com.example.itda.program.persistence

import com.example.itda.program.persistence.enums.ProgramCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProgramRepository : JpaRepository<ProgramEntity, Long> {
    @Query(
        // TODO: Address
        value = """
        SELECT p.*
        FROM program p
        JOIN "user" u ON u.id = :userId
        WHERE (:category IS NULL OR p.category = :category)
        AND (p.eligibility_gender IS NULL OR p.eligibility_gender = u.gender)
        AND (p.eligibility_marital_status is NULL OR p.eligibility_marital_status = u.marital_status)
        AND (p.eligibility_education is NULL OR p.eligibility_education = u.education_level)
        AND (p.eligibility_min_household is NULL OR p.eligibility_min_household <= u.household_size)
        AND (p.eligibility_max_household is NULL OR p.eligibility_max_household >= u.household_size)
        AND (p.eligibility_min_income is NULL OR p.eligibility_min_income <= u.household_income)
        AND (p.eligibility_max_income is NULL OR p.eligibility_max_income >= u.household_income)
        AND (p.eligibility_employment is NULL OR p.eligibility_employment = u.employment_status)
        AND (p.eligibility_min_age IS NULL OR u.birth_date IS NULL OR p.eligibility_min_age <= DATE_PART('year', AGE(CURRENT_DATE, u.birth_date)))
        AND (p.eligibility_max_age IS NULL OR u.birth_date IS NULL OR p.eligibility_max_age >= DATE_PART('year', AGE(CURRENT_DATE, u.birth_date)))
        ORDER BY
          CASE WHEN u.preference_embedding IS NULL THEN p.created_at END DESC,
          CASE WHEN u.preference_embedding IS NOT NULL THEN p.embedding <-> u.preference_embedding END ASC
        """,
        nativeQuery = true,
        countQuery = """
        SELECT count(*)
        FROM program p
        JOIN "user" u ON u.id = :userId
        WHERE (:category IS NULL OR p.category = :category)
        AND (p.eligibility_gender IS NULL OR p.eligibility_gender = u.gender)
        AND (p.eligibility_marital_status is NULL OR p.eligibility_marital_status = u.marital_status)
        AND (p.eligibility_education is NULL OR p.eligibility_education = u.education_level)
        AND (p.eligibility_min_household is NULL OR p.eligibility_min_household <= u.household_size)
        AND (p.eligibility_max_household is NULL OR p.eligibility_max_household >= u.household_size)
        AND (p.eligibility_min_income is NULL OR p.eligibility_min_income <= u.household_income)
        AND (p.eligibility_max_income is NULL OR p.eligibility_max_income >= u.household_income)
        AND (p.eligibility_employment is NULL OR p.eligibility_employment = u.employment_status)
        AND (p.eligibility_min_age IS NULL OR u.birth_date IS NULL OR p.eligibility_min_age <= DATE_PART('year', AGE(CURRENT_DATE, u.birth_date)))
        AND (p.eligibility_max_age IS NULL OR u.birth_date IS NULL OR p.eligibility_max_age >= DATE_PART('year', AGE(CURRENT_DATE, u.birth_date)))
        """,
    )
    fun findAllByPreference(
        userId: String,
        category: String?,
        pageable: Pageable,
    ): Page<ProgramEntity>

    @Query(
        """
        SELECT p FROM ProgramEntity p
        WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))) 
           OR (LOWER(p.preview) LIKE LOWER(CONCAT('%', :query, '%')))
           OR (LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')))
           OR (:category IS NOT NULL AND p.category = :category)
           
        ORDER BY p.createdAt DESC
    """,
    )
    fun searchWithCategoryFilter(
        @Param("query") query: String,
        @Param("category") category: ProgramCategory?,
        pageable: Pageable,
    ): Page<ProgramEntity>

    @Query(
        """
        SELECT p
        FROM ProgramEntity p
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(p.preview) LIKE LOWER(CONCAT('%', :query, '%'))
           OR (:categoryEnum IS NOT NULL AND p.category = :categoryEnum) 
           OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.details) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY 
            (CASE WHEN LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) THEN 5 ELSE 0 END) +
            (CASE WHEN LOWER(p.preview) LIKE LOWER(CONCAT('%', :query, '%')) THEN 4 ELSE 0 END) +
            (CASE WHEN p.category = :categoryEnum THEN 3 ELSE 0 END) +
            (CASE WHEN LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')) THEN 2 ELSE 0 END) +
            (CASE WHEN LOWER(p.details) LIKE LOWER(CONCAT('%', :query, '%')) THEN 1 ELSE 0 END) DESC,
            p.createdAt DESC
    """,
    )
    fun searchAndRankPrograms(
        @Param("query") query: String,
        @Param("categoryEnum") categoryEnum: ProgramCategory?,
        pageable: Pageable,
    ): Page<ProgramEntity>
}
