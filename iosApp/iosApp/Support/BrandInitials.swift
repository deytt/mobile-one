import Foundation

/// Deriva as iniciais exibidas nos badges de marca/avatar (ex: "Banco Principal" → "BP",
/// "Heitor Bastos" → "HB") a partir das duas primeiras palavras do nome — mesmo algoritmo
/// usado no Android (`ui/component/BrandInitials.kt`) e nos mockups do Figma.
func brandInitials(_ name: String) -> String {
    name
        .trimmingCharacters(in: .whitespacesAndNewlines)
        .split(separator: " ")
        .prefix(2)
        .compactMap { $0.first.map { String($0).uppercased() } }
        .joined()
}
