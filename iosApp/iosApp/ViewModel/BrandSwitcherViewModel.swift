import Foundation
import shared

struct BrandSwitcherUiState {
    var brands: [WhiteLabelConfig] = []
    var selectedBrandId: String = BrandCatalog.shared.DEFAULT_BRAND_ID
    var isApplying = false
    var applied = false
}

@MainActor
final class BrandSwitcherViewModel: ObservableObject {
    @Published private(set) var uiState = BrandSwitcherUiState()

    init() {
        uiState.brands = IOSDependencyProvider.shared.allBrands()
        uiState.selectedBrandId = IOSDependencyProvider.shared.currentBrandId()
    }

    func onSelectBrand(_ brandId: String) {
        uiState.selectedBrandId = brandId
        uiState.applied = false
    }

    func onApply() {
        uiState.isApplying = true
        Task {
            try? await IOSDependencyProvider.shared.switchBrand(brandId: uiState.selectedBrandId)
            uiState.isApplying = false
            uiState.applied = true
        }
    }
}
