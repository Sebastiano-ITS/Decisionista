package com.example.decisionista.data

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.decisionista.model.SavedDecision
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

// Estensione per creare/accedere a DataStore.
// Questo crea un singolo file chiamato "decisionista_settings.preferences_pb".
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "decisionista_settings"
)

object DecisionRepository {

    private const val TAG = "DecisionRepository"
    private const val GUEST_USER_ID_FROM_MAIN_ACTIVITY = "_decisionista_guest_user_"
    private const val KEY_SAVED_DECISIONS_PREFIX = "saved_decisions_"

    // Genera una chiave DataStore specifica per utente
    private fun getUserSpecificDecisionsKey(userIdentifier: String): Preferences.Key<String> {
        val sanitizedIdentifier = userIdentifier.replace(Regex("[^a-zA-Z0-9_.-]"), "_")
        return stringPreferencesKey("${KEY_SAVED_DECISIONS_PREFIX}$sanitizedIdentifier")
    }

    suspend fun saveDecisions(
        context: Context,
        userIdentifier: String,
        decisions: List<SavedDecision>
    ) {
        if (userIdentifier == GUEST_USER_ID_FROM_MAIN_ACTIVITY || userIdentifier.isBlank()) {
            Log.i(TAG, "Guest user or blank identifier. Decisions not saved.")
            return
        }

        val dataStoreKey = getUserSpecificDecisionsKey(userIdentifier)
        try {
            val jsonString = Json.encodeToString(decisions)
            val encryptedBytes = CryptoManager.encrypt(context.applicationContext, jsonString)

            if (encryptedBytes != null) {
                val encryptedBase64String =
                    Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                context.dataStore.edit { preferences ->
                    preferences[dataStoreKey] = encryptedBase64String
                }
                Log.i(TAG, "Decisions saved for user: $userIdentifier")
            } else {
                Log.e(TAG, "Encryption returned null. Decisions not saved for $userIdentifier")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving decisions for $userIdentifier", e)
        }
    }

    suspend fun loadDecisions(
        context: Context,
        userIdentifier: String
    ): List<SavedDecision> {
        if (userIdentifier == GUEST_USER_ID_FROM_MAIN_ACTIVITY || userIdentifier.isBlank()) {
            Log.i(TAG, "Guest user or blank identifier. Returning empty list.")
            return emptyList()
        }

        val dataStoreKey = getUserSpecificDecisionsKey(userIdentifier)
        return try {
            val preferences = context.dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        Log.e(TAG, "Error reading preferences for $userIdentifier", exception)
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .first()

            val encryptedBase64String = preferences[dataStoreKey]
            if (encryptedBase64String.isNullOrEmpty()) {
                Log.i(TAG, "No saved decisions found for $userIdentifier")
                return emptyList()
            }

            val encryptedBytes = Base64.decode(encryptedBase64String, Base64.DEFAULT)
            val decryptedJsonString =
                CryptoManager.decrypt(context.applicationContext, encryptedBytes)

            if (decryptedJsonString != null) {
                Json.decodeFromString<List<SavedDecision>>(decryptedJsonString)
            } else {
                Log.e(TAG, "Decryption returned null for $userIdentifier. Clearing corrupted data.")
                clearUserDecisions(context, userIdentifier)
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading decisions for $userIdentifier", e)
            if (e is kotlinx.serialization.SerializationException ||
                e is java.security.GeneralSecurityException
            ) {
                Log.w(TAG, "Potentially corrupt data for $userIdentifier, clearing.")
                clearUserDecisions(context, userIdentifier)
            }
            emptyList()
        }
    }

    private suspend fun clearUserDecisions(context: Context, userIdentifier: String) {
        if (userIdentifier == GUEST_USER_ID_FROM_MAIN_ACTIVITY || userIdentifier.isBlank()) {
            return
        }
        val dataStoreKey = getUserSpecificDecisionsKey(userIdentifier)
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(dataStoreKey)
            }
            Log.i(TAG, "Cleared decisions for user: $userIdentifier due to corruption.")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing decisions for $userIdentifier", e)
        }
    }
}