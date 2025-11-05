package com.example.itda.user.controller

import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import com.example.itda.user.AuthUser
import com.example.itda.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/auth/signup")
    fun signUp(
        @RequestBody request: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response = userService.signUp(request.email, request.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/auth/login")
    fun logIn(
        @RequestBody request: AuthRequest,
    ): ResponseEntity<AuthResponse> {
        val response = userService.logIn(request.email, request.password)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/my-profile")
    fun getProfile(
        @AuthUser user: User,
    ): ResponseEntity<User> {
        val response = userService.getProfile(user.id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/my-profile")
    fun updateProfile(
        @RequestBody request: ProfileRequest,
        @AuthUser user: User,
    ): ResponseEntity<Void> {
        userService.updateProfile(
            userId = user.id,
            request = request,
        )
        return ResponseEntity.ok().build()
    }

    @PostMapping("/update/preferences")
    fun updateUserPreferences(
        @AuthUser user: User,
        @RequestBody request: UserPreferenceUpdateRequest,
    ): ResponseEntity<Void> {
        userService.updateUserPreferenceVector(user.id, request.satisfactionScores)
        return ResponseEntity.ok().build()
    }
}

data class AuthRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
)

data class ProfileRequest(
    val name: String?,
    val birthDate: String?,
    val gender: Gender?,
    val address: String?,
    val postcode: String?,
    val maritalStatus: MaritalStatus?,
    val educationLevel: EducationLevel?,
    val householdSize: Int?,
    val householdIncome: Int?,
    val employmentStatus: EmploymentStatus?,
)

data class UserPreferenceUpdateRequest(
    val satisfactionScores: List<Int>,
)
