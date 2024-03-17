package com.example.sbma_project.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date


@Dao
interface RunDao {
    @Insert
    suspend fun insertRun(run: Run)

    @Query("SELECT * FROM runs")
    fun getAllRuns(): LiveData<List<Run>>

    @Query("SELECT * FROM runs WHERE id = :runId")
    fun getRunById(runId: Long): LiveData<Run?>

    @Query("DELETE FROM runs WHERE id = :runId")
    suspend fun deleteRunById(runId: Long)

    @Query("UPDATE runs SET rating = :newRating WHERE id = :runId")
    suspend fun updateRunRating(runId: Long, newRating: Int)

    @Query("UPDATE runs SET notes = :newNote WHERE id = :runId")
    suspend fun updateRunNotes(runId: Long, newNote: String)

    @Query("UPDATE runs SET modifiedAt = :newModifiedAt WHERE id = :runId")
    suspend fun updateModifiedAt(runId: Long, newModifiedAt : Date)

}