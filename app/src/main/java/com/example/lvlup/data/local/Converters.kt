// app/src/main/java/com/example/lvlup/data/local/Converters.kt

package com.example.lvlup.data.local

import androidx.room.TypeConverter
import com.example.lvlup.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)
}