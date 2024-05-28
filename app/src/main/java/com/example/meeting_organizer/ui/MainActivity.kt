package com.example.meeting_organizer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.meeting_organizer.data.database.AppDatabase
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.ui.theme.MeetingOrganizerTheme
import com.example.meeting_organizer.ui.theme.TopBar
import androidx.navigation.compose.rememberNavController
import com.example.meeting_organizer.util.NavigationController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
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
            val navController = rememberNavController()

            val isDarkTheme = remember { mutableStateOf(false) }
            MeetingOrganizerTheme(darkTheme = isDarkTheme.value) {
                NavigationController(navController, userRepository, meetingRepository, this, isDarkTheme)
            }



        }
    }
}

