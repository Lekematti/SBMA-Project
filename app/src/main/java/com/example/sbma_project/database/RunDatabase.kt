package com.example.sbma_project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 13
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {
    abstract fun runDao() :RunDao
}