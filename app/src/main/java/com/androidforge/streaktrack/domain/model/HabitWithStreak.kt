package com.androidforge.streaktrack.domain.model

data class HabitWithStreak(
    val habit: Habit,
    val currentStreak: Int,
    val bestStreak: Int,
    val isCompletedToday: Boolean
)