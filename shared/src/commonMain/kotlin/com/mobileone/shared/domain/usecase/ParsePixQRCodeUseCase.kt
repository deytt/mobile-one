package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.PixKeyType
import com.mobileone.shared.domain.entity.PixQRCodeData
import com.mobileone.shared.domain.error.PixError

/**
 * Parseia um payload QR Code PIX no padrão EMV do Banco Central (SPEC-003).
 * Operação pura, sem IO. Suporta QR estático (campo 26) e dinâmico (campo 62).
 *
 * Estrutura EMV: sequência de TLV (tag 2 chars, length 2 chars, value N chars).
 * Campos relevantes para PIX:
 *   - 26/27/28/29: merchant account info (contém a chave PIX no subcampo 01)
 *   - 54: transaction amount
 *   - 59: merchant name
 *   - 62/05: additional data (txid)
 */
class ParsePixQRCodeUseCase {

    operator fun invoke(rawPayload: String): Result<PixQRCodeData> {
        if (rawPayload.isBlank()) {
            return Result.failure(PixError.QRCodeInvalid("Payload vazio"))
        }

        return try {
            val fields = parseTlv(rawPayload)

            // Chave PIX fica no subcampo 01 dos campos de merchant account info (26-29)
            val pixKey = extractPixKey(fields)
                ?: return Result.failure(PixError.QRCodeInvalid("Chave PIX não encontrada no payload"))

            val merchantName = fields["59"] ?: "Destinatário"
            val amountStr = fields["54"]
            val amountCents = amountStr?.let { parseAmountToCents(it) }
            val txId = extractSubfield(fields["62"] ?: "", "05") ?: ""
            val description = extractSubfield(fields["62"] ?: "", "02") ?: ""

            val keyType = detectKeyType(pixKey)

            Result.success(
                PixQRCodeData(
                    pixKey = pixKey,
                    pixKeyType = keyType,
                    merchantName = merchantName,
                    amountCents = amountCents,
                    description = description,
                    txId = txId
                )
            )
        } catch (e: Exception) {
            Result.failure(PixError.QRCodeInvalid("Payload EMV inválido: ${e.message}"))
        }
    }

    /** Parseia a string EMV em mapa de tag → value de nível raiz. */
    private fun parseTlv(payload: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        var pos = 0
        while (pos + 4 <= payload.length) {
            val tag = payload.substring(pos, pos + 2)
            val lengthStr = payload.substring(pos + 2, pos + 4)
            val length = lengthStr.toIntOrNull()
                ?: throw IllegalArgumentException("Length inválido na posição $pos")
            if (pos + 4 + length > payload.length) break
            val value = payload.substring(pos + 4, pos + 4 + length)
            result[tag] = value
            pos += 4 + length
        }
        return result
    }

    /** Extrai um subcampo de um valor TLV já parseado (ex: subcampo "01" do campo 26). */
    private fun extractSubfield(value: String, subtag: String): String? {
        val subfields = parseTlv(value)
        return subfields[subtag]
    }

    /** Busca a chave PIX nos campos de merchant account info (26-29). */
    private fun extractPixKey(fields: Map<String, String>): String? {
        for (tag in listOf("26", "27", "28", "29")) {
            val value = fields[tag] ?: continue
            val key = extractSubfield(value, "01")
            if (!key.isNullOrBlank()) return key
        }
        return null
    }

    /** Converte string de valor EMV ("123.45") para centavos (12345L). */
    private fun parseAmountToCents(amount: String): Long {
        val clean = amount.replace(",", ".")
        val value = clean.toDoubleOrNull() ?: return 0L
        return (value * 100).toLong()
    }

    private fun detectKeyType(key: String): PixKeyType = when {
        key.length == 36 && key.contains('-') -> PixKeyType.RandomKey
        key.filter { it.isDigit() }.length == 14 -> PixKeyType.CNPJ
        key.filter { it.isDigit() }.length == 11 && !key.contains('@') -> PixKeyType.CPF
        key.contains('@') -> PixKeyType.Email
        key.startsWith("+") -> PixKeyType.Phone
        else -> PixKeyType.RandomKey
    }
}
