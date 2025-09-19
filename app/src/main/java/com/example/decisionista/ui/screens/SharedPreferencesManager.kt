package com.example.decisionista.ui.screens

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val LOGGED_IN_EMAIL_KEY = "logged_in_email"
    }

    fun saveLoggedInUserEmail(email: String) {
        prefs.edit().putString(LOGGED_IN_EMAIL_KEY, email).apply()
    }

    fun getLoggedInUserEmail(): String? {
        return prefs.getString(LOGGED_IN_EMAIL_KEY, null)
    }

    fun clearLoggedInUserEmail() {
        prefs.edit().remove(LOGGED_IN_EMAIL_KEY).apply()
    }
}
