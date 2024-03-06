package com.example.sbma_project.locationData

import kotlinx.coroutines.flow.Flow

interface ILocationService {

    fun requestLocationUpdates(): Flow<LocationWithSpeed?>

    fun requestCurrentLocation(): Flow<LocationWithSpeed?>
}