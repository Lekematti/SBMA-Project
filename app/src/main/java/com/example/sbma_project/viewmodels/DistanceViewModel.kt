/*
package com.example.sbma_project.viewmodels

import androidx.lifecycle.ViewModel
import com.example.sbma_project.distance.DistanceCalculator
import com.google.android.gms.maps.model.LatLng

class DistanceViewModel : ViewModel() {
    // Mutable list to keep track of path points
    private val _pathPoints = mutableListOf<LatLng>()
    val pathPoints: List<LatLng>
        get() = _pathPoints

    // Variable to keep the total distance that can be observed from UI
    private var totalDistance = 0.0
    private var isDistanceUpdateEnabled = true // Flag to control distance updates

    fun addPathPoint(point: LatLng) {
        _pathPoints.add(point)
        if (isDistanceUpdateEnabled) {
            updateDistance()
        }
    }

    private fun calculateDistance() {
        totalDistance = DistanceCalculator.calculateTotalDistance(_pathPoints)
    }

    fun pauseDistance() {
        isDistanceUpdateEnabled = false
    }

    fun resumeDistance() {
        isDistanceUpdateEnabled = true
        updateDistance()
    }

    fun resetDistance() {
        totalDistance = 0.0
        _pathPoints.clear()
    }

    private fun updateDistance() {
        if (isDistanceUpdateEnabled) {
            calculateDistance()
        }
    }
}*/
