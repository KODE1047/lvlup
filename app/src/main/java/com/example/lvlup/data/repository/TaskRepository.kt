// app/src/main/java/com/example/lvlup/data/repository/TaskRepository.kt

package com.example.lvlup.data.repository

import com.example.lvlup.data.local.TaskDao
import com.example.lvlup.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    fun getTaskById(id: Int): Flow<Task?> = taskDao.getTaskById(id)

    suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}