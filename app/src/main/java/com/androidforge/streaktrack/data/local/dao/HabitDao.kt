package com.androidforge.streaktrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.androidforge.streaktrack.data.local.entity.HabitCompletionEntity
import com.androidforge.streaktrack.data.local.entity.HabitEntity
import com.androidforge.streaktrack.data.local.entity.HabitWithCompletionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM HabitEntity WHERE id = :habitId")
    suspend fun deleteHabit(habitId: String)

    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    fun getHabitById(habitId: String): Flow<HabitEntity?>

    @Transaction
    @Query("SELECT * FROM HabitEntity WHERE id = :habitId")
    fun getHabitWithCompletions(habitId: String): Flow<HabitWithCompletionsEntity?>

    @Transaction
    @Query("SELECT * FROM HabitEntity ORDER BY creationDate DESC")
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletionsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM HabitCompletionEntity WHERE habitId = :habitId AND completionDate = :date")
    suspend fun deleteHabitCompletion(habitId: String, date: Long)

    @Query("SELECT * FROM HabitCompletionEntity WHERE habitId = :habitId AND completionDate = :date LIMIT 1")
    suspend fun getHabitCompletionForDate(habitId: String, date: Long): HabitCompletionEntity?

    @Query("DELETE FROM HabitCompletionEntity WHERE habitId = :habitId")
    suspend fun deleteAllCompletionsForHabit(habitId: String)
}