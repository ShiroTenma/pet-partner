package com.shirotenma.petpartnertest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(val loading: Boolean = false, val error: String? = null, val loggedIn: Boolean = false)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, pass: String) = viewModelScope.launch {
        _uiState.value = AuthUiState(loading = true)
        val ok = authRepository.login(email, pass)
        _uiState.value = if (ok) AuthUiState(loggedIn = true) else AuthUiState(error = "Email/Password salah")
    }
    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }

}
