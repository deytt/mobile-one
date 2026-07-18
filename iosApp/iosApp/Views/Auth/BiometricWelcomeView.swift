import SwiftUI
import shared

/// Boas-vindas + biometria (SPEC-008 layout / SPEC-001 comportamento).
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
    @Environment(\.whiteLabelConfig) private var config
    @Environment(\.brandTheme) private var brandTheme

    let uiState: AuthUiState
    let onBiometricTap: () -> Void
    let onUsePasswordTap: () -> Void

    private var firstName: String {
        uiState.userName?.split(separator: " ").first.map(String.init) ?? "você"
    }

    private var isPremium: Bool { config.brandId == "banco_premium" }

    var body: some View {
        VStack(spacing: 0) {
            BiometricHeader(config: config, brandTheme: brandTheme)

            VStack(spacing: 0) {
                ZStack {
                    Circle()
                        .fill(brandTheme.primary)
                        .frame(width: 72, height: 72)
                        .shadow(color: .black.opacity(0.1), radius: 3, x: 0, y: 4)

                    Text(brandInitials(uiState.userName ?? "?"))
                        .font(greetingFont)
                        .tracking(-0.44)
                        .foregroundStyle(.white)
                }
                .padding(.bottom, 16)

                Text("Olá, \(firstName)!")
                    .font(greetingFont)
                    .tracking(-0.44)
                    .foregroundStyle(brandTheme.onBackground)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 8)

                Text("Confirme sua identidade para acessar sua conta")
                    .font(brandTheme.font(size: 13))
                    .foregroundStyle(brandTheme.onSurface)
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: 220)
                    .lineSpacing(4.875)
                    .padding(.bottom, 48)

                Button(action: onBiometricTap) {
                    ZStack {
                        Circle()
                            .fill(brandTheme.primary.opacity(0.10))
                            .frame(width: 72, height: 72)

                        if uiState.isLoading {
                            ProgressView()
                                .tint(brandTheme.primary)
                        } else {
                            Image(systemName: "touchid")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 52, height: 52)
                                .foregroundStyle(brandTheme.primary)
                        }
                    }
                    .frame(width: 96, height: 96)
                }
                .disabled(uiState.isLoading)
                .padding(.bottom, 12)

                Text("Toque para usar biometria")
                    .font(brandTheme.font(size: 13, weight: .medium))
                    .foregroundStyle(brandTheme.primary)

                if uiState.errorMessage != nil {
                    Text("Não foi possível confirmar sua biometria. Tente novamente ou use CPF e senha.")
                        .font(brandTheme.font(size: 12))
                        .foregroundStyle(brandTheme.error)
                        .multilineTextAlignment(.center)
                        .padding(.top, 16)
                }

                Spacer()

                Button(action: onUsePasswordTap) {
                    Text("Usar CPF e senha")
                        .font(brandTheme.font(size: 13, weight: .medium))
                        .foregroundStyle(brandTheme.onSurface)
                        .underline()
                }
                .padding(.bottom, 32)
            }
            .padding(.horizontal, 24)
            .padding(.top, 40)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(brandTheme.background)
        }
        .background(brandTheme.background)
        .toolbar(.hidden, for: .navigationBar)
    }

    private var greetingFont: Font {
        if isPremium {
            return brandTheme.font(size: 22, weight: .bold, italic: true)
        }
        return brandTheme.font(size: 22, weight: .bold)
    }
}

private struct BiometricHeader: View {
    let config: WhiteLabelConfig
    let brandTheme: BrandTheme

    private var isPremium: Bool { config.brandId == "banco_premium" }
    private var isFintech: Bool { config.brandId == "fintech_verde" }

    var body: some View {
        HStack(spacing: 6) {
            BrandLogoView(size: 28, config: config)
            Text(config.brandName)
                .font(nameFont)
                .tracking(isPremium ? 0.16 : -0.26)
                .foregroundStyle(.white)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 64)
        .background(brandTheme.primary.ignoresSafeArea(edges: .top))
    }

    private var nameFont: Font {
        if isPremium {
            return brandTheme.font(size: 13, weight: .bold, italic: true)
        }
        if isFintech {
            return brandTheme.font(size: 13, weight: .semibold)
        }
        return brandTheme.font(size: 13, weight: .bold)
    }
}

#Preview("Banco Principal") {
    BiometricWelcomeContent(
        uiState: AuthUiState(userName: "Heitor Bastos"),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}

#Preview("Fintech Verde — carregando") {
    BiometricWelcomeContent(
        uiState: AuthUiState(isLoading: true, userName: "Heitor Bastos"),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.fintechVerde()))
}

#Preview("Banco Premium — erro") {
    BiometricWelcomeContent(
        uiState: AuthUiState(userName: "Heitor Bastos", errorMessage: "Não foi possível confirmar a biometria."),
        onBiometricTap: {},
        onUsePasswordTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPremium())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPremium()))
}
