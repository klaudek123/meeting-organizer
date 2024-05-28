
package com.example.meeting_organizer.ui.meetingScheduler

import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.theme.TopBar
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MeetingSchedulerScreen(
    user: User,
    onScheduleMeeting: (Int, String, String, String, List<User>) -> Unit,
    userRepository: UserRepository,
    navController: NavController,
    isDarkTheme: MutableState<Boolean>
) {
    var meetingTitle by remember { mutableStateOf("") }
    var meetingStartTime by remember { mutableStateOf("") }
    var meetingEndTime by remember { mutableStateOf("") }
    var meetingLocation by remember { mutableStateOf<LatLng?>(null) }
    var addressText by remember { mutableStateOf("") }
    val selectedUsers = remember { mutableStateListOf<User>() }
    val availableUsers = remember { mutableStateListOf<User>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val users = userRepository.getAllUsers()
        availableUsers.addAll(users)
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()


    fun showDateTimePicker(context: Context, onDateTimeSelected: (String) -> Unit) {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        android.app.DatePickerDialog(context, { _, year, month, dayOfMonth ->
            TimePickerDialog(context, { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                onDateTimeSelected(dateFormatter.format(calendar.time))
            }, currentHour, currentMinute, true).show()
        }, currentYear, currentMonth, currentDay).show()
    }
    TopBar(navController = navController, isDarkTheme = isDarkTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Welcome, ${user.firstName}!", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = meetingTitle,
                onValueChange = { meetingTitle = it },
                label = { Text("Meeting Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = meetingStartTime,
                    onValueChange = {},
                    label = { Text("Start Time") },
                    modifier = Modifier
                        .width(200.dp),
                    readOnly = true
                )
                Button(
                    onClick = {
                        showDateTimePicker(context) { dateTime ->
                            meetingStartTime = dateTime
                        }
                    }
                ) {
                    Text("Calendar")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = meetingEndTime,
                    onValueChange = {},
                    label = { Text("End Time") },
                    modifier = Modifier
                        .width(200.dp),
                    readOnly = true
                )
                Button(
                    onClick = {
                        showDateTimePicker(context) { dateTime ->
                            meetingEndTime = dateTime
                        }
                    }
                ) {
                    Text("Calendar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    label = { Text("Location") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // Perform action on Done button click
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                )
                AutocompleteFragmentContainer(placeSelectionListener = object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        // Obsługa wybranego miejsca
                        addressText = place.address ?: ""
                    }

                    override fun onError(status: Status) {
                        // Obsługa błędu
                        Log.e(TAG, "Error selecting place: $status")
                    }
                })
            }
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                AndroidView(factory = { context ->
                    MapView(context).apply {
                        onCreate(null)
                        getMapAsync { googleMap ->
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(52.4064, 16.9252), // (Poznań, Poland)
                                    9.0f
                                )
                            )

                            googleMap.setOnMapClickListener { latLng ->
                                meetingLocation = latLng
                                addressText = getAddressFromLocation(context, latLng)
                                googleMap.clear() // Usuń poprzednie znaczniki
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .anchor(0.5f, 1.0f) // Ustawienie kotwicy
                                )
                            }
                            meetingLocation?.let {
                                googleMap.clear()
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(it)
                                        .anchor(0.5f, 1.0f)
                                )
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15.0f))
                            }
                        }
                    }
                })
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Lista wielokrotnego wyboru dla użytkowników
            Text("Select participants:")
            UserSelectionScreen(selectedItems = selectedUsers, availableUsers = availableUsers, onItemSelected = { user ->
                if (selectedUsers.contains(user)) {
                    selectedUsers.remove(user)
                } else {
                    selectedUsers.add(user)
                }
            })

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                onScheduleMeeting(user.id,meetingTitle, meetingStartTime, addressText, selectedUsers)
            }) {
                Text("Schedule Meeting")
            }
        }
    }
}


@Composable
fun AutocompleteFragmentContainer(
    placeSelectionListener: PlaceSelectionListener
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val fragmentActivity = context as? FragmentActivity ?: return@DisposableEffect onDispose {  }
        val fragmentWrapper = AutocompleteFragmentWrapper(context, placeSelectionListener)
        fragmentActivity.supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragmentWrapper)
            .commit()
        onDispose {
            fragmentWrapper.requireFragmentManager().beginTransaction()
                .remove(fragmentWrapper)
                .commitAllowingStateLoss()
        }
    }
}
fun getAddressFromLocation(context: Context, location: LatLng): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return if (addresses!!.isNotEmpty() && addresses[0] != null) {
        addresses[0]?.getAddressLine(0) ?: "Unknown location"
    } else {
        "Unknown location"
    }
}