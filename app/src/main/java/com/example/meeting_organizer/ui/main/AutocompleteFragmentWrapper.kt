package com.example.meeting_organizer.ui.main

import android.content.Context
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class AutocompleteFragmentWrapper(
    private val context: Context,
    private val placeSelectionListener: PlaceSelectionListener
) : AutocompleteSupportFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        setOnPlaceSelectedListener(object : com.google.android.libraries.places.widget.listener.PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                placeSelectionListener.onPlaceSelected(place)
            }

            override fun onError(status: Status) {
                placeSelectionListener.onError(status)
            }
        })
    }
}