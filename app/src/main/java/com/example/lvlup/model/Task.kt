// app/src/main/java/com/example/lvlup/model/Task.kt

package com.example.lvlup.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// CHANGED: Removed the entire foreignKeys = [...] block from this annotation.
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false
)