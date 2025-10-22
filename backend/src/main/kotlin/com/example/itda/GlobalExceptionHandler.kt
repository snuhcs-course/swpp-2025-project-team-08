package com.example.itda

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ErrorResponse> {
        val httpStatus = ex.code.value()
        val errorResponse =
            ErrorResponse(
                code = ex.code,
                message = ex.message,
            )
        return ResponseEntity
            .status(httpStatus)
            .body(errorResponse)
    }
}
