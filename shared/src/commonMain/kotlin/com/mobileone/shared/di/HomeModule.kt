package com.mobileone.shared.di

import com.mobileone.shared.data.repository.FakeAccountRepository
import com.mobileone.shared.data.repository.FakeTransactionRepository
import com.mobileone.shared.data.repository.PreferencesRepositoryImpl
import com.mobileone.shared.domain.repository.AccountRepository
import com.mobileone.shared.domain.repository.PreferencesRepository
import com.mobileone.shared.domain.repository.TransactionRepository
import com.mobileone.shared.domain.usecase.GetTransactionHistoryUseCase
import com.mobileone.shared.domain.usecase.ObserveAccountUseCase
import com.mobileone.shared.domain.usecase.RefreshAccountDataUseCase
import com.mobileone.shared.domain.usecase.ToggleBalanceVisibilityUseCase
import org.koin.dsl.module

/**
 * Bindings de Home/Extrato (SPEC-002). Usa fakes para a POC; em produção substituir por
 * implementações reais (SQLDelight + Ktor). `PreferencesRepositoryImpl` usa `SecureStorage`
 * já registrado em [securityPlatformModule].
 */
val homeModule = module {
    single<AccountRepository> { FakeAccountRepository() }
    single<TransactionRepository> { FakeTransactionRepository() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    factory { ObserveAccountUseCase(get()) }
    factory { GetTransactionHistoryUseCase(get()) }
    factory { RefreshAccountDataUseCase(get(), get()) }
    factory { ToggleBalanceVisibilityUseCase(get()) }
}
