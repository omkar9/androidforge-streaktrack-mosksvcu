package com.androidforge.streaktrack.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithCompletionsEntity(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    ) val completions: List<HabitCompletionEntity>
)