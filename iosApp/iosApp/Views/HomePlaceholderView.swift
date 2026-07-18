import SwiftUI
import shared

/// Destino final do fluxo de login (SPEC-001). A Home real chega na SPEC-002 — aqui apenas
/// confirmamos que a sessão foi estabelecida com sucesso.
struct HomePlaceholderView: View {
    let onLogoutTap: () -> Void
    @Environment(\.whiteLabelConfig) private var theme

    var body: some View {
        VStack(spacing: 8) {
            Text("Bem-vindo, \(theme.brandName)!")
                .font(.title2).bold()
            Text("Login realizado com sucesso. A Home completa chega na SPEC-002.")
                .font(.subheadline)
                .foregroundStyle(theme.onSurfaceColor)
                .multilineTextAlignment(.center)
            Button("Sair", action: onLogoutTap)
                .padding(.top, 24)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(theme.backgroundColor)
    }
}

#Preview {
    HomePlaceholderView(onLogoutTap: {})
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
