package com.shirotenma.petpartnertest

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named   // 👈 ADD THIS

@HiltViewModel
class MainViewModel @Inject constructor(
    @Named("greeting") private val greeting: String   // 👈 keep this
) : ViewModel() {
    fun greeting() = greeting
}
