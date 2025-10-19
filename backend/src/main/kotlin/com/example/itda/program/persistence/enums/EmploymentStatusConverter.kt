package com.example.itda.program.persistence.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EmploymentStatusConverter : AttributeConverter<EmploymentStatus, String> {
    override fun convertToDatabaseColumn(attribute: EmploymentStatus?): String? =
        attribute?.dbValue

    override fun convertToEntityAttribute(dbData: String?): EmploymentStatus? =
        dbData?.let { EmploymentStatus.fromDb(it) }
}
