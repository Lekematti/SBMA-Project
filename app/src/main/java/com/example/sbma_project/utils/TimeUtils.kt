package com.example.sbma_project.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun adjustTimeToStartOfDay(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

fun adjustTimeToEndOfDay(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.time
}

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMMM d, yyyy 'at' HH:mm:ss", Locale.US)
    return formatter.format(date)
}

fun formatDateRange(date: Date): String {
    val formatter = SimpleDateFormat("MM/dd/yy", Locale.US)
    return formatter.format(date)
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}