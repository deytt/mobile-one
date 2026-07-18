package com.mobileone.shared.data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Placeholder expect/actual para validar que o driver SQLDelight (ADR-002) compila e
 * instancia nas duas plataformas. O schema real de negócio entra com a SPEC-002.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
