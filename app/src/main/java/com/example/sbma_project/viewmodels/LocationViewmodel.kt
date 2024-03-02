package com.example.sbma_project.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.distance.DistanceCalculator
import com.example.sbma_project.domain.GetLocationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
) : ViewModel(){

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    // State to hold the list of LatLng points representing the polyline
    private val _pathPoints: MutableStateFlow<List<LatLng>> = MutableStateFlow(emptyList())
    val pathPoints = _pathPoints.asStateFlow()

    // Variable to keep the total distance that can be observed from UI
    private var totalDistance = 0.0
    private var isDistanceUpdateEnabled = true // Flag to control distance updates

    private fun calculateDistance() {
        totalDistance = DistanceCalculator.calculateTotalDistance(pathPoints.value)
    }

    fun resumeDistance() {
        isDistanceUpdateEnabled = true
        updateDistance()
    }

    fun pauseDistance() {
        isDistanceUpdateEnabled = false
    }

    fun resetDistance() {
        totalDistance = 0.0
        _pathPoints.value = emptyList()
    }

    // Modify the existing updateDistance method to call calculateDistance
    private fun updateDistance() {
        if (isDistanceUpdateEnabled) {
            calculateDistance()
        }
    }

    var runningState : RunningState = RunningState.Stopped
        private set

    // Function to start the run
    fun startRun() {
        runningState = RunningState.Running
        _stopButtonEnabled.value = true
        startLocationUpdates()
    }

    // Function to pause the run
    fun pauseRun() {
        runningState = RunningState.Paused
        _stopButtonEnabled.value = false
    }

    // Function to resume the run
    fun resumeRun() {
        runningState = RunningState.Running
        _stopButtonEnabled.value = true
        startLocationUpdates()
    }

    // Function to finish the run
    fun finishRun() {
        runningState = RunningState.Stopped
        _stopButtonEnabled.value = false
        locationJob?.cancel()
    }
    fun resetPathPoints(){
        _pathPoints.value = emptyList()

    }

    private var locationJob: Job? = null
    // Modify the existing startLocationUpdates method to update pathPoints

    private fun startLocationUpdates() {
        if (runningState != RunningState.Paused) {
            locationJob?.cancel()
        }

        locationJob = viewModelScope.launch {
            getLocationUseCase.invoke().collect { location ->
                // Update viewState with location data
                _viewState.value = ViewState.Success(location)

                // If the user is currently running, update pathPoints with new location
                if (runningState == RunningState.Running) {
                    _pathPoints.value = _pathPoints.value + (location ?: LatLng(0.00, 0.00))
                    updateDistance()
                }
            }
        }
    }

    private val _time = MutableStateFlow(0L)
    val time = _time.asStateFlow()

    private val _stopButtonEnabled = MutableStateFlow(false)
    val stopButtonEnabled = _stopButtonEnabled.asStateFlow()

    fun resetTime() {
        _time.value = 0L
        runningState = RunningState.Stopped
        _stopButtonEnabled.value = false
    }
    fun updateTime(newTime: Long) {
        _time.value = newTime
    }

    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                startLocationUpdates()
            }
            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }
}

sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val location: LatLng?) : ViewState
    data object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    data object Granted : PermissionEvent
    data object Revoked : PermissionEvent
}

sealed class RunningState {
    data object Running : RunningState()
    data object Paused : RunningState()
    data object Stopped : RunningState()
}
