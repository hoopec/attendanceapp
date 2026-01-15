package com.example.attendanceapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Member(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    var status: AttendanceStatus = AttendanceStatus.ABSENT
) : Parcelable {

    fun toggleStatus() {
        status = status.next()
    }

    fun resetStatus() {
        status = AttendanceStatus.ABSENT
    }
}