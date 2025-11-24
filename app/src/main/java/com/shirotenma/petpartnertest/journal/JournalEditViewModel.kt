package com.shirotenma.petpartnertest.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class JournalEditViewModel @Inject constructor(
    private val repo: JournalRepository
) : ViewModel() {

    data class Ui(
        val id: Long? = null,
        val petId: Long,
        val mood: String = "",
        val content: String = "",
        val date: String = "",
        val saving: Boolean = false,
        val canShare: Boolean = true,
        val shareError: String? = null
    )

    private val _ui = MutableStateFlow<Ui?>(null)
    val ui: StateFlow<Ui?> = _ui

    fun startNew(petId: Long) {
        viewModelScope.launch {
            val canShare = repo.canShareBirdMessage()
            _ui.value = Ui(petId = petId, date = today(), canShare = canShare)
        }
    }

    fun load(petId: Long, id: Long) = viewModelScope.launch {
        val entry = repo.get(id)
        val canShare = repo.canShareBirdMessage()
        _ui.value = if (entry != null) {
            Ui(
                id = entry.id,
                petId = entry.petId,
                mood = entry.mood,
                content = entry.content,
                date = entry.date,
                canShare = canShare
            )
        } else {
            Ui(petId = petId, date = today(), canShare = canShare)
        }
    }

    fun edit(block: (Ui) -> Ui) {
        _ui.update { current -> current?.let(block) }
    }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val state = _ui.value ?: return@launch
        _ui.update { it?.copy(saving = true) }
        repo.upsert(
            JournalEntry(
                id = state.id ?: 0,
                petId = state.petId,
                mood = state.mood,
                content = state.content,
                date = state.date.ifBlank { today() }
            )
        )
        _ui.update { it?.copy(saving = false) }
        onDone()
    }

    fun shareAsBirdMessage(onDone: () -> Unit) = viewModelScope.launch {
        val state = _ui.value ?: return@launch
        if (!state.canShare) {
            _ui.update { it?.copy(shareError = "Hanya boleh kirim satu Pesan Burung") }
            return@launch
        }
        _ui.update { it?.copy(saving = true, shareError = null) }
        val journalId = repo.upsert(
            JournalEntry(
                id = state.id ?: 0,
                petId = state.petId,
                mood = state.mood,
                content = state.content,
                date = state.date.ifBlank { today() }
            )
        )
        try {
            repo.shareAsBirdMessage(
                journalId = journalId,
                title = state.mood.ifBlank { "Pesan Burung" },
                preview = state.content.ifBlank { "Jurnal kosong" }
            )
            _ui.update { it?.copy(id = journalId, saving = false, canShare = false) }
            onDone()
        } catch (t: Throwable) {
            _ui.update { it?.copy(saving = false, shareError = t.message ?: "Gagal mengirim") }
        }
    }

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
