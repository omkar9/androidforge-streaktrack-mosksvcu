package com.androidforge.streaktrack.domain.usecase

import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.domain.repository.HabitRepository
import javax.inject.Inject

class ToggleHabitCompletionUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, dateMillis: Long): Result<Unit> {
        return repository.toggleHabitCompletion(habitId, dateMillis)
    }
}