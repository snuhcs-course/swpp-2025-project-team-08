package com.example.itda.ui.auth.components



fun formatBirthDate(birthDate: String): String? {
    return "${birthDate.substring(0, 4)}-${birthDate.substring(4, 6)}-${birthDate.substring(6, 8)}"
}


/**
 * 생년월일 유효성 검사
 */
fun isValidBirthDate(birthDate: String): Boolean {
    if (birthDate.length != 8) return false

    val year = birthDate.substring(0, 4).toIntOrNull() ?: return false
    val month = birthDate.substring(4, 6).toIntOrNull() ?: return false
    val day = birthDate.substring(6, 8).toIntOrNull() ?: return false

    // 기본 범위 체크
    if (year < 1900 || year > 2025) return false
    if (month < 1 || month > 12) return false

    return true
}

