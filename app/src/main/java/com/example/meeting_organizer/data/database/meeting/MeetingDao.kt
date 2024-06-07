package com.example.meeting_organizer.data.database.meeting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.MeetingUserCrossRef
import com.example.meeting_organizer.data.model.User

@Dao
interface MeetingDao {

    @Insert
    suspend fun insertMeeting(meeting: Meeting): Long

    @Insert
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteMeeting(meeting: Meeting)

    @Insert
    suspend fun insertMeetingUserCrossRef(crossRef: MeetingUserCrossRef)

    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getMeetingById(id: Int): Meeting?

    @Query("SELECT * FROM meetings")
    suspend fun getAllMeetings(): List<Meeting>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id IN (SELECT userId FROM meeting_user_cross_ref WHERE meetingId = :meetingId)")
    suspend fun getUsersForMeeting(meetingId: Int): List<User>

    @Query("SELECT * FROM meetings WHERE owner_id = :userId")
    suspend fun getMeetingsForUser(userId: Int): List<Meeting>

    @Query("SELECT * FROM users INNER JOIN meeting_user_cross_ref ON users.id = meeting_user_cross_ref.userId WHERE meeting_user_cross_ref.meetingId = :meetingId")
    suspend fun getParticipantsForMeeting(meetingId: Int): List<User>
}