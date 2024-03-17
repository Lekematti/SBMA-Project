package com.example.sbma_project.calculators

fun calculateAverageSpeed(totalDistanceInMeters: Double, totalTimeInSeconds: Long): Float {
    val totalDistanceInKm = totalDistanceInMeters / 1000 // Convert total distance to kilometers
    val totalTimeInHours = totalTimeInSeconds / 3600f // Convert total time to hours
    return if (totalTimeInHours > 0 && totalDistanceInKm > 0) {
        (totalDistanceInKm / totalTimeInHours).toFloat() // Calculate average speed
    } else {
        0f // Return 0 if either time or distance is 0
    }
}
