package com.example.itda.program.persistence.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class MaritalStatusConverter : AttributeConverter<MaritalStatus, String> {
    override fun convertToDatabaseColumn(attribute: MaritalStatus?): String? =
        attribute?.dbValue

    override fun convertToEntityAttribute(dbData: String?): MaritalStatus? =
        dbData?.let { MaritalStatus.fromDb(it) }
}