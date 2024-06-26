package com.example.meeting_organizer.ui.meetingScheduler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.meeting_organizer.data.model.User

@Composable
fun UserSelectionScreen(selectedItems: MutableList<User>, availableUsers: List<User>, onItemSelected: (User) -> Unit) {
    var filterText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = filterText,
            onValueChange = { filterText = it },
            label = { Text("Filter users") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CheckboxList(
            selectedItems = selectedItems,
            items = availableUsers.filter { user ->
                user.firstName.contains(filterText, ignoreCase = true) || user.lastName.contains(filterText, ignoreCase = true)
            },
            onItemSelected = onItemSelected
        )
    }
}


@Composable
fun CheckboxList(selectedItems: MutableList<User>, items: List<User>, onItemSelected: (User) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(6.dp)
    ) {
        items(items.size) { index ->
            val user = items[index]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemSelected(user) }
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = selectedItems.contains(user),
                    onCheckedChange = {
                        onItemSelected(user)
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}