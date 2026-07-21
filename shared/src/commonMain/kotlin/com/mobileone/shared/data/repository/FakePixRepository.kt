package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.domain.entity.PixTransferRequest
import com.mobileone.shared.domain.entity.Recipient
import com.mobileone.shared.domain.error.PixError
import com.mobileone.shared.domain.repository.PixRepository
import kotlinx.coroutines.delay

/**
 * Implementação em memória de [PixRepository] para validação local da SPEC-003.
 * Uma implementação integrada deve usar Ktor para consultar o DICT BCB.
 */
class FakePixRepository : PixRepository {

    override suspend fun lookupRecipient(pixKey: String): Result<Recipient> {
        delay(1_200)

        // Mantém um caminho determinístico para validar o erro de destinatário inexistente.
        if (pixKey.trimStart('0').isEmpty() || pixKey.startsWith("000")) {
            return Result.failure(PixError.RecipientNotFound)
        }

        val recipient = MOCK_RECIPIENTS[pixKey] ?: buildGenericRecipient(pixKey)
        return Result.success(recipient)
    }

    override suspend fun executeTransfer(request: PixTransferRequest): Result<String> {
        delay(2_000)

        val e2eId = generateE2eId()
        return Result.success(e2eId)
    }

    private fun buildGenericRecipient(pixKey: String): Recipient {
        val keyType = when {
            pixKey.length == 36 && pixKey.contains('-') -> PixKeyType.RandomKey
            pixKey.filter { it.isDigit() }.length == 14 -> PixKeyType.CNPJ
            pixKey.filter { it.isDigit() }.length == 11 -> PixKeyType.CPF
            pixKey.contains('@') -> PixKeyType.Email
            pixKey.startsWith("+") -> PixKeyType.Phone
            else -> PixKeyType.RandomKey
        }
        return Recipient(
            name = "João da Silva",
            taxId = "123.456.789-09",
            institution = "Banco Demo S.A.",
            pixKey = pixKey,
            pixKeyType = keyType
        )
    }

    private fun generateE2eId(): String {
        val timestamp = "20260718120000"
        val random = (100_000_000..999_999_999).random().toString()
        return "E0000000${timestamp}${random}"
    }

    companion object {
        private val MOCK_RECIPIENTS = mapOf(
            "joao@email.com" to Recipient(
                name = "João da Silva",
                taxId = "123.456.789-09",
                institution = "Nubank",
                pixKey = "joao@email.com",
                pixKeyType = PixKeyType.Email
            ),
            "123.456.789-09" to Recipient(
                name = "João da Silva",
                taxId = "123.456.789-09",
                institution = "Nubank",
                pixKey = "123.456.789-09",
                pixKeyType = PixKeyType.CPF
            ),
            "+5511999887766" to Recipient(
                name = "Maria Oliveira",
                taxId = "987.654.321-00",
                institution = "Banco do Brasil",
                pixKey = "+5511999887766",
                pixKeyType = PixKeyType.Phone
            ),
            "a1b2c3d4-e5f6-4789-a0b1-c2d3e4f50001" to Recipient(
                name = "Carlos Mendes",
                taxId = "111.222.333-44",
                institution = "Itaú",
                pixKey = "a1b2c3d4-e5f6-4789-a0b1-c2d3e4f50001",
                pixKeyType = PixKeyType.RandomKey
            )
        )
    }
}
