package com.example.itda.ui.common.enums

enum class EmploymentStatus(val korean: String, val serverValue: String) {
    EMPLOYED("재직자", "EMPLOYED"),
    UNEMPLOYED("미취업자", "UNEMPLOYED"),
    SELF_EMPLOYED("자영업자", "SELF_EMPLOYED");

    companion object {
        fun fromKorean(korean: String?): EmploymentStatus? {
            if (korean.isNullOrBlank()) return null
            return entries.find { it.korean == korean }
        }

        fun fromServerValue(value: String?): EmploymentStatus? {
            if (value.isNullOrBlank()) return null
            return entries.find { it.serverValue == value }
        }
    }
}
