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
    if (day < 1) return false

    // 월별 최대 일수 체크
    val maxDay = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31  // 31일까지 있는 달
        4, 6, 9, 11 -> 30              // 30일까지 있는 달
        2 -> {
            // 2월은 윤년 계산
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                29  // 윤년
            } else {
                28  // 평년
            }
        }
        else -> return false
    }

    if (day > maxDay) return false

    return true
}