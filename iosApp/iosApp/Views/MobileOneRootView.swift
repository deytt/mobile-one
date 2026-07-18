import SwiftUI
import shared

private enum AppRoute: Hashable {
    case login
    case biometricWelcome
    case home
    case brandSwitcher
    case pix
}

/// Raiz de navegação do app (SPEC-001 + SPEC-002):
/// - Splash decide entre Login e Boas-vindas com biometria (SPEC-001)
/// - Autenticação converge para Home real com saldo/extrato (SPEC-002)
/// - Home expõe ícone ⚙ que navega para BrandSwitcher (SPEC-004)
struct MobileOneRootView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @EnvironmentObject private var configObserver: AppConfigObserver
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
            .navigationDestination(for: AppRoute.self) { route in
                destination(for: route)
            }
        }
        .onChange(of: authViewModel.uiState.navigateToHome) { _, navigate in
            guard navigate else { return }
            path = NavigationPath([AppRoute.home])
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
    private func destination(for route: AppRoute) -> some View {
        switch route {
        case .login:
            LoginView(viewModel: authViewModel)
                .navigationBarBackButtonHidden(true)
                .toolbar(.hidden, for: .navigationBar)
        case .biometricWelcome:
            BiometricWelcomeView(viewModel: authViewModel, onUsePasswordTap: {
                path = NavigationPath([AppRoute.login])
            })
            .navigationBarBackButtonHidden(true)
            .toolbar(.hidden, for: .navigationBar)
        case .home:
            HomeView(
                onBrandSwitcherTap: {
                    path.append(AppRoute.brandSwitcher)
                },
                onPixTap: {
                    path.append(AppRoute.pix)
                },
                onLogoutTap: {
                    authViewModel.onLogoutTap()
                    path = NavigationPath([AppRoute.login])
                }
            )
            .navigationBarBackButtonHidden(true)
        case .brandSwitcher:
            BrandSwitcherView(
                onBack: { path.removeLast() },
                onApplied: { _ in
                    // Tema já foi aplicado via AppConfigObserver (observado em iosAppApp).
                    // Volta para Home — o Environment já reflete o novo config.
                    path.removeLast()
                }
            )
        case .pix:
            PixFlowView(onClose: { path.removeLast() })
        }
    }

    private func handleSplashFinished() {
        isSplashVisible = false
        let initialRoute: AppRoute = authViewModel.uiState.isBiometricEnabled ? .biometricWelcome : .login
        path = NavigationPath([initialRoute])
    }
}

#Preview {
    MobileOneRootView()
        .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
        .environmentObject(AppConfigObserver())
}
