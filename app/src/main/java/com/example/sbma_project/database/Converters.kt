package com.example.sbma_project.database

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

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

    @TypeConverter
    @JvmStatic
    fun fromFloatList(value: List<Float>?): String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    @JvmStatic
    fun toFloatList(value: String?): List<Float>? {
        return value?.split(",")?.map { it.toFloat() }
    }

    @TypeConverter
    @JvmStatic
    fun fromTimestampsList(value: List<Long>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun toTimestampsList(value: String?): List<Long>? {
        return value?.split(",")?.map { it.toLong() }
    }
}