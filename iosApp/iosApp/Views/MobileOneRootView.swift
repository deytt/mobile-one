import SwiftUI
import shared

private enum AuthRoute: Hashable {
    case login
    case biometricWelcome
    case home
}

/// Raiz de navegação do fluxo de autenticação (SPEC-001): Splash decide entre Login e
/// Boas-vindas com biometria; ambos convergem para Home ao autenticar. Usa `NavigationStack`
/// com um único item de caminho por vez (sem histórico de "voltar"), já que cada tela é uma
/// transição de estado completa — não uma hierarquia de navegação — seguindo o mermaid do plano.
struct MobileOneRootView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @State private var path = NavigationPath()
    @State private var isSplashVisible = true

    var body: some View {
        NavigationStack(path: $path) {
            Group {
                if isSplashVisible {
                    SplashView(onFinished: handleSplashFinished)
                } else {
                    Color.clear
                }
            }
            .navigationDestination(for: AuthRoute.self) { route in
                destination(for: route)
                    .navigationBarBackButtonHidden(true)
                    .toolbar(.hidden, for: .navigationBar)
            }
        }
        .onChange(of: authViewModel.uiState.navigateToHome) { _, navigate in
            guard navigate else { return }
            path = NavigationPath([AuthRoute.home])
            authViewModel.onConsumeNavigation()
        }
        .alert(
            "Ativar login por biometria?",
            isPresented: Binding(
                get: { authViewModel.uiState.showBiometricSetupPrompt },
                set: { _ in }
            )
        ) {
            Button("Ativar", action: authViewModel.onSetupBiometricConfirm)
            Button("Agora não", role: .cancel, action: authViewModel.onSkipBiometricSetup)
        } message: {
            Text("Use sua digital ou Face ID para entrar mais rápido nas próximas vezes.")
        }
    }

    @ViewBuilder
    private func destination(for route: AuthRoute) -> some View {
        switch route {
        case .login:
            LoginView(viewModel: authViewModel)
        case .biometricWelcome:
            BiometricWelcomeView(viewModel: authViewModel, onUsePasswordTap: {
                path = NavigationPath([AuthRoute.login])
            })
        case .home:
            HomePlaceholderView(onLogoutTap: {
                authViewModel.onLogoutTap()
                path = NavigationPath([AuthRoute.login])
            })
        }
    }

    private func handleSplashFinished() {
        isSplashVisible = false
        let initialRoute: AuthRoute = authViewModel.uiState.isBiometricEnabled ? .biometricWelcome : .login
        path = NavigationPath([initialRoute])
    }
}

#Preview {
    MobileOneRootView()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
