package com.androidforge.streaktrack.domain.model

data class HabitWithCompletions(
    val habit: Habit,
    val completions: List<HabitCompletion>
)