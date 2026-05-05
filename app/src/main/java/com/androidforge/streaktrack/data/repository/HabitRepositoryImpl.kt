package com.androidforge.streaktrack.data.repository

import com.androidforge.streaktrack.R
import com.androidforge.streaktrack.core.util.DateTimeUtil
import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.core.util.UiText
import com.androidforge.streaktrack.data.local.dao.HabitDao
import com.androidforge.streaktrack.data.local.mapper.toDomain
import com.androidforge.streaktrack.data.local.mapper.toEntity
import com.androidforge.streaktrack.domain.model.Habit
import com.androidforge.streaktrack.domain.model.HabitCompletion
import com.androidforge.streaktrack.domain.model.HabitWithCompletions
import com.androidforge.streaktrack.domain.model.HabitWithStreak
import com.androidforge.streaktrack.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getHabits(): Flow<Result<List<HabitWithStreak>>> = habitDao.getAllHabitsWithCompletions()
        .onStart { emit(Result.Loading()) }
        .map { list ->
            val habitsWithStreaks = list.map { habitWithCompletionsEntity ->
                val habit = habitWithCompletionsEntity.habit.toDomain()
                val completions = habitWithCompletionsEntity.completions.map { it.toDomain() }
                val (currentStreak, bestStreak) = DateTimeUtil.calculateStreaks(
                    completions.map { it.completionDate },
                    habit.creationDate
                )
                HabitWithStreak(
                    habit = habit,
                    currentStreak = currentStreak,
                    bestStreak = bestStreak,
                    isCompletedToday = completions.any { it.completionDate == DateTimeUtil.today() }
                )
            }
            Result.Success(habitsWithStreaks)
        }
        .catch { e ->
            Timber.e(e, "Error getting all habits with completions")
            emit(Result.Error(UiText.StringResource(R.string.error_loading_habits)))
        }

    override fun getHabitById(id: String): Flow<Result<HabitWithCompletions>> = habitDao.getHabitWithCompletions(id)
        .onStart { emit(Result.Loading()) }
        .map { entity ->
            entity?.let { Result.Success(it.toDomain()) } ?: Result.Error(UiText.StringResource(R.string.habit_not_found))
        }
        .catch { e ->
            Timber.e(e, "Error getting habit by ID: %s", id)
            emit(Result.Error(UiText.StringResource(R.string.error_loading_habit_details)))
        }

    override suspend fun insertHabit(habit: Habit): Result<String> {
        return try {
            val habitToInsert = habit.copy(
                id = habit.id.ifBlank { UUID.randomUUID().toString() },
                creationDate = habit.creationDate // Ensure creationDate is set if not provided
            )
            habitDao.insertHabit(habitToInsert.toEntity())
            Result.Success(habitToInsert.id)
        } catch (e: Exception) {
            Timber.e(e, "Error inserting habit")
            Result.Error(UiText.StringResource(R.string.error_saving_habit))
        }
    }

    override suspend fun updateHabit(habit: Habit): Result<Unit> {
        return try {
            habitDao.updateHabit(habit.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating habit")
            Result.Error(UiText.StringResource(R.string.error_saving_habit))
        }
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> {
        return try {
            habitDao.deleteHabit(habitId)
            // Foreign key CASCADE should handle completions, but explicitly deleting for clarity/safety
            habitDao.deleteAllCompletionsForHabit(habitId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting habit: %s", habitId)
            Result.Error(UiText.StringResource(R.string.error_deleting_habit))
        }
    }

    override suspend fun toggleHabitCompletion(habitId: String, dateMillis: Long): Result<Unit> {
        return try {
            val date = DateTimeUtil.toLocalDate(dateMillis)
            val existingCompletion = habitDao.getHabitCompletionForDate(habitId, dateMillis)
            if (existingCompletion == null) {
                habitDao.insertHabitCompletion(HabitCompletion(habitId, date).toEntity())
            } else {
                habitDao.deleteHabitCompletion(habitId, dateMillis)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling habit completion for habitId: %s, date: %s", habitId, DateTimeUtil.toLocalDate(dateMillis))
            Result.Error(UiText.StringResource(R.string.error_toggling_habit))
        }
    }
}