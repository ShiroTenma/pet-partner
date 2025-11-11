// app/src/main/java/com/shirotenma/petpartnertest/AuthViewModel.kt
package com.shirotenma.petpartnertest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.data.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepositoryImpl // atau AuthRepository kalau sudah di-bind
) : ViewModel() {

    val tokenState: StateFlow<String?> =
        repo.observeToken().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.login(email, pass)
            onResult(ok)
        }
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val r = repo.register(name, email, pass)
            r.onSuccess {
                onResult(true, null)
            }.onFailure { e ->
                val err = if (e is IllegalStateException && e.message == "EMAIL_ALREADY_USED")
                    "EMAIL_ALREADY_USED" else "UNKNOWN"
                onResult(false, err)
            }
        }
    }

    fun logout() {
        viewModelScope.launch { repo.logout() }
    }
}
