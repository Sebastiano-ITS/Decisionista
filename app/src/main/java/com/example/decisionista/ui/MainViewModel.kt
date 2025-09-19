package com.example.decisionista.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionista.ui.screens.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class User(val email: String)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val sharedPrefs = SharedPreferencesManager(application)

    init {
        // Carica l'utente loggato all'avvio del ViewModel
        viewModelScope.launch {
            val email = sharedPrefs.getLoggedInUserEmail()
            if (email != null) {
                _currentUser.value = User(email)
            }
        }
    }

    fun register(email: String) {
        // Logica di registrazione
        // In un'app reale, qui salveresti l'utente su un database
    }

    fun login(email: String) {
        viewModelScope.launch {
            _currentUser.value = User(email = email)
            sharedPrefs.saveLoggedInUserEmail(email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            sharedPrefs.clearLoggedInUserEmail()
        }
    }
}
