package com.example.itda.program.service

import com.example.itda.program.controller.ProgramResponse
import com.example.itda.program.controller.ProgramSummaryResponse
import com.example.itda.program.persistence.ProgramRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProgramService(
    val programRepository: ProgramRepository,
) {

    fun getPrograms(): List<ProgramSummaryResponse> {
        val programs = programRepository.findAll()

        return programs.map { ProgramSummaryResponse.fromEntity(it) }
    }

    fun getProgram(id: Long): ProgramResponse {
        val program = programRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found: $id")

        return ProgramResponse.fromEntity(program)
    }
}