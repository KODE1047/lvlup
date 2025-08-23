// app/src/main/java/com/example/lvlup/ui/viewmodel/TaskViewModel.kt

package com.example.lvlup.ui.viewmodel

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lvlup.data.repository.TaskRepository
import com.example.lvlup.model.HierarchicalTask
import com.example.lvlup.model.Task
import com.example.lvlup.notification.TaskAlarmScheduler
import com.example.lvlup.widgets.DetailedAnalyticsWidget
import com.example.lvlup.widgets.SimpleAnalyticsWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// REMOVED: The HierarchicalTask data class is no longer here.

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: TaskAlarmScheduler,
    private val application: Application
) : ViewModel() {
    // ... rest of the file is unchanged ...
    val hierarchicalTasks: StateFlow<List<HierarchicalTask>> = repository.getTasks()
        .map { tasks ->
            val subTaskMap = tasks.filter { it.parentId != null }
                .groupBy { it.parentId!! }

            fun buildHierarchy(task: Task): HierarchicalTask {
                val subTasks = subTaskMap[task.id]?.map { buildHierarchy(it) } ?: emptyList()
                return HierarchicalTask(task, subTasks)
            }

            tasks.filter { it.parentId == null }.map { buildHierarchy(it) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTasks: StateFlow<List<Task>> = repository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getTaskById(id: Int) = repository.getTaskById(id)

    fun upsertTask(task: Task) = viewModelScope.launch {
        repository.upsertTask(task)
        if (task.dueDate != null && !task.isCompleted) {
            alarmScheduler.schedule(task)
        } else {
            alarmScheduler.cancel(task)
        }
        updateWidgets()
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        alarmScheduler.cancel(task)
        repository.deleteTask(task)
        updateWidgets()
    }

    fun toggleTaskCompleted(task: Task) = viewModelScope.launch {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        repository.upsertTask(updatedTask)
        if (updatedTask.isCompleted) {
            alarmScheduler.cancel(updatedTask)
        } else if (updatedTask.dueDate != null) {
            alarmScheduler.schedule(updatedTask)
        }
        updateWidgets()
    }

    private suspend fun updateWidgets() {
        SimpleAnalyticsWidget().updateAll(application)
        DetailedAnalyticsWidget().updateAll(application)
    }
}