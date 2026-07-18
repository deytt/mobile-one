package com.mobileone.shared.domain.validation

/**
 * Valida CNPJ segundo o algoritmo dos dígitos verificadores da Receita Federal (SPEC-003).
 * Aceita CNPJ com ou sem formatação (##.###.###/####-##).
 */
object CnpjValidator {

    fun validate(cnpj: String): Boolean {
        val digits = cnpj.filter { it.isDigit() }
        if (digits.length != 14) return false
        if (digits.all { it == digits[0] }) return false
        val d1 = calculateDigit(digits.take(12), weights1)
        val d2 = calculateDigit(digits.take(13), weights2)
        return digits[12].digitToInt() == d1 && digits[13].digitToInt() == d2
    }

    private val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    private val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    private fun calculateDigit(digits: String, weights: IntArray): Int {
        val sum = digits.mapIndexed { i, c -> c.digitToInt() * weights[i] }.sum()
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
}
