package com.example.itda.program.controller

import com.example.itda.program.persistence.enums.ProgramCategory
import com.example.itda.program.service.ProgramService
import com.example.itda.user.AuthUser
import com.example.itda.user.controller.User
import com.example.itda.utils.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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

    @GetMapping("/programs/examples")
    fun getProgramExamples(): List<ProgramSummaryResponse> {
        return programService.getProgramExamples()
    }

    @GetMapping("/programs/examples/{id}")
    fun getProgramExamples(
        @PathVariable id: Long,
    ): ProgramResponse {
        return programService.getProgramExample(id)
    }

    @GetMapping("/programs/search/latest")
    fun searchLatestPrograms(
        @RequestParam("query") searchTerm: String,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        return programService.searchLatestPrograms(searchTerm, pageable)
    }

    @GetMapping("/programs/search/rank")
    fun searchProgramsByRank(
        @RequestParam("query") searchTerm: String,
        pageable: Pageable,
    ): PageResponse<ProgramSummaryResponse> {
        return programService.searchProgramsByRank(searchTerm, pageable)
    }

    @PostMapping("/programs/{programId}/bookmark")
    fun bookmark(
        @PathVariable programId: Long,
        @AuthUser user: User,
    ): ResponseEntity<String> {
        programService.bookmarkProgram(user.id, programId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/programs/{programId}/unbookmark")
    fun unbookmark(
        @PathVariable programId: Long,
        @AuthUser user: User,
    ): ResponseEntity<String> {
        programService.unbookmarkProgram(user.id, programId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/programs/{programId}/like")
    fun likeProgrma(
        @PathVariable programId: Long,
        @RequestParam("type") isLike: Boolean,
        @AuthUser user: User,
    ): ResponseEntity<String> {
        programService.likeProgram(user.id, programId, isLike)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/programs/{programId}/unlike")
    fun unlikeProgram(
        @PathVariable programId: Long,
        @AuthUser user: User,
    ): ResponseEntity<String> {
        programService.unLikeProgram(user.id, programId)
        return ResponseEntity.noContent().build()
    }
}
