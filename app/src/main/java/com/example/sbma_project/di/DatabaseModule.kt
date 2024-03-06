package com.example.sbma_project.di

import android.content.Context
import androidx.room.Room
import com.example.sbma_project.database.RunDatabase
import com.example.sbma_project.database.RunDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): RunDatabase {
        return Room.databaseBuilder(context, RunDatabase::class.java, "run_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideTimerDao(runDatabase: RunDatabase): RunDao {
        return runDatabase.runDao()
    }
}