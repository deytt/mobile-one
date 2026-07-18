package com.mobileone.shared.platform

/**
 * Placeholder expect/actual usado para validar o padrão descrito no ADR-005 antes de
 * qualquer implementação real (BiometricAuthenticator, SecureStorage, etc.) chegar via spec.
 */
expect class Platform() {
    val name: String
}

fun greet(): String = "Hello, mobile-one! ${Platform().name}"
