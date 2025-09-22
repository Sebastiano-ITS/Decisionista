package com.example.decisionista.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionista.ui.screens.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class User(val email: String)
data class OracleConsultation(val message: String, val timestamp: Long = System.currentTimeMillis())

data class RecentActivity(
    val label: String,
    val time: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {


    // SharedPreferencesManager
    private val sharedPrefs = SharedPreferencesManager(application)


    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _decisionCount = MutableStateFlow(sharedPrefs.getDecisionCount()) // 🔹 carica valore salvato
    val decisionCount: StateFlow<Int> = _decisionCount.asStateFlow()

    private val _optionsList = MutableStateFlow<List<String>>(emptyList())
    val optionsList: StateFlow<List<String>> = _optionsList.asStateFlow()

    private val _finalDecision = MutableStateFlow<String?>(null)
    val finalDecision: StateFlow<String?> = _finalDecision.asStateFlow()

    private val _glimmerioList = MutableStateFlow<List<OracleConsultation>>(emptyList())
    val glimmerioList: StateFlow<List<OracleConsultation>> = _glimmerioList.asStateFlow()

    private val _oracleState = MutableStateFlow<String?>(null)
    val oracleState: StateFlow<String?> = _oracleState.asStateFlow()


    private val gson = Gson()

    private val _recentActivities = MutableStateFlow<List<RecentActivity>>(emptyList())
    val recentActivities: StateFlow<List<RecentActivity>> = _recentActivities.asStateFlow()

    init {
        viewModelScope.launch {
            // Carica utente loggato
            val email = sharedPrefs.getLoggedInUserEmail()
            if (email != null) {
                _currentUser.value = User(email)
            }

            // Carica attività recenti salvate
            val savedActivities = sharedPrefs.getRecentActivities()
            _recentActivities.value = savedActivities
        }
    }

    // Aggiungi una nuova attività
    fun addRecentActivity(label: String) {
        val newActivity = RecentActivity(
            label = label,
            time = java.text.SimpleDateFormat("HH:mm, dd/MM", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
        val updatedList = (listOf(newActivity) + _recentActivities.value).take(10)
        _recentActivities.value = updatedList

        // Salva subito su SharedPreferences
        sharedPrefs.saveRecentActivities(updatedList)
    }

    fun register(email: String) {
        // Logica di registrazione (se serve)
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
        sharedPrefs.saveDecisionCount(_decisionCount.value) // 🔹 salva subito
    }

    fun resetDecisionCount() {
        _decisionCount.value = 0
        sharedPrefs.clearDecisionCount()
    }

    fun setOptions(options: List<String>) {
        _optionsList.value = options
    }

    fun setFinalDecision(decision: String) {
        _finalDecision.value = decision
    }

    fun clearFinalDecision() {
        _finalDecision.value = null
    }

    fun addOracleConsultation(message: String) {
        val newConsultation = OracleConsultation(message)
        _glimmerioList.value = _glimmerioList.value + newConsultation
    }

    fun setOracleState(message: String?) {
        _oracleState.value = message
    }
}
