package com.shirotenma.petpartnertest.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleEditViewModel @Inject constructor(
    private val repo: ScheduleRepository,
    private val reminderManager: ReminderManager
) : ViewModel() {

    data class Ui(
        val id: Long = 0,
        val petId: Long,
        val title: String = "",
        val type: String = "",
        val date: String = "",
        val time: String = "",
        val notes: String = "",
        val remind: Boolean = false,
        val saving: Boolean = false
    )

    private val _ui = MutableStateFlow<Ui?>(null)
    val ui: StateFlow<Ui?> = _ui

    fun startNew(petId: Long) {
        _ui.value = Ui(petId = petId)
    }

    fun load(id: Long) = viewModelScope.launch {
        val data = repo.get(id)
        if (data != null) {
            _ui.value = Ui(
                id = data.id,
                petId = data.petId,
                title = data.title,
                type = data.type,
                date = data.date,
                time = data.time,
                notes = data.notes.orEmpty(),
                remind = data.remind
            )
        }
    }

    fun edit(block: (Ui) -> Ui) {
        _ui.update { it?.let(block) }
    }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val state = _ui.value ?: return@launch
        _ui.update { it?.copy(saving = true) }
        val savedId = repo.upsert(
            Schedule(
                id = state.id,
                petId = state.petId,
                title = state.title,
                type = state.type,
                date = state.date,
                time = state.time,
                notes = state.notes.ifBlank { null },
                remind = state.remind
            )
        )
        // Schedule/cancel reminder
        if (state.remind) {
            reminderManager.scheduleReminder(
                Schedule(
                    id = savedId,
                    petId = state.petId,
                    title = state.title,
                    type = state.type,
                    date = state.date,
                    time = state.time,
                    notes = state.notes.ifBlank { null },
                    remind = state.remind
                )
            )
        } else {
            reminderManager.cancelReminder(savedId)
        }
        _ui.update { it?.copy(saving = false) }
        onDone()
    }
}
