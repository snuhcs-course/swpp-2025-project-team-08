package com.example.itda.program.persistence

import com.example.itda.user.persistence.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookmarkRepository : JpaRepository<BookmarkEntity, Long> {
    @Query(
        """
        SELECT b 
        FROM BookmarkEntity b 
        JOIN FETCH b.program p 
        WHERE b.user = :user
        """,
        countQuery = "SELECT COUNT(b) FROM BookmarkEntity b WHERE b.user = :user",
    )
    fun findByUserWithProgram(
        @Param("user") user: UserEntity,
        pageable: Pageable,
    ): Page<BookmarkEntity>

    @Query(
        """
        SELECT b 
        FROM BookmarkEntity b 
        JOIN FETCH b.program p 
        WHERE b.user = :user
        ORDER BY 
            p.applyEndAt ASC NULLS LAST, 
            b.createdAt DESC
        """,
        countQuery = "SELECT COUNT(b) FROM BookmarkEntity b WHERE b.user = :user",
    )
    fun findByUserWithProgramOrderByDeadline(
        @Param("user") user: UserEntity,
        pageable: Pageable,
    ): Page<BookmarkEntity>

    @Query(
        """
        SELECT b.program.id
        FROM BookmarkEntity b
        WHERE b.user.id = :userId
        AND b.program.id IN :programIds""",
    )
    fun findBookmarkedProgramIds(
        userId: String,
        programIds: List<Long>,
    ): List<Long>

    @Query(
        """
        SELECT p.embedding 
        FROM BookmarkEntity b 
        JOIN b.program p 
        WHERE b.user.id = :userId 
        ORDER BY b.createdAt DESC
    """,
    )
    fun findRecentEmbeddingsByUserId(
        userId: String,
        pageable: Pageable,
    ): List<FloatArray>

    fun existsByUserIdAndProgramId(
        userId: String,
        programId: Long,
    ): Boolean

    fun findByUserIdAndProgramId(
        userId: String,
        programId: Long,
    ): BookmarkEntity?
}
