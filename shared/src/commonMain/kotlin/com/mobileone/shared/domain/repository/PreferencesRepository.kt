package com.mobileone.shared.domain.repository

import kotlinx.coroutines.flow.Flow

/** Preferências de UI persistidas entre sessões (SPEC-002). Implementação usa [com.mobileone.shared.security.SecureStorage]. */
interface PreferencesRepository {
    /** Fluxo do estado de visibilidade do saldo — `true` = oculto. */
    fun observeBalanceHidden(): Flow<Boolean>
    /** Alterna o estado de visibilidade e persiste. */
    suspend fun toggleBalanceHidden()
}
