package com.example.itda.user

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class UserException(
    code: HttpStatusCode,
    message: String,
) : RuntimeException(message)

class AuthenticateException : UserException(
    code = HttpStatus.UNAUTHORIZED,
    message = "Authenticate failed",
)

class SignUpInvalidEmailException : UserException(
    code = HttpStatus.BAD_REQUEST,
    message = "Invalid email format",
)

class SignUpBadPasswordException : UserException(
    code = HttpStatus.BAD_REQUEST,
    message = "Password's length should be 8~16",
)

class SignUpEmailConflictException : UserException(
    code = HttpStatus.CONFLICT,
    message = "Email conflict",
)

class UserNotFoundException : UserException(
    code = HttpStatus.NOT_FOUND,
    message = "User not found",
)

class LogInInvalidPasswordException : UserException(
    code = HttpStatus.BAD_REQUEST,
    message = "Wrong password",
)
