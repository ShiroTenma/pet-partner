package com.shirotenma.petpartnertest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.data.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepositoryImpl
) : ViewModel() {

    val tokenState = auth.observeToken()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(auth.login(email, pass))
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(auth.register(name, email, pass))
        }
    }

    fun logout() {
        viewModelScope.launch { auth.logout() }
    }
}
