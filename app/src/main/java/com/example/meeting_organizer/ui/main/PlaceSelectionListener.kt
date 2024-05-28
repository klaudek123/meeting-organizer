package com.example.meeting_organizer.ui.main

import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place

interface PlaceSelectionListener {
    fun onPlaceSelected(place: Place)
    fun onError(status: Status)
}