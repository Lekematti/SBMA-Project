package com.example.sbma_project.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.StepCounter
//import com.example.sbma_project.calculators.StepLengthCalculator
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





