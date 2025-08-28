// app/src/main/java/com/example/lvlup/widgets/AnalyticsWidget.kt

package com.example.lvlup.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.lvlup.data.repository.TaskRepository
import com.example.lvlup.ui.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsWidgetEntryPoint {
    fun taskRepository(): TaskRepository
}

abstract class BaseAnalyticsWidget : GlanceAppWidget() {

    protected data class TaskStats(
        val total: Int,
        val completed: Int,
        val pending: Int,
        val completionRate: Float
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AnalyticsWidgetEntryPoint::class.java
        )
        val taskRepository = hiltEntryPoint.taskRepository()

        val tasks = taskRepository.getTasks().first()
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val stats = TaskStats(
            total = total,
            completed = completed,
            pending = total - completed,
            completionRate = if (total > 0) completed.toFloat() / total.toFloat() else 0f
        )

        provideContent {
            WidgetContent(stats = stats, context = context)
        }
    }

    @Composable
    protected abstract fun WidgetContent(stats: TaskStats, context: Context)
}

class SimpleAnalyticsWidget : BaseAnalyticsWidget() {
    @Composable
    // CHANGED: Added 'protected override' to match base class visibility
    protected override fun WidgetContent(stats: TaskStats, context: Context) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF2E2E2E))
                .padding(16.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text(text = "${(stats.completionRate * 100).toInt()}%")
        }
    }
}

class DetailedAnalyticsWidget : BaseAnalyticsWidget() {
    @Composable
    // CHANGED: Added 'protected override' to match base class visibility
    protected override fun WidgetContent(stats: TaskStats, context: Context) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF2E2E2E))
                .padding(16.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = GlanceModifier.size(80.dp))
                Text(text = "${(stats.completionRate * 100).toInt()}%")
            }

            Spacer(modifier = GlanceModifier.height(16.dp))

            Text("Total Tasks: ${stats.total}", style = TextStyle(color = ColorProvider(Color.White)))
            Text("Completed: ${stats.completed}", style = TextStyle(color = ColorProvider(Color.Green)))
            Text("Pending: ${stats.pending}", style = TextStyle(color = ColorProvider(Color.Yellow)))
        }
    }
}