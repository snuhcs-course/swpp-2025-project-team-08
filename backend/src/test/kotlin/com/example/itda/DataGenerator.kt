package com.example.itda

import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.ProgramExampleEntity
import com.example.itda.program.persistence.ProgramExampleRepository
import com.example.itda.program.persistence.ProgramRepository
import com.example.itda.program.persistence.enums.OperatingEntityType
import com.example.itda.program.persistence.enums.ProgramCategory
import com.example.itda.user.controller.AuthRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@Component
class DataGenerator(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val programRepository: ProgramRepository,
    private val programExampleRepository: ProgramExampleRepository,
) {
    fun generateUniqueEmail(testName: String) = "${testName.replace(" ", "_").lowercase()}@test.com"

    fun generatePassword() = "somepassword"

    fun signUpAndLogIn(
        email: String,
        password: String,
    ): String {
        val authRequest = AuthRequest(email = email, password = password)

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)),
        ).andExpect(status().isOk)

        val result =
            mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest)),
            )
                .andExpect(status().isOk)
                .andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        return responseJson.get("accessToken").asText()
    }

    fun saveProgramExample(
        title: String,
        embeddingValues: FloatArray,
    ): ProgramExampleEntity {
        val entity =
            ProgramExampleEntity(
                uuid = UUID.randomUUID().toString(),
                title = "Program $title",
                summary = "Summary $title",
                details = "Details $title",
                category = ProgramCategory.CASH,
                operatingEntityType = OperatingEntityType.CENTRAL,
                operatingEntity = "Government",
                preview = "Preview $title",
                embedding = embeddingValues,
            )
        return programExampleRepository.save(entity)
    }

    fun saveProgram(
        title: String,
        embeddingValues: FloatArray,
        category: ProgramCategory = ProgramCategory.CASH,
    ): ProgramEntity {
        val entity =
            ProgramEntity(
                uuid = UUID.randomUUID().toString(),
                title = title,
                summary = "Summary for $title",
                details = "Details for $title",
                category = category,
                operatingEntityType = OperatingEntityType.LOCAL,
                operatingEntity = "Local Entity $title",
                preview = "Preview for $title",
                embedding = embeddingValues,
            )
        return programRepository.save(entity)
    }
}
