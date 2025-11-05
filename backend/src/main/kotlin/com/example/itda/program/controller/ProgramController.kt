package com.example.itda.program.controller

import com.example.itda.program.persistence.enums.ProgramCategory
import com.example.itda.program.service.ProgramService
import com.example.itda.user.AuthUser
import com.example.itda.user.controller.User
import com.example.itda.utils.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProgramController(
    private val programService: ProgramService,
) {
    @GetMapping("/programs")
    fun getPrograms(
        @AuthUser user: User,
        @RequestParam(required = false) category: ProgramCategory?,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        return programService.getPrograms(user, category, pageable)
    }

    @GetMapping("/programs/{id}")
    fun getProgram(
        @PathVariable id: Long,
    ): ProgramResponse {
        return programService.getProgram(id)
    }

    @GetMapping("/programs/categories")
    fun getProgramCategories(): List<ProgramCategoryResponse> {
        return programService.getProgramCategories()
    }

    @GetMapping("/programs/search/latest")
    fun searchLatestPrograms(
        @RequestParam("query") searchTerm: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") pageSize: Int,
    ): ResponseEntity<Page<ProgramSummaryResponse>> {
        val response: Page<ProgramSummaryResponse> =
            programService.searchLatestPrograms(
                searchTerm = searchTerm,
                page = page,
                pageSize = pageSize,
            )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/programs/search/rank")
    fun searchProgramsByRank(
        @RequestParam("query") searchTerm: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") pageSize: Int,
    ): ResponseEntity<Page<ProgramSummaryResponse>> {
        val response: Page<ProgramSummaryResponse> =
            programService.searchProgramsByRank(
                searchTerm = searchTerm,
                page = page,
                pageSize = pageSize,
            )
        return ResponseEntity.ok(response)
    }
}
