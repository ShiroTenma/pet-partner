package com.shirotenma.petpartnertest.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirdMessageViewModel @Inject constructor(
    private val repo: JournalRepository
) : ViewModel() {

    data class State(
        val loading: Boolean = false,
        val message: BirdMessage? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    fun loadRandom() {
        viewModelScope.launch {
            _state.value = State(loading = true)
            val msg = repo.randomForestMessage()
            _state.value = if (msg != null) State(loading = false, message = msg) else State(loading = false, error = "Belum ada Pesan Burung di hutan lain.")
        }
    }

    fun reply(id: Long, text: String, onDone: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                repo.sendReply(id, text)
                onDone()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}
