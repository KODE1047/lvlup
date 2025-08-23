// app/src/main/java/com/example/lvlup/notification/NotificationReceiver.kt

package com.example.lvlup.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.lvlup.R
import com.example.lvlup.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)
        val taskTitle = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Task Reminder"

        if (taskId == -1) return

        // NEW: Get the due date from the intent to display it
        val dueDateMillis = intent.getLongExtra("EXTRA_DUE_DATE", -1L)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dueTime = if (dueDateMillis != -1L) timeFormatter.format(Date(dueDateMillis)) else "now"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "task_reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Due: $taskTitle")
            // CHANGED: Updated notification text with the specific time
            .setContentText("Your task is due at $dueTime. Time to level up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId, notification)
    }
}