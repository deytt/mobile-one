package com.mobileone.shared.data.repository

import com.mobileone.shared.domain.repository.PreferencesRepository
import com.mobileone.shared.security.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação de [PreferencesRepository] usando [SecureStorage] para persistência
 * entre sessões (SPEC-002). O estado em memória é sincronizado no init.
 */
class PreferencesRepositoryImpl(private val secureStorage: SecureStorage) : PreferencesRepository {

    private val _isHidden = MutableStateFlow(false)

    suspend fun initialize() {
        val stored = secureStorage.get(KEY_BALANCE_HIDDEN)
        _isHidden.value = stored == "true"
    }

    override fun observeBalanceHidden(): Flow<Boolean> = _isHidden.asStateFlow()

    override suspend fun toggleBalanceHidden() {
        val newValue = !_isHidden.value
        _isHidden.value = newValue
        secureStorage.put(KEY_BALANCE_HIDDEN, newValue.toString())
    }

    companion object {
        const val KEY_BALANCE_HIDDEN = "prefs.balance_hidden"
    }
}
