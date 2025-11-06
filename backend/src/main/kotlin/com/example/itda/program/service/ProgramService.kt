package com.example.itda.program.service

import com.example.itda.program.controller.ProgramCategoryResponse
import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramExampleRepository
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.program.persistence.enums.ProgramCategory
import com.example.itda.user.controller.User
import com.example.itda.utils.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProgramService(
    val programRepository: ProgramRepository,
    val programExampleRepository: ProgramExampleRepository,
) {
    @Transactional(readOnly = true)
    fun getPrograms(
        user: User,
        category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val programs = programRepository.findAllByPreference(user.id, category?.toString(), pageable)

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
    }

    @Transactional(readOnly = true)
    fun getProgram(id: Long): ProgramResponse {
        val program =
            programRepository.findByIdOrNull(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found: $id")

        return ProgramResponse.fromEntity(program)
    }

    fun getProgramCategories(): List<ProgramCategoryResponse> {
        return ProgramCategory.entries.map(ProgramCategoryResponse::fromEntity)
    }

    fun getProgramExamples(): List<ProgramSummaryResponse> {
        return programExampleRepository.findAll().map(ProgramSummaryResponse::fromEntity)
    }

    fun getProgramExample(id: Long): ProgramResponse {
        val program =
            programExampleRepository.findByIdOrNull(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found: $id")

        return ProgramResponse.fromEntity(program)
    }

    @Transactional(readOnly = true)
    fun searchLatestPrograms(
        searchTerm: String,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val category: ProgramCategory? = mapSearchTermToCategory(searchTerm)
        val programs: Page<ProgramEntity> =
            programRepository.searchWithCategoryFilter(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
    }

    private fun mapSearchTermToCategory(searchTerm: String): ProgramCategory? {
        val matchedByName =
            ProgramCategory.entries
                .find { it.name.equals(searchTerm, ignoreCase = true) }
        if (matchedByName != null) return matchedByName

        return ProgramCategory.entries
            .find { it.value.contains(searchTerm) }
    }

    @Transactional
    fun searchProgramsByRank(
        searchTerm: String,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        val category: ProgramCategory? = mapSearchTermToCategory(searchTerm)

        val programs: Page<ProgramEntity> =
            programRepository.searchAndRankPrograms(
                query = searchTerm,
                category = category,
                pageable = pageable,
            )

        return PageResponse.from(programs, ProgramSummaryResponse::fromEntity)
    }
}
