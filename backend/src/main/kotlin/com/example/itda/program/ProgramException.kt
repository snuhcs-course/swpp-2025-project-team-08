package com.example.itda.program

import com.example.itda.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class ProgramException(
    code: HttpStatusCode,
    message: String,
) : DomainException(code, message)

class ProgramNotFoundException : ProgramException(
    code = HttpStatus.NOT_FOUND,
    message = "Program not found",
)
