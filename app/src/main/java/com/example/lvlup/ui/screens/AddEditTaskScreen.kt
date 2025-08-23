// app/src/main/java/com/example/lvlup/ui/screens/AddEditTaskScreen.kt
package com.example.lvlup.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
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

    LaunchedEffect(currentTask) {
        if (taskId != null && currentTask != null) {
            title = currentTask!!.title
            description = currentTask!!.description ?: ""
            priority = currentTask!!.priority
            dueDate = currentTask!!.dueDate
        }
    }

    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= startOfToday
            }
        }
    )

    val openDatePicker = remember { mutableStateOf(false) }
    val openTimePicker = remember { mutableStateOf(false) }
    val selectedDateTime = remember { Calendar.getInstance() }

    if (openDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { openDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    openDatePicker.value = false
                    datePickerState.selectedDateMillis?.let {
                        selectedDateTime.timeInMillis = it
                    }
                    openTimePicker.value = true
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { openDatePicker.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (openTimePicker.value) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                openTimePicker.value = false
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)
                dueDate = selectedDateTime.timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Add Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

                        if (dueDate != null && dueDate!! < System.currentTimeMillis()) {
                            Toast.makeText(context, "Cannot set a due time in the past", Toast.LENGTH_LONG).show()
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
                            dueDate = dueDate
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
                // CHANGED: Corrected typo from onValue_change to onValueChange
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            val dateTimeFormatter = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
            OutlinedTextField(
                value = dueDate?.let { dateTimeFormatter.format(Date(it)) } ?: "Not Set",
                // CHANGED: Corrected typo from onValue_change to onValueChange
                onValueChange = {},
                label = { Text("Due Date & Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker.value = true },
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
        }
    }
}