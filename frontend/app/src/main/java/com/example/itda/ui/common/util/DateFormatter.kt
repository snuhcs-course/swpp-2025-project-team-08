package com.example.itda.ui.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getTodayString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun getDDayLabel(endDate: String): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val today = Calendar.getInstance()
    val end = Calendar.getInstance().apply {
        time = sdf.parse(endDate) ?: Date()
    }

    val diff = (end.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)
    return diff
}

/**
 * ISO 8601 형식의 날짜 문자열 (예: "2024-12-01T00:00:00Z")을
 * "yyyy.MM.dd" (예: "2024.12.01") 형식으로 변환합니다.
 *
 * @param isoDateString 변환할 ISO 형식의 날짜 문자열
 * @return "yyyy.MM.dd" 형식의 문자열. 실패 시 null 또는 적절한 기본값 반환.
 */
fun formatIsoDateToYmd(isoDateString: String): String? {
    try {
        // 문자열의 앞 10자리("yyyy-MM-dd")를 잘라냅니다.
        // (입력: "2024-12-01T00:00:00Z" -> 결과: "2024-12-01")
        val datePart = isoDateString.substring(0, 10)

        // 하이픈(-)을 점(.)으로 교체합니다.
        // (입력: "2024-12-01" -> 결과: "2024.12.01")
        return datePart.replace('-', '.')

    } catch (e: Exception) {
        // 문자열이 10자리보다 짧거나 형식이 다르면 예외 발생
        e.printStackTrace()
        return null
    }
}