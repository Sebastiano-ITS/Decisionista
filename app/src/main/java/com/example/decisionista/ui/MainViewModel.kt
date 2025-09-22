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

    private val _decisionCount = MutableStateFlow(0)
    val decisionCount: StateFlow<Int> = _decisionCount.asStateFlow()

    private val _optionsList = MutableStateFlow<List<String>>(emptyList())
    val optionsList: StateFlow<List<String>> = _optionsList.asStateFlow()

    private val _finalDecision = MutableStateFlow<String?>(null)
    val finalDecision: StateFlow<String?> = _finalDecision.asStateFlow()

    // Usiamo SharedPreferencesManager per gestire l'utente loggato
    private val sharedPrefs = SharedPreferencesManager(application)

    init {
        viewModelScope.launch {
            val email = sharedPrefs.getLoggedInUserEmail()
            if (email != null) {
                _currentUser.value = User(email)
            }
        }
    }

    fun register(email: String) {
        // Logica di registrazione
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

    fun incrementDecisionCount() {
        _decisionCount.value += 1
    }

    fun setOptions(options: List<String>) {
        _optionsList.value = options
    }

    // Nuova funzione per impostare la decisione finale
    fun setFinalDecision(decision: String) {
        _finalDecision.value = decision
    }

    // Funzione per resettare la decisione (opzionale, utile se vuoi ricominciare)
    fun clearFinalDecision() {
        _finalDecision.value = null
    }
}
