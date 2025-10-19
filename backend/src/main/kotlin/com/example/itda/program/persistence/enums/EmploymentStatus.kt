package com.example.itda.program.persistence.enums

enum class EmploymentStatus(val dbValue: String) {
    ANY("무관"),
    EMPLOYED("재직자"),
    UNEMPLOYED("미취업자"),
    SELF_EMPLOYED("자영업자");

    companion object {
        fun fromDb(value: String): EmploymentStatus =
            EmploymentStatus.entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Invalid employment status: $value")
    }
}