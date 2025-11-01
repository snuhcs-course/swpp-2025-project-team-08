package com.example.itda

import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import com.example.itda.user.controller.AuthRequest
import com.example.itda.user.controller.ProfileRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {
    private fun generateUniqueEmail(testName: String) = "${testName.replace(" ", "_").lowercase()}@test.com"

    private fun generatePassword() = "somepassword"

    private fun signUpAndLogIn(
        email: String,
        password: String,
    ): String {
        val authRequest = AuthRequest(email = email, password = password)

        // 1. 회원가입
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber)

        // 2. 로그인 (토큰 발급)
        val result =
            mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest)),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").isNotEmpty)
                .andExpect(jsonPath("$.refreshToken").isNotEmpty)
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber)
                .andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        return responseJson.get("accessToken").asText()
    }

// --- 1. 회원가입 및 응답 확인 --------------------------------------------------

    @Test
    fun `signUp API는 성공적으로 새 사용자를 등록하고 전체 토큰 정보를 반환해야 한다`() {
        val testEmail = generateUniqueEmail("signup")
        val request = AuthRequest(email = testEmail, password = generatePassword())

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber)
    }

// --- 2. 로그인 (Get Token) ----------------------------------------------------

    @Test
    fun `logIn API는 등록된 사용자로 성공적으로 로그인하고 전체 토큰 정보를 발급해야 한다`() {
        val testEmail = generateUniqueEmail("login")
        val password = generatePassword()

        signUpAndLogIn(testEmail, password)

        val request = AuthRequest(email = testEmail, password = password)
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber)
    }

// --- 3. 프로필 조회 (Authenticated GET) -----------------------------------------

    @Test
    fun `getProfile API는 유효한 토큰으로 사용자 정보를 반환해야 한다`() {
        val testEmail = generateUniqueEmail("getProfile")

        val token = signUpAndLogIn(testEmail, generatePassword())

        mockMvc.perform(
            get("/api/v1/my-profile")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(testEmail))
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.name").isEmpty)
    }

// --- 4. 프로필 수정 (Authenticated PUT) -----------------------------------------

    @Test
    fun `updateProfile API는 사용자 정보를 수정하고 200 OK를 반환해야 한다`() {
        val testEmail = generateUniqueEmail("updateProfile")

        val token = signUpAndLogIn(testEmail, generatePassword())
        val updatedName = "Updated User Name"

        val updateRequest =
            ProfileRequest(
                name = updatedName,
                birthDate = "2000-01-01",
                gender = Gender.FEMALE,
                address = "Busan",
                maritalStatus = MaritalStatus.MARRIED,
                educationLevel = EducationLevel.HIGHSCHOOL,
                householdSize = 4,
                householdIncome = 70000000,
                employmentStatus = EmploymentStatus.EMPLOYED,
            )

        mockMvc.perform(
            put("/api/v1/my-profile")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)),
        )
            .andExpect(status().isOk)
            .andExpect(content().string(""))

        mockMvc.perform(
            get("/api/v1/my-profile")
                .header("Authorization", "Bearer $token"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(updatedName))
            .andExpect(jsonPath("$.householdIncome").value(70000000))
            .andExpect(jsonPath("$.email").value(testEmail))
    }

    @Test
    fun `signUp API는 잘못된 이메일 형식으로 SignUpInvalidEmailException을 반환해야 한다`() {
        val request = AuthRequest(email = "invalid-email.com", password = generatePassword())

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid email format"))
    }

    @Test
    fun `signUp API는 짧은 비밀번호로 SignUpBadPasswordException을 반환해야 한다`() {
        val testEmail = generateUniqueEmail("bad_password")
        val request = AuthRequest(email = testEmail, password = "short")

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Password's length should be 8~16"))
    }

    @Test
    fun `signUp API는 중복 이메일로 SignUpEmailConflictException을 반환해야 한다`() {
        val testEmail = generateUniqueEmail("email_conflict")
        val password = generatePassword()
        val request = AuthRequest(email = testEmail, password = password)

        signUpAndLogIn(testEmail, password)

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Email conflict"))
    }

    @Test
    fun `logIn API는 존재하지 않는 유저로 UserNotFoundException을 반환해야 한다`() {
        val testEmail = generateUniqueEmail("not_found_user")
        val request = AuthRequest(email = testEmail, password = generatePassword())

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found"))
    }

    @Test
    fun `logIn API는 잘못된 비밀번호로 LogInInvalidPasswordException을 반환해야 한다`() {
        val testEmail = generateUniqueEmail("wrong_password")
        val correctPassword = generatePassword()

        signUpAndLogIn(testEmail, correctPassword)

        val wrongPasswordRequest = AuthRequest(email = testEmail, password = "wrong_password")
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Wrong password"))
    }

    @Test
    fun `updateProfile API는 잘못된 생년월일 형식으로 InvalidBirthDateFormatException을 반환해야 한다`() {
        val testEmail = generateUniqueEmail("invalid_birth")
        val token = signUpAndLogIn(testEmail, generatePassword())

        val updateRequest =
            ProfileRequest(
                name = "Test",
                birthDate = "20000101",
                gender = Gender.FEMALE,
                address = "Seoul",
                maritalStatus = MaritalStatus.MARRIED,
                educationLevel = EducationLevel.HIGHSCHOOL,
                householdSize = 4,
                householdIncome = 70000000,
                employmentStatus = EmploymentStatus.EMPLOYED,
            )

        mockMvc.perform(
            put("/api/v1/my-profile")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Invalid birth date format. Must be in YYYY-MM-DD format."))
    }

    @Test
    fun `인증이 필요한 API는 토큰 없이 AuthenticateException을 반환해야 한다`() {
        mockMvc.perform(
            get("/api/v1/my-profile")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Authenticate failed"))
    }

    @BeforeAll
    fun setupEnvironment() {
        System.setProperty("JWT_SECRET_KEY", "test-secret-key-for-local-development-and-tests-must-be-at-least-32-chars")
    }
}
