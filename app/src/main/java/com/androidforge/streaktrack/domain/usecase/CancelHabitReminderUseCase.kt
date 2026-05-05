package com.androidforge.streaktrack.domain.usecase

import androidx.work.WorkManager
import com.androidforge.streaktrack.presentation.habits.util.HabitReminderWorker
import javax.inject.Inject

class CancelHabitReminderUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke(habitId: String) {
        val workName = HabitReminderWorker.WORK_NAME_PREFIX + habitId
        workManager.cancelUniqueWork(workName)
    }
}