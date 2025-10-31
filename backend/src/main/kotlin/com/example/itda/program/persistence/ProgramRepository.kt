package com.example.itda.program.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<ProgramEntity, Long> {
    fun findTop10ByTitleContainingIgnoreCaseOrDetailsContainingIgnoreCaseAndIdLessThanOrderByIdDesc(
        title: String,
        details: String,
        id: Long,
    ): List<ProgramEntity>
}
