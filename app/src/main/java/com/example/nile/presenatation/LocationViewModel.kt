package com.example.nile.presenatation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nile.data.model.LocationState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    lateinit var fusedLocationClient: FusedLocationProviderClient

    var locationState by mutableStateOf<LocationState>(LocationState.NoPermission)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        locationState = LocationState.LocationLoading
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                locationState = if (location == null && locationState !is LocationState.LocationAvailable) {
                    LocationState.Error
                } else {
                    LocationState.LocationAvailable(LatLng(location.latitude, location.longitude))
                }
            }
    }
}