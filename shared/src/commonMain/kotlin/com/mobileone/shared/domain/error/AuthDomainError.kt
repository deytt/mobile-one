package com.mobileone.shared.domain.error

/**
 * Erros de domínio do fluxo de autenticação (SPEC-001, ADR-005) — nunca `Exception` genérica
 * exposta ao domínio, ver `.cursor/rules/04-kmp-conventions.mdc`.
 */
sealed class AuthDomainError {
    data class InvalidCredentials(val attemptsRemaining: Int, val failedAttempts: Int) : AuthDomainError()
    data class AccountLocked(val remainingSeconds: Int) : AuthDomainError()
    object NetworkError : AuthDomainError()
    object BiometricNotAvailable : AuthDomainError()
    object BiometricBlocked : AuthDomainError()
    object BiometricFailed : AuthDomainError()
    data class CompromisedDevice(val reason: String) : AuthDomainError()
    data class Validation(val field: String, val reason: String) : AuthDomainError()
    data class Unknown(val message: String) : AuthDomainError()
}

/**
 * Envelope para transportar um [AuthDomainError] via `Result.failure`, seguindo o mesmo padrão
 * de `UnknownBrandException` em `WhiteLabelConfigRepository.kt`.
 */
class AuthException(val error: AuthDomainError) : Exception(error.toString())

/** Extrai o [AuthDomainError] de um `Result.failure`, com fallback para erros inesperados. */
fun Throwable.toAuthDomainError(): AuthDomainError =
    (this as? AuthException)?.error ?: AuthDomainError.Unknown(message ?: "Erro desconhecido")

/** Atalho para construir um `Result.failure` tipado a partir de um [AuthDomainError]. */
fun AuthDomainError.asFailure(): Result<Nothing> = Result.failure(AuthException(this))
