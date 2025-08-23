// app/src/main/java/com/example/lvlup/notification/TaskAlarmScheduler.kt

package com.example.lvlup.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.lvlup.model.Task

class TaskAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(task: Task) {
        if (task.dueDate == null) return

        // CHANGED: Pass the due date into the intent
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TASK_ID", task.id)
            putExtra("EXTRA_TASK_TITLE", task.title)
            putExtra("EXTRA_DUE_DATE", task.dueDate) // <-- ADD THIS LINE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (task.dueDate > System.currentTimeMillis()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun cancel(task: Task) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}