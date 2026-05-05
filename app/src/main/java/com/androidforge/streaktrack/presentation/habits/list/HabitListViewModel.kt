package com.androidforge.streaktrack.presentation.habits.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streaktrack.R
import com.androidforge.streaktrack.core.util.DateTimeUtil
import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.core.util.UiText
import com.androidforge.streaktrack.domain.usecase.GetHabitsUseCase
import com.androidforge.streaktrack.domain.usecase.ToggleHabitCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitListUiState())
    val uiState: StateFlow<HabitListUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    fun loadHabits() {
        _uiState.update { it.copy(screenState = HabitListScreenState.Loading) }
        getHabitsUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(screenState = HabitListScreenState.Success(result.data ?: emptyList())) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(screenState = HabitListScreenState.Error(result.message ?: UiText.StringResource(R.string.error_loading_habits))) }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(screenState = HabitListScreenState.Loading) }
                    }
                    is Result.Offline -> {
                        _uiState.update { it.copy(screenState = HabitListScreenState.Offline) }
                    }
                }
            }.launchIn(viewModelScope) // Use launchIn for Flow collection in ViewModel
    }

    fun onToggleHabitCompletion(habitId: String, dateMillis: Long) {
        viewModelScope.launch {
            when (val result = toggleHabitCompletionUseCase(habitId, dateMillis)) {
                is Result.Success -> {
                    // State will be re-emitted by the getHabitsUseCase flow due to Room's Flow reactivity
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

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = UiText.DynamicString(message)) }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}