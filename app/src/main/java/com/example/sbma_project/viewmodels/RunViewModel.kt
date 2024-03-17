package com.example.sbma_project.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.StepCounter
import com.example.sbma_project.database.Run
import com.example.sbma_project.repository.RunRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(private val runRepository: RunRepository, /*private val userRepository: UserRepository*/) :
    ViewModel() {
    val runs: LiveData<List<Run>> = runRepository.getAllRuns()

    fun createRun(
        startTime: Long,
        routePath: List<LatLng>,
        speedList: List<Float>,
        rating : Int? = null,
        notes : String? = null,
        speedTimestamps: List<Long>?,
        avgSpeed :Float?
    ) {
        viewModelScope.launch {
            val currentTimestamp = Date() // Get current date and time
            val totalDistance = DistanceCalculator.calculateTotalDistance(routePath)
            val steps = StepCounter.saveSteps()
            /*val stepLengthCalculator = StepLengthCalculator(this@RunViewModel)
            val stepLength = stepLengthCalculator.calculateAverageStepLength()*/
            Log.d("Stats","Steps: $steps, Step Length: $/*stepLength*/, Distance: $totalDistance, Speed: $avgSpeed")
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
                stepLength = null,
                steps = steps
            )
            viewModelScope.launch {
                runRepository.insertRun(newRun)
            }
        }
    }

    /*private var _showHeightDialog by mutableStateOf(false)
    val showHeightDialog: Boolean
        get() = _showHeightDialog

    fun setShowHeightDialog(showDialog: Boolean) {
        _showHeightDialog = showDialog
    }


    var onSaveHeight: ((Boolean) -> Unit)? = null
    private var userHeight: Double = 0.0
    init {
        // Initialize userHeight with the stored height from the repository
        viewModelScope.launch {
            val storedHeight = userRepository.getUserHeight()
            userHeight = storedHeight ?: 0.0
        }
    }
    // Function to update user height
    fun setUserHeight(height: Double) {
        userHeight = height
        viewModelScope.launch {
            userRepository.updateRunUserHeight(height)
            onSaveHeight?.invoke(true)
        }
    }

    fun getUserHeight(): Double {
        viewModelScope.launch {
            val storedHeight = userRepository.getUserHeight()
            userHeight = storedHeight ?: 0.0
        }
        return userHeight
    }*/
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