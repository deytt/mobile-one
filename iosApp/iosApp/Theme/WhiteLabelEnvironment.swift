import SwiftUI
import shared

/// Contrato de presentation (SPEC-004 / SPEC-005):
/// - `whiteLabelConfig` — marca ativa (`brandName`, feature flags, etc.)
/// - `brandTheme` — tokens visuais (8 cores + fonte + corner radius)
///
/// A UI nunca lê hex/`borderRadiusDp` diretamente; consome via `BrandTheme` ou pelas
/// propriedades computadas de `WhiteLabelConfig` abaixo.
private struct WhiteLabelConfigKey: EnvironmentKey {
    static let defaultValue: WhiteLabelConfig = BrandCatalog.shared.bancoPrincipal()
}

private struct BrandThemeKey: EnvironmentKey {
    static let defaultValue: BrandTheme = BrandTheme(config: BrandCatalog.shared.bancoPrincipal())
}

extension EnvironmentValues {
    var whiteLabelConfig: WhiteLabelConfig {
        get { self[WhiteLabelConfigKey.self] }
        set { self[WhiteLabelConfigKey.self] = newValue }
    }

    var brandTheme: BrandTheme {
        get { self[BrandThemeKey.self] }
        set { self[BrandThemeKey.self] = newValue }
    }
}

extension WhiteLabelConfig {
    var brandTheme: BrandTheme { BrandTheme(config: self) }

    var primaryColor: Color { brandTheme.primary }
    var secondaryColor: Color { brandTheme.secondary }
    var backgroundColor: Color { brandTheme.background }
    var surfaceColor: Color { brandTheme.surface }
    var onPrimaryColor: Color { brandTheme.onPrimary }
    var onBackgroundColor: Color { brandTheme.onBackground }
    var onSurfaceColor: Color { brandTheme.onSurface }
    var errorColor: Color { brandTheme.error }
    var cornerRadius: CGFloat { brandTheme.cornerRadius }
}
