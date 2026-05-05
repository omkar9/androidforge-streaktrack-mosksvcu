package com.androidforge.streaktrack.domain.usecase

import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.domain.model.HabitWithCompletions
import com.androidforge.streaktrack.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(id: String): Flow<Result<HabitWithCompletions>> {
        return repository.getHabitById(id)
    }
}