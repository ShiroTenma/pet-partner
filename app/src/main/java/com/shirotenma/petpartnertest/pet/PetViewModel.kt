package com.shirotenma.petpartnertest.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val repo: PetRepository
) : ViewModel() {

    val pets = repo.observePets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observePet(id: Long): Flow<Pet?> = repo.observePet(id)

    fun savePet(
        id: Long?,
        name: String,
        species: String,
        birthDate: String?,
        notes: String?,
        onDone: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            // validasi sederhana
            val n = name.trim()
            val s = species.trim()
            if (n.isBlank() || s.isBlank()) {
                onDone?.invoke(); return@launch
            }
            val pet = Pet(
                id = id ?: 0,
                name = n,
                species = s,
                birthDate = birthDate?.trim().takeUnless { it.isNullOrBlank() },
                notes = notes?.trim().takeUnless { it.isNullOrBlank() }
            )
            repo.upsert(pet)
            onDone?.invoke()
        }
    }

    fun deletePet(pet: Pet, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.delete(pet)
            onDone?.invoke()
        }
    }
}
