package com.shirotenma.petpartnertest.pet.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetRecordViewModel @Inject constructor(
    private val repo: PetRecordRepository
) : ViewModel() {

    data class Ui(
        val id: Long? = null,
        val petId: Long,
        val type: String = "",
        val title: String = "",
        val date: String = "",
        val notes: String = ""
    )

    private val _ui = MutableStateFlow<Ui?>(null)
    val ui: StateFlow<Ui?> = _ui

    fun startNew(petId: Long) {
        _ui.value = Ui(petId = petId)
    }

    fun load(id: Long) = viewModelScope.launch {
        repo.get(id)?.let { r ->
            _ui.value = Ui(
                id = r.id,
                petId = r.petId,
                type = r.type,
                title = r.title,
                date = r.date,
                notes = r.notes ?: ""
            )
        }
    }

    fun edit(block: (Ui) -> Ui) {
        _ui.update { it?.let(block) }
    }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val s = _ui.value ?: return@launch
        repo.upsert(
            id = s.id,
            petId = s.petId,
            type = s.type,
            title = s.title,
            date = s.date,
            notes = s.notes.ifBlank { null }
        )
        onDone()
    }

    /** Hapus berdasar ID dari state, lalu callback */
    fun deleteCurrent(onDone: () -> Unit) = viewModelScope.launch {
        val id = _ui.value?.id ?: return@launch
        repo.delete(id)
        onDone()
    }

    /** Opsional: hapus langsung by id (dipakai jika mau panggil dari luar) */
    fun deleteById(id: Long, onDone: () -> Unit) = viewModelScope.launch {
        repo.delete(id)
        onDone()
    }
}
