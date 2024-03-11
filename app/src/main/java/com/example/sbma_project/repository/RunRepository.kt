package com.example.sbma_project.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.StepCounter
import com.example.sbma_project.database.Run
import com.example.sbma_project.database.RunDao
import com.example.sbma_project.uiComponents.calculateAverageSpeed
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


class RunRepository @Inject constructor(private val runDao: RunDao) {

    suspend fun insertRun(run: Run) {
        runDao.insertRun(run)
    }

    fun getAllRuns(): LiveData<List<Run>> {
        return runDao.getAllRuns()
    }

    suspend fun deleteRunById(runId: Long) {
        runDao.deleteRunById(runId)
    }
}

@HiltViewModel
class RunViewModel @Inject constructor(private val runRepository: RunRepository) :
    ViewModel() {
    val runs: LiveData<List<Run>> = runRepository.getAllRuns()


    fun createRun(
        startTime: Long,
        routePath: List<LatLng>,
        rating : Int? = null,
        notes : String? = null,
        stepLength : Double?,

    ) {
        viewModelScope.launch {
            val currentTimestamp = Date() // Get current date and time
            // Calculate the total distance before creating the Run object
            val totalDistance = DistanceCalculator.calculateTotalDistance(routePath)
            val avgSpeed = calculateAverageSpeed(totalDistance, startTime)
            val steps = StepCounter.saveSteps()

            Log.d("Steppi","$steps")
            val newRun = Run(durationInMillis = startTime,
                routePath = routePath,
                createdAt = currentTimestamp,
                modifiedAt = null,
                rating = rating,
                notes = notes,
                speed = avgSpeed.toDouble(),
                distance = totalDistance,
                stepLength = stepLength,
                steps = steps,
            )
            viewModelScope.launch {
                runRepository.insertRun(newRun)
            }
        }
    }

    fun deleteRunById(runId: Long) {
        viewModelScope.launch {
            runRepository.deleteRunById(runId)
        }
    }

}


