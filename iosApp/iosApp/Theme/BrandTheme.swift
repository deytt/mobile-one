import SwiftUI
import shared

/// Tema de presentation white-label (SPEC-005 / SPEC-006): os 8 tokens de cor + tipografia +
/// corner radius, derivados de `WhiteLabelConfig.theme` do shared — sem hex hardcoded.
struct BrandTheme: Equatable {
    let brandId: String
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
        brandId = config.brandId
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
    func font(size: CGFloat, weight: Font.Weight = .regular, italic: Bool = false) -> Font {
        BrandFonts.font(familyName: fontFamily, size: size, weight: weight, italic: italic)
    }

    /// Letter-spacing do nome da marca na Splash (SPEC-006).
    var splashNameTracking: CGFloat {
        brandId == "banco_premium" ? 0.88 : -0.22
    }

    /// Gradiente radial da Splash: centro em (0.5, 0.4), stops por marca (SPEC-006).
    func splashGradient(endRadius: CGFloat) -> RadialGradient {
        RadialGradient(
            gradient: Gradient(stops: splashGradientStops),
            center: UnitPoint(x: 0.5, y: 0.4),
            startRadius: 0,
            endRadius: endRadius
        )
    }

    private var splashGradientStops: [Gradient.Stop] {
        switch brandId {
        case "fintech_verde":
            return [
                .init(color: Color(red: 0, green: 168 / 255, blue: 107 / 255), location: 0.2),
                .init(color: Color(red: 0, green: 138 / 255, blue: 88 / 255), location: 0.6),
                .init(color: Color(red: 0, green: 107 / 255, blue: 68 / 255), location: 1.0)
            ]
        case "banco_premium":
            return [
                .init(color: Color(red: 123 / 255, green: 45 / 255, blue: 0), location: 0.2),
                .init(color: Color(red: 74 / 255, green: 26 / 255, blue: 0), location: 1.0)
            ]
        default:
            return [
                .init(color: Color(red: 0, green: 59 / 255, blue: 111 / 255), location: 0.2),
                .init(color: Color(red: 0, green: 45 / 255, blue: 87 / 255), location: 0.6),
                .init(color: Color(red: 0, green: 31 / 255, blue: 63 / 255), location: 1.0)
            ]
        }
    }
}
