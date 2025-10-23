package com.example.itda.program.persistence.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EducationLevelConverter : AttributeConverter<EducationLevel, String> {
    override fun convertToDatabaseColumn(attribute: EducationLevel?): String? = attribute?.dbValue

    override fun convertToEntityAttribute(dbData: String?): EducationLevel? = dbData?.let { EducationLevel.fromDb(it) }
}
