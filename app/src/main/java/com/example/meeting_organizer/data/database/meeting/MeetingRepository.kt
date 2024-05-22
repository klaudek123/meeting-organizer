package com.example.meeting_organizer.data.database.meeting

import com.example.meeting_organizer.data.model.Meeting

class MeetingRepository(private val meetingDao: MeetingDao) {

    suspend fun insertMeeting(meeting: Meeting) {
        meetingDao.insertMeeting(meeting)
    }

    suspend fun getMeetingById(id: Int): Meeting? {
        return meetingDao.getMeetingById(id)
    }

//    suspend fun getAllMeetings(): List<Meeting> {
//        return meetingDao.getAllMeetings()
//    }
}