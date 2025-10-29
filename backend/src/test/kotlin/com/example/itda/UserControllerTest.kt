package com.example.itda.user.controller

import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import com.example.itda.user.UserArgumentResolver
import com.example.itda.user.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@Import(UserControllerTest.TestConfig::class)
class UserControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private var userService: UserService,
    @Autowired private val userArgumentResolver: UserArgumentResolver,
) {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun userService(): UserService {
            return mockk(relaxed = true)
        }

        @Bean
        fun userArgumentResolver(): UserArgumentResolver {
            return mockk(relaxed = true)
        }
    }

    private val mockUser =
        User(
            id = "mock-user-12345",
            email = "test@example.com",
            name = "Mock",
            birthDate = null,
            gender = "MALE",
            address = null,
            maritalStatus = null,
            educationLevel = null,
            householdSize = null,
            householdIncome = null,
            employmentStatus = null,
        )

    @Test
    fun `signUp API는 정상적으로 동작해야 한다`() {
        // Given
        val request = AuthRequest(email = "test@example.com", password = "testpassword")
        val expectedResponse =
            AuthResponse(
                accessToken = "mock_access_token",
                refreshToken = "mock_refresh_token",
                tokenType = "Bearer",
                expiresIn = 3600L,
            )
        every { userService.signUp(request.email, request.password) } returns expectedResponse

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value(expectedResponse.accessToken))
            .andExpect(jsonPath("$.tokenType").value(expectedResponse.tokenType))

        // And
        verify(exactly = 1) { userService.signUp(request.email, request.password) }
    }

    @Test
    fun `logIn API는 정상적으로 동작해야 한다`() {
        // Given
        val request = AuthRequest(email = "test@example.com", password = "password123")
        val expectedResponse =
            AuthResponse(
                accessToken = "login_access_token",
                refreshToken = "login_refresh_token",
                tokenType = "Bearer",
                expiresIn = 7200L,
            )
        every { userService.logIn(request.email, request.password) } returns expectedResponse

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value(expectedResponse.accessToken))
            .andExpect(jsonPath("$.expiresIn").value(expectedResponse.expiresIn))

        // And
        verify(exactly = 1) { userService.logIn(request.email, request.password) }
    }

    @Test
    fun `getProfile API는 인증 후 Mock User 정보로 프로필을 반환해야 한다`() {
        every { userArgumentResolver.supportsParameter(any()) } returns true
        every { userArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns mockUser

        every { userService.getProfile(mockUser.id) } returns mockUser

        // When & Then
        mockMvc.perform(
            get("/api/v1/my-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy_token"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(mockUser.id))
            .andExpect(jsonPath("$.email").value(mockUser.email))

        // And
        verify(exactly = 1) { userService.getProfile(mockUser.id) }
        verify(exactly = 1) { userArgumentResolver.resolveArgument(any(), any(), any(), any()) }
    }

    @Test
    fun `updateProfile API는 인증 후 Mock User 정보로 프로필을 수정해야 한다`() {
        // Given
        val request =
            ProfileRequest(
                name = "New Name",
                birthDate = "1995-05-05",
                gender = Gender.FEMALE,
                address = "New Address",
                maritalStatus = MaritalStatus.MARRIED,
                educationLevel = EducationLevel.BACHELOR,
                householdSize = 2,
                householdIncome = 60000000,
                employmentStatus = EmploymentStatus.SELF_EMPLOYED,
            )

        every { userArgumentResolver.supportsParameter(any()) } returns true
        every { userArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns mockUser

        every { userService.updateProfile(userId = mockUser.id, request = request) } returns Unit

        // When & Then
        mockMvc.perform(
            put("/api/v1/my-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer dummy_token"),
        )
            .andExpect(status().isOk)
            .andExpect(content().string(""))

        // And
        verify(exactly = 1) { userService.updateProfile(userId = mockUser.id, request = request) }
        verify(exactly = 1) { userArgumentResolver.resolveArgument(any(), any(), any(), any()) }
    }

    @AfterEach
    fun tearDown() {
        io.mockk.clearMocks(userService, userArgumentResolver)
    }
}
