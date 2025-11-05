package com.example.itda.program.controller

import com.example.itda.program.service.ProgramService
import org.springframework.data.domain.Page
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
    fun getPrograms(): List<ProgramSummaryResponse> {
        return programService.getPrograms()
    }

    @GetMapping("/programs/{id}")
    fun getProgram(
        @PathVariable id: Long,
    ): ProgramResponse {
        return programService.getProgram(id)
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
