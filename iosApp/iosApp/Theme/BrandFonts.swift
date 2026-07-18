import SwiftUI

/// Resolução tipográfica white-label (SPEC-005 / SPEC-006):
/// - Banco Principal → Roboto → `Font.system` (SF Pro no iOS; equivalente sans-serif)
/// - Fintech Verde → Inter (TTF em `Resources/Fonts`, registrados via `UIAppFonts`)
/// - Banco Premium → Georgia (já disponível no sistema iOS), inclusive Bold Italic na Splash
enum BrandFonts {
    static func font(
        familyName: String,
        size: CGFloat,
        weight: Font.Weight = .regular,
        italic: Bool = false
    ) -> Font {
        switch familyName {
        case "Inter":
            return Font.custom(interPostScriptName(for: weight), size: size)
        case "Georgia":
            return Font.custom(georgiaPostScriptName(for: weight, italic: italic), size: size)
        case "Roboto":
            return Font.system(size: size, weight: weight)
        default:
            return Font.system(size: size, weight: weight)
        }
    }

    /// Nomes PostScript dos TTFs Inter empacotados (`Inter-Regular`, etc.).
    private static func interPostScriptName(for weight: Font.Weight) -> String {
        switch weight {
        case .ultraLight, .thin, .light, .regular:
            return "Inter-Regular"
        case .medium:
            return "Inter-Medium"
        case .semibold:
            return "Inter-SemiBold"
        case .bold, .heavy, .black:
            return "Inter-Bold"
        default:
            return "Inter-Regular"
        }
    }

    /// Georgia do sistema (não precisa de arquivo no bundle).
    private static func georgiaPostScriptName(for weight: Font.Weight, italic: Bool) -> String {
        let isBold = weight == .bold || weight == .heavy || weight == .black || weight == .semibold
        switch (isBold, italic) {
        case (true, true):
            return "Georgia-BoldItalic"
        case (false, true):
            return "Georgia-Italic"
        case (true, false):
            return "Georgia-Bold"
        case (false, false):
            return "Georgia"
        }
    }
}
