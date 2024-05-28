package com.example.meeting_organizer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.meeting_organizer.data.database.meeting.ListConverter

@Entity(tableName = "meetings")
@TypeConverters(ListConverter::class)
data class Meeting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "owner_id") val ownerId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "address") val address: String,
    var participants: List<User>
)