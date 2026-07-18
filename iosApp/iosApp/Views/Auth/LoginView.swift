import SwiftUI
import shared

/// Login por CPF + senha (SPEC-001, node `29:20015`). Segue a separação View/Content de
/// `.cursor/rules/06-ios-conventions.mdc`.
struct LoginView: View {
    @ObservedObject var viewModel: AuthViewModel
    var onRegisterTap: () -> Void = {}

    var body: some View {
        LoginContent(
            uiState: viewModel.uiState,
            onLoginTap: viewModel.onLoginTap,
            onBiometricTap: viewModel.onBiometricTap,
            onDismissError: viewModel.onDismissError,
            onRegisterTap: onRegisterTap
        )
    }
}

struct LoginContent: View {
    @Environment(\.whiteLabelConfig) private var theme
    let uiState: AuthUiState
    let onLoginTap: (String, String) -> Void
    let onBiometricTap: () -> Void
    var onDismissError: () -> Void = {}
    var onRegisterTap: () -> Void = {}

    @State private var cpf = ""
    @State private var password = ""
    @State private var passwordVisible = false

    private var canSubmit: Bool { !cpf.isEmpty && !password.isEmpty && !uiState.isLoading && !uiState.isAccountLocked }

    var body: some View {
        VStack(spacing: 0) {
            LoginTopBar(theme: theme)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Text("Bem-vindo de volta")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundStyle(theme.onBackgroundColor)

                    Text("Entre com seu CPF e senha")
                        .font(.system(size: 13))
                        .foregroundStyle(theme.onSurfaceColor)
                        .padding(.top, 4)
                        .padding(.bottom, 24)

                    FieldLabel(text: "CPF", theme: theme)
                    HStack {
                        TextField("000.000.000-00", text: $cpf)
                            .keyboardType(.numberPad)
                            .onChange(of: cpf) { _, newValue in cpf = Self.maskCpf(newValue) }
                        Image(systemName: "square.grid.3x3.fill")
                            .foregroundStyle(theme.onSurfaceColor)
                    }
                    .padding(.horizontal, 15)
                    .frame(height: 48)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(theme.onSurfaceColor.opacity(0.3), lineWidth: 1)
                    )
                    .padding(.bottom, 20)

                    FieldLabel(text: "Senha", theme: theme)
                    HStack {
                        Group {
                            if passwordVisible {
                                TextField("••••••", text: $password)
                            } else {
                                SecureField("••••••", text: $password)
                            }
                        }
                        Button(action: { passwordVisible.toggle() }) {
                            Image(systemName: passwordVisible ? "eye.slash" : "eye")
                                .foregroundStyle(theme.onSurfaceColor)
                        }
                    }
                    .padding(.horizontal, 15)
                    .frame(height: 48)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(theme.onSurfaceColor.opacity(0.3), lineWidth: 1)
                    )

                    HStack {
                        Spacer()
                        Button("Esqueci minha senha") {}
                            .font(.system(size: 12, weight: .medium))
                            .foregroundStyle(theme.primaryColor)
                    }
                    .padding(.top, 8)
                    .padding(.bottom, 24)

                    if let errorMessage = uiState.errorMessage {
                        Text(errorMessage)
                            .font(.system(size: 13))
                            .foregroundStyle(theme.errorColor)
                            .padding(.bottom, 16)
                    }

                    Button(action: {
                        onDismissError()
                        onLoginTap(cpf, password)
                    }) {
                        if uiState.isLoading {
                            ProgressView().tint(theme.onPrimaryColor)
                        } else {
                            Text("Entrar")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .foregroundStyle(theme.onPrimaryColor)
                    .background(theme.primaryColor)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .disabled(!canSubmit)
                    .opacity(canSubmit ? 1 : 0.3)
                    .padding(.bottom, 16)

                    if uiState.isBiometricAvailable && uiState.isBiometricEnabled {
                        Button(action: onBiometricTap) {
                            HStack(spacing: 8) {
                                Image(systemName: "touchid")
                                Text("Entrar com biometria").font(.system(size: 15, weight: .semibold))
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .foregroundStyle(theme.primaryColor)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(theme.primaryColor, lineWidth: 1)
                        )
                        .disabled(uiState.isLoading)
                        .padding(.bottom, 24)
                    }

                    HStack(spacing: 12) {
                        Rectangle().fill(theme.onSurfaceColor.opacity(0.25)).frame(height: 1)
                        Text("ou").font(.system(size: 12)).foregroundStyle(theme.onSurfaceColor)
                        Rectangle().fill(theme.onSurfaceColor.opacity(0.25)).frame(height: 1)
                    }
                    .padding(.bottom, 20)

                    HStack {
                        Spacer()
                        Text("Ainda não tem conta? ")
                            .font(.system(size: 13))
                            .foregroundStyle(theme.onSurfaceColor)
                        Text("Abra a sua grátis")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundStyle(theme.primaryColor)
                            .onTapGesture(perform: onRegisterTap)
                        Spacer()
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 32)
                .padding(.bottom, 24)
            }
        }
        .background(theme.backgroundColor)
        .ignoresSafeArea(edges: .bottom)
    }

    private static func maskCpf(_ raw: String) -> String {
        let digits = raw.filter(\.isNumber).prefix(11)
        var result = ""
        for (index, character) in digits.enumerated() {
            if index == 3 || index == 6 { result.append(".") }
            if index == 9 { result.append("-") }
            result.append(character)
        }
        return result
    }
}

private struct LoginTopBar: View {
    let theme: WhiteLabelConfig

    var body: some View {
        HStack(spacing: 6) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.white.opacity(0.18))
                    .frame(width: 28, height: 28)
                Text(brandInitials(theme.brandName))
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(.white)
            }
            Text(theme.brandName)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(.white)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 64)
        .background(theme.primaryColor)
    }
}

private struct FieldLabel: View {
    let text: String
    let theme: WhiteLabelConfig

    var body: some View {
        Text(text)
            .font(.system(size: 12, weight: .semibold))
            .foregroundStyle(theme.onBackgroundColor)
            .padding(.bottom, 4)
    }
}

#Preview {
    LoginContent(
        uiState: AuthUiState(isBiometricAvailable: true, isBiometricEnabled: true),
        onLoginTap: { _, _ in },
        onBiometricTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}

#Preview("Erro de credenciais") {
    LoginContent(
        uiState: AuthUiState(failedAttempts: 2, errorMessage: "CPF ou senha incorretos. Tentativa 2 de 5."),
        onLoginTap: { _, _ in },
        onBiometricTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
}
