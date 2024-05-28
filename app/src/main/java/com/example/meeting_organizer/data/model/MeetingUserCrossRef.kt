package com.example.meeting_organizer.data.model

import androidx.room.Entity


@Entity(tableName = "meeting_user_cross_ref", primaryKeys = ["meetingId", "userId"])
data class MeetingUserCrossRef(
    val meetingId: Int,
    val userId: Int
)