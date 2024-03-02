package com.example.sbma_project.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.database.Timer
import com.example.sbma_project.database.TimerDao
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


class TimerRepository @Inject constructor(private val timerDao: TimerDao) {

    suspend fun insertTimer(timer: Timer) {
        timerDao.insertTimer(timer)
    }

    fun getAllTimers(): LiveData<List<Timer>> {
        return timerDao.getAllTimers()
    }
}

@HiltViewModel
class TimerViewModel @Inject constructor(private val timerRepository: TimerRepository) :
    ViewModel() {
    val timers: LiveData<List<Timer>> = timerRepository.getAllTimers()

    fun createTimer(startTime: Long, routePath: List<LatLng>) {
        viewModelScope.launch {
            val currentTimestamp = Date() // Get current date and time
            val newTimer = Timer(durationInMillis = startTime, routePath = routePath, createdAt = currentTimestamp, modifiedAt = null)
            viewModelScope.launch {
                timerRepository.insertTimer(newTimer)
            }
        }
    }

}


