package com.example.itda.ui.common.enums

enum class Gender(val korean: String, val serverValue: String) {
    MALE("남성", "MALE"),
    FEMALE("여성", "FEMALE");

    companion object {
        fun fromKorean(korean: String?): Gender? {
            if (korean.isNullOrBlank()) return null
            return entries.find { it.korean == korean }
        }

        fun fromServerValue(value: String?): Gender? {
            if (value.isNullOrBlank()) return null
            return entries.find { it.serverValue == value }
        }
    }
}
