package com.example.meeting_organizer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.meeting_organizer.data.database.AppDatabase
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.ui.theme.MeetingOrganizerTheme
import com.example.meeting_organizer.util.NavigationController

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var meetingRepository: MeetingRepository
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userDao())
        meetingRepository = MeetingRepository(database.meetingDao())

        setContent {
            val isDarkTheme = remember { mutableStateOf(false) }
            MeetingOrganizerTheme(darkTheme = isDarkTheme.value) {
                NavigationController(userRepository, meetingRepository, this)
            }
        }
    }
}


