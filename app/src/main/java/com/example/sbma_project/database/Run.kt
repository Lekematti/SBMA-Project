package com.example.sbma_project.database

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Entity(tableName = "runs")
@TypeConverters(Converters::class)
data class Run(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationInMillis: Long = 0L,
    val routePath : List<LatLng>?,
    val rating : Int?,
    val notes : String?,
    val speed: Double?,
    val distance: Double?,
    val steps: Int?,
    val stepLength: Double?,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var modifiedAt: Date?,
)

@Dao
interface RunDao {
    @Insert
    suspend fun insertRun(run: Run)

    @Query("SELECT * FROM runs")
    fun getAllRuns(): LiveData<List<Run>>

    @Query("DELETE FROM runs WHERE id = :runId")
    suspend fun deleteRunById(runId: Long)

}

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromLatLngList(value: List<LatLng>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toLatLngList(value: String?): List<LatLng>? {
        val listType = object : TypeToken<List<LatLng>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}