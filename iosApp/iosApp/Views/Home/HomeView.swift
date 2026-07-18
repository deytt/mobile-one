import SwiftUI
import shared

/**
 * Alias de compatibilidade (SPEC-002 → SPEC-010): a Home de Conta vive em [HomeContaView].
 */
struct HomeView: View {
    var onNavigateToCartoes: () -> Void = {}
    let onBrandSwitcherTap: () -> Void
    var onPixTap: () -> Void = {}
    let onLogoutTap: () -> Void

    var body: some View {
        HomeContaView(
            onNavigateToCartoes: onNavigateToCartoes,
            onBrandSwitcherTap: onBrandSwitcherTap,
            onPixTap: onPixTap,
            onLogoutTap: onLogoutTap
        )
    }
}
