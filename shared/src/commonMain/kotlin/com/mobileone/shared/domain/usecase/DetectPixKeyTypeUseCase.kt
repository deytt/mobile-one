package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.entity.PixKeyType

/**
 * Detecta o tipo de chave PIX a partir do input do usuário (SPEC-003).
 * Operação pura, sem IO — executada a cada caractere digitado.
 */
class DetectPixKeyTypeUseCase {

    operator fun invoke(input: String): PixKeyType? {
        val clean = input.trim()
        return when {
            clean.isEmpty() -> null
            // UUID v4 (36 chars com hífens)
            clean.length == 36 && clean.contains('-') -> PixKeyType.RandomKey
            // CNPJ: 14 dígitos ou formatado
            clean.filter { it.isDigit() }.length == 14 &&
                (clean.all { it.isDigit() } || cnpjFormatted(clean)) -> PixKeyType.CNPJ
            // CPF: 11 dígitos ou formatado
            clean.filter { it.isDigit() }.length == 11 &&
                (clean.all { it.isDigit() } || cpfFormatted(clean)) &&
                !clean.contains('@') -> PixKeyType.CPF
            // Telefone: começa com + ou tem DDD no início
            clean.startsWith("+") || (clean.filter { it.isDigit() }.length in 10..13
                && !clean.contains('@')) -> PixKeyType.Phone
            // E-mail: contém @
            clean.contains('@') -> PixKeyType.Email
            else -> null
        }
    }

    private fun cpfFormatted(s: String) = s.matches(Regex("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"))
    private fun cnpjFormatted(s: String) = s.matches(Regex("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"))
}
