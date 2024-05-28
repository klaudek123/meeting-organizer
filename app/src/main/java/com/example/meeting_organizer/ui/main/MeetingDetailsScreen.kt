package com.example.meeting_organizer.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.model.Meeting
import kotlinx.coroutines.launch

@Composable
fun MeetingListScreen(userId: Int, meetingRepository: MeetingRepository) {
    val meetings = remember { mutableStateOf(emptyList<Meeting>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            meetings.value = meetingRepository.getMeetingsForUser(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Meetings for User $userId", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(meetings.value) { meeting ->
                MeetingItem(meeting = meeting)
                Divider()
            }
        }
    }
}

@Composable
fun MeetingItem(meeting: Meeting) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Title: ${meeting.title}", fontWeight = FontWeight.Bold)
        Text("Start Time: ${meeting.startTime}")
        Text("End Time: ${meeting.endTime}")
        Text("Address: ${meeting.address}")
    }
}


// TODO dodać:
// 1. guzik do dodawania meetingu
// 2. użytkowników do meetingów
// 3. powiadomienia do nich
// 4.