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
}
