package com.example.meeting_organizer.ui.meetingList

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.theme.TopBar
import kotlinx.coroutines.launch

@Composable
fun MeetingListScreen(
    user: User,
    onButtonClick: (userId: Int) -> Unit,
    userRepository: UserRepository,
    meetingRepository: MeetingRepository,
    navController: NavController,
    isDarkTheme: MutableState<Boolean>
) {
    val meetings = remember { mutableStateOf(emptyList<Meeting>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    TopBar(navController = navController, isDarkTheme = isDarkTheme) {
        LaunchedEffect(Unit) {
            scope.launch {
                // Pobranie wszystkich spotkań
                val allMeetings = meetingRepository.getAllMeetings()

                // Filtracja spotkań, w których użytkownik jest uczestnikiem
                val userMeetings = allMeetings.filter { meeting ->
                    meeting.participants.any { participant ->
                        participant.id == user.id
                    }
                }

                meetings.value = userMeetings
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    "Meetings for User: ${user.firstName} ${user.lastName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(meetings.value) { meeting ->
                val leader by produceState<User?>(null, meeting.ownerId) {
                    value = userRepository.getUserById(meeting.ownerId)
                }

                ExpandableMeetingItem(
                    context = context,
                    meeting = meeting,
                    leader = leader,
                    currentUser = user,
                    onDeleteClick = { meetingToDelete ->
                        scope.launch {
                            meetingRepository.deleteMeeting(meetingToDelete)
                            meetings.value = meetings.value.filter { it.id != meetingToDelete.id }
                        }
                    }
                )
                Divider()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onButtonClick(user.id) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Meeting")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Meeting", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExpandableMeetingItem(
    context: Context,
    meeting: Meeting,
    leader: User?,
    currentUser: User,
    onDeleteClick: (Meeting) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column {
                    Text("Title: ${meeting.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "Leader: ${leader?.firstName ?: ""} ${leader?.lastName ?: ""}",
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("Start: ${meeting.startTime}", fontSize = 14.sp)
                Text("End: ${meeting.endTime}", fontSize = 14.sp)
                Text("Address: ${meeting.address}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Participants:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                meeting.participants.forEach { participant ->
                    Text("${participant.firstName} ${participant.lastName}", fontSize = 14.sp)
                }

                if (currentUser.id == meeting.ownerId) {
                    Button(
                        onClick = { onDeleteClick(meeting) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }

                Button(
                    onClick = { exportToCalendar(context, meeting, leader) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Export to Calendar")
                }

            }
        }
    }
}

fun exportToCalendar(context: Context, meeting: Meeting, leader: User?) {
    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.Events.TITLE, meeting.title)
        .putExtra(CalendarContract.Events.EVENT_LOCATION, meeting.address)
        .putExtra(
            CalendarContract.Events.DESCRIPTION,
            "Organized by ${leader?.firstName} ${leader?.lastName}"
        )
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, meeting.startTime)
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, meeting.endTime)
    context.startActivity(intent)
}