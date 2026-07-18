package com.mobileone.shared.domain.validation

/**
 * Valida endereço de e-mail como chave PIX (SPEC-003).
 * Implementa subset da RFC 5322 suficiente para o contexto regulatório do BCB.
 */
object EmailValidator {

    // Regex simplificado RFC 5322: local@domain.tld (sem aspas/comentários)
    private val EMAIL_REGEX = Regex(
        "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+" +
        "@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
        "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*" +
        "\\.[a-zA-Z]{2,}$"
    )

    fun validate(email: String): Boolean {
        if (email.length > 77) return false  // limite do DICT
        return EMAIL_REGEX.matches(email)
    }
}
