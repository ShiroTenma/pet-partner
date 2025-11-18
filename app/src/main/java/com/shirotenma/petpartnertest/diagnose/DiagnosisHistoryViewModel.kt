package com.shirotenma.petpartnertest.diagnose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.diagnose.data.DiagnosisRepository
import com.shirotenma.petpartnertest.diagnose.db.Diagnosis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisHistoryViewModel @Inject constructor(
    private val repo: DiagnosisRepository
) : ViewModel() {
    fun history(petId: Long): Flow<List<Diagnosis>> = repo.history(petId)
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}
