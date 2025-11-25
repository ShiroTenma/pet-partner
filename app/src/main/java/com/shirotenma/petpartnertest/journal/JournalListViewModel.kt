package com.shirotenma.petpartnertest.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalListViewModel @Inject constructor(
    private val repo: JournalRepository
) : ViewModel() {

    data class State(
        val loading: Boolean = true,
        val items: List<JournalEntry> = emptyList(),
        val refreshing: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var petId: Long? = null
    private var job: Job? = null

    fun observe(petId: Long) {
        if (this.petId == petId) return
        this.petId = petId
        job?.cancel()
        job = viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repo.observeByPet(petId).collectLatest { list ->
                _state.value = State(loading = false, refreshing = false, items = list)
            }
        }
    }

    fun refresh() {
        _state.update { it.copy(refreshing = true) }
    }

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
    }
}
