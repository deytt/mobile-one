import Foundation
import shared

/// Estado de UI do fluxo de login/biometria (SPEC-001) — equivalente Swift do `AuthUiState`
/// consumido pelo `AuthViewModel` do Android. Não usamos o `AuthUiState` do shared diretamente
/// porque tipos genéricos/`Result` do Kotlin degradam para `Any?` ao cruzar a ponte Objective-C
/// (ver `IOSDependencyProvider.kt`); o próprio estado observável, porém, é modelado aqui em
/// Swift puro para manter o `@Published` idiomático.
struct AuthUiState {
    var isLoading = false
    var isBiometricAvailable = false
    var isBiometricEnabled = false
    var failedAttempts = 0
    var isAccountLocked = false
    var lockRemainingSeconds = 0
    var userName: String?
    var errorMessage: String?
    var navigateToHome = false
    var showBiometricSetupPrompt = false
}

@MainActor
final class AuthViewModel: ObservableObject {
    @Published private(set) var uiState = AuthUiState()

    init() {
        Task { try await refreshBiometricState() }
    }

    func refreshBiometricState() async throws {
        let available = try await IOSDependencyProvider.shared.isBiometricAvailable()
        let enabled = try await IOSDependencyProvider.shared.isBiometricEnabled()
        uiState.isBiometricAvailable = available.boolValue
        uiState.isBiometricEnabled = enabled.boolValue
    }

    func onLoginTap(cpf: String, password: String) {
        uiState.isLoading = true
        uiState.errorMessage = nil
        Task {
            let outcome = try await IOSDependencyProvider.shared.loginWithCredentials(cpf: cpf, password: password)
            await handleLoginOutcome(outcome, offerBiometricSetup: true)
        }
    }

    func onBiometricTap() {
        uiState.isLoading = true
        uiState.errorMessage = nil
        Task {
            let outcome = try await IOSDependencyProvider.shared.loginWithBiometric()
            await handleLoginOutcome(outcome, offerBiometricSetup: false)
        }
    }

    func onSetupBiometricConfirm() {
        uiState.isLoading = true
        Task {
            let outcome = try await IOSDependencyProvider.shared.setupBiometricLogin()
            if outcome.isSuccess {
                try await IOSDependencyProvider.shared.setBiometricEnabled(enabled: true)
                uiState.isLoading = false
                uiState.isBiometricEnabled = true
                uiState.showBiometricSetupPrompt = false
                uiState.navigateToHome = true
            } else {
                uiState.isLoading = false
                uiState.errorMessage = message(for: outcome.error)
            }
        }
    }

    func onSkipBiometricSetup() {
        uiState.showBiometricSetupPrompt = false
        uiState.navigateToHome = true
    }

    func onConsumeNavigation() {
        uiState.navigateToHome = false
    }

    func onDismissError() {
        uiState.errorMessage = nil
    }

    func onLogoutTap() {
        Task {
            try await IOSDependencyProvider.shared.logout()
            let enabled = try await IOSDependencyProvider.shared.isBiometricEnabled()
            uiState = AuthUiState(isBiometricAvailable: uiState.isBiometricAvailable, isBiometricEnabled: enabled.boolValue)
        }
    }

    private func handleLoginOutcome(_ outcome: AuthTokenOutcome, offerBiometricSetup: Bool) async {
        if outcome.isSuccess {
            let userName = try? await IOSDependencyProvider.shared.currentUserName()
            let shouldOfferSetup = offerBiometricSetup && uiState.isBiometricAvailable && !uiState.isBiometricEnabled
            uiState.isLoading = false
            uiState.failedAttempts = 0
            uiState.isAccountLocked = false
            uiState.lockRemainingSeconds = 0
            uiState.userName = userName ?? nil
            if shouldOfferSetup {
                uiState.showBiometricSetupPrompt = true
            } else {
                uiState.navigateToHome = true
            }
        } else {
            applyError(outcome.error)
        }
    }

    private func applyError(_ error: AuthDomainError?) {
        uiState.isLoading = false
        uiState.errorMessage = message(for: error)
        if let invalidCredentials = error as? AuthDomainError.InvalidCredentials {
            uiState.failedAttempts = Int(invalidCredentials.failedAttempts)
            uiState.isAccountLocked = false
        } else if let accountLocked = error as? AuthDomainError.AccountLocked {
            uiState.isAccountLocked = true
            uiState.lockRemainingSeconds = Int(accountLocked.remainingSeconds)
        }
    }

    private func message(for error: AuthDomainError?) -> String? {
        switch error {
        case is AuthDomainError.InvalidCredentials:
            return "CPF ou senha incorretos. Tentativa \(uiState.failedAttempts) de 5."
        case let locked as AuthDomainError.AccountLocked:
            return "Conta bloqueada por \(locked.remainingSeconds)s após muitas tentativas."
        case is AuthDomainError.BiometricNotAvailable, is AuthDomainError.BiometricBlocked, is AuthDomainError.BiometricFailed:
            return "Não foi possível confirmar a biometria."
        case is AuthDomainError.NetworkError:
            return "Falha de conexão. Tente novamente."
        case let unknown as AuthDomainError.Unknown:
            return unknown.message
        case let validation as AuthDomainError.Validation:
            return validation.reason
        case let compromised as AuthDomainError.CompromisedDevice:
            return compromised.reason
        default:
            return nil
        }
    }
}
