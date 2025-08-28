// app/src/main/java/com/example/lvlup/widgets/AnalyticsWidgetReceiver.kt

package com.example.lvlup.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

// Receiver for the simple widget
class SimpleAnalyticsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleAnalyticsWidget()
}

// Receiver for the detailed widget
class DetailedAnalyticsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DetailedAnalyticsWidget()
}