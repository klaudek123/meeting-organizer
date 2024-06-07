package com.example.meeting_organizer.data.database.meeting

import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.MeetingUserCrossRef
import com.example.meeting_organizer.data.model.User

class MeetingRepository(private val meetingDao: MeetingDao) {

    // Metody związane ze spotkaniami
    suspend fun insertMeeting(meeting: Meeting): Long {
        return meetingDao.insertMeeting(meeting)
    }

    suspend fun getMeetingById(id: Int): Meeting? {
        return meetingDao.getMeetingById(id)
    }

    suspend fun getAllMeetings(): List<Meeting> {
        return meetingDao.getAllMeetings()
    }

    // Metody związane z użytkownikami
    suspend fun insertUser(user: User): Long {
        return meetingDao.insertUser(user)
    }

    suspend fun getAllUsers(): List<User> {
        return meetingDao.getAllUsers()
    }

    // Metody związane z relacją wiele do wielu (spotkania i użytkownicy)
    suspend fun insertMeetingUserCrossRef(crossRef: MeetingUserCrossRef) {
        meetingDao.insertMeetingUserCrossRef(crossRef)
    }

    suspend fun getUsersForMeeting(meetingId: Int): List<User> {
        return meetingDao.getUsersForMeeting(meetingId)
    }

    suspend fun getMeetingsForUser(userId: Int): List<Meeting> {
        val meetings = meetingDao.getMeetingsForUser(userId)
        meetings.forEach { meeting ->
            meeting.participants = meetingDao.getParticipantsForMeeting(meeting.id)
        }
        return meetings
    }

    suspend fun deleteMeeting(meeting: Meeting) {
        meetingDao.deleteMeeting(meeting)
    }
}