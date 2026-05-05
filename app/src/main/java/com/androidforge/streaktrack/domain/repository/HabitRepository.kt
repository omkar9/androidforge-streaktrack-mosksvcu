package com.androidforge.streaktrack.domain.repository

import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.domain.model.Habit
import com.androidforge.streaktrack.domain.model.HabitWithCompletions
import com.androidforge.streaktrack.domain.model.HabitWithStreak
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<Result<List<HabitWithStreak>>>
    fun getHabitById(id: String): Flow<Result<HabitWithCompletions>>
    suspend fun insertHabit(habit: Habit): Result<String>
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun deleteHabit(habitId: String): Result<Unit>
    suspend fun toggleHabitCompletion(habitId: String, dateMillis: Long): Result<Unit>
}