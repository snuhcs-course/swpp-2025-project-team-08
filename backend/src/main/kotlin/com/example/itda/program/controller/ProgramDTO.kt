package com.example.itda.program.controller

import com.example.itda.program.persistence.ProgramEntity
import com.example.itda.program.persistence.enums.ProgramCategory
import java.time.OffsetDateTime

data class ProgramSummaryResponse(
    val id: Long,
    val title: String,
    val preview: String,
    val operatingEntity: String,
    val category: String,
    val categoryValue: String,
) {
    companion object {
        fun fromEntity(entity: ProgramEntity): ProgramSummaryResponse =
            ProgramSummaryResponse(
                id = entity.id,
                title = entity.title,
                preview = entity.preview,
                operatingEntity = entity.operatingEntity,
                category = entity.category.toString().lowercase(),
                categoryValue = entity.category.value,
            )
    }
}

data class ProgramResponse(
    val id: Long,
    val uuid: String,
    val category: String,
    val categoryValue: String,
    val title: String,
    val details: String,
    val summary: String,
    val preview: String,
    val applicationMethod: String?,
    val applyUrl: String?,
    val referenceUrl: String?,
    val eligibilityMinAge: Int?,
    val eligibilityMaxAge: Int?,
    val eligibilityMinHousehold: Int?,
    val eligibilityMaxHousehold: Int?,
    val eligibilityMinIncome: Int?,
    val eligibilityMaxIncome: Int?,
    val eligibilityRegion: String?,
    val eligibilityGender: String?,
    val eligibilityMaritalStatus: String?,
    val eligibilityEducation: String?,
    val eligibilityEmployment: String?,
    val applyStartAt: OffsetDateTime?,
    val applyEndAt: OffsetDateTime?,
    val createdAt: OffsetDateTime?,
    var operatingEntity: String,
) {
    companion object {
        fun fromEntity(entity: ProgramEntity): ProgramResponse =
            ProgramResponse(
                id = entity.id,
                uuid = entity.uuid,
                category = entity.category.toString().lowercase(),
                categoryValue = entity.category.value,
                title = entity.title,
                details = entity.details,
                summary = entity.summary,
                preview = entity.preview,
                applicationMethod = entity.applicationMethod,
                applyUrl = entity.applyUrl,
                referenceUrl = entity.referenceUrl,
                eligibilityMinAge = entity.eligibilityMinAge,
                eligibilityMaxAge = entity.eligibilityMaxAge,
                eligibilityRegion = entity.eligibilityRegion,
                eligibilityMinHousehold = entity.eligibilityMinHousehold,
                eligibilityMaxHousehold = entity.eligibilityMaxHousehold,
                eligibilityMinIncome = entity.eligibilityMinIncome,
                eligibilityMaxIncome = entity.eligibilityMaxIncome,
                eligibilityGender = entity.eligibilityGender?.value,
                eligibilityMaritalStatus = entity.eligibilityMaritalStatus?.value,
                eligibilityEducation = entity.eligibilityEducation?.value,
                eligibilityEmployment = entity.eligibilityEmployment?.value,
                applyStartAt = entity.applyStartAt,
                applyEndAt = entity.applyEndAt,
                createdAt = entity.createdAt,
                operatingEntity = entity.operatingEntity,
            )
    }
}

data class ProgramCategoryResponse(
    val category: String,
    val value: String,
) {
    companion object {
        fun fromEntity(category: ProgramCategory): ProgramCategoryResponse =
            ProgramCategoryResponse(
                category = category.toString().lowercase(),
                value = category.value,
            )
    }
}
