import SwiftUI
import shared

/// Boas-vindas + biometria (SPEC-001, node `29:20689`): oferece login rápido por biometria
/// após uma sessão previamente autenticada com biometria habilitada.
struct BiometricWelcomeView: View {
    @ObservedObject var viewModel: AuthViewModel
    let onUsePasswordTap: () -> Void

    var body: some View {
        BiometricWelcomeContent(
            uiState: viewModel.uiState,
            onBiometricTap: viewModel.onBiometricTap,
            onUsePasswordTap: onUsePasswordTap
        )
        .task { viewModel.onBiometricTap() }
    }
}

struct BiometricWelcomeContent: View {
    @Environment(\.whiteLabelConfig) private var theme
    let uiState: AuthUiState
    let onBiometricTap: () -> Void
    let onUsePasswordTap: () -> Void

    private var firstName: String {
        uiState.userName?.split(separator: " ").first.map(String.init) ?? "você"
    }

    var body: some View {
        VStack {
            Spacer()
            VStack(spacing: 0) {
                ZStack {
                    Circle().fill(theme.primaryColor).frame(width: 64, height: 64)
                    Text(brandInitials(uiState.userName ?? "?"))
                        .font(.system(size: 22, weight: .bold))
                        .foregroundStyle(theme.onPrimaryColor)
                }

                Text("Olá, \(firstName)!")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(theme.onBackgroundColor)
                    .multilineTextAlignment(.center)
                    .padding(.top, 16)
                    .padding(.bottom, 8)

                Text("Confirme sua identidade para acessar sua conta")
                    .font(.system(size: 13))
                    .foregroundStyle(theme.onSurfaceColor)
                    .multilineTextAlignment(.center)
                    .frame(width: 220)
                    .padding(.bottom, 48)

                Button(action: onBiometricTap) {
                    ZStack {
                        Circle().fill(theme.primaryColor.opacity(0.12)).frame(width: 96, height: 96)
                        if uiState.isLoading {
                            ProgressView().tint(theme.primaryColor)
                        } else {
                            Image(systemName: "touchid")
                                .font(.system(size: 40))
                                .foregroundStyle(theme.primaryColor)
                        }
                    }
                }
                .disabled(uiState.isLoading)

                Text("Toque para usar biometria")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(theme.primaryColor)
                    .padding(.top, 16)

                if uiState.errorMessage != nil {
                    Text("Não foi possível confirmar sua biometria. Tente novamente ou use CPF e senha.")
                        .font(.system(size: 12))
                        .foregroundStyle(theme.errorColor)
                        .multilineTextAlignment(.center)
                        .padding(.top, 16)
                }
            }
            Spacer()
            Button("Usar CPF e senha", action: onUsePasswordTap)
                .font(.system(size: 13))
                .foregroundStyle(theme.onSurfaceColor)
                .underline()
                .padding(.bottom, 32)
        }
        .padding(.horizontal, 32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(theme.backgroundColor)
        .ignoresSafeArea()
    }
}

#Preview {
    BiometricWelcomeContent(
        uiState: AuthUiState(userName: "Heitor Bastos"),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}

#Preview("Fintech Verde — carregando") {
    BiometricWelcomeContent(
        uiState: AuthUiState(isLoading: true, userName: "Heitor Bastos"),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
}

#Preview("Banco Premium — erro") {
    BiometricWelcomeContent(
        uiState: AuthUiState(userName: "Heitor Bastos", errorMessage: "Não foi possível confirmar a biometria."),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPremium())
}
