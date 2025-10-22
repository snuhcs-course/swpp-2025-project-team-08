package com.example.itda

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

open class DomainException(
    val code: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    override val message: String,
) : RuntimeException(message) {
    override fun toString(): String {
        return "DomainException(msg='$message', errorCode=$code)"
    }
}

data class ErrorResponse(
    val code: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    val message: String,
)
