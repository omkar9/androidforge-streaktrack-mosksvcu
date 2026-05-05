package com.androidforge.streaktrack.domain.usecase

import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.domain.model.Habit
import com.androidforge.streaktrack.domain.repository.HabitRepository
import javax.inject.Inject

class AddEditHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<String> {
        return if (habit.id.isBlank()) {
            repository.insertHabit(habit)
        } else {
            repository.updateHabit(habit)
            Result.Success(habit.id)
        }
    }
}