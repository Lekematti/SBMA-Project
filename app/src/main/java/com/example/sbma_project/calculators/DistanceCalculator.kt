package com.example.sbma_project.calculators

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object DistanceCalculator {
    fun calculateTotalDistance(pathPoints: List<LatLng>): Double {
        var totalDistance = 0.0

        if (pathPoints.size > 1) {
            for (i in 0 until pathPoints.size - 1) {
                val startPoint = pathPoints[i]
                val endPoint = pathPoints[i + 1]

                val startLocation = Location("").apply {
                    latitude = startPoint.latitude
                    longitude = startPoint.longitude
                }

                val endLocation = Location("").apply {
                    latitude = endPoint.latitude
                    longitude = endPoint.longitude
                }

                // Convert the result to Double
                totalDistance += startLocation.distanceTo(endLocation).toDouble()
            }
        }
        // Format to keep only one decimal place
        return String.format("%.1f", totalDistance).replace(',', '.').toDouble()
    }
}
