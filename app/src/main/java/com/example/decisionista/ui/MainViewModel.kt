package com.example.decisionista.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class User(val email: String)

class MainViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String) {
        _currentUser.value = User(email = email)
    }

    fun logout() {
        _currentUser.value = null
    }
}
