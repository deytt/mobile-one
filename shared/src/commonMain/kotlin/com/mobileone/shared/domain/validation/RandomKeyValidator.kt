package com.mobileone.shared.domain.validation

/**
 * Valida chave aleatória PIX (UUID v4) no padrão do Banco Central (SPEC-003).
 * Formato: 8-4-4-4-12 dígitos hexadecimais separados por hífens, 36 caracteres total.
 */
object RandomKeyValidator {

    private val UUID_REGEX = Regex(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    )

    fun validate(key: String): Boolean {
        if (key.length != 36) return false
        return UUID_REGEX.matches(key)
    }
}
