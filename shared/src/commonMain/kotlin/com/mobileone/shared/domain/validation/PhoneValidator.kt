package com.mobileone.shared.domain.validation

/**
 * Valida número de telefone como chave PIX (SPEC-003).
 * Formato aceito: +55 com DDD válido + 9 dígitos (celular) ou 8 dígitos (fixo).
 * Aceita com ou sem espaços/traços/parênteses: +55 (11) 99999-9999 ou +5511999999999.
 */
object PhoneValidator {

    // DDDs válidos no Brasil (ANATEL)
    private val VALID_DDDS = setOf(
        11, 12, 13, 14, 15, 16, 17, 18, 19,
        21, 22, 24,
        27, 28,
        31, 32, 33, 34, 35, 37, 38,
        41, 42, 43, 44, 45, 46,
        47, 48, 49,
        51, 53, 54, 55,
        61, 62, 63, 64, 65, 66, 67, 68, 69,
        71, 73, 74, 75, 77, 79,
        81, 82, 83, 84, 85, 86, 87, 88, 89,
        91, 92, 93, 94, 95, 96, 97, 98, 99
    )

    fun validate(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        // Formato internacional: +55 + DDD (2) + número (8 ou 9) = 12 ou 13 dígitos
        // Se vier com o +55, remover o prefixo 55
        val normalized = when {
            phone.startsWith("+55") -> digits.drop(2)
            digits.startsWith("55") && digits.length >= 12 -> digits.drop(2)
            else -> digits
        }
        if (normalized.length != 10 && normalized.length != 11) return false
        val ddd = normalized.take(2).toIntOrNull() ?: return false
        if (ddd !in VALID_DDDS) return false
        val number = normalized.drop(2)
        // Celular: 9 dígitos começando com 9; fixo: 8 dígitos
        return (number.length == 9 && number.startsWith("9")) || number.length == 8
    }

    private fun String.startsWith(prefix: String): Boolean = this.take(prefix.length) == prefix
}
