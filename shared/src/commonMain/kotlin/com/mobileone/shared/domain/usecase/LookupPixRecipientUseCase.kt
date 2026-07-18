package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.Recipient
import com.mobileone.shared.domain.repository.PixRepository

/**
 * Consulta o destinatário no diretório DICT do Banco Central (SPEC-003).
 * Operação suspensa — faz IO via [PixRepository].
 */
class LookupPixRecipientUseCase(
    private val pixRepository: PixRepository
) {
    suspend operator fun invoke(key: String): Result<Recipient> =
        pixRepository.lookupRecipient(key)
}
