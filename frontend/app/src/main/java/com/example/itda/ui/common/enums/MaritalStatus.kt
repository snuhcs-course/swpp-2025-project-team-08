package com.example.itda.ui.common.enums

enum class MaritalStatus(val korean: String, val serverValue: String) {
    SINGLE("미혼", "SINGLE"),
    MARRIED("기혼", "MARRIED"),
    DIVORCED_OR_BEREAVED("이혼/사별", "DIVORCED_OR_BEREAVED");

    companion object {
        fun fromKorean(korean: String?): MaritalStatus? {
            if (korean.isNullOrBlank()) return null
            return entries.find { it.korean == korean }
        }

        fun fromServerValue(value: String?): MaritalStatus? {
            if (value.isNullOrBlank()) return null
            return entries.find { it.serverValue == value }
        }
    }
}
