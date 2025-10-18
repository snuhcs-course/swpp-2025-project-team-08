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