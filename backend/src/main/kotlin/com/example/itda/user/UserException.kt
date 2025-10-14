package com.example.itda.user

import org.springframework.http.HttpStatusCode

sealed class UserException(
    code: HttpStatusCode,
    message: String,
) : RuntimeException(message)