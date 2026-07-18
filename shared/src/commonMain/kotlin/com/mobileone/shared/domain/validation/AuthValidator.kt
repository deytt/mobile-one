package com.mobileone.shared.domain.validation

import com.mobileone.shared.domain.error.AuthDomainError
import com.mobileone.shared.domain.error.asFailure

/**
 * Validações de CPF e senha do formulário de login (SPEC-001) — regra de negócio única no
 * shared, nunca replicada na UI nativa.
 */
class AuthValidator {

    fun validateCpf(cpf: String): Result<Unit> {
        val digits = cpf.filter { it.isDigit() }
        if (digits.length != 11 || !isValidCpfChecksum(digits)) {
            return AuthDomainError.Validation("cpf", "CPF inválido").asFailure()
        }
        return Result.success(Unit)
    }

    fun validatePassword(password: String): Result<Unit> {
        val hasDigit = password.any { it.isDigit() }
        if (password.length !in 6..20 || !hasDigit) {
            return AuthDomainError.Validation("password", "Senha fraca").asFailure()
        }
        return Result.success(Unit)
    }

    private fun isValidCpfChecksum(digits: String): Boolean {
        if (digits.toSet().size == 1) return false // sequências repetidas (ex: 000...) são inválidas
        val numbers = digits.map { it - '0' }

        fun verifierDigit(base: List<Int>): Int {
            val weightStart = base.size + 1
            val sum = base.mapIndexed { index, n -> n * (weightStart - index) }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }

        val firstVerifier = verifierDigit(numbers.take(9))
        val secondVerifier = verifierDigit(numbers.take(9) + firstVerifier)
        return numbers[9] == firstVerifier && numbers[10] == secondVerifier
    }
}
