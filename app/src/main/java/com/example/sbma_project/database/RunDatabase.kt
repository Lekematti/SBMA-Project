package com.example.sbma_project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [/*Run::class,*/ Timer::class],
    version = 8
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {

    //abstract fun runDao(): RunDao

    abstract fun timerDao() :TimerDao

    companion object {
        @Volatile
        private var INSTANCE: RunDatabase? = null

        fun getDatabase(context: Context): RunDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunDatabase::class.java,
                    "run_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}