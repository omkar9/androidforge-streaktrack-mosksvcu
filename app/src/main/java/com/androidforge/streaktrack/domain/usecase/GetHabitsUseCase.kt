package com.androidforge.streaktrack.domain.usecase

import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.domain.model.HabitWithStreak
import com.androidforge.streaktrack.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Result<List<HabitWithStreak>>> {
        return repository.getHabits()
    }
}