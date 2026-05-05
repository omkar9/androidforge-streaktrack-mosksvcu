package com.androidforge.streaktrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate

@Entity(primaryKeys = ["habitId", "completionDate"],
    foreignKeys = [
        ForeignKey(entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class HabitCompletionEntity(
    val habitId: String,
    val completionDate: LocalDate
)