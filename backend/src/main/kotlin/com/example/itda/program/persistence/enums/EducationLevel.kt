package com.example.itda.program.persistence.enums

enum class EducationLevel(val dbValue: String) {
    ANY("무관"),
    HIGHSCHOOL("고졸"),
    ASSOCIATE("전문대졸"),
    BACHELOR("학사"),
    MASTER("석사"),
    PHD("박사"),
    ;

    companion object {
        fun fromDb(value: String): EducationLevel =
            EducationLevel.entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Invalid education level: $value")
    }
}
