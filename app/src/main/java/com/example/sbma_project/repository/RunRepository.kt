package com.example.sbma_project.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.StepCounter
import com.example.sbma_project.database.Run
import com.example.sbma_project.database.RunDao
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

    fun getRunById(runId: Long): LiveData<Run?> {
        return runDao.getRunById(runId)
    }

    suspend fun updateRunRating(runId: Long, rating: Int){
        return runDao.updateRunRating(runId, rating)
    }

    suspend fun updateRunNotes(runId: Long, notes: String?){
        return runDao.updateRunNotes(runId, notes ?: "")
    }

    suspend fun updateModifiedDate(runId: Long, newModifiedAt : Date){
        return runDao.updateModifiedAt(runId, newModifiedAt)
    }
}

@HiltViewModel
class RunViewModel @Inject constructor(private val runRepository: RunRepository) :
    ViewModel() {
    val runs: LiveData<List<Run>> = runRepository.getAllRuns()

    fun createRun(
        startTime: Long,
        routePath: List<LatLng>,
        speedList: List<Float>,
        rating : Int? = null,
        notes : String? = null,
        speedTimestamps: List<Long>?,
        avgSpeed :Float?,
        stepLength : Double?,
    ) {
        viewModelScope.launch {
            val currentTimestamp = Date() // Get current date and time
            val totalDistance = DistanceCalculator.calculateTotalDistance(routePath)
            val steps = StepCounter.saveSteps()
            Log.d("Stats","Steps: $steps, Step Length: $stepLength, Distance: $totalDistance, Speed: $avgSpeed")
            val newRun = Run(
                durationInMillis = startTime,
                routePath = routePath,
                createdAt = currentTimestamp,
                modifiedAt = null,
                rating = rating,
                notes = notes,
                speedList = speedList,
                speedTimestamps = speedTimestamps,
                avgSpeed = avgSpeed,
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

     fun updateRunRating(runId: Long, rating: Int){
         viewModelScope.launch {
             runRepository.updateRunRating(runId, rating)
         }
    }

    fun updateRunNotes(runId: Long, notes: String?){
        viewModelScope.launch {
            runRepository.updateRunNotes(runId, notes ?: "")
        }
    }

    fun getRunById(runId: Long): LiveData<Run?> {
        return runRepository.getRunById(runId)
    }

    fun updateModifiedDate(runId: Long, newModifiedAt: Date){
        viewModelScope.launch {
            runRepository.updateModifiedDate(runId, newModifiedAt)
        }
    }
}


