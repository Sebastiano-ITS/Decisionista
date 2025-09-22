package com.example.decisionista.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.google.crypto.tink.Aead
// import com.google.crypto.tink.CleartextKeysetHandle // Non sembra usato, lo commento per pulizia
// import com.google.crypto.tink.JsonKeysetReader // Non sembra usato
// import com.google.crypto.tink.JsonKeysetWriter // Non sembra usato
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig // <-- IMPORT AGGIUNTO/VERIFICATO
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
// import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient // Non sembra usato
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException

object CryptoManager {

    private const val TAG = "CryptoManager"
    private const val PREFERENCE_FILE = "decisionista_tink_prefs"
    private const val KEYSET_NAME = "decisionista_master_keyset"

    // Chiave usata in Android Keystore per cifrare il keyset di Tink.
    private const val MASTER_KEY_ALIAS = "_decisionista_tink_master_key_alias_"
    private const val ANDROID_KEYSTORE_KMS_URI_PREFIX = "android-keystore://"

    private var aead: Aead? = null

    @Synchronized
    fun initialize(context: Context) {
        if (aead != null) {
            Log.i(TAG, "Tink AEAD already initialized.")
            return
        }
        try {
            // PRIMO PASSO FONDAMENTALE: Registra i tipi di chiave AEAD
            AeadConfig.register() // <------------------- RIGA AGGIUNTA
            Log.i(TAG, "AeadConfig registered successfully.")

            // Inizializza o carica il keyset di Tink, protetto da Android Keystore.
            val keysetHandle = AndroidKeysetManager.Builder()
                .withSharedPref(context.applicationContext, KEYSET_NAME, PREFERENCE_FILE) // Usare applicationContext per sicurezza
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM) // Template per nuove chiavi
                .withMasterKeyUri("${ANDROID_KEYSTORE_KMS_URI_PREFIX}${MASTER_KEY_ALIAS}")
                .build()
                .keysetHandle

            aead = keysetHandle.getPrimitive(Aead::class.java)
            Log.i(TAG, "Tink AEAD initialized successfully.")
        } catch (e: GeneralSecurityException) {
            Log.e(TAG, "Error initializing Tink AEAD", e)
            throw RuntimeException("Failed to initialize Tink", e)
        } catch (e: IOException) {
            Log.e(TAG, "Error initializing Tink AEAD (IOException)", e)
            throw RuntimeException("Failed to initialize Tink (IOException)", e)
        }
    }

    private fun getAead(context: Context): Aead {
        if (aead == null) {
            initialize(context.applicationContext) // Assicura l'inizializzazione
        }
        return aead ?: throw IllegalStateException("AEAD not initialized and initialization failed.")
    }

    fun encrypt(context: Context, plaintext: String, associatedData: ByteArray? = null): ByteArray? {
        return try {
            val plaintextBytes = plaintext.toByteArray(StandardCharsets.UTF_8)
            getAead(context.applicationContext).encrypt(plaintextBytes, associatedData) // Usare applicationContext
        } catch (e: GeneralSecurityException) {
            Log.e(TAG, "Encryption failed", e)
            null
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Encryption failed due to AEAD not being initialized", e)
            null
        }
    }

    fun decrypt(context: Context, ciphertext: ByteArray, associatedData: ByteArray? = null): String? {
        return try {
            val decryptedBytes = getAead(context.applicationContext).decrypt(ciphertext, associatedData) // Usare applicationContext
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: GeneralSecurityException) {
            Log.e(TAG, "Decryption failed", e)
            null
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Decryption failed due to AEAD not being initialized", e)
            null
        }
    }
}
