package com.example.itda.program.persistence.enums

enum class Gender(val dbValue: String) {
    MALE("남성"),
    FEMALE("여성"),
    ;

    companion object {
        fun fromDb(value: String): Gender =
            Gender.entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Invalid gender: $value")
    }
}
