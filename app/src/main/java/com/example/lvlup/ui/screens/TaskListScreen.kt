// app/src/main/java/com/example/lvlup/ui/screens/TaskListScreen.kt

package com.example.lvlup.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lvlup.model.Task
import com.example.lvlup.ui.composables.TaskItem
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
    // CHANGED: We now collect the simple 'tasks' StateFlow.
    val allTasks by viewModel.tasks.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ongoing", "Done")

    // CHANGED: Filtering logic is now simpler.
    val ongoingTasks = allTasks.filter { !it.isCompleted }
    val doneTasks = allTasks.filter { it.isCompleted }


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
                title = { Text("Lvl Up") },
                actions = {
                    IconButton(onClick = onNavigateToAnalytics) {
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
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> TaskListContent(tasks = ongoingTasks, viewModel = viewModel, onEditTask = onEditTask)
                1 -> TaskListContent(tasks = doneTasks, viewModel = viewModel, onEditTask = onEditTask)
            }
        }
    }
}

@Composable
fun TaskListContent(
    tasks: List<Task>, // CHANGED: The parameter is now a simple List<Task>.
    viewModel: TaskViewModel,
    onEditTask: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // CHANGED: We iterate over the simple list and call TaskItem directly.
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onCompleteClick = { viewModel.toggleTaskCompleted(task) },
                onClick = { onEditTask(task.id) }
            )
        }
    }
}