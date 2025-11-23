package com.example.itda.ui.common.enums

enum class EducationLevel(val korean: String, val serverValue: String) {
    ELEMENTARY_SCHOOL_STUDENT("초등학생", "ELEMENTARY_SCHOOL_STUDENT"),
    MIDDLE_SCHOOL_STUDENT("중학생", "MIDDLE_SCHOOL_STUDENT"),
    HIGH_SCHOOL_STUDENT("고등학생", "HIGH_SCHOOL_STUDENT"),
    COLLEGE_STUDENT("대학생", "COLLEGE_STUDENT"),
    ELEMENTARY_SCHOOL("초졸", "ELEMENTARY_SCHOOL"),
    MIDDLE_SCHOOL("중졸", "MIDDLE_SCHOOL"),
    HIGH_SCHOOL("고졸", "HIGH_SCHOOL"),
    ASSOCIATE("전문대졸", "ASSOCIATE"),
    BACHELOR("대졸", "BACHELOR");

    companion object {
        fun fromKorean(korean: String?): EducationLevel? {
            if (korean.isNullOrBlank()) return null
            return entries.find { it.korean == korean }
        }

        fun fromServerValue(value: String?): EducationLevel? {
            if (value.isNullOrBlank()) return null
            return entries.find { it.serverValue == value }
        }
    }
}
