// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordListViewModel.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PetRecordListViewModel @Inject constructor(
    private val repo: PetRecordRepository
) : ViewModel() {
    fun records(petId: Long) = repo.records(petId)

    suspend fun exportJson(): String = repo.exportAllAsJson()
}

