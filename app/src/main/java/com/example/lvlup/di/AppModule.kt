// app/src/main/java/com/example/lvlup/di/AppModule.kt

package com.example.lvlup.di

import android.content.Context
import androidx.room.Room
import com.example.lvlup.data.local.AppDatabase
import com.example.lvlup.data.local.TaskDao
import com.example.lvlup.data.repository.TaskRepository
import com.example.lvlup.notification.TaskAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lvlup_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }

    @Provides
    @Singleton
    fun provideTaskAlarmScheduler(@ApplicationContext context: Context): TaskAlarmScheduler {
        return TaskAlarmScheduler(context)
    }
}