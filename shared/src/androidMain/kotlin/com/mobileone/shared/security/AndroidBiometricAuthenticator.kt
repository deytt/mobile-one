package com.mobileone.shared.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Biometria real via `BiometricPrompt` (ADR-005). `BiometricPrompt` exige uma `FragmentActivity`
 * visível na tela — obtida de [CurrentActivityHolder].
 */
class AndroidBiometricAuthenticator(
    private val context: Context
) : BiometricAuthenticator {

    override suspend fun isAvailable(): Boolean =
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

    override suspend fun authenticate(reason: String): BiometricResult {
        val activity = CurrentActivityHolder.activity
            ?: return BiometricResult.Error("Nenhuma tela em primeiro plano para exibir o prompt biométrico")

        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) continuation.resume(BiometricResult.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!continuation.isActive) return
                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> BiometricResult.UserCancelled
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.TooManyAttempts
                        else -> BiometricResult.Error(errString.toString())
                    }
                    continuation.resume(result)
                }

                override fun onAuthenticationFailed() {
                    // Tentativa individual não reconhecida; o prompt nativo continua aberto e
                    // deixa o usuário tentar de novo ou cancelar — não resolve a coroutine aqui.
                }
            }

            val prompt = BiometricPrompt(activity, executor, callback)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticação biométrica")
                .setSubtitle(reason)
                .setNegativeButtonText("Usar senha")
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()

            continuation.invokeOnCancellation { prompt.cancelAuthentication() }
            prompt.authenticate(promptInfo)
        }
    }
}
