package com.example.meeting_organizer.ui.meetingList

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.theme.TopBar
import com.example.meeting_organizer.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun MeetingListScreen(
    user: User,
    onButtonClick: (userId: Int) -> Unit,
    meetingRepository: MeetingRepository,
    navController: NavController,
    isDarkTheme: MutableState<Boolean>
) {
    val meetings = remember { mutableStateOf(emptyList<Meeting>()) }
    val scope = rememberCoroutineScope()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Meetings for User ${user.firstName} ${user.lastName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(meetings.value) { meeting ->
                    ExpandableMeetingItem(meeting = meeting)
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onButtonClick(user.id) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Meeting")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Meeting", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExpandableMeetingItem(meeting: Meeting) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Text("Title: ${meeting.title}", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Start Time: ${meeting.startTime}", fontSize = 14.sp)
                Text("End Time: ${meeting.endTime}", fontSize = 14.sp)
                Text("Address: ${meeting.address}", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Participants:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                meeting.participants.forEach { participant ->
                    Text("${participant.firstName} ${participant.lastName}", fontSize = 14.sp)
                }
            }
        }
    }
}


// TODO dodać:
// 3. powiadomienia do nich
// 4.