package com.example.meeting_organizer.data.database.meeting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.meeting_organizer.data.model.Meeting

@Dao
interface MeetingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting)

    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getMeetingById(id: Int): Meeting?

    @Query("SELECT * FROM meetings")
    suspend fun getAllMeetings(): List<Meeting>
}