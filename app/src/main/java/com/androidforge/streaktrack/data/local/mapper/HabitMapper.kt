package com.androidforge.streaktrack.data.local.mapper

import com.androidforge.streaktrack.data.local.entity.HabitEntity
import com.androidforge.streaktrack.data.local.entity.HabitWithCompletionsEntity
import com.androidforge.streaktrack.domain.model.Habit
import com.androidforge.streaktrack.domain.model.HabitWithCompletions

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = this.id,
        name = this.name,
        description = this.description,
        creationDate = this.creationDate,
        reminderTime = this.reminderTime
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        creationDate = this.creationDate,
        reminderTime = this.reminderTime
    )
}

fun HabitWithCompletionsEntity.toDomain(): HabitWithCompletions {
    return HabitWithCompletions(
        habit = this.habit.toDomain(),
        completions = this.completions.map { it.toDomain() }
    )
}