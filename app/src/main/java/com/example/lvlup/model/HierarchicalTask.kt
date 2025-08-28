// app/src/main/java/com/example/lvlup/model/HierarchicalTask.kt

package com.example.lvlup.model

// NEW: Moved this data class to its own file to make it public.
data class HierarchicalTask(
    val task: Task,
    val subTasks: List<HierarchicalTask>
)