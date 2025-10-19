package com.example.itda.program.persistence.enums

enum class MaritalStatus(val dbValue: String) {
    ANY("무관"),
    SINGLE("미혼"),
    MARRIED("기혼"),
    DIVORCED_OR_BEREAVED("이혼/사별");

    companion object {
        fun fromDb(value: String): MaritalStatus =
            MaritalStatus.entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Invalid marital status: $value")
    }
}