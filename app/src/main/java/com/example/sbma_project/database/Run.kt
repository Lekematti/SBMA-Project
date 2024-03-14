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

//@Entity(tableName = "user")
//data class User(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val userHeight: Double?
//)
//
//@Dao
//interface UserDao {
//    @Insert
//    suspend fun insertUser(user: User)
//
//    @Query("SELECT * FROM user WHERE id = :userId")
//    suspend fun getUserById(userId: Long): User?
//
//    @Query("UPDATE user SET userHeight = :height")
//    suspend fun updateRunUserHeight(height: Double)
//
//    @Query("SELECT userHeight FROM user LIMIT 1")
//    suspend fun getUserHeight(): Double?
//}
