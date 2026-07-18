import SwiftUI
import shared

/// Login por CPF + senha (SPEC-007 layout / SPEC-001 comportamento).
/// Separação View/Content conforme `.cursor/rules/06-ios-conventions.mdc`.
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
    @Environment(\.whiteLabelConfig) private var config
    @Environment(\.brandTheme) private var brandTheme

    let uiState: AuthUiState
    let onLoginTap: (String, String) -> Void
    let onBiometricTap: () -> Void
    var onDismissError: () -> Void = {}
    var onRegisterTap: () -> Void = {}

    // Pré-preenchido com credenciais de demo (POC) para agilizar o desenvolvimento.
    @State private var cpf = Self.maskCpf(FakeAuthRepository.companion.DEMO_CPF)
    @State private var password = FakeAuthRepository.companion.DEMO_PASSWORD
    @State private var passwordVisible = false

    private var canSubmit: Bool {
        !cpf.isEmpty && !password.isEmpty && !uiState.isLoading && !uiState.isAccountLocked
    }

    private var isPremium: Bool { config.brandId == "banco_premium" }
    private var dividerColor: Color { Color(hex: "#6B7280").opacity(0.25) }

    var body: some View {
        VStack(spacing: 0) {
            LoginHeader(config: config, brandTheme: brandTheme)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Text("Bem-vindo de volta")
                        .font(titleFont)
                        .tracking(-0.48)
                        .foregroundStyle(brandTheme.onBackground)
                        .padding(.top, 32)

                    Text("Entre com seu CPF e senha")
                        .font(brandTheme.font(size: 13))
                        .foregroundStyle(brandTheme.onSurface)
                        .padding(.top, 4)
                        .padding(.bottom, 24)

                    FieldLabel(text: "CPF", brandTheme: brandTheme)
                    BrandTextField(
                        text: $cpf,
                        placeholder: "000.000.000-00",
                        brandTheme: brandTheme,
                        keyboardType: .numberPad,
                        trailingSystemImage: "square.grid.3x3.fill"
                    )
                    .onChange(of: cpf) { _, newValue in
                        cpf = Self.maskCpf(newValue)
                    }
                    .padding(.bottom, 16)

                    FieldLabel(text: "Senha", brandTheme: brandTheme)
                    BrandSecureField(
                        text: $password,
                        passwordVisible: $passwordVisible,
                        brandTheme: brandTheme
                    )

                    HStack {
                        Spacer()
                        Button("Esqueci minha senha") {}
                            .font(brandTheme.font(size: 12, weight: .medium))
                            .foregroundStyle(brandTheme.primary)
                    }
                    .padding(.bottom, 32)

                    if let errorMessage = uiState.errorMessage {
                        Text(errorMessage)
                            .font(brandTheme.font(size: 13))
                            .foregroundStyle(brandTheme.error)
                            .padding(.bottom, 12)
                    }

                    Button(action: {
                        onDismissError()
                        onLoginTap(cpf, password)
                    }) {
                        Group {
                            if uiState.isLoading {
                                ProgressView().tint(brandTheme.onPrimary)
                            } else {
                                Text("Entrar")
                                    .font(brandTheme.font(size: 15, weight: .bold))
                            }
                        }
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                    }
                    .background(canSubmit ? brandTheme.primary : Color(hex: "#6B7280").opacity(0.3))
                    .clipShape(RoundedRectangle(cornerRadius: brandTheme.cornerRadius))
                    .disabled(!canSubmit)
                    .padding(.bottom, 12)

                    if uiState.isBiometricAvailable && uiState.isBiometricEnabled {
                        Button(action: onBiometricTap) {
                            HStack(spacing: 8) {
                                Image(systemName: "touchid")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 18, height: 18)
                                Text("Entrar com biometria")
                                    .font(brandTheme.font(size: 15, weight: .semibold))
                            }
                            .foregroundStyle(brandTheme.primary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .overlay(
                                RoundedRectangle(cornerRadius: brandTheme.cornerRadius)
                                    .stroke(brandTheme.primary, lineWidth: 1)
                            )
                        }
                        .disabled(uiState.isLoading)
                        .padding(.bottom, 24)
                    }

                    HStack(spacing: 12) {
                        Rectangle().fill(dividerColor).frame(height: 1)
                        Text("ou")
                            .font(brandTheme.font(size: 12))
                            .foregroundStyle(brandTheme.onSurface.opacity(0.6))
                        Rectangle().fill(dividerColor).frame(height: 1)
                    }
                    .padding(.bottom, 20)

                    HStack(spacing: 0) {
                        Spacer(minLength: 0)
                        Text("Ainda não tem conta? ")
                            .font(brandTheme.font(size: 13))
                            .foregroundStyle(brandTheme.onSurface)
                        Text("Abra a sua grátis")
                            .font(brandTheme.font(size: 13, weight: .semibold))
                            .foregroundStyle(brandTheme.primary)
                            .onTapGesture(perform: onRegisterTap)
                        Spacer(minLength: 0)
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 24)
            }
            .background(brandTheme.background)
        }
        .background(brandTheme.background)
        .toolbar(.hidden, for: .navigationBar)
    }

    private var titleFont: Font {
        if isPremium {
            return brandTheme.font(size: 24, weight: .bold, italic: true)
        }
        return brandTheme.font(size: 24, weight: .bold)
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

private struct LoginHeader: View {
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

private struct FieldLabel: View {
    let text: String
    let brandTheme: BrandTheme

    var body: some View {
        Text(text)
            .font(brandTheme.font(size: 12, weight: .semibold))
            .foregroundStyle(brandTheme.onBackground)
            .padding(.bottom, 4)
    }
}

private struct BrandTextField: View {
    @Binding var text: String
    let placeholder: String
    let brandTheme: BrandTheme
    var keyboardType: UIKeyboardType = .default
    var trailingSystemImage: String

    var body: some View {
        ZStack(alignment: .trailing) {
            TextField(placeholder, text: $text)
                .font(brandTheme.font(size: 14))
                .foregroundStyle(brandTheme.onBackground)
                .keyboardType(keyboardType)
                .padding(.leading, 15)
                .padding(.trailing, 45)
                .frame(maxWidth: .infinity, alignment: .leading)

            Image(systemName: trailingSystemImage)
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
                .foregroundStyle(brandTheme.onSurface)
                .padding(.trailing, 15)
        }
        .frame(height: 48)
        .background(brandTheme.surface)
        .overlay(
            RoundedRectangle(cornerRadius: brandTheme.cornerRadius)
                .stroke(brandTheme.onSurface.opacity(0.3), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: brandTheme.cornerRadius))
    }
}

private struct BrandSecureField: View {
    @Binding var text: String
    @Binding var passwordVisible: Bool
    let brandTheme: BrandTheme

    var body: some View {
        ZStack(alignment: .trailing) {
            Group {
                if passwordVisible {
                    TextField("••••••", text: $text)
                } else {
                    SecureField("••••••", text: $text)
                }
            }
            .font(brandTheme.font(size: 14))
            .foregroundStyle(brandTheme.onBackground)
            .padding(.leading, 15)
            .padding(.trailing, 45)
            .frame(maxWidth: .infinity, alignment: .leading)

            Button(action: { passwordVisible.toggle() }) {
                Image(systemName: passwordVisible ? "eye.slash" : "eye")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundStyle(brandTheme.onSurface)
            }
            .padding(.trailing, 15)
        }
        .frame(height: 48)
        .background(brandTheme.surface)
        .overlay(
            RoundedRectangle(cornerRadius: brandTheme.cornerRadius)
                .stroke(brandTheme.onSurface.opacity(0.3), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: brandTheme.cornerRadius))
    }
}

#Preview("Banco Principal") {
    LoginContent(
        uiState: AuthUiState(isBiometricAvailable: true, isBiometricEnabled: true),
        onLoginTap: { _, _ in },
        onBiometricTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPrincipal()))
}

#Preview("Fintech Verde") {
    LoginContent(
        uiState: AuthUiState(isBiometricAvailable: true, isBiometricEnabled: true),
        onLoginTap: { _, _ in },
        onBiometricTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.fintechVerde())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.fintechVerde()))
}

#Preview("Erro de credenciais") {
    LoginContent(
        uiState: AuthUiState(failedAttempts: 2, errorMessage: "CPF ou senha incorretos. Tentativa 2 de 5."),
        onLoginTap: { _, _ in },
        onBiometricTap: {}
    )
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPremium())
    .environment(\.brandTheme, BrandTheme(config: BrandCatalog.shared.bancoPremium()))
}
