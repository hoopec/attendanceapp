package com.example.attendanceapp.model

import androidx.annotation.ColorRes
import com.example.attendanceapp.R

enum class AttendanceStatus(val displayName: String, @ColorRes val colorRes: Int) {
    ABSENT("未到", R.color.status_absent),
    PRESENT("已到", R.color.status_present),
    LEAVE("请假", R.color.status_leave);

    fun next(): AttendanceStatus {
        return when (this) {
            ABSENT -> PRESENT
            PRESENT -> LEAVE
            LEAVE -> ABSENT
        }
    }

    companion object {
        fun fromString(value: String): AttendanceStatus {
            return values().find { it.name == value } ?: ABSENT
        }
    }
}