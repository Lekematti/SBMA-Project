package com.example.sbma_project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class, Timer::class],
    version = 8
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {

    abstract fun runDao(): RunDao

    abstract fun timerDao() :TimerDao
}