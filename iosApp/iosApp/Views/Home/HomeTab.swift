import SwiftUI

/// Abas do Bottom Tab Switcher (SPEC-009 / SPEC-010).
enum HomeTab: String, CaseIterable, Identifiable {
    case cartoes
    case conta

    var id: String { rawValue }

    var title: String {
        switch self {
        case .cartoes: return "Cartões"
        case .conta: return "Conta"
        }
    }
}
