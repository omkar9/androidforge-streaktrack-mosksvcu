package com.androidforge.streaktrack.core.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateTimeUtil {

    val defaultZoneId: ZoneId = ZoneId.systemDefault()

    fun today(): LocalDate = LocalDate.now(defaultZoneId)

    fun todayMillis(): Long = today().atStartOfDay(defaultZoneId).toInstant().toEpochMilli()

    fun toLocalDate(millis: Long): LocalDate = Instant.ofEpochMilli(millis).atZone(defaultZoneId).toLocalDate()

    fun toEpochMilli(localDate: LocalDate): Long = localDate.atStartOfDay(defaultZoneId).toInstant().toEpochMilli()

    fun formatTime(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return time.format(formatter)
    }

    fun calculateStreaks(completedDates: List<LocalDate>, creationDate: LocalDate): Pair<Int, Int> {
        if (completedDates.isEmpty()) return 0 to 0

        val sortedUniqueCompletedDates = completedDates.distinct().sorted()
        if (sortedUniqueCompletedDates.isEmpty()) return 0 to 0

        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0

        // Calculate current streak
        // The current streak considers completion status up to and including today.
        // If today is not completed, it counts the streak up to yesterday.
        var dateForCurrentStreak = today()
        if (!sortedUniqueCompletedDates.contains(dateForCurrentStreak)) {
            dateForCurrentStreak = dateForCurrentStreak.minusDays(1) // If today isn't completed, check streak up to yesterday
        }

        while (sortedUniqueCompletedDates.contains(dateForCurrentStreak) && !dateForCurrentStreak.isBefore(creationDate)) {
            currentStreak++
            dateForCurrentStreak = dateForCurrentStreak.minusDays(1)
        }

        // Calculate best streak (iterating through all dates from creation to today)
        var dateIterator = creationDate
        while (!dateIterator.isAfter(today())) {
            val isCompleted = sortedUniqueCompletedDates.contains(dateIterator)
            if (isCompleted) {
                tempStreak++
            } else {
                bestStreak = maxOf(bestStreak, tempStreak)
                tempStreak = 0
            }
            dateIterator = dateIterator.plusDays(1)
        }
        bestStreak = maxOf(bestStreak, tempStreak) // Check streak at the end of iteration

        return currentStreak to bestStreak
    }

    fun getNotificationDelay(reminderTime: LocalTime): Long {
        val now = LocalTime.now(defaultZoneId)
        val today = LocalDate.now(defaultZoneId)

        val reminderDateTime = today.atTime(reminderTime)
        val currentDateTime = today.atTime(now)

        var delayMillis = ChronoUnit.MILLIS.between(currentDateTime, reminderDateTime)

        if (delayMillis < 0) {
            // If reminder time has passed today, schedule for tomorrow
            delayMillis += ChronoUnit.MILLIS.between(currentDateTime, today.plusDays(1).atTime(reminderTime))
        }
        return delayMillis
    }
}