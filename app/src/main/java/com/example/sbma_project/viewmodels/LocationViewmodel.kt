package com.example.sbma_project.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.StepCounter
import com.example.sbma_project.domain.GetLocationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject



@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel(){


    //MutableStateFlow to hold the list of speeds
    private val _speedList: MutableStateFlow<List<Float>> = MutableStateFlow(emptyList())
    val speedList = _speedList.asStateFlow()

    // MutableStateFlow to hold the list of timestamps corresponding to each speed
    private val _speedTimestamps: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    val speedTimestamps = _speedTimestamps.asStateFlow()

    // Update the speed list and timestamps
    private fun updateSpeedListAndTimestamps(speed: Float, timestamp: Long) {
        val currentSpeedList = _speedList.value.toMutableList()
        val currentTimestamps = _speedTimestamps.value.toMutableList()

        currentSpeedList.add(speed)
        currentTimestamps.add(timestamp)

        _speedList.value = currentSpeedList
        _speedTimestamps.value = currentTimestamps
    }
    var totalTimeInHours: Float = 0f
        private set

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    private val _speed: MutableStateFlow<Float> = MutableStateFlow(0f)
    val speed = _speed.asStateFlow()

    // State to hold the list of LatLng points representing the polyline
    private val _pathPoints: MutableStateFlow<List<LatLng>> = MutableStateFlow(emptyList())
    val pathPoints = _pathPoints.asStateFlow()

    //DISTANCE
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
        StepCounter.start(context)
    }


    // Function to pause the run
    fun pauseRun() {
        runningState = RunningState.Paused
        _stopButtonEnabled.value = false
        _speed.value = 0f
        StepCounter.pause()
    }

    // Function to resume the run
    fun resumeRun() {
        runningState = RunningState.Running
        _stopButtonEnabled.value = true
        startLocationUpdates()
        StepCounter.resume()
    }

    // Function to finish the run
    fun finishRun() {
        runningState = RunningState.Stopped
        _stopButtonEnabled.value = false
        locationJob?.cancel()
        totalTimeInHours = time.value / 3600f
        _speed.value = 0f
        StepCounter.stop()
    }

    fun resetPathPoints(){
        _pathPoints.value = emptyList()

    }

    private var locationJob: Job? = null

    private fun startLocationUpdates() {
        if (runningState != RunningState.Paused) {
            locationJob?.cancel()
        }

        locationJob = viewModelScope.launch {
            getLocationUseCase.invoke().collect { locationWithSpeed ->
                // Update viewState with location data

                if (locationWithSpeed != null) {
                    _viewState.value = ViewState.Success(locationWithSpeed.location)
                }

                // If the user is currently running, update pathPoints with new location
                if (runningState == RunningState.Running) {
                    if (locationWithSpeed != null) {
                        _pathPoints.value = _pathPoints.value + (locationWithSpeed.location ?: LatLng(0.00, 0.00))
                        Log.d("locationSpeed", "${locationWithSpeed.location}")
                        val speed = (locationWithSpeed.speed * 3.6f).toBigDecimal()
                            .setScale(2, RoundingMode.HALF_UP).toFloat()
                        _speed.value = speed
                        val currentTimeMillis = System.currentTimeMillis()
                        updateSpeedListAndTimestamps(speed, currentTimeMillis)
                    }
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
