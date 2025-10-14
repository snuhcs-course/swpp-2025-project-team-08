package com.example.itda.user.controller

import com.example.itda.user.service.UserService
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody request: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response = userService.signUp(request.email, request.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun logIn(
        @RequestBody request: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response = userService.logIn(request.email, request.password)
        return ResponseEntity.ok(response)
    }
}

data class AuthRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
)
