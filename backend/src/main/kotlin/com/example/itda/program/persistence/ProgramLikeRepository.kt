package com.example.itda.program.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProgramLikeRepository : JpaRepository<ProgramLikeEntity, Long> {
    @Query(
        """
        SELECT p.embedding 
        FROM ProgramLikeEntity pl 
        JOIN pl.program p 
        WHERE pl.user.id = :userId 
        AND pl.isLike = :isLike 
        ORDER BY pl.createdAt DESC
    """,
    )
    fun findRecentEmbeddingsByUserIdAndLikeStatus(
        userId: String,
        isLike: Boolean,
        pageable: Pageable,
    ): List<FloatArray>

    @Query(
        """
        SELECT pl
        FROM ProgramLikeEntity pl
        WHERE pl.user.id = :userId
        AND pl.program.id IN :programIds""",
    )
    fun findByUserIdAndProgramIdIn(
        userId: String,
        programIds: List<Long>,
    ): List<ProgramLikeEntity>

    fun findByUserIdAndProgramId(
        userId: String,
        programId: Long,
    ): ProgramLikeEntity?
}
