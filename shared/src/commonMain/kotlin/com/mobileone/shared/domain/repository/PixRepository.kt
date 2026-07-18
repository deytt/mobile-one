package com.mobileone.shared.domain.repository

import com.mobileone.shared.domain.entity.Recipient
import com.mobileone.shared.domain.entity.PixTransferRequest

/**
 * Contrato de acesso a dados do fluxo PIX (SPEC-003).
 * Na POC, implementado por [FakePixRepository]. Em produção, chamaria o DICT via Ktor.
 */
interface PixRepository {
    /**
     * Consulta o destinatário pelo diretório DICT do Banco Central.
     * @return Success com [Recipient] ou Failure com [PixError.RecipientNotFound].
     */
    suspend fun lookupRecipient(pixKey: String): Result<Recipient>

    /**
     * Executa a transferência PIX após confirmação biométrica.
     * @return Success com o e2eId gerado ou Failure com [PixError.TransferFailed].
     */
    suspend fun executeTransfer(request: PixTransferRequest): Result<String>
}
