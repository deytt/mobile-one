import SwiftUI
import shared

/// Contrato de presentation para a SPEC-004: expõe o `WhiteLabelConfig` ativo via
/// `@Environment`, para que qualquer View leia `brandName`, feature flags, etc. sem depender
/// diretamente de `BrandCatalog`/Koin. Cores e corner radius são consumidos através das
/// propriedades computadas abaixo — nunca lendo `theme.colorPrimary` (hex) diretamente na UI.
private struct WhiteLabelConfigKey: EnvironmentKey {
    static let defaultValue: WhiteLabelConfig = BrandCatalog.shared.bancoPrincipal()
}

extension EnvironmentValues {
    var whiteLabelConfig: WhiteLabelConfig {
        get { self[WhiteLabelConfigKey.self] }
        set { self[WhiteLabelConfigKey.self] = newValue }
    }
}

/// Nota (fundação SPEC-004): o valor acima é lido uma única vez de `BrandCatalog`, sem
/// observar `AppStateRepository.currentConfig` (`StateFlow`) em tempo real. A ponte
/// Flow → Combine/AsyncSequence só é necessária quando a tela de Brand Switcher (próximo PR)
/// permitir troca de marca em runtime, e será resolvida nesse momento.
extension WhiteLabelConfig {
    var primaryColor: Color { Color(hex: theme.colorPrimary) }
    var secondaryColor: Color { Color(hex: theme.colorSecondary) }
    var backgroundColor: Color { Color(hex: theme.colorBackground) }
    var onPrimaryColor: Color { Color(hex: theme.colorOnPrimary) }
    var onBackgroundColor: Color { Color(hex: theme.colorOnBackground) }
    var cornerRadius: CGFloat { CGFloat(theme.borderRadiusDp) }
}
