package com.example.meeting_organizer.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.meeting_organizer.data.database.meeting.ListConverter
//import com.example.meeting_organizer.data.database.meeting.ListConverter
import com.example.meeting_organizer.data.database.meeting.MeetingDao
import com.example.meeting_organizer.data.database.user.UserDao
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.MeetingUserCrossRef
import com.example.meeting_organizer.data.model.User

@Database(
    entities = [User::class, Meeting::class, MeetingUserCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun meetingDao(): MeetingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}