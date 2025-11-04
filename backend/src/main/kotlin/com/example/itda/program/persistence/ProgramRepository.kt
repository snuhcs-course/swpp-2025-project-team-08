package com.example.itda.program.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<ProgramEntity, Long> {
    fun findByTitleContainingIgnoreCaseOrPreviewContainingIgnoreCase(
        title: String,
        preview: String,
        pageable: Pageable,
    ): Page<ProgramEntity>
}
