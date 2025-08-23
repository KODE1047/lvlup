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
import com.example.lvlup.ui.composables.TaskItem
import com.example.lvlup.model.HierarchicalTask
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
    val allHierarchicalTasks by viewModel.hierarchicalTasks.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ongoing", "Done")

    // NEW: Filter tasks based on completion status
    val ongoingTasks = allHierarchicalTasks.mapNotNull { filterCompleted(it, false) }
    val doneTasks = allHierarchicalTasks.mapNotNull { filterCompleted(it, true) }


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
            // NEW: TabRow for Ongoing/Done tasks
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            // NEW: Show content based on selected tab
            when (selectedTabIndex) {
                0 -> TaskListContent(tasks = ongoingTasks, viewModel = viewModel, onEditTask = onEditTask)
                1 -> TaskListContent(tasks = doneTasks, viewModel = viewModel, onEditTask = onEditTask)
            }
        }
    }
}

// NEW: Extracted LazyColumn content into a reusable composable
@Composable
fun TaskListContent(
    tasks: List<HierarchicalTask>,
    viewModel: TaskViewModel,
    onEditTask: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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

// NEW: Helper function to filter hierarchical tasks
fun filterCompleted(task: HierarchicalTask, isCompleted: Boolean): HierarchicalTask? {
    val filteredSubTasks = task.subTasks.mapNotNull { filterCompleted(it, isCompleted) }
    return if (task.task.isCompleted == isCompleted || filteredSubTasks.isNotEmpty()) {
        task.copy(subTasks = filteredSubTasks)
    } else {
        null
    }
}


@Composable
fun TaskItemRecursive(
    hierarchicalTask: HierarchicalTask,
    viewModel: TaskViewModel,
    onEditTask: (Int) -> Unit,
    level: Int = 0
) {
    // Only display the parent if it matches the filter criteria,
    // or if it has subtasks that match. The filtering is now done before this composable is called.
    Column {
        TaskItem(
            task = hierarchicalTask.task,
            // CHANGED: The onCheckedChange now only triggers the icon, not the DB update
            onCompleteClick = { viewModel.toggleTaskCompleted(hierarchicalTask.task) },
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