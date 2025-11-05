package com.example.itda.program.service

import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.program.persistence.enums.ProgramCategory
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

        val categoryEnum: ProgramCategory? = mapSearchTermToCategory(searchTerm)

        val programEntitiesPage: Page<ProgramEntity> =
            programRepository.searchWithCategoryFilter(
                query = searchTerm,
                category = categoryEnum,
                pageable = pageable,
            )

        return programEntitiesPage.map { entity ->
            ProgramSummaryResponse.fromEntity(entity)
        }
    }

    private fun mapSearchTermToCategory(searchTerm: String): ProgramCategory? {
        val matchedByName =
            ProgramCategory.entries
                .find { it.name.equals(searchTerm, ignoreCase = true) }
        if (matchedByName != null) return matchedByName

        return ProgramCategory.entries
            .find { it.dbValue.contains(searchTerm) }
    }
}
