package com.shirotenma.petpartnertest.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleListViewModel @Inject constructor(
    private val repo: ScheduleRepository
) : ViewModel() {

    data class State(
        val loading: Boolean = true,
        val items: List<Schedule> = emptyList()
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var job: Job? = null

    fun load(petId: Long) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.value = State(loading = true)
            repo.schedules(petId).collectLatest { list ->
                _state.value = State(loading = false, items = list)
            }
        }
    }

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
    }
}
