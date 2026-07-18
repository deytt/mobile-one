import SwiftUI
import shared

// MARK: - Stateful

struct EnterKeyView: View {
    let onBack: () -> Void
    @ObservedObject var viewModel: PixViewModel

    var body: some View {
        EnterKeyContent(
            uiState: viewModel.uiState,
            onKeyChanged: viewModel.onKeyChanged,
            onContinue: viewModel.onContinueFromKey,
            onScanQRCode: viewModel.onScanQRCode,
            onBack: onBack
        )
    }
}

// MARK: - Stateless

struct EnterKeyContent: View {
    let uiState: PixUiState
    let onKeyChanged: (String) -> Void
    let onContinue: () -> Void
    let onScanQRCode: () -> Void
    let onBack: () -> Void
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Qual é a chave PIX?")
                    .font(.title2).bold()
                    .padding(.top, 8)

                Text("Digite CPF, CNPJ, e-mail, telefone ou chave aleatória.")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)

                Spacer().frame(height: 8)

                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Image(systemName: keyIcon)
                            .foregroundStyle(config.primaryColor)
                        TextField("Chave PIX", text: Binding(
                            get: { uiState.keyInput },
                            set: { onKeyChanged($0) }
                        ))
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                    }
                    .padding()
                    .background(Color(.secondarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(validationColor, lineWidth: 1.5)
                    )

                    if case .invalid(let reason) = uiState.keyValidationState {
                        Text(reason)
                            .font(.caption)
                            .foregroundStyle(.red)
                    } else if case .valid = uiState.keyValidationState,
                              let type = uiState.detectedKeyType {
                        Text("\(type) detectado")
                            .font(.caption)
                            .foregroundStyle(config.primaryColor)
                    }
                }

                if case .valid = uiState.keyValidationState {
                    Button(action: onContinue) {
                        HStack {
                            Spacer()
                            if uiState.isLoading {
                                ProgressView()
                                    .tint(.white)
                                Text("Consultando...")
                                    .foregroundStyle(.white)
                            } else {
                                Text("Continuar")
                                    .foregroundStyle(.white)
                                    .fontWeight(.semibold)
                            }
                            Spacer()
                        }
                        .padding()
                        .background(config.primaryColor)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    .disabled(uiState.isLoading)
                }
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("Transferência PIX")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: onScanQRCode) {
                    Image(systemName: "qrcode.viewfinder")
                        .foregroundStyle(config.onPrimaryColor)
                }
            }
        }
        .toolbarBackground(config.primaryColor, for: .navigationBar)
        .toolbarColorScheme(.dark, for: .navigationBar)
    }

    private var keyIcon: String {
        switch uiState.detectedKeyType {
        case "Email": return "envelope"
        case "CPF", "CNPJ": return "person"
        case "Phone": return "phone"
        case "RandomKey": return "key"
        case "QRCode": return "qrcode"
        default: return "creditcard"
        }
    }

    private var validationColor: Color {
        switch uiState.keyValidationState {
        case .invalid: return .red
        case .valid: return .green
        case .idle: return Color(.separator)
        }
    }
}

#Preview {
    NavigationStack {
        EnterKeyContent(
            uiState: {
                var s = PixUiState()
                s.keyInput = "joao@email.com"
                s.detectedKeyType = "Email"
                s.keyValidationState = .valid
                return s
            }(),
            onKeyChanged: { _ in },
            onContinue: {},
            onScanQRCode: {},
            onBack: {}
        )
    }
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
