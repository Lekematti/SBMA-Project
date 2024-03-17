package com.example.sbma_project.repository

import androidx.lifecycle.LiveData
import com.example.sbma_project.database.Run
import com.example.sbma_project.database.RunDao
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





