package com.mobileone.shared.security

/**
 * Armazenamento seguro de sessão (ADR-005) — Android: `EncryptedSharedPreferences`/Keystore;
 * iOS: Keychain Services. Interface (em vez de `expect/actual class`) para permitir fake em
 * teste — ver nota em [BiometricAuthenticator].
 */
interface SecureStorage {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun delete(key: String)
    suspend fun clear()
}

/** Chaves usadas pelo fluxo de autenticação (SPEC-001) dentro do [SecureStorage]. */
object AuthSecureStorageKeys {
    const val ACCESS_TOKEN = "auth.access_token"
    const val REFRESH_TOKEN = "auth.refresh_token"
    const val USER_ID = "auth.user_id"
    const val USER_NAME = "auth.user_name"
    const val BIOMETRIC_ENABLED = "auth.biometric_enabled"

    val ALL = listOf(ACCESS_TOKEN, REFRESH_TOKEN, USER_ID, USER_NAME, BIOMETRIC_ENABLED)
}
