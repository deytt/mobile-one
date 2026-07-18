package com.mobileone.shared.domain.validation

/**
 * Valida CPF segundo o algoritmo dos dígitos verificadores do Banco Central (SPEC-003).
 * Aceita CPF com ou sem formatação (###.###.###-##).
 */
object CpfValidator {

    fun validate(cpf: String): Boolean {
        val digits = cpf.filter { it.isDigit() }
        if (digits.length != 11) return false
        if (digits.all { it == digits[0] }) return false
        val d1 = calculateDigit(digits.take(9), 10)
        val d2 = calculateDigit(digits.take(10), 11)
        return digits[9].digitToInt() == d1 && digits[10].digitToInt() == d2
    }

    private fun calculateDigit(digits: String, weight: Int): Int {
        val sum = digits.mapIndexed { i, c -> c.digitToInt() * (weight - i) }.sum()
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
}
