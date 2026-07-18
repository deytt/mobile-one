package com.mobileone.shared.domain.usecase

import com.mobileone.shared.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Alterna e observa a visibilidade do saldo (SPEC-002).
 * O estado é persistido no [com.mobileone.shared.security.SecureStorage] via [PreferencesRepository].
 */
class ToggleBalanceVisibilityUseCase(private val preferencesRepository: PreferencesRepository) {
    suspend operator fun invoke() = preferencesRepository.toggleBalanceHidden()
    fun observe(): Flow<Boolean> = preferencesRepository.observeBalanceHidden()
}
