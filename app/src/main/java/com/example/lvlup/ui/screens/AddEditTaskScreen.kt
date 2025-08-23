// app/src/main/java/com/example/lvlup/ui/screens/AddEditTaskScreen.kt

package com.example.lvlup.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lvlup.model.Priority
import com.example.lvlup.model.Task
import com.example.lvlup.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Int?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTask by if (taskId != null) {
        viewModel.getTaskById(taskId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    val allTasks by viewModel.allTasks.collectAsState()
    val availableParents = allTasks.filter { it.id != taskId }
    var parentId by remember { mutableStateOf<Int?>(null) }
    var showParentSelector by remember { mutableStateOf(false) }

    LaunchedEffect(currentTask) {
        if (taskId != null && currentTask != null) {
            title = currentTask!!.title
            description = currentTask!!.description ?: ""
            priority = currentTask!!.priority
            dueDate = currentTask!!.dueDate
            parentId = currentTask!!.parentId
        }
    }

    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // CHANGED: This is the final, correct way to set the date validation.
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= startOfToday
            }
        }
    )

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = { openDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    dueDate = datePickerState.selectedDateMillis
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
            }
        ) {
            // CHANGED: The incorrect dateValidator parameter is now removed from here.
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Add Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (taskId != null && currentTask != null) {
                        IconButton(onClick = {
                            viewModel.deleteTask(currentTask!!)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                        }
                    }
                    IconButton(onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }
                        val taskToSave = (currentTask ?: Task(
                            title = "",
                            description = null,
                            dueDate = null,
                            priority = Priority.MEDIUM
                        )).copy(
                            title = title,
                            description = description.ifBlank { null },
                            priority = priority,
                            dueDate = dueDate,
                            parentId = parentId
                        )
                        viewModel.upsertTask(taskToSave)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Save Task")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            OutlinedTextField(
                value = dueDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Not Set",
                onValueChange = {},
                label = { Text("Due Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDialog.value = true },
                enabled = false,
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text("Priority", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Priority.values().forEach { prio ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { priority = prio }
                    ) {
                        RadioButton(
                            selected = priority == prio,
                            onClick = { priority = prio }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(prio.name)
                    }
                }
            }

            Box {
                OutlinedTextField(
                    value = availableParents.find { it.id == parentId }?.title ?: "No Parent Task",
                    onValueChange = {},
                    label = { Text("Subtask of") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showParentSelector = true }
                )
                DropdownMenu(
                    expanded = showParentSelector,
                    onDismissRequest = { showParentSelector = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(text = { Text("No Parent Task") }, onClick = {
                        parentId = null
                        showParentSelector = false
                    })
                    availableParents.forEach { parent ->
                        DropdownMenuItem(text = { Text(parent.title) }, onClick = {
                            parentId = parent.id
                            showParentSelector = false
                        })
                    }
                }
            }
        }
    }
}