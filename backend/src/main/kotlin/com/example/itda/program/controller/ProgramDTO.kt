package com.example.itda.program.controller

import com.example.itda.program.persistence.ProgramEntity
import java.time.OffsetDateTime


data class ProgramSummaryResponse(
    val id: Int,
    val title: String
) {
    companion object {
        fun fromEntity(entity: ProgramEntity): ProgramSummaryResponse =
            ProgramSummaryResponse(
                id = entity.id ?: error("ProgramEntity.id is null"),
                title = entity.title
            )
    }
}

data class ProgramResponse(
    val id: Int,
    val uuid: String,
    val title: String,
    val details: String?,
    val applicationMethod: String?,
    val referenceUrl: String?,
    val eligibilityGender: String?,
    val eligibilityMinAge: Int?,
    val eligibilityMaxAge: Int?,
    val eligibilityRegion: String?,
    val eligibilityMaritalStatus: String?,
    val eligibilityEducation: String?,
    val eligibilityMinHousehold: Int?,
    val eligibilityMaxHousehold: Int?,
    val eligibilityMinIncome: Long?,
    val eligibilityMaxIncome: Long?,
    val eligibilityEmployment: String?,
    val applyStartAt: OffsetDateTime?,
    val applyEndAt: OffsetDateTime?,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
) {
    companion object {
        fun fromEntity(entity: ProgramEntity): ProgramResponse =
            ProgramResponse(
                id = entity.id ?: error("ProgramEntity.id is null"),
                uuid = entity.uuid,
                title = entity.title,
                details = entity.details,
                applicationMethod = entity.applicationMethod,
                referenceUrl = entity.referenceUrl,
                eligibilityGender = entity.eligibilityGender?.dbValue,
                eligibilityMinAge = entity.eligibilityMinAge,
                eligibilityMaxAge = entity.eligibilityMaxAge,
                eligibilityRegion = entity.eligibilityRegion,
                eligibilityMaritalStatus = entity.eligibilityMaritalStatus?.dbValue,
                eligibilityEducation = entity.eligibilityEducation,
                eligibilityMinHousehold = entity.eligibilityMinHousehold,
                eligibilityMaxHousehold = entity.eligibilityMaxHousehold,
                eligibilityMinIncome = entity.eligibilityMinIncome,
                eligibilityMaxIncome = entity.eligibilityMaxIncome,
                eligibilityEmployment = entity.eligibilityEmployment?.dbValue,
                applyStartAt = entity.applyStartAt,
                applyEndAt = entity.applyEndAt,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
    }
}
