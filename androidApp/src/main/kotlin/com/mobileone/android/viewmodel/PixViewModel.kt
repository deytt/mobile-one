package com.mobileone.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.domain.entity.PixTransferRequest
import com.mobileone.shared.domain.error.PixError
import com.mobileone.shared.domain.usecase.DetectPixKeyTypeUseCase
import com.mobileone.shared.domain.usecase.ExecutePixTransferUseCase
import com.mobileone.shared.domain.usecase.LookupPixRecipientUseCase
import com.mobileone.shared.domain.usecase.ParsePixQRCodeUseCase
import com.mobileone.shared.domain.usecase.ValidatePixKeyUseCase
import com.mobileone.shared.feature.pix.PixKeyValidation
import com.mobileone.shared.feature.pix.PixReceipt
import com.mobileone.shared.feature.pix.PixStep
import com.mobileone.shared.feature.pix.PixTransferUiState
import com.mobileone.shared.feature.pix.RecipientDisplay
import com.mobileone.shared.security.QRCodeScanner
import com.mobileone.shared.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel do fluxo PIX (SPEC-003). Consome os use cases do shared e expõe
 * [PixTransferUiState] via [StateFlow]. UI Compose observa e nunca contém regras de negócio.
 */
class PixViewModel(
    private val detectKeyType: DetectPixKeyTypeUseCase,
    private val validateKey: ValidatePixKeyUseCase,
    private val lookupRecipient: LookupPixRecipientUseCase,
    private val executeTransfer: ExecutePixTransferUseCase,
    private val parseQRCode: ParsePixQRCodeUseCase,
    private val qrCodeScanner: QRCodeScanner
) : ViewModel() {

    private val _uiState = MutableStateFlow(PixTransferUiState())
    val uiState: StateFlow<PixTransferUiState> = _uiState.asStateFlow()

    // ── Tela 1: Inserir chave ─────────────────────────────────────────────────

    fun onKeyChanged(input: String) {
        val detected = detectKeyType(input)
        val validation = when {
            input.isBlank() -> PixKeyValidation.Idle
            detected == null -> PixKeyValidation.Idle
            else -> {
                val result = validateKey(input, detected)
                if (result.isSuccess) PixKeyValidation.Valid
                else PixKeyValidation.Idle  // não mostrar erro enquanto ainda digita
            }
        }
        _uiState.update {
            it.copy(keyInput = input, detectedKeyType = detected, keyValidation = validation)
        }
    }

    fun onContinueFromKey() {
        val state = _uiState.value
        val keyType = state.detectedKeyType ?: return
        val validationResult = validateKey(state.keyInput, keyType)
        if (validationResult.isFailure) {
            val error = validationResult.exceptionOrNull() as? PixError.InvalidKey
            _uiState.update {
                it.copy(keyValidation = PixKeyValidation.Invalid(error?.reason ?: "Chave inválida"))
            }
            return
        }
        lookupRecipientFor(state.keyInput)
    }

    fun onScanQRCode() {
        viewModelScope.launch {
            val scanResult = qrCodeScanner.scan()
            scanResult.onSuccess { payload ->
                val parseResult = parseQRCode(payload)
                parseResult.onSuccess { qrData ->
                    _uiState.update {
                        it.copy(
                            keyInput = qrData.pixKey,
                            detectedKeyType = PixKeyType.QRCode,
                            keyValidation = PixKeyValidation.Valid,
                            amount = qrData.amountCents ?: 0L,
                            amountFormatted = CurrencyFormatter.formatBalance(qrData.amountCents ?: 0L)
                        )
                    }
                    lookupRecipientFor(qrData.pixKey)
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(error = PixTransferUiState.PixError.InvalidKey(
                            (error as? PixError.QRCodeInvalid)?.reason ?: "QR Code inválido"
                        ))
                    }
                }
            }.onFailure {
                // Usuário cancelou o scan — nenhuma ação necessária
            }
        }
    }

    // ── Tela 2: Confirmar destinatário ────────────────────────────────────────

    fun onConfirmRecipient() {
        _uiState.update { it.copy(step = PixStep.EnterAmount) }
    }

    fun onRejectRecipient() {
        _uiState.update {
            it.copy(step = PixStep.EnterKey, recipient = null, keyValidation = PixKeyValidation.Idle)
        }
    }

    // ── Tela 3: Inserir valor ─────────────────────────────────────────────────

    fun onAmountChanged(cents: Long) {
        _uiState.update {
            it.copy(
                amount = cents,
                amountFormatted = CurrencyFormatter.formatBalance(cents)
            )
        }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onContinueFromAmount() {
        if (_uiState.value.amount <= 0) return
        _uiState.update { it.copy(step = PixStep.Review) }
    }

    // ── Tela 4: Revisão / Confirmação ─────────────────────────────────────────

    fun onConfirmTransfer() {
        val state = _uiState.value
        val recipient = state.recipient ?: return
        val keyType = state.detectedKeyType ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(step = PixStep.Processing, isLoading = true) }

            val request = PixTransferRequest(
                pixKey = state.keyInput,
                pixKeyType = keyType,
                amountCents = state.amount,
                description = state.description,
                recipientName = recipient.name,
                recipientTaxId = "",  // maskedKey no display, taxId não exposto na UI
                recipientInstitution = recipient.institution
            )

            executeTransfer(request)
                .onSuccess { e2eId ->
                    val receipt = PixReceipt(
                        transactionId = "TXN-${e2eId.takeLast(8)}",
                        e2eId = e2eId,
                        recipientName = recipient.name,
                        amountFormatted = state.amountFormatted,
                        dateTimeFormatted = buildDateTimeFormatted(),
                        authenticationCode = e2eId.takeLast(6).uppercase()
                    )
                    _uiState.update {
                        it.copy(step = PixStep.Receipt, isLoading = false, receipt = receipt)
                    }
                }
                .onFailure { error ->
                    val uiError = error.toUiError()
                    _uiState.update {
                        it.copy(
                            step = if (error is PixError.BiometricCancelled) PixStep.Review else PixStep.Review,
                            isLoading = false,
                            error = uiError
                        )
                    }
                }
        }
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    fun onDismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onReset() {
        _uiState.value = PixTransferUiState()
    }

    private fun lookupRecipientFor(key: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            lookupRecipient(key)
                .onSuccess { recipient ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipient = RecipientDisplay(
                                name = recipient.name,
                                institution = recipient.institution,
                                maskedKey = maskKey(recipient.pixKey, recipient.pixKeyType)
                            ),
                            step = PixStep.ConfirmRecipient
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.toUiError()
                        )
                    }
                }
        }
    }

    private fun maskKey(key: String, type: PixKeyType): String = when (type) {
        is PixKeyType.CPF -> {
            val digits = key.filter { it.isDigit() }
            "•••.${digits.substring(3, 6)}.•••-••"
        }
        is PixKeyType.CNPJ -> {
            val digits = key.filter { it.isDigit() }
            "••.•••.${digits.substring(5, 8)}/••••-••"
        }
        is PixKeyType.Email -> {
            val parts = key.split("@")
            if (parts.size == 2) "${parts[0].take(2)}•••@${parts[1]}"
            else "•••@•••"
        }
        is PixKeyType.Phone -> "+55 (••) •••••-${key.takeLast(4)}"
        is PixKeyType.RandomKey -> "${key.take(8)}-••••-••••-••••-••••••••••••"
        is PixKeyType.QRCode -> key.take(20) + "..."
    }

    private fun buildDateTimeFormatted(): String {
        val epochSeconds = com.mobileone.shared.platform.currentEpochSeconds()
        val epochDay = (epochSeconds / 86400).toInt()
        val hour = ((epochSeconds % 86400) / 3600).toInt()
        val minute = ((epochSeconds % 3600) / 60).toInt()
        val dateStr = CurrencyFormatter.formatEpochDay(epochDay, epochDay)
        return "$dateStr às ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    private fun Throwable.toUiError(): PixTransferUiState.PixError = when (this) {
        is PixError.InvalidKey -> PixTransferUiState.PixError.InvalidKey(reason)
        is PixError.RecipientNotFound -> PixTransferUiState.PixError.RecipientNotFound
        is PixError.LimitExceeded -> PixTransferUiState.PixError.LimitExceeded(
            "R$ ${limitCents / 100}"
        )
        is PixError.BiometricCancelled -> PixTransferUiState.PixError.BiometricCancelled
        is PixError.TransferFailed -> PixTransferUiState.PixError.TransferFailed(reason)
        else -> PixTransferUiState.PixError.TransferFailed(message ?: "Erro desconhecido")
    }
}
