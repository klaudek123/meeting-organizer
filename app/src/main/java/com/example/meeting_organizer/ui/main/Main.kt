package com.example.meeting_organizer.ui.main

import android.app.TimePickerDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.MotionEvent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.User
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: User, onScheduleMeeting: (String, String, LatLng) -> Unit) {
    var meetingTitle by remember { mutableStateOf("") }
    var meetingStartTime by remember { mutableStateOf("") }
    var meetingEndTime by remember { mutableStateOf("") }
    var meetingLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationText by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Initialize osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )
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

    suspend fun getAddressFromGeoPoint(geoPoint: GeoPoint): String {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressFragments = with(address) {
                    listOfNotNull(thoroughfare, subThoroughfare, locality)
                }
                addressFragments.joinToString(", ")
            } else {
                "Unknown location"
            }
        }
    }

    LaunchedEffect(meetingLocation) {
        meetingLocation?.let {
            addressText = getAddressFromGeoPoint(it)
        }
    }

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
        OutlinedTextField(
            value = locationText,
            onValueChange = { newValue ->
                locationText = newValue
                val parts = newValue.split(",").map { it.trim() }
                if (parts.size == 2) {
                    val latitude = parts[0].toDoubleOrNull()
                    val longitude = parts[1].toDoubleOrNull()
                    if (latitude != null && longitude != null) {
                        meetingLocation = GeoPoint(latitude, longitude)
                    }
                }
            },
            label = { Text("Location (lat, lon)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Address: $addressText", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        val mapController = this.controller
                        mapController.setZoom(9.0)
                        mapController.setCenter(GeoPoint(52.4064, 16.9252)) // (Poznań, Poland)

                        this.setMultiTouchControls(true)

                        // Add a touch overlay to capture click events
                        val touchOverlay = object : Overlay() {
                            override fun onSingleTapConfirmed(
                                e: MotionEvent,
                                mapView: MapView
                            ): Boolean {
                                val geoPoint = mapView.projection.fromPixels(
                                    e.x.toInt(),
                                    e.y.toInt()
                                ) as GeoPoint
                                meetingLocation = geoPoint
                                locationText = "${geoPoint.latitude}, ${geoPoint.longitude}"
                                mapView.overlays.clear() // Clear previous markers
                                val marker = Marker(mapView)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                mapView.overlays.add(marker)
                                mapView.invalidate() // Redraw the map
                                return true
                            }
                        }
                        this.overlays.add(touchOverlay)
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear() // Clear previous overlays
                    meetingLocation?.let {
                        val marker = Marker(mapView)
                        marker.position = it
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(marker)
                        mapView.controller.setCenter(it)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
        Button(onClick = {
            meetingLocation?.let { location ->
                if (meetingTitle.isNotEmpty() && meetingStartTime.isNotEmpty() && meetingEndTime.isNotEmpty()) {
                    scope.launch {
                        meetingRepository.insertMeeting(
                            Meeting(
                                title = meetingTitle,
                                startTime = meetingStartTime,
                                endTime = meetingEndTime,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                address = addressText
                            )
                        )
                    }
                    onScheduleMeeting(meetingTitle, "$meetingStartTime to $meetingEndTime", location.toLatLng())
                }
            }
        }) {
            Text("Schedule Meeting")
        }
    }
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

