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
import java.security.GeneralSecurityException // Importato per il controllo esplicito
import kotlinx.serialization.SerializationException // Importato per il controllo esplicito


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
            Log.i(TAG, "[SAVE] Guest user or blank identifier. Decisions not saved for '$userIdentifier'.")
            return
        }

        val dataStoreKey = getUserSpecificDecisionsKey(userIdentifier)
        Log.i(TAG, "[SAVE] Attempting to save/update decisions for user: '$userIdentifier' with DataStore key: '${dataStoreKey.name}'")

        if (decisions.isEmpty()) {
            Log.w(TAG, "[SAVE] Decisions list is empty for user: '$userIdentifier'. Clearing DataStore entry to reflect this.")
            try {
                context.dataStore.edit { preferences ->
                    preferences.remove(dataStoreKey)
                }
                Log.i(TAG, "[SAVE] Successfully cleared DataStore entry for empty decisions list for user: '$userIdentifier'.")
            } catch (e: Exception) {
                Log.e(TAG, "[SAVE] Error clearing DataStore for empty decisions list for user: '$userIdentifier'.", e)
            }
            return // Niente da salvare se la lista è vuota
        }

        val appCtx = context.applicationContext
        if (appCtx == null) {
            Log.e(TAG, "[SAVE] CRITICAL: ApplicationContext is null for user: '$userIdentifier'. CANNOT PROCEED WITH SAVE.")
            return
        }

        try {
            Log.d(TAG, "[SAVE] Step 1: Serializing decisions list for user: '$userIdentifier'. List size: ${decisions.size}")
            val jsonString = Json.encodeToString(decisions)
            Log.v(TAG, "[SAVE] JSON for user '$userIdentifier' (first 200 chars): ${jsonString.take(200)}")

            if (jsonString.isBlank()) {
                Log.e(TAG, "[SAVE] CRITICAL: JSON string is blank after serialization for user: '$userIdentifier'. Decisions NOT saved.")
                return
            }
            Log.d(TAG, "[SAVE] Step 1 SUCCESS: Serialization complete for user: '$userIdentifier'.")

            Log.d(TAG, "[SAVE] Step 2: Encrypting JSON string for user: '$userIdentifier'...")
            val encryptedBytes = CryptoManager.encrypt(appCtx, jsonString)

            if (encryptedBytes == null) {
                Log.e(TAG, "[SAVE] CRITICAL: Encryption returned null for user: '$userIdentifier'. Decisions NOT saved.")
                return
            }
            if (encryptedBytes.isEmpty()) {
                Log.e(TAG, "[SAVE] CRITICAL: Encryption returned an empty byte array for user: '$userIdentifier'. Decisions NOT saved.")
                return
            }
            Log.d(TAG, "[SAVE] Step 2 SUCCESS: Encryption complete for user: '$userIdentifier'. Encrypted bytes length: ${encryptedBytes.size}")

            Log.d(TAG, "[SAVE] Step 3: Encoding encrypted bytes to Base64 string for user: '$userIdentifier'...")
            val encryptedBase64String = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            if (encryptedBase64String.isNullOrBlank()) {
                Log.e(TAG, "[SAVE] CRITICAL: Base64 encoded string is null or blank for user: '$userIdentifier'. Decisions NOT saved.")
                return
            }
            Log.d(TAG, "[SAVE] Step 3 SUCCESS: Base64 encoding complete for user: '$userIdentifier'.")

            Log.i(TAG, "[SAVE] Step 4: Writing to DataStore for user: '$userIdentifier'...")
            context.dataStore.edit { preferences ->
                preferences[dataStoreKey] = encryptedBase64String
            }
            Log.i(TAG, "[SAVE] FINAL SUCCESS: Decisions presumed saved and written to DataStore for user: '$userIdentifier'.")

        } catch (e: SerializationException) {
            Log.e(TAG, "[SAVE] CRITICAL FAILURE: SerializationException for user: '$userIdentifier'. Decisions NOT saved.", e)
        } catch (e: Exception) {
            Log.e(TAG, "[SAVE] CRITICAL FAILURE: Generic error saving decisions for user: '$userIdentifier'. Decisions NOT saved.", e)
        }
    }

    suspend fun loadDecisions(
        context: Context,
        userIdentifier: String
    ): List<SavedDecision> {
        if (userIdentifier == GUEST_USER_ID_FROM_MAIN_ACTIVITY || userIdentifier.isBlank()) {
            Log.i(TAG, "[LOAD] Guest user or blank identifier. Returning empty list for '$userIdentifier'.")
            return emptyList()
        }
        Log.i(TAG, "[LOAD] Attempting to load decisions for user: '$userIdentifier'")

        val dataStoreKey = getUserSpecificDecisionsKey(userIdentifier)
        try {
            Log.d(TAG, "[LOAD] Step 1: Reading from DataStore for user '$userIdentifier', key '${dataStoreKey.name}'.")
            val preferences = context.dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        Log.e(TAG, "[LOAD] IOException reading preferences for '$userIdentifier'. Emitting empty preferences.", exception)
                        emit(emptyPreferences())
                    } else {
                        Log.e(TAG, "[LOAD] Unexpected exception reading preferences flow for '$userIdentifier'. Rethrowing.", exception)
                        throw exception // Rethrow per essere gestito dal try-catch esterno di loadDecisions
                    }
                }
                .first()

            val encryptedBase64String = preferences[dataStoreKey]
            if (encryptedBase64String.isNullOrEmpty()) {
                Log.i(TAG, "[LOAD] No saved decisions found in DataStore for '$userIdentifier' with key '${dataStoreKey.name}'. Returning empty list.")
                return emptyList()
            }
            Log.d(TAG, "[LOAD] Step 1 SUCCESS: Found encrypted data (Base64) for '$userIdentifier'.")

            val appCtx = context.applicationContext
            if (appCtx == null) {
                Log.e(TAG, "[LOAD] CRITICAL: ApplicationContext is null for user: '$userIdentifier'. CANNOT PROCEED WITH LOAD.")
                return emptyList()
            }

            Log.d(TAG, "[LOAD] Step 2: Decoding Base64 string for '$userIdentifier'...")
            val encryptedBytes = Base64.decode(encryptedBase64String, Base64.DEFAULT)
            if (encryptedBytes == null || encryptedBytes.isEmpty()) {
                Log.e(TAG, "[LOAD] CRITICAL: Failed to decode Base64 string or result is empty for '$userIdentifier'. Returning empty list. Data NOT cleared.")
                return emptyList()
            }
            Log.d(TAG, "[LOAD] Step 2 SUCCESS: Base64 decoding complete. Byte array length: ${encryptedBytes.size}.")

            Log.d(TAG, "[LOAD] Step 3: Attempting to decrypt for '$userIdentifier'...")
            val decryptedJsonString = CryptoManager.decrypt(appCtx, encryptedBytes)

            if (decryptedJsonString == null) {
                 Log.e(TAG, "[LOAD] CRITICAL: Decryption returned null for '$userIdentifier'. Returning empty list for this session. Data NOT cleared from disk.")
                return emptyList()
            }
            if (decryptedJsonString.isBlank()) {
                Log.e(TAG, "[LOAD] CRITICAL: Decryption returned a blank string for '$userIdentifier'. Returning empty list. Data NOT cleared.")
                return emptyList()
            }
            Log.d(TAG, "[LOAD] Step 3 SUCCESS: Decryption complete for '$userIdentifier'.")
            Log.v(TAG, "[LOAD] Decrypted JSON for user '$userIdentifier' (first 200 chars): ${decryptedJsonString.take(200)}")


            Log.d(TAG, "[LOAD] Step 4: Parsing JSON for '$userIdentifier'...")
            val loadedDecisions = Json.decodeFromString<List<SavedDecision>>(decryptedJsonString)
            Log.i(TAG, "[LOAD] FINAL SUCCESS: Decisions successfully loaded and parsed for user: '$userIdentifier'. Count: ${loadedDecisions.size}")
            return loadedDecisions

        } catch (e: SerializationException) {
            Log.e(TAG, "[LOAD] CRITICAL FAILURE: SerializationException while parsing decrypted JSON for '$userIdentifier'. Returning empty list. Data NOT cleared.", e)
            return emptyList()
        } catch (e: GeneralSecurityException) {
            Log.e(TAG, "[LOAD] CRITICAL FAILURE: GeneralSecurityException (likely during decryption) for '$userIdentifier'. Returning empty list. Data NOT cleared.", e)
            return emptyList()
        } catch (e: IllegalArgumentException) {
             Log.e(TAG, "[LOAD] CRITICAL FAILURE: IllegalArgumentException (e.g. invalid Base64 or JSON) for '$userIdentifier'. Returning empty list. Data NOT cleared.", e)
             return emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "[LOAD] CRITICAL FAILURE: Generic error during loadDecisions for '$userIdentifier': ${e.javaClass.simpleName}. Returning empty list. Data NOT cleared.", e)
            return emptyList()
        }
    }

//    private suspend fun clearUserDecisions(context: Context, userIdentifier: String) {
//        // ... (commentato come prima)
//    }
}
