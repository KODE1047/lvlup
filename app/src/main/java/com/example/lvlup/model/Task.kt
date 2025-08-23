// app/src/main/java/com/example/lvlup/model/Task.kt

package com.example.lvlup.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val parentId: Int? = null,
    val isRecurring: Boolean = false
)