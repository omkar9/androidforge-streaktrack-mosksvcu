package com.androidforge.streaktrack.presentation.habits.add_edit_habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streaktrack.R
import com.androidforge.streaktrack.core.util.Result
import com.androidforge.streaktrack.core.util.UiText
import com.androidforge.streaktrack.domain.model.Habit
import com.androidforge.streaktrack.domain.usecase.AddEditHabitUseCase
import com.androidforge.streaktrack.domain.usecase.CancelHabitReminderUseCase
import com.androidforge.streaktrack.domain.usecase.DeleteHabitUseCase
import com.androidforge.streaktrack.domain.usecase.GetHabitByIdUseCase
import com.androidforge.streaktrack.domain.usecase.ScheduleHabitReminderUseCase
import com.androidforge.streaktrack.presentation.common.AdInterstitialManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val addEditHabitUseCase: AddEditHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val scheduleHabitReminderUseCase: ScheduleHabitReminderUseCase,
    private val cancelHabitReminderUseCase: CancelHabitReminderUseCase,
    private val adInterstitialManager: AdInterstitialManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditHabitUiState())
    val uiState: StateFlow<AddEditHabitUiState> = _uiState.asStateFlow()

    private var currentHabitId: String? = savedStateHandle["habitId"]

    init {
        adInterstitialManager.loadInterstitialAd()
    }

    fun loadHabit(habitId: String?) {
        currentHabitId = habitId
        if (habitId == null) {
            _uiState.update { it.copy(screenState = AddEditHabitScreenState.Success(HabitInputState())) }
            return
        }

        _uiState.update { it.copy(screenState = AddEditHabitScreenState.Loading) }
        viewModelScope.launch {
            when (val result = getHabitByIdUseCase(habitId)) {
                is Result.Success -> {
                    result.data?.let { habitWithCompletions ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                screenState = AddEditHabitScreenState.Success(
                                    HabitInputState(
                                        name = habitWithCompletions.habit.name,
                                        description = habitWithCompletions.habit.description,
                                        reminderTime = habitWithCompletions.habit.reminderTime
                                    )
                                )
                            )
                        }
                    } ?: _uiState.update { it.copy(screenState = AddEditHabitScreenState.Error(UiText.StringResource(R.string.habit_not_found))) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(screenState = AddEditHabitScreenState.Error(result.message ?: UiText.StringResource(R.string.error_loading_habit_details))) }
                }
                is Result.Offline -> {
                    _uiState.update { it.copy(screenState = AddEditHabitScreenState.Offline) }
                }
                is Result.Loading -> {
                    // Handled by initial state
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { currentState ->
            val currentHabitState = (currentState.screenState as? AddEditHabitScreenState.Success)?.habitState ?: HabitInputState()
            currentState.copy(
                screenState = AddEditHabitScreenState.Success(currentHabitState.copy(name = name, nameError = null))
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { currentState ->
            val currentHabitState = (currentState.screenState as? AddEditHabitScreenState.Success)?.habitState ?: HabitInputState()
            currentState.copy(
                screenState = AddEditHabitScreenState.Success(currentHabitState.copy(description = description))
            )
        }
    }

    fun onReminderTimeChange(time: LocalTime?) {
        _uiState.update { currentState ->
            val currentHabitState = (currentState.screenState as? AddEditHabitScreenState.Success)?.habitState ?: HabitInputState()
            currentState.copy(
                screenState = AddEditHabitScreenState.Success(currentHabitState.copy(reminderTime = time))
            )
        }
    }

    fun onSaveHabit() {
        val currentHabitState = (_uiState.value.screenState as? AddEditHabitScreenState.Success)?.habitState ?: return

        if (currentHabitState.name.isBlank()) {
            _uiState.update { currentState ->
                val updatedHabitState = (currentState.screenState as? AddEditHabitScreenState.Success)?.habitState ?: HabitInputState()
                currentState.copy(
                    screenState = AddEditHabitScreenState.Success(updatedHabitState.copy(nameError = UiText.StringResource(R.string.habit_name_cannot_be_empty)))
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val habit = Habit(
                id = currentHabitId ?: "", // ID will be generated by Room if empty
                name = currentHabitState.name.trim(),
                description = currentHabitState.description.trim(),
                reminderTime = currentHabitState.reminderTime
            )

            when (val result = addEditHabitUseCase(habit)) {
                is Result.Success -> {
                    result.data?.let { savedHabitId ->
                        if (habit.reminderTime != null) {
                            scheduleHabitReminderUseCase(savedHabitId, habit.name, habit.reminderTime)
                        } else {
                            cancelHabitReminderUseCase(savedHabitId)
                        }
                        _uiState.update { it.copy(habitSaved = true, isSaving = false, snackbarMessage = UiText.StringResource(R.string.habit_saved_successfully)) }
                        adInterstitialManager.showInterstitialAd() // Show ad after saving
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(snackbarMessage = result.message ?: UiText.StringResource(R.string.error_saving_habit), isSaving = false) }
                }
                is Result.Offline -> {
                    _uiState.update { it.copy(screenState = AddEditHabitScreenState.Offline, isSaving = false) }
                }
                is Result.Loading -> {
                    // Should not happen for this use case
                }
            }
        }
    }

    fun onDeleteHabit() {
        currentHabitId?.let { id ->
            viewModelScope.launch {
                when (val result = deleteHabitUseCase(id)) {
                    is Result.Success -> {
                        cancelHabitReminderUseCase(id)
                        _uiState.update { it.copy(habitSaved = true, snackbarMessage = UiText.StringResource(R.string.habit_deleted_successfully)) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(snackbarMessage = result.message ?: UiText.StringResource(R.string.error_deleting_habit)) }
                    }
                    is Result.Offline -> {
                        _uiState.update { it.copy(snackbarMessage = UiText.StringResource(R.string.offline_message)) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}