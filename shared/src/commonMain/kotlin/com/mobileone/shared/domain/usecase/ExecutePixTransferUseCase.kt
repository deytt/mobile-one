package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.PixTransferRequest
import com.mobileone.shared.domain.error.PixError
import com.mobileone.shared.domain.repository.PixRepository
import com.mobileone.shared.feature.pix.PixLimitsValidator
import com.mobileone.shared.platform.currentEpochSeconds
import com.mobileone.shared.security.BiometricAuthenticator
import com.mobileone.shared.security.BiometricResult

/**
 * Executa uma transferência PIX com confirmação biométrica (SPEC-003).
 * Fluxo: validar limite → autenticar biometria → executar transferência.
 * [getCurrentHour] é injetável para testabilidade (padrão lambda com default de plataforma).
 */
class ExecutePixTransferUseCase(
    private val pixRepository: PixRepository,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val limitsValidator: PixLimitsValidator,
    private val getCurrentHour: () -> Int = { ((currentEpochSeconds() % 86400L) / 3600L).toInt() }
) {
    suspend operator fun invoke(request: PixTransferRequest): Result<String> {
        // 1. Validar limite diurno/noturno
        val limitResult = limitsValidator.validate(request.amountCents, getCurrentHour())
        if (limitResult.isFailure) return limitResult.map { "" }

        // 2. Confirmar com biometria
        val biometricResult = biometricAuthenticator.authenticate(
            reason = "Confirme para enviar ${formatCentsSimple(request.amountCents)} para ${request.recipientName}"
        )
        if (biometricResult != BiometricResult.Success) {
            return Result.failure(PixError.BiometricCancelled)
        }

        // 3. Executar a transferência
        return pixRepository.executeTransfer(request)
    }

    private fun formatCentsSimple(cents: Long): String {
        val reais = cents / 100
        val centavos = cents % 100
        return "R$ $reais,${centavos.toString().padStart(2, '0')}"
    }
}
