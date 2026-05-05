package com.androidforge.streaktrack.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val creationDate: LocalDate = LocalDate.now(),
    val reminderTime: LocalTime? = null
)