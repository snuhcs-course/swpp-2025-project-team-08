package com.example.itda

import com.example.itda.embedding.service.EmbeddingService
import com.example.itda.program.persistence.enums.EducationLevel
import com.example.itda.program.persistence.enums.EmploymentStatus
import com.example.itda.program.persistence.enums.Gender
import com.example.itda.program.persistence.enums.MaritalStatus
import com.example.itda.user.controller.AuthRequest
import com.example.itda.user.controller.ProfileRequest
import com.example.itda.user.controller.RefreshRequest
import com.example.itda.user.persistence.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class UserTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val dataGenerator: DataGenerator,
    @Autowired private val userRepository: UserRepository,
) {
    @MockitoBean
    private lateinit var embeddingService: EmbeddingService

    companion object {
        private val PGVECTOR_IMAGE: DockerImageName =
            DockerImageName.parse("ankane/pgvector:latest")
                .asCompatibleSubstituteFor("postgres")
        const val TEST_EMBEDDING_DIMENSION = 1024

        @JvmStatic
        @Container
        val postgreSQLContainer =
            PostgreSQLContainer(PGVECTOR_IMAGE).apply {
                withDatabaseName("testdb")
                withUsername("testuser")
                withPassword("testpass")
                withInitScript("init_db.sql")
            }

        @JvmStatic
        @ServiceConnection
        val connection = postgreSQLContainer
    }

    @Test
    fun `refresh api는 성공적으로 accessToken을 갱신해줘야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("refresh")
        val password = dataGenerator.generatePassword()

        val token = dataGenerator.signUpAndLogIn(testEmail, password)

        val request = RefreshRequest(token)
        mockMvc.perform(
            post("/api/v1/auth/refresh")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber)
    }

    @Test
    fun `signUp API는 성공적으로 새 사용자를 등록하고 전체 토큰 정보를 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("signup")
        val request = AuthRequest(email = testEmail, password = dataGenerator.generatePassword())

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

    @Test
    fun `logIn API는 등록된 사용자로 성공적으로 로그인하고 전체 토큰 정보를 발급해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("login")
        val password = dataGenerator.generatePassword()

        dataGenerator.signUpAndLogIn(testEmail, password)

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

    @Test
    fun `getProfile API는 유효한 토큰으로 사용자 정보를 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("getProfile")

        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())

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

    @Test
    fun `updateProfile API는 사용자 정보를 수정하고 200 OK를 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("updateProfile")
        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())
        val updatedName = "Updated User Name"

        val dummyEmbedding = FloatArray(1024) { 0.0f } // Match your dimension constant

        given(embeddingService.getEmbedding(anyString())).willReturn(dummyEmbedding)

        val updateRequest =
            ProfileRequest(
                name = updatedName,
                birthDate = "2000-01-01",
                gender = Gender.FEMALE,
                address = "Busan",
                postcode = "12345",
                maritalStatus = MaritalStatus.MARRIED,
                educationLevel = EducationLevel.HIGH_SCHOOL,
                householdSize = 4,
                householdIncome = 7000,
                employmentStatus = EmploymentStatus.EMPLOYED,
                tags = listOf(),
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
            .andExpect(jsonPath("$.householdIncome").value(7000))
            .andExpect(jsonPath("$.email").value(testEmail))
    }

//    @Test
//    fun `updateUserPreferences API는 선호도 점수에 따라 임베딩을 계산하고 저장해야 한다`() {
//        val testEmail = dataGenerator.generateUniqueEmail("updatePreferences")
//        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())
//
//        val embeddingA = FloatArray(TEST_EMBEDDING_DIMENSION) { if (it == 0) 1.0f else 0.0f }
//        val programA = dataGenerator.saveProgramExample("programA", embeddingA)
//
//        val embeddingB = FloatArray(TEST_EMBEDDING_DIMENSION) { if (it == 1) 1.0f else 0.0f }
//        val programB = dataGenerator.saveProgramExample("programB", embeddingB)
//
//        val requestBody =
//            listOf(
//                PreferenceRequest(id = programA.id!!, score = 7),
//                PreferenceRequest(id = programB.id!!, score = 3),
//            )
//        mockMvc.perform(
//            put("/api/v1/my-profile/preferences")
//                .header("Authorization", "Bearer $token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestBody)),
//        )
//            .andExpect(status().isOk)
//            .andExpect(content().string(""))
//
//        val userEntity = userRepository.findByEmail(testEmail) ?: throw RuntimeException("Test User not found in DB")
//        val savedEmbedding = userEntity.embedding ?: throw RuntimeException("User preferenceEmbedding is null")
//
//        val expectedEmbedding = FloatArray(TEST_EMBEDDING_DIMENSION)
//        expectedEmbedding[0] = 0.7f
//        expectedEmbedding[1] = 0.3f
//
//        assert(savedEmbedding[0] closeTo expectedEmbedding[0]) { "First element mismatch: Expected 0.7f, Got ${savedEmbedding[0]}" }
//        assert(savedEmbedding[1] closeTo expectedEmbedding[1]) { "Second element mismatch: Expected 0.3f, Got ${savedEmbedding[1]}" }
//
//        for (i in 2 until TEST_EMBEDDING_DIMENSION) {
//            assert(savedEmbedding[i] closeTo 0.0f) { "Element at index $i was non-zero: Got ${savedEmbedding[i]}" }
//        }
//    }
//
//    private infix fun Float.closeTo(expected: Float): Boolean {
//        val epsilon = 0.0001f
//        return kotlin.math.abs(this - expected) < epsilon
//    }

    @Test
    fun `getBookmarkedPrograms API는 유저 북마크 리스트를 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("getBookmarks")
        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())
        val embedding = FloatArray(TEST_EMBEDDING_DIMENSION)

        val programA = dataGenerator.saveProgram("Program A", embedding)
        val programB = dataGenerator.saveProgram("Program B", embedding)

        mockMvc.perform(
            post("/api/v1/programs/{programId}/bookmark", programA.id)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            post("/api/v1/programs/{programId}/bookmark", programB.id)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/v1/users/me/bookmarks")
                .header("Authorization", "Bearer $token")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "LATEST")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].title").value("Program B"))
            .andExpect(jsonPath("$.content[1].title").value("Program A"))
            .andExpect(jsonPath("$.totalElements").value(2))
    }

    // --- Exceptions -----------------------------------------

    @Test
    fun `signUp API는 잘못된 이메일 형식으로 SignUpInvalidEmailException을 반환해야 한다`() {
        val request = AuthRequest(email = "invalid-email.com", password = dataGenerator.generatePassword())

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
        val testEmail = dataGenerator.generateUniqueEmail("bad_password")
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
        val testEmail = dataGenerator.generateUniqueEmail("email_conflict")
        val password = dataGenerator.generatePassword()
        val request = AuthRequest(email = testEmail, password = password)

        dataGenerator.signUpAndLogIn(testEmail, password)

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
        val testEmail = dataGenerator.generateUniqueEmail("not_found_user")
        val request = AuthRequest(email = testEmail, password = dataGenerator.generatePassword())

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
        val testEmail = dataGenerator.generateUniqueEmail("wrong_password")
        val correctPassword = dataGenerator.generatePassword()

        dataGenerator.signUpAndLogIn(testEmail, correctPassword)

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
        val testEmail = dataGenerator.generateUniqueEmail("invalid_birth")
        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())

        val updateRequest =
            ProfileRequest(
                name = "Test",
                birthDate = "20000101",
                gender = Gender.FEMALE,
                address = "Seoul",
                postcode = "12345",
                maritalStatus = MaritalStatus.MARRIED,
                educationLevel = EducationLevel.HIGH_SCHOOL,
                householdSize = 4,
                householdIncome = 7000,
                employmentStatus = EmploymentStatus.EMPLOYED,
                tags = listOf(),
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
        System.setProperty(
            "JWT_SECRET_KEY",
            "test-secret-key-for-local-development-and-tests-must-be-at-least-32-chars",
        )
    }
}
