package com.mobileone.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileone.shared.config.AppStateRepository
import com.mobileone.shared.config.BrandCatalog
import com.mobileone.shared.config.WhiteLabelConfig
import com.mobileone.shared.domain.usecase.SwitchBrandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrandSwitcherUiState(
    val brands: List<WhiteLabelConfig> = BrandCatalog.all(),
    val selectedBrandId: String = BrandCatalog.DEFAULT_BRAND_ID,
    val isApplying: Boolean = false,
    val applied: Boolean = false
)

/**
 * ViewModel da tela Dev Mode de troca de marca (SPEC-004), acessada a partir da Home (SPEC-002).
 * Usa [SwitchBrandUseCase] já existente no shared.
 */
class BrandSwitcherViewModel(
    private val switchBrand: SwitchBrandUseCase,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrandSwitcherUiState())
    val uiState: StateFlow<BrandSwitcherUiState> = _uiState.asStateFlow()

    init {
        val current = appStateRepository.currentConfig.value.brandId
        _uiState.update { it.copy(selectedBrandId = current) }
    }

    fun onSelectBrand(brandId: String) {
        _uiState.update { it.copy(selectedBrandId = brandId, applied = false) }
    }

    fun onApply() {
        viewModelScope.launch {
            _uiState.update { it.copy(isApplying = true) }
            switchBrand(_uiState.value.selectedBrandId)
            _uiState.update { it.copy(isApplying = false, applied = true) }
        }
    }
}
