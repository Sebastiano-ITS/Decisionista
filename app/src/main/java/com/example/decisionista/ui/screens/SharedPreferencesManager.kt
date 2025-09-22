package com.example.decisionista.ui.screens

import android.content.Context
import android.content.SharedPreferences
import com.example.decisionista.ui.RecentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("decisionista_prefs", Context.MODE_PRIVATE)

    companion object {
        const val LOGGED_IN_EMAIL_KEY = "logged_in_email"
        const val RECENT_ACTIVITIES_KEY = "recent_activities"
        const val DECISION_COUNT_KEY = "decision_count" // 🔹 nuovo
    }

    private val gson = Gson()

    fun saveLoggedInUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getLoggedInUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun clearLoggedInUserEmail() {
        prefs.edit().remove("user_email").apply()
    }

    fun saveRecentActivities(activities: List<RecentActivity>) {
        val json = gson.toJson(activities)
        prefs.edit().putString("recent_activities", json).apply()
    }

    fun getRecentActivities(): List<RecentActivity> {
        val json = prefs.getString("recent_activities", null) ?: return emptyList()
        val type = object : TypeToken<List<RecentActivity>>() {}.type
        return gson.fromJson(json, type)
    }

    // 🔹 Salva il contatore decisioni
    fun saveDecisionCount(count: Int) {
        prefs.edit().putInt(DECISION_COUNT_KEY, count).apply()
    }

    // 🔹 Recupera il contatore decisioni
    fun getDecisionCount(): Int {
        return prefs.getInt(DECISION_COUNT_KEY, 0)
    }

    // 🔹 Reset contatore decisioni
    fun clearDecisionCount() {
        prefs.edit().remove(DECISION_COUNT_KEY).apply()
    }
}
