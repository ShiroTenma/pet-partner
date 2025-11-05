package com.shirotenma.petpartnertest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthGateViewModel @Inject constructor(
    repo: AuthRepository
) : ViewModel() {
    // null/blank → belum login
    val tokenState = repo.observeToken()
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}
