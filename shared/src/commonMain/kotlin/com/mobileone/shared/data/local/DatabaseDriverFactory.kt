package com.mobileone.shared.data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory expect/actual do driver SQLDelight (ADR-002).
 * O schema de negócio é definido nas specs de dados correspondentes.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
