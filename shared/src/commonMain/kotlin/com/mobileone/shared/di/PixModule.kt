package com.mobileone.shared.di

import com.mobileone.shared.data.repository.FakePixRepository
import com.mobileone.shared.domain.repository.PixRepository
import com.mobileone.shared.domain.usecase.DetectPixKeyTypeUseCase
import com.mobileone.shared.domain.usecase.ExecutePixTransferUseCase
import com.mobileone.shared.domain.usecase.LookupPixRecipientUseCase
import com.mobileone.shared.domain.usecase.ParsePixQRCodeUseCase
import com.mobileone.shared.domain.usecase.ValidatePixKeyUseCase
import com.mobileone.shared.feature.pix.PixLimitsValidator
import org.koin.dsl.module

/**
 * Bindings do fluxo PIX (SPEC-003). Usa fake para a POC; em produção substituir por
 * [PixRepositoryImpl] que chama o DICT via Ktor. [ExecutePixTransferUseCase] já recebe
 * [BiometricAuthenticator] e [PixLimitsValidator] registrados neste módulo.
 */
val pixModule = module {
    single<PixRepository> { FakePixRepository() }
    single { PixLimitsValidator() }

    factory { DetectPixKeyTypeUseCase() }
    factory { ValidatePixKeyUseCase() }
    factory { ParsePixQRCodeUseCase() }
    factory { LookupPixRecipientUseCase(get()) }
    factory { ExecutePixTransferUseCase(get(), get(), get()) }
}
