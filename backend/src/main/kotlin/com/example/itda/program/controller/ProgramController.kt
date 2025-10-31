package com.example.itda.program.controller

import com.example.itda.program.service.ProgramService
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

    @GetMapping("/programs/search/{id}")
    fun searchPrograms(
        @PathVariable id: Long,
        @RequestParam("text") text: String,
    ): ResponseEntity<List<ProgramResponse>> {
        val response = programService.searchPrograms(text, id)
        return ResponseEntity.ok(response)
    }
}
