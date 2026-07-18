import SwiftUI
import shared

struct ConfirmRecipientView: View {
    let onBack: () -> Void
    @ObservedObject var viewModel: PixViewModel

    var body: some View {
        ConfirmRecipientContent(
            uiState: viewModel.uiState,
            onConfirm: viewModel.onConfirmRecipient,
            onReject: viewModel.onRejectRecipient
        )
    }
}

struct ConfirmRecipientContent: View {
    let uiState: PixUiState
    let onConfirm: () -> Void
    let onReject: () -> Void
    @Environment(\.whiteLabelConfig) private var config

    var body: some View {
        guard let recipient = uiState.recipient else {
            return AnyView(ProgressView())
        }
        return AnyView(
            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        Text("É este o destinatário?")
                            .font(.title2).bold()
                            .padding(.top, 8)

                        HStack(spacing: 16) {
                            Image(systemName: "person.circle.fill")
                                .resizable()
                                .frame(width: 56, height: 56)
                                .foregroundStyle(config.primaryColor)

                            VStack(alignment: .leading, spacing: 4) {
                                Text(recipient.name)
                                    .font(.headline)
                                Text(recipient.institution)
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                                Text(recipient.maskedKey)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .padding(.horizontal, 24)
                }

                VStack(spacing: 12) {
                    Button(action: onConfirm) {
                        Text("Confirmar")
                            .fontWeight(.semibold)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(config.primaryColor)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    Button(action: onReject) {
                        Text("Não é esse? Digitar outra chave")
                            .fontWeight(.medium)
                            .foregroundStyle(config.primaryColor)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(config.primaryColor, lineWidth: 1.5)
                            )
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 32)
            }
            .navigationTitle("Confirmar destinatário")
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(config.primaryColor, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
        )
    }
}

#Preview {
    NavigationStack {
        ConfirmRecipientContent(
            uiState: {
                var s = PixUiState()
                s.recipient = PixRecipientDisplay(name: "João da Silva", institution: "Nubank", maskedKey: "•••.456.•••-••")
                return s
            }(),
            onConfirm: {},
            onReject: {}
        )
    }
    .environment(\.whiteLabelConfig, BrandCatalog.shared.bancoPrincipal())
}
