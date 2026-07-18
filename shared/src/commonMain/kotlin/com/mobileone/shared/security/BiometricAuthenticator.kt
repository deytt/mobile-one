package com.mobileone.shared.security

/**
 * Contrato de biometria (ADR-005): a lógica de tentativas/bloqueio fica nos use cases do
 * `domain`; aqui só existe o resultado nativo do sensor. Declarado como interface (em vez de
 * `expect/actual class`) para permitir fakes em teste sem dispositivo físico — mesmo padrão de
 * `AuthRepository`/`WhiteLabelConfigRepository`. A separação real por plataforma continua
 * existindo nas implementações nativas (`AndroidBiometricAuthenticator`/`IosBiometricAuthenticator`),
 * ligadas via Koin.
 */
interface BiometricAuthenticator {
    suspend fun isAvailable(): Boolean
    suspend fun authenticate(reason: String): BiometricResult
}

sealed class BiometricResult {
    object Success : BiometricResult()
    object UserCancelled : BiometricResult()
    object TooManyAttempts : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}
