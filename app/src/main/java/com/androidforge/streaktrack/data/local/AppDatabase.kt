package com.androidforge.streaktrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidforge.streaktrack.data.local.converter.LocalDateConverter
import com.androidforge.streaktrack.data.local.converter.LocalTimeConverter
import com.androidforge.streaktrack.data.local.dao.HabitDao
import com.androidforge.streaktrack.data.local.entity.HabitCompletionEntity
import com.androidforge.streaktrack.data.local.entity.HabitEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, LocalTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}