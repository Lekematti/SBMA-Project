package com.example.sbma_project.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface ILocationService {

    fun requestLocationUpdates(): Flow<LocationWithSpeed?>

    fun requestCurrentLocation(): Flow<LocationWithSpeed?>
}