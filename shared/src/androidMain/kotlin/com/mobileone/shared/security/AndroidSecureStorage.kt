package com.mobileone.shared.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Persistência segura via `EncryptedSharedPreferences` + Android Keystore (ADR-005).
 * `EncryptedSharedPreferences` foi descontinuada na `security-crypto` 1.1.0 em favor de
 * DataStore + Tink, mas é mantida aqui pela simplicidade — suficiente para esta POC; uma
 * migração para DataStore+Tink pode ser avaliada em um ADR futuro.
 */
class AndroidSecureStorage(private val context: Context) : SecureStorage {

    private val preferences: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun put(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override suspend fun get(key: String): String? = preferences.getString(key, null)

    override suspend fun delete(key: String) {
        preferences.edit().remove(key).apply()
    }

    override suspend fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "mobile_one_secure_prefs"
    }
}
