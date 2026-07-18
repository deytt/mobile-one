package com.mobileone.shared.domain.usecase

import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.WhiteLabelConfigRepository

/**
 * Troca a marca (white-label) ativa no app: carrega a configuração pelo `brandId` e, em caso
 * de sucesso, publica a nova configuração no [AppStateRepository] para que a camada de
 * presentation nativa reaja. Ver SPEC-004.
 */
class SwitchBrandUseCase(
    private val configRepository: WhiteLabelConfigRepository,
    private val appStateRepository: AppStateRepository
) {
    suspend operator fun invoke(brandId: String): Result<Unit> =
        configRepository.loadConfig(brandId)
            .onSuccess { appStateRepository.setCurrentConfig(it) }
            .map { }
}
