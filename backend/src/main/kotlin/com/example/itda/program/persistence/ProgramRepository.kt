package com.example.itda.program.persistence

import com.example.itda.program.persistence.enums.ProgramCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProgramRepository : JpaRepository<ProgramEntity, Long> {
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
