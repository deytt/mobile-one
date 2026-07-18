package com.mobileone.shared.domain.error

/**
 * Erros de domínio do fluxo PIX (SPEC-003). Sempre typed — nunca Exception genérica.
 */
sealed class PixError : Throwable() {
    data class InvalidKey(val reason: String) : PixError()
    object RecipientNotFound : PixError()
    data class LimitExceeded(val limitCents: Long) : PixError()
    object BiometricCancelled : PixError()
    data class TransferFailed(val reason: String) : PixError()
    data class QRCodeInvalid(val reason: String) : PixError()
}
