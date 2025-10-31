package com.example.itda.program.service

import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProgramService(
    val programRepository: ProgramRepository,
) {
    @Transactional
    fun getPrograms(): List<ProgramSummaryResponse> {
        val programs = programRepository.findAll()

        return programs.map { ProgramSummaryResponse.fromEntity(it) }
    }

    @Transactional
    fun getProgram(id: Long): ProgramResponse {
        val program =
            programRepository.findByIdOrNull(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found: $id")

        return ProgramResponse.fromEntity(program)
    }

    fun searchPrograms(
        text: String,
        articleId: Long,
    ): List<ProgramResponse> {
        val programEntities: List<ProgramEntity> =
            programRepository.findTop10ByTitleContainingIgnoreCaseOrDetailsContainingIgnoreCaseAndIdLessThanOrderByIdDesc(
                text,
                text,
                articleId,
            )
        return programEntities.map { ProgramResponse.fromEntity(it) }
    }
}
