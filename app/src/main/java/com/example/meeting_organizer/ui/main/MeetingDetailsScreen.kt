package com.example.meeting_organizer.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeting_organizer.data.model.User

@Composable
fun MeetingDetailsScreen(
    user: User,
    meetingTitle: String,
    meetingTime: String,
    onConfirmAttendance: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Meeting: $meetingTitle", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Time: $meetingTime", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onConfirmAttendance(true) }) {
            Text("Confirm Attendance")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onConfirmAttendance(false) }) {
            Text("Decline")
        }
    }
}