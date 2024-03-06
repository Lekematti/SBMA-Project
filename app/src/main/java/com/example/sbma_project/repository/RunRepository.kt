package com.example.sbma_project.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}

@HiltViewModel
class RunViewModel @Inject constructor(private val runRepository: RunRepository) :
    ViewModel() {
    val runs: LiveData<List<Run>> = runRepository.getAllRuns()

    fun createRun(startTime: Long, routePath: List<LatLng>, rating : Int? = null, notes : String? = null) {
        viewModelScope.launch {
            val currentTimestamp = Date() // Get current date and time
            val newRun = Run(durationInMillis = startTime, routePath = routePath, createdAt = currentTimestamp, modifiedAt = null, rating = rating, notes = notes)
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


