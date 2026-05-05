package com.androidforge.streaktrack.presentation.habits.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streaktrack.R
import com.androidforge.streaktrack.core.util.DateTimeUtil
import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.core.util.UiText
import com.androidforge.streaktrack.domain.usecase.GetHabitByIdUseCase
import com.androidforge.streaktrack.domain.usecase.ToggleHabitCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    private val habitId: String = checkNotNull(savedStateHandle["habitId"])

    init {
        loadHabitDetails(habitId)
    }

    fun loadHabitDetails(id: String) {
        _uiState.update { it.copy(screenState = HabitDetailScreenState.Loading) }
        viewModelScope.launch {
            getHabitByIdUseCase(id).collect {
                when (it) {
                    is Result.Success -> {
                        it.data?.let { habitWithCompletions ->
                            val (currentStreak, bestStreak) = DateTimeUtil.calculateStreaks(
                                habitWithCompletions.completions.map { it.completionDate },
                                habitWithCompletions.habit.creationDate
                            )
                            _uiState.update { currentState ->
                                currentState.copy(
                                    screenState = HabitDetailScreenState.Success(habitWithCompletions),
                                    habitWithCompletions = habitWithCompletions,
                                    currentStreak = currentStreak,
                                    bestStreak = bestStreak
                                )
                            }
                        } ?: _uiState.update { it.copy(screenState = HabitDetailScreenState.Error(UiText.StringResource(R.string.habit_not_found))) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(screenState = HabitDetailScreenState.Error(it.message ?: UiText.StringResource(R.string.error_loading_habit_details))) }
                    }
                    is Result.Offline -> {
                        _uiState.update { it.copy(screenState = HabitDetailScreenState.Offline) }
                    }
                    is Result.Loading -> {
                        // Handled by initial state or when the flow starts
                    }
                }
            }
        }
    }

    fun onToggleHabitCompletion(habitId: String, dateMillis: Long) {
        viewModelScope.launch {
            when (val result = toggleHabitCompletionUseCase(habitId, dateMillis)) {
                is Result.Success -> {
                    // Reload details to update streaks and calendar, as the underlying data has changed
                    loadHabitDetails(habitId)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(snackbarMessage = result.message ?: UiText.StringResource(R.string.error_toggling_habit)) }
                }
                else -> {
                    // Loading and Offline states are not expected for a direct action like this, handle as needed
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}