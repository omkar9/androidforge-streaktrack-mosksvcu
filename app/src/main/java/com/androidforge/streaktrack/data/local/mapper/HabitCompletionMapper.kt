package com.androidforge.streaktrack.data.local.mapper

import com.androidforge.streaktrack.data.local.entity.HabitCompletionEntity
import com.androidforge.streaktrack.domain.model.HabitCompletion

fun HabitCompletionEntity.toDomain(): HabitCompletion {
    return HabitCompletion(
        habitId = this.habitId,
        completionDate = this.completionDate
    )
}

fun HabitCompletion.toEntity(): HabitCompletionEntity {
    return HabitCompletionEntity(
        habitId = this.habitId,
        completionDate = this.completionDate
    )
}