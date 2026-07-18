import SwiftUI
import shared

/// Tema de presentation white-label (SPEC-005): os 8 tokens de cor + tipografia +
/// corner radius, derivados de `WhiteLabelConfig.theme` do shared — sem hex hardcoded.
struct BrandTheme: Equatable {
    let primary: Color
    let secondary: Color
    let background: Color
    let surface: Color
    let onPrimary: Color
    let onBackground: Color
    let onSurface: Color
    let error: Color
    let fontFamily: String
    let cornerRadius: CGFloat

    init(config: WhiteLabelConfig) {
        let tokens = config.theme
        primary = Color(hex: tokens.colorPrimary)
        secondary = Color(hex: tokens.colorSecondary)
        background = Color(hex: tokens.colorBackground)
        surface = Color(hex: tokens.colorSurface)
        onPrimary = Color(hex: tokens.colorOnPrimary)
        onBackground = Color(hex: tokens.colorOnBackground)
        onSurface = Color(hex: tokens.colorOnSurface)
        error = Color(hex: tokens.colorError)
        fontFamily = tokens.fontFamilyName
        cornerRadius = CGFloat(tokens.borderRadiusDp)
    }

    /// Fonte da marca ativa (Roboto → system; Inter → bundle; Georgia → system).
    func font(size: CGFloat, weight: Font.Weight = .regular) -> Font {
        BrandFonts.font(familyName: fontFamily, size: size, weight: weight)
    }
}
