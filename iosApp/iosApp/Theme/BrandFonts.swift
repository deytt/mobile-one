import SwiftUI

/// Resolução tipográfica white-label (SPEC-005):
/// - Banco Principal → Roboto → `Font.system` (SF Pro no iOS; equivalente sans-serif)
/// - Fintech Verde → Inter (TTF em `Resources/Fonts`, registrados via `UIAppFonts`)
/// - Banco Premium → Georgia (já disponível no sistema iOS)
enum BrandFonts {
    static func font(
        familyName: String,
        size: CGFloat,
        weight: Font.Weight = .regular
    ) -> Font {
        switch familyName {
        case "Inter":
            return Font.custom(interPostScriptName(for: weight), size: size)
        case "Georgia":
            return Font.custom(georgiaPostScriptName(for: weight), size: size)
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
    private static func georgiaPostScriptName(for weight: Font.Weight) -> String {
        switch weight {
        case .bold, .heavy, .black, .semibold:
            return "Georgia-Bold"
        default:
            return "Georgia"
        }
    }
}
