package com.mobileone.shared.domain.error

/** Erros tipados do domínio de Home/Extrato (SPEC-002). */
sealed class HomeDomainError {
    data object NetworkError : HomeDomainError()
    data object Unauthorized : HomeDomainError()
    data class Unknown(val message: String) : HomeDomainError()
}
