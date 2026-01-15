package com.example.attendanceapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Group(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val members: MutableList<Member> = mutableListOf()
) : Parcelable {

    fun addMember(member: Member) {
        members.add(member)
    }

    fun removeMember(memberId: String) {
        members.removeIf { it.id == memberId }
    }

    fun updateMember(updatedMember: Member) {
        val index = members.indexOfFirst { it.id == updatedMember.id }
        if (index != -1) {
            members[index] = updatedMember
        }
    }

    fun resetAllStatus() {
        members.forEach { it.resetStatus() }
    }

    fun getPresentCount(): Int = members.count { it.status == AttendanceStatus.PRESENT }

    fun getLeaveCount(): Int = members.count { it.status == AttendanceStatus.LEAVE }

    fun getAbsentCount(): Int = members.count { it.status == AttendanceStatus.ABSENT }
}