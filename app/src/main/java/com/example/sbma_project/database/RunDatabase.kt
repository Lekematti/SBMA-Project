package com.example.sbma_project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Timer::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {
    abstract fun timerDao() :TimerDao
}