// app/src/main/java/com/example/lvlup/ui/composables/TaskItem.kt

package com.example.lvlup.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.lvlup.model.Priority
import com.example.lvlup.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItem(
    task: Task,
    onCompleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // NEW: Local state to manage the checkbox state separately
    var isChecked by remember { mutableStateOf(task.isCompleted) }

    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color.Red.copy(alpha = 0.7f)
        Priority.MEDIUM -> Color.Yellow.copy(alpha = 0.7f)
        Priority.LOW -> Color.Green.copy(alpha = 0.7f)
    }

    val textStyle = if (task.isCompleted) {
        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough, color = Color.Gray)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                // CHANGED: Checkbox only updates local state. Disabled if task is already completed.
                onCheckedChange = { newCheckedState -> isChecked = newCheckedState },
                enabled = !task.isCompleted
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = textStyle)
                task.dueDate?.let {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            // NEW: Logic to show either completion icon or priority indicator
            if (isChecked && !task.isCompleted) {
                IconButton(onClick = onCompleteClick) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "Mark as complete",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color = priorityColor, shape = MaterialTheme.shapes.small)
                )
            }
        }
    }
}