package com.androidforge.streaktrack.domain.usecase

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidforge.streaktrack.core.util.Constants
import com.androidforge.streaktrack.core.util.DateTimeUtil
import com.androidforge.streaktrack.presentation.habits.util.HabitReminderWorker
import com.androidforge.streaktrack.presentation.habits.util.NotificationConstants
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleHabitReminderUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke(habitId: String, habitName: String, reminderTime: LocalTime) {
        val initialDelayMillis = DateTimeUtil.getNotificationDelay(reminderTime)

        val inputData = Data.Builder()
            .putString(NotificationConstants.HABIT_ID_KEY, habitId)
            .putString(NotificationConstants.HABIT_NAME_KEY, habitName)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Schedule daily reminders. Minimum interval is 15 minutes for PeriodicWorkRequest.
        // We calculate the initial delay and then set it to repeat daily.
        val reminderWorkRequest = PeriodicWorkRequestBuilder<HabitReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(Constants.REMINDER_WORK_TAG)
            .build()

        val workName = HabitReminderWorker.WORK_NAME_PREFIX + habitId

        workManager.enqueueUniquePeriodicWork(
            workName,
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            reminderWorkRequest
        )

        Timber.d("Scheduled reminder for habit %s at %s. Initial delay: %d ms", habitName, reminderTime, initialDelayMillis)
    }
}