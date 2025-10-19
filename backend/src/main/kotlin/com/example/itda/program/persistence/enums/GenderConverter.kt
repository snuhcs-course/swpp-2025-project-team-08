package com.example.itda.program.persistence.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class GenderConverter : AttributeConverter<Gender, String> {
    override fun convertToDatabaseColumn(attribute: Gender?): String? = attribute?.dbValue

    override fun convertToEntityAttribute(dbData: String?): Gender? = dbData?.let { Gender.fromDb(it) }
}
