package com.mobileone.shared.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Mantém a configuração white-label atualmente ativa no app, observável pela camada de
 * presentation nativa (`LocalWhiteLabelConfig` no Compose, `whiteLabelConfig` Environment no
 * SwiftUI). Ver SPEC-004.
 */
interface AppStateRepository {
    val currentConfig: StateFlow<WhiteLabelConfig>
    suspend fun setCurrentConfig(config: WhiteLabelConfig)
}

class AppStateRepositoryImpl(
    initialConfig: WhiteLabelConfig = BrandCatalog.bancoPrincipal()
) : AppStateRepository {

    private val _currentConfig = MutableStateFlow(initialConfig)
    override val currentConfig: StateFlow<WhiteLabelConfig> = _currentConfig.asStateFlow()

    override suspend fun setCurrentConfig(config: WhiteLabelConfig) {
        _currentConfig.value = config
    }
}
