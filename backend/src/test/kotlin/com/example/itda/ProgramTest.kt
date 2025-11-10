package com.example.itda

import com.example.itda.program.persistence.enums.ProgramCategory
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
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
class ProgramTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val dataGenerator: DataGenerator,
) {
    companion object {
        private val PGVECTOR_IMAGE: DockerImageName =
            DockerImageName.parse("ankane/pgvector:latest")
                .asCompatibleSubstituteFor("postgres")

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

    @BeforeAll
    fun setupEnvironment() {
        System.setProperty(
            "JWT_SECRET_KEY",
            "test-secret-key-for-local-development-and-tests-must-be-at-least-32-chars",
        )
    }

    private val testEmbeddingDimension = 1024

    @Test
    @Transactional
    fun `getPrograms API는 인증된 사용자의 선호도 기반으로 프로그램 목록을 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("getPrograms")
        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())

        val embeddingA = FloatArray(testEmbeddingDimension) { if (it == 0) 0.5f else 0.0f }
        dataGenerator.saveProgram("ProgramA", embeddingA)

        val embeddingB = FloatArray(testEmbeddingDimension) { if (it == 1) 1.0f else 0.0f }
        dataGenerator.saveProgram("ProgramB", embeddingB)

        mockMvc.perform(
            get("/api/v1/programs")
                .header("Authorization", "Bearer $token")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].title").isNotEmpty)
            .andExpect(jsonPath("$.totalElements").value(2))
    }

    @Test
    @Transactional
    fun `getPrograms API는 카테고리 필터를 적용하여 프로그램 목록을 반환해야 한다`() {
        val testEmail = dataGenerator.generateUniqueEmail("getProgramsFiltered")
        val token = dataGenerator.signUpAndLogIn(testEmail, dataGenerator.generatePassword())

        dataGenerator.saveProgram("Cash Program", FloatArray(testEmbeddingDimension), category = ProgramCategory.CASH)
        dataGenerator.saveProgram("Health Program", FloatArray(testEmbeddingDimension), category = ProgramCategory.HEALTH)

        mockMvc.perform(
            get("/api/v1/programs")
                .header("Authorization", "Bearer $token")
                .param("category", ProgramCategory.CASH.name) // Enum Name 사용
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].title").value("Cash Program"))
            .andExpect(jsonPath("$.totalElements").value(1))
    }

    @Test
    @Transactional
    fun `getProgram API는 유효한 ID로 프로그램 상세 정보를 반환해야 한다`() {
        val program = dataGenerator.saveProgram("Detail Test Program", FloatArray(testEmbeddingDimension))
        val programId = program.id

        mockMvc.perform(
            get("/api/v1/programs/{id}", programId)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(programId))
            .andExpect(jsonPath("$.title").value("Detail Test Program"))
            .andExpect(jsonPath("$.category").value(program.category.name.lowercase()))
            .andExpect(jsonPath("$.details").value(program.details))
            .andExpect(jsonPath("$.uuid").isNotEmpty)
    }

    @Test
    @Transactional
    fun `getProgramCategories API는 모든 프로그램 카테고리 목록을 반환해야 한다`() {
        mockMvc.perform(
            get("/api/v1/programs/categories")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(ProgramCategory.entries.size))
            .andExpect(jsonPath("$[0].category").isNotEmpty)
            .andExpect(jsonPath("$[0].value").isNotEmpty)
    }

    @Test
    @Transactional
    fun `getProgramExamples API는 모든 예시 프로그램 목록을 반환해야 한다`() {
        dataGenerator.saveProgramExample("ProgramA", FloatArray(testEmbeddingDimension))
        dataGenerator.saveProgramExample("ProgramB", FloatArray(testEmbeddingDimension))

        mockMvc.perform(
            get("/api/v1/programs/examples")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @Transactional
    fun `getProgramExample API는 유효한 ID로 예시 프로그램 상세 정보를 반환해야 한다`() {
        val example = dataGenerator.saveProgramExample("exampleA", FloatArray(testEmbeddingDimension))
        val exampleId = example.id

        mockMvc.perform(
            get("/api/v1/programs/examples/{id}", exampleId)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(exampleId))
            .andExpect(jsonPath("$.title").value(example.title))
            .andExpect(jsonPath("$.category").value(example.category.name.lowercase()))
    }

    @Test
    @Transactional
    fun `searchLatestPrograms API는 검색어와 카테고리 필터로 최신순 프로그램 목록을 반환해야 한다`() {
        dataGenerator.saveProgram("Job Searching Service", FloatArray(testEmbeddingDimension), category = ProgramCategory.EMPLOYMENT)
        dataGenerator.saveProgram("Employment Support", FloatArray(testEmbeddingDimension), category = ProgramCategory.EMPLOYMENT)
        dataGenerator.saveProgram("Housing Aid", FloatArray(testEmbeddingDimension), category = ProgramCategory.HOUSING)

        mockMvc.perform(
            get("/api/v1/programs/search/latest")
                .param("query", "Employment")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
    }

    @Test
    @Transactional
    fun `searchProgramsByRank API는 검색어와 카테고리 필터로 랭크순 프로그램 목록을 반환해야 한다`() {
        dataGenerator.saveProgram("Health Checkup", FloatArray(testEmbeddingDimension), category = ProgramCategory.HEALTH)
        dataGenerator.saveProgram("Care Service", FloatArray(testEmbeddingDimension), category = ProgramCategory.CARE)

        mockMvc.perform(
            get("/api/v1/programs/search/rank")
                .param("query", "Service")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
    }

    // --- Exceptions ---

    @Test
    @Transactional
    fun `getProgram API는 존재하지 않는 ID로 요청 시 ProgramNotFoundException을 반환해야 한다`() {
        val nonExistentId = 99999L

        mockMvc.perform(
            get("/api/v1/programs/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Program not found"))
    }
}
