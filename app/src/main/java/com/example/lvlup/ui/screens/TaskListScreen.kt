// app/src/main/java/com/example/lvlup/ui/screens/TaskListScreen.kt

package com.example.lvlup.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueryStats // <-- CORRECT IMPORT
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lvlup.ui.composables.TaskItem
import com.example.lvlup.ui.viewmodel.HierarchicalTask
import com.example.lvlup.ui.viewmodel.TaskViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Int) -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val tasks by viewModel.hierarchicalTasks.collectAsState()

    // Request notification permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lvl Up Tasks") },
                actions = {
                    IconButton(onClick = onNavigateToAnalytics) {
                        // THIS IS THE CORRECTED LINE:
                        Icon(Icons.Default.QueryStats, contentDescription = "Analytics")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.task.id }) { hierarchicalTask ->
                TaskItemRecursive(
                    hierarchicalTask = hierarchicalTask,
                    viewModel = viewModel,
                    onEditTask = onEditTask
                )
            }
        }
    }
}

@Composable
fun TaskItemRecursive(
    hierarchicalTask: HierarchicalTask,
    viewModel: TaskViewModel,
    onEditTask: (Int) -> Unit,
    level: Int = 0
) {
    Column {
        TaskItem(
            task = hierarchicalTask.task,
            onCheckedChange = { viewModel.toggleTaskCompleted(hierarchicalTask.task) },
            onClick = { onEditTask(hierarchicalTask.task.id) },
            modifier = Modifier.padding(start = (16 * level).dp)
        )
        hierarchicalTask.subTasks.forEach { subTask ->
            TaskItemRecursive(
                hierarchicalTask = subTask,
                viewModel = viewModel,
                onEditTask = onEditTask,
                level = level + 1
            )
        }
    }
}