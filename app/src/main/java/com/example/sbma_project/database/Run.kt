package com.example.sbma_project.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
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
    val speedList: List<Float>?,
    val speedTimestamps: List<Long>?,
    val avgSpeed :Float?,
    val distance: Double?,
    val steps: Int?,
    val stepLength: Double?,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    var modifiedAt: Date?,
)