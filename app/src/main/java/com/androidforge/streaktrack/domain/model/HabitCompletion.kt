package com.androidforge.streaktrack.domain.model

import java.time.LocalDate

data class HabitCompletion(
    val habitId: String,
    val completionDate: LocalDate
)