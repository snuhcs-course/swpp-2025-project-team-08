package com.example.itda.program.service

import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @Transactional
    fun searchLatestPrograms(
        searchTerm: String,
        page: Int,
        pageSize: Int,
    ): Page<ProgramSummaryResponse> {
        val pageable =
            PageRequest.of(
                page,
                pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"),
            )

        val programEntitiesPage: Page<ProgramEntity> =
            programRepository.findByTitleContainingIgnoreCaseOrPreviewContainingIgnoreCase(
                title = searchTerm,
                preview = searchTerm,
                pageable = pageable,
            )

        return programEntitiesPage.map { entity ->
            ProgramSummaryResponse.fromEntity(entity)
        }
    }
}
