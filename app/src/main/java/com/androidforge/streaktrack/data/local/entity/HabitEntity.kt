package com.androidforge.streaktrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val creationDate: LocalDate,
    val reminderTime: LocalTime?
)