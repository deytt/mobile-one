package com.mobileone.shared.feature.pix

import com.mobileone.shared.domain.entity.PixKeyType

/**
 * Estado imutável do fluxo PIX (SPEC-003), consumido pelo PixViewModel nativo
 * (Android/iOS) através de StateFlow. Todas as strings de exibição já estão pré-formatadas.
 */
data class PixTransferUiState(
    val step: PixStep = PixStep.EnterKey,
    val keyInput: String = "",
    val detectedKeyType: PixKeyType? = null,
    val keyValidation: PixKeyValidation = PixKeyValidation.Idle,
    val recipient: RecipientDisplay? = null,
    val amount: Long = 0L,                          // centavos
    val amountFormatted: String = "R$ 0,00",
    val description: String = "",
    val isLoading: Boolean = false,
    val receipt: PixReceipt? = null,
    val error: PixError? = null
) {
    /** Erros de domínio do fluxo PIX para exibição na UI. */
    sealed class PixError {
        data class InvalidKey(val reason: String) : PixError()
        object RecipientNotFound : PixError()
        data class LimitExceeded(val limitFormatted: String) : PixError()
        object BiometricCancelled : PixError()
        data class TransferFailed(val reason: String) : PixError()
    }
}

enum class PixStep {
    EnterKey,
    ConfirmRecipient,
    EnterAmount,
    Review,
    Processing,
    Receipt
}

sealed class PixKeyValidation {
    object Idle : PixKeyValidation()
    object Valid : PixKeyValidation()
    data class Invalid(val reason: String) : PixKeyValidation()
}

/**
 * Dados do destinatário formatados para exibição na tela de confirmação.
 * A chave é mascarada: "•••.456.•••-••" para CPF, etc.
 */
data class RecipientDisplay(
    val name: String,
    val institution: String,
    val maskedKey: String
)

/**
 * Comprovante de transferência PIX — exibido na tela final do fluxo (SPEC-003).
 */
data class PixReceipt(
    val transactionId: String,
    val e2eId: String,                  // End-to-End ID gerado pelo Banco Central
    val recipientName: String,
    val amountFormatted: String,
    val dateTimeFormatted: String,
    val authenticationCode: String
)
